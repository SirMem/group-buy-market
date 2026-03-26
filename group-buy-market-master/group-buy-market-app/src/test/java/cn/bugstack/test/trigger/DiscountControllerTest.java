package cn.bugstack.test.trigger;

import cn.bugstack.api.admin.IDiscountApi;
import cn.bugstack.api.dto.discount.DiscountCreateRequestDTO;
import cn.bugstack.api.dto.discount.DiscountResponseDTO;
import cn.bugstack.api.dto.discount.DiscountUpdateRequestDTO;
import cn.bugstack.api.response.Response;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author needyou
 * @description 折扣管理接口集成测试
 * @create 2026-03-21
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class DiscountControllerTest {

    @Resource
    private IDiscountApi discountController;

    @Test
    public void test_createDiscount_ZJ() {
        DiscountCreateRequestDTO requestDTO = new DiscountCreateRequestDTO();
        requestDTO.setDiscountName("直减20元");
        requestDTO.setDiscountDesc("直接减免20元优惠");
        requestDTO.setDiscountType(0);
        requestDTO.setMarketPlan("ZJ");
        requestDTO.setMarketExpr("20");

        Response<String> response = discountController.createDiscount(requestDTO);

        log.info("测试结果 req:{} res:{}", JSON.toJSONString(requestDTO), JSON.toJSONString(response));
    }

    @Test
    public void test_createDiscount_MJ() {
        DiscountCreateRequestDTO requestDTO = new DiscountCreateRequestDTO();
        requestDTO.setDiscountName("满99减30");
        requestDTO.setDiscountDesc("消费满99元减30元");
        requestDTO.setDiscountType(0);
        requestDTO.setMarketPlan("MJ");
        requestDTO.setMarketExpr("99:30");

        Response<String> response = discountController.createDiscount(requestDTO);

        log.info("测试结果 req:{} res:{}", JSON.toJSONString(requestDTO), JSON.toJSONString(response));
    }

    @Test
    public void test_createDiscount_N() {
        DiscountCreateRequestDTO requestDTO = new DiscountCreateRequestDTO();
        requestDTO.setDiscountName("八折优惠");
        requestDTO.setDiscountDesc("八折优惠活动");
        requestDTO.setDiscountType(0);
        requestDTO.setMarketPlan("N");
        requestDTO.setMarketExpr("0.8");

        Response<String> response = discountController.createDiscount(requestDTO);

        log.info("测试结果 req:{} res:{}", JSON.toJSONString(requestDTO), JSON.toJSONString(response));
    }

    @Test
    public void test_queryDiscountList() {
        Response<List<DiscountResponseDTO>> response = discountController.queryDiscountList();

        log.info("测试结果:{}", JSON.toJSONString(response));
    }

    @Test
    public void test_queryDiscount() {
        // 先查列表取一个discountId
        Response<List<DiscountResponseDTO>> listResponse = discountController.queryDiscountList();
        if (listResponse.getData() == null || listResponse.getData().isEmpty()) {
            log.info("暂无折扣数据，请先执行 test_createDiscount_ZJ");
            return;
        }
        String discountId = listResponse.getData().get(0).getDiscountId();

        Response<DiscountResponseDTO> response = discountController.queryDiscount(discountId);

        log.info("测试结果 discountId:{} res:{}", discountId, JSON.toJSONString(response));
    }

    @Test
    public void test_updateDiscount() {
        Response<List<DiscountResponseDTO>> listResponse = discountController.queryDiscountList();
        if (listResponse.getData() == null || listResponse.getData().isEmpty()) {
            log.info("暂无折扣数据，请先执行 test_createDiscount_ZJ");
            return;
        }
        String discountId = listResponse.getData().get(0).getDiscountId();

        DiscountUpdateRequestDTO requestDTO = new DiscountUpdateRequestDTO();
        requestDTO.setDiscountName("直减20元（已更新）");
        requestDTO.setDiscountDesc("更新后的折扣描述");

        Response<Boolean> response = discountController.updateDiscount(discountId, requestDTO);

        log.info("测试结果 discountId:{} req:{} res:{}", discountId, JSON.toJSONString(requestDTO), JSON.toJSONString(response));
    }

    @Test
    public void test_deleteDiscount() {
        Response<List<DiscountResponseDTO>> listResponse = discountController.queryDiscountList();
        if (listResponse.getData() == null || listResponse.getData().isEmpty()) {
            log.info("暂无折扣数据，请先执行 test_createDiscount_ZJ");
            return;
        }
        String discountId = listResponse.getData().get(listResponse.getData().size() - 1).getDiscountId();

        Response<Boolean> response = discountController.deleteDiscount(discountId);

        log.info("测试结果 discountId:{} res:{}", discountId, JSON.toJSONString(response));
    }

}
