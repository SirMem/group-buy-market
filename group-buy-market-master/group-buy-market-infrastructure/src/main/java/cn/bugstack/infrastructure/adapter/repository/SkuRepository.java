package cn.bugstack.infrastructure.adapter.repository;

import cn.bugstack.domain.admin.sku.adapter.repository.ISkuRepository;
import cn.bugstack.domain.admin.sku.model.entity.SkuInventoryEntity;
import cn.bugstack.domain.admin.sku.model.vo.SkuInfoVO;
import cn.bugstack.infrastructure.persistent.dao.IActivityInventoryDao;
import cn.bugstack.infrastructure.persistent.dao.IGroupBuyActivityDao;
import cn.bugstack.infrastructure.persistent.dao.ISCSkuActivityDao;
import cn.bugstack.infrastructure.persistent.dao.ISkuDao;
import cn.bugstack.infrastructure.persistent.po.ActivityInventory;
import cn.bugstack.infrastructure.persistent.po.GroupBuyActivity;
import cn.bugstack.infrastructure.persistent.po.SCSkuActivity;
import cn.bugstack.infrastructure.persistent.po.Sku;
import cn.bugstack.infrastructure.persistent.po.join.SkuWithInventoryPO;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@Slf4j
public class SkuRepository implements ISkuRepository {

    @Resource
    private ISkuDao skuDao;

    @Resource
    private IActivityInventoryDao activityInventoryDao;

    @Resource
    private ISCSkuActivityDao scSkuActivityDao;

    @Resource
    private IGroupBuyActivityDao groupBuyActivityDao;

    /**
     * 创建商品并生成 goodsId
     *
     * @return 生成的 goodsId
     */
    @Override
    public String insertSku(String source, String channel, String goodsName, BigDecimal originalPrice) {
        // 最多重试 3 次，防止极低概率的 goodsId 撞库
        for (int i = 0; i < 3; i++) {
            String goodsId = RandomStringUtils.randomNumeric(12);

            Sku sku = Sku.builder()
                    .source(source)
                    .channel(channel)
                    .goodsId(goodsId)
                    .goodsName(goodsName)
                    .originalPrice(originalPrice)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            try {
                skuDao.insert(sku);
                // DB 插入成功后，直接返回本次生成的 goodsId
                return goodsId;
            } catch (DuplicateKeyException e) {
                // UNIQUE KEY uq_goods_id(goods_id) 冲突，重试一次
                log.info(e.getMessage(), "商品插入异常，请重试");
                throw e;

            }
        }

        // 多次重试仍失败，抛业务异常
        throw new AppException(ResponseCode.INDEX_EXCEPTION);
    }

    @Override
    public void insertInventoryWithoutActivityId(String goodsId, Long totalStock) {
        // 目前没有活动维度，这里先用占位 activityId = 0L
        ActivityInventory activityInventory = ActivityInventory.builder()
                .activityId(0L)
                .goodsId(goodsId)
                .totalStock(totalStock)
                .reservedStock(0L)
                .soldStock(0L)
                .version("0")
                .createTime(new Date())
                .updateTime(new Date())
                .build();

        activityInventoryDao.insert(activityInventory);
    }

    @Override
    @Transactional
    public String createSkuAndInventory(String source, String channel, String name, BigDecimal originalPrice, Long totalStock) {

        // 最多重试 3 次，防止极低概率的 goodsId 撞库
        String goodsId = null;
        for (int i = 0; i < 3; i++) {
            goodsId = RandomStringUtils.randomNumeric(12);

            Sku sku = Sku.builder()
                    .source(source)
                    .channel(channel)
                    .goodsId(goodsId)
                    .goodsName(name)
                    .originalPrice(originalPrice)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            try {
                skuDao.insert(sku);
                // DB 插入成功后，直接返回本次生成的 goodsId
                log.info("商家创建商品完成 goodsId:{} source:{} channel:{} name:{}",
                        goodsId, source, channel, name);
                break;
            } catch (DuplicateKeyException e) {
                log.warn("goodsId 撞库，重试一次 goodsId={}", goodsId);
                if (i == 2) {
                    throw new AppException(ResponseCode.INDEX_EXCEPTION);
                }
            }
        }

        ActivityInventory activityInventory = ActivityInventory.builder()
                .activityId(0L)
                .goodsId(goodsId)
                .totalStock(totalStock)
                .reservedStock(0L)
                .soldStock(0L)
                .version("0")
                .createTime(new Date())
                .updateTime(new Date())
                .build();

        activityInventoryDao.insert(activityInventory);


        return goodsId;
    }

