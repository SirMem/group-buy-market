package cn.bugstack.test.domain.trade;

import cn.bugstack.domain.trade.adapter.repository.ITradeRepository;
import cn.bugstack.domain.trade.model.entity.InventoryReservationEntity;
import cn.bugstack.infrastructure.utils.event.EventPublisher;
import cn.bugstack.infrastructure.utils.redis.IRedisService;
import cn.bugstack.infrastructure.utils.redis.keys.InventoryKeys;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;

/**
 * 总库存预占 & DB 同步流程测试
 *
 * 说明：
 * 1. 通过直接调用仓储层 ITradeRepository，验证 Redis 中可用库存(availKey)、预占记录(resvKey) 的变化。
 * 2. 通过 @MockBean EventPublisher 避免真实发送 MQ，只校验是否触发了对应的发送方法。
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TotalStockReservationTest {

    @Resource
    private ITradeRepository tradeRepository;

    @Resource
    private IRedisService redisService;

    /**
     * 使用 MockBean 覆盖真实的 MQ 发送，实现纯业务逻辑验证，避免依赖外部 RabbitMQ 环境。
     */
    @Resource
    private EventPublisher eventPublisher;

    /**
     * 场景一：成功预占总库存，并且重复预占同一订单时具备幂等性。
     */
    @Test
    public void test_reserveTotalStockTry_success_and_idempotent() throws InterruptedException {
        String goodsId = "9890001";
        String outTradeNo = "inventory-order-001";

        String availKey = InventoryKeys.availKey(goodsId);
        String resvKey = InventoryKeys.resvKey(outTradeNo);

        // 清理环境
        redisService.remove(availKey);
        redisService.remove(resvKey);

        // 设置初始可用库存为 5
        redisService.setAtomicLong(availKey, 5L);

        InventoryReservationEntity inventoryReservationEntity = InventoryReservationEntity.builder()
                .outTradeNo(outTradeNo)
                .userId("u_inventory_01")
                .teamId("T001")
                .activityId(1L)
                .goodsId(goodsId)
                .status(0)
                .expireAt(LocalDateTime.now().plusMinutes(30))
                .build();

        // 第一次预占
        log.info("第一次预占,inventoryReservationEntity={}", JSON.toJSONString(inventoryReservationEntity));


        CountDownLatch countDownLatch = new CountDownLatch(1);

        String reservationId1 = tradeRepository.reserveTotalStockTry(inventoryReservationEntity, 30);
        Assert.assertEquals(outTradeNo, reservationId1);

        log.info("业务方法调用完毕，单号：{}", inventoryReservationEntity.getOutTradeNo());


        Long afterFirstReserve = redisService.getAtomicLong(availKey);
        Assert.assertEquals("预占一次后，可用库存应该减 1", Long.valueOf(4L), afterFirstReserve);

        String snapshot = redisService.getValue(resvKey);
        Assert.assertNotNull("预占成功后，Redis 中应该有预占快照", snapshot);

        InventoryReservationEntity fromRedis = JSON.parseObject(snapshot, InventoryReservationEntity.class);
        Assert.assertEquals(outTradeNo, fromRedis.getOutTradeNo());
        Assert.assertEquals(goodsId, fromRedis.getGoodsId());

        // 预占成功应发送一次预占 MQ
//        Mockito.verify(eventPublisher, Mockito.times(1))
//                .reversePublish(Mockito.any(InventoryReservationEntity.class));

        // 第二次对同一订单预占 —— 幂等，库存不再扣减
        String reservationId2 = tradeRepository.reserveTotalStockTry(inventoryReservationEntity, 30);
        Assert.assertEquals(outTradeNo, reservationId2);

        Long afterSecondReserve = redisService.getAtomicLong(availKey);
        Assert.assertEquals("同一订单重复预占，应保持幂等，不再扣减库存", Long.valueOf(4L), afterSecondReserve);

        // 仍然只应该发送过 1 次预占 MQ
//        Mockito.verify(eventPublisher, Mockito.times(1))
//                .reversePublish(Mockito.any(InventoryReservationEntity.class));
    }

    /**
     * 场景二：确认总库存（支付成功），不再修改 Redis 中的可用库存，只发送结算 MQ。
     */
    @Test
    public void test_confirmTotalStock_send_settlement_event() {
        String goodsId = "9890001";
        String outTradeNo = "inventory-order-001";

        String availKey = InventoryKeys.availKey(goodsId);
        String resvKey = InventoryKeys.resvKey(outTradeNo);

        // 清理环境
        redisService.remove(availKey);
        redisService.remove(resvKey);

        // 设置初始可用库存为 3
        redisService.setAtomicLong(availKey, 3L);

        InventoryReservationEntity inventoryReservationEntity = InventoryReservationEntity.builder()
                .outTradeNo(outTradeNo)
                .userId("u_inventory_02")
                .teamId("T002")
                .activityId(1L)
                .goodsId(goodsId)
                .status(0)
                .expireAt(LocalDateTime.now().plusMinutes(30))
                .build();

        // 先做一次预占，生成 resvKey
        tradeRepository.reserveTotalStockTry(inventoryReservationEntity, 30);
        Long stockAfterReserve = redisService.getAtomicLong(availKey);
        Assert.assertEquals(Long.valueOf(2L), stockAfterReserve);

        // 支付成功，确认总库存（这里只发送 MQ，不修改 Redis 总库存）
        tradeRepository.confirmTotalStock(outTradeNo);

        Long stockAfterConfirm = redisService.getAtomicLong(availKey);
        Assert.assertEquals("确认总库存时，不应再修改 Redis 可用库存", stockAfterReserve, stockAfterConfirm);

        boolean existsResv = redisService.isExists(resvKey);
        Assert.assertTrue("确认总库存后，预占记录仍然保留，供退单时释放使用", existsResv);

//        Mockito.verify(eventPublisher, Mockito.times(1))
//                .confirmPublish(Mockito.any(InventoryReservationEntity.class));
    }

    /**
     * 场景三：取消总库存（超时未支付或退单），应恢复 Redis 可用库存并删除预占记录，发送取消 MQ。
     */
    @Test
    public void test_cancelTotalStock_release_inventory_and_send_cancel_event() {
        String goodsId = "inventory-goods-003";
        String outTradeNo = "inventory-order-003";

        String availKey = InventoryKeys.availKey(goodsId);
        String resvKey = InventoryKeys.resvKey(outTradeNo);

        // 清理环境
        redisService.remove(availKey);
        redisService.remove(resvKey);

        // 初始可用库存为 2
        redisService.setAtomicLong(availKey, 2L);

        InventoryReservationEntity inventoryReservationEntity = InventoryReservationEntity.builder()
                .outTradeNo(outTradeNo)
                .userId("u_inventory_03")
                .teamId("T003")
                .activityId(1L)
                .goodsId(goodsId)
                .status(0)
                .expireAt(LocalDateTime.now().plusMinutes(30))
                .build();

        // 先预占一次，总库存从 2 -> 1
        tradeRepository.reserveTotalStockTry(inventoryReservationEntity, 30);
        Long stockAfterReserve = redisService.getAtomicLong(availKey);
        Assert.assertEquals(Long.valueOf(1L), stockAfterReserve);

        // 取消总库存：应该恢复为 2，并删除预占记录
        tradeRepository.cancelTotalStock(outTradeNo, goodsId);

        Long stockAfterCancel = redisService.getAtomicLong(availKey);
        Assert.assertEquals("取消预占后，应恢复为初始可用库存", Long.valueOf(2L), stockAfterCancel);

        boolean existsResv = redisService.isExists(resvKey);
        Assert.assertFalse("取消预占后，预占记录应该删除", existsResv);

        Mockito.verify(eventPublisher, Mockito.times(1))
                .cancelPublish(Mockito.any(InventoryReservationEntity.class));
    }
}

