package cn.bugstack.test.trigger;

import cn.bugstack.api.admin.IActivityApi;
import cn.bugstack.api.admin.IDiscountApi;
import cn.bugstack.api.dto.activity.ActivityCreateRequestDTO;
import cn.bugstack.api.dto.activity.ActivityResponseDTO;
import cn.bugstack.api.dto.activity.ActivityUpdateRequestDTO;
import cn.bugstack.api.dto.activity.SCSkuActivityBindRequestDTO;
import cn.bugstack.api.dto.activity.SCSkuActivityResponseDTO;
import cn.bugstack.api.dto.discount.DiscountResponseDTO;
import cn.bugstack.api.response.Response;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author needyou
 * @description 活动管理接口集成测试
 * @create 2026-03-21
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivityControllerTest {

    @Resource
    private IActivityApi activityController;

    @Resource
    private IDiscountApi discountController;

    @Test
    public void test_createActivity() {
        // 取第一个可用折扣ID
        Response<List<DiscountResponseDTO>> discountList = discountController.queryDiscountList();
        if (discountList.getData() == null || discountList.getData().isEmpty()) {
            log.info("暂无折扣数据，请先执行 DiscountControllerTest#test_createDiscount_ZJ");
            return;
        }
        String discountId = discountList.getData().get(0).getDiscountId();

        ActivityCreateRequestDTO requestDTO = new ActivityCreateRequestDTO();
        requestDTO.setActivityName("测试拼团活动");
        requestDTO.setDiscountId(discountId);
        requestDTO.setGroupType(0);
        requestDTO.setTakeLimitCount(3);
        requestDTO.setTarget(2);
        requestDTO.setValidTime(24);
        requestDTO.setStatus(1);
        requestDTO.setStartTime(new Date());
        // 有效期30天
        requestDTO.setEndTime(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000));

        Response<Long> response = activityController.createActivity(requestDTO);

        log.info("测试结果 req:{} res:{}", JSON.toJSONString(requestDTO), JSON.toJSONString(response));
    }

    @Test
    public void test_queryActivityList() {
        Response<List<ActivityResponseDTO>> response = activityController.queryActivityList();

        log.info("测试结果:{}", JSON.toJSONString(response));
    }

    @Test
    public void test_queryActivity() {
        Response<List<ActivityResponseDTO>> listResponse = activityController.queryActivityList();
        if (listResponse.getData() == null || listResponse.getData().isEmpty()) {
            log.info("暂无活动数据，请先执行 test_createActivity");
            return;
        }
        Long activityId = listResponse.getData().get(0).getActivityId();

        Response<ActivityResponseDTO> response = activityController.queryActivity(activityId);

        log.info("测试结果 activityId:{} res:{}", activityId, JSON.toJSONString(response));
    }

    @Test
    public void test_updateActivity() {
        Response<List<ActivityResponseDTO>> listResponse = activityController.queryActivityList();
        if (listResponse.getData() == null || listResponse.getData().isEmpty()) {
            log.info("暂无活动数据，请先执行 test_createActivity");
            return;
        }
        Long activityId = listResponse.getData().get(0).getActivityId();

        ActivityUpdateRequestDTO requestDTO = new ActivityUpdateRequestDTO();
        requestDTO.setActivityName("测试拼团活动（已更新）");
        requestDTO.setStatus(1);

        Response<Boolean> response = activityController.updateActivity(activityId, requestDTO);

        log.info("测试结果 activityId:{} req:{} res:{}", activityId, JSON.toJSONString(requestDTO), JSON.toJSONString(response));
    }

    @Test
    public void test_bindSkuActivity() {
        Response<List<ActivityResponseDTO>> listResponse = activityController.queryActivityList();
        if (listResponse.getData() == null || listResponse.getData().isEmpty()) {
            log.info("暂无活动数据，请先执行 test_createActivity");
            return;
        }
        Long activityId = listResponse.getData().get(0).getActivityId();

        SCSkuActivityBindRequestDTO requestDTO = new SCSkuActivityBindRequestDTO();
        requestDTO.setSource("s01");
        requestDTO.setChannel("c01");
        requestDTO.setGoodsId("9890001");
        requestDTO.setActivityId(activityId);

        Response<Boolean> response = activityController.bindSkuActivity(requestDTO);

        log.info("测试结果 req:{} res:{}", JSON.toJSONString(requestDTO), JSON.toJSONString(response));
    }

    @Test
    public void test_querySkuActivity() {
        Response<SCSkuActivityResponseDTO> response = activityController.querySkuActivity("s01", "c01", "9890001");

        log.info("测试结果:{}", JSON.toJSONString(response));
    }

    @Test
    public void test_unbindSkuActivity() {
        Response<Boolean> response = activityController.unbindSkuActivity("s01", "c01", "9890001");

        log.info("测试结果:{}", JSON.toJSONString(response));
    }

    @Test
    public void test_deleteActivity() {
        Response<List<ActivityResponseDTO>> listResponse = activityController.queryActivityList();
        if (listResponse.getData() == null || listResponse.getData().isEmpty()) {
            log.info("暂无活动数据");
            return;
        }
        Long activityId = listResponse.getData().get(listResponse.getData().size() - 1).getActivityId();

        Response<Boolean> response = activityController.deleteActivity(activityId);

        log.info("测试结果 activityId:{} res:{}", activityId, JSON.toJSONString(response));
    }

}