    /**
     * 查询商品信息（含库存与活动），映射为领域 VO
     */
    @Override
    public PageInfo<SkuInfoVO> querySkuInfoPage(SkuInventoryEntity skuInventoryEntity) {
        PageHelper.startPage(skuInventoryEntity.getPage(), skuInventoryEntity.getPageSize());

        List<SkuWithInventoryPO> skuWithInventoryList = skuDao.querySkuWithInventory(
                skuInventoryEntity.getSource(),
                skuInventoryEntity.getChannel(),
                skuInventoryEntity.getGoodsId(),
                skuInventoryEntity.getKeyword()
        );
        if (skuWithInventoryList == null) {
            skuWithInventoryList = new ArrayList<>();
        }

        PageInfo<SkuWithInventoryPO> poPageInfo = new PageInfo<>(skuWithInventoryList);

        List<SkuInfoVO> resultList = new ArrayList<>(skuWithInventoryList.size());
        for (SkuWithInventoryPO po : skuWithInventoryList) {

            // 商品信息：当前仅能从 sku 表中拿到基础字段
            SkuInfoVO.Goods goods = SkuInfoVO.Goods.builder()
                    .goodsId(po.getGoodsId())
                    .source(po.getSource())
                    .channel(po.getChannel())
                    .name(po.getGoodsName())
                    // BigDecimal 元 -> Long 分
                    .price(po.getOriginalPrice() == null ? null : po.getOriginalPrice().movePointRight(2).longValue())
                    .createTime(po.getCreateTime())
                    .updateTime(po.getUpdateTime())
                    .build();

            // 库存信息
            Long totalStock = po.getTotalStock();
            Long reservedStock = po.getReservedStock();
            Long soldStock = po.getSoldStock();

            long total = totalStock == null ? 0L : totalStock;
            long reserved = reservedStock == null ? 0L : reservedStock;
            long sold = soldStock == null ? 0L : soldStock;

            Long available = (totalStock == null && reservedStock == null && soldStock == null)
                    ? null
                    : total - reserved - sold;

            SkuInfoVO.Inventory inventory = SkuInfoVO.Inventory.builder()
                    .totalStock(totalStock)
                    .reservedStock(reservedStock)
                    .soldStock(soldStock)
                    .availableStock(available)
                    .build();

            // 活动绑定信息：通过渠道商品活动关联表 + 拼团活动表查询
            SCSkuActivity scSkuActivityReq = new SCSkuActivity();
            scSkuActivityReq.setSource(po.getSource());
            scSkuActivityReq.setChannel(po.getChannel());
            scSkuActivityReq.setGoodsId(po.getGoodsId());

            List<SkuInfoVO.ActivityBinding> activityBindings = new ArrayList<>();
            SCSkuActivity scSkuActivity = scSkuActivityDao.querySCSkuActivityBySCGoodsId(scSkuActivityReq);
            if (scSkuActivity != null && scSkuActivity.getActivityId() != null) {
                GroupBuyActivity groupBuyActivity =
                        groupBuyActivityDao.queryGroupBuyActivityByActivityId(scSkuActivity.getActivityId());
                if (groupBuyActivity != null) {
                    SkuInfoVO.ActivityBinding binding = SkuInfoVO.ActivityBinding.builder()
                            .activityId(groupBuyActivity.getActivityId())
                            .activityName(groupBuyActivity.getActivityName())
                            .status(groupBuyActivity.getStatus() == null ? null : String.valueOf(groupBuyActivity.getStatus()))
                            .groupType(groupBuyActivity.getGroupType() == null ? null : String.valueOf(groupBuyActivity.getGroupType()))
                            .startTime(groupBuyActivity.getStartTime())
                            .endTime(groupBuyActivity.getEndTime())
                            .build();
                    activityBindings.add(binding);
                }
            }


            SkuInfoVO skuInfoVO = SkuInfoVO.builder()
                    .goods(goods)
                    .inventory(inventory)
                    .activityBindings(activityBindings)
                    .build();

            resultList.add(skuInfoVO);
        }

        PageInfo<SkuInfoVO> voPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(poPageInfo, voPageInfo, "list");
        voPageInfo.setList(resultList);
        return voPageInfo;
    }

    /**
     * 根据商品ID删除商品及其库存、活动关联，保证在同一事务中回滚
     */
    @Override
    @Transactional
    public void deleteSkuAndInventory(String goodsId) {
        // 1. 删除渠道商品活动关联记录
        scSkuActivityDao.deleteByGoodsId(goodsId);
        // 2. 删除库存记录
        activityInventoryDao.deleteByGoodsId(goodsId);
        // 3. 删除商品记录
        skuDao.deleteByGoodsId(goodsId);
    }

    @Override
    @Transactional
    public void updateSkuAndInventory(String goodsId, String name, BigDecimal originalPrice, Long totalStock) {
        if (goodsId == null || goodsId.isEmpty()) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
        }
        if ((name == null || name.isEmpty()) && originalPrice == null && totalStock == null) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
        }

        // 1. 更新库存（如果传了总库存）
        if (totalStock != null) {
            ActivityInventory inventory = activityInventoryDao.queryByGoodsId(goodsId);
            if (inventory == null) {
                throw new AppException(ResponseCode.UPDATE_ZERO);
            }
            long reserved = inventory.getReservedStock() == null ? 0L : inventory.getReservedStock();
            long sold = inventory.getSoldStock() == null ? 0L : inventory.getSoldStock();
            if (totalStock < reserved + sold) {
                // 总库存不能小于已锁单+已售
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
            }
            int count = activityInventoryDao.updateTotalStockByGoodsId(goodsId, totalStock);
            if (count == 0) {
                throw new AppException(ResponseCode.UPDATE_ZERO);
            }
        }

        // 2. 更新商品（如果传了名称/价格）
        if ((name != null && !name.isEmpty()) || originalPrice != null) {
            int count = skuDao.updateByGoodsId(goodsId, name, originalPrice);
            if (count == 0) {
                throw new AppException(ResponseCode.UPDATE_ZERO);
            }
        }
    }

}
