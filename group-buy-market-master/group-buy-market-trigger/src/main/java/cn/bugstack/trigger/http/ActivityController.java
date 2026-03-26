package cn.bugstack.trigger.http;

import cn.bugstack.api.admin.IActivityApi;
import cn.bugstack.api.dto.activity.ActivityCreateRequestDTO;
import cn.bugstack.api.dto.activity.ActivityResponseDTO;
import cn.bugstack.api.dto.activity.ActivityUpdateRequestDTO;
import cn.bugstack.api.dto.activity.SCSkuActivityBindRequestDTO;
import cn.bugstack.api.dto.activity.SCSkuActivityResponseDTO;
import cn.bugstack.api.response.Response;
import cn.bugstack.domain.activity.service.IActivityService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author needyou
 * @description 活动管理接口（后台管理）
 * @create 2026-01-13
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/gbm/merchant/activity/")
public class ActivityController implements IActivityApi {

    @Resource
    private IActivityService activityService;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public Response<Long> createActivity(@RequestBody ActivityCreateRequestDTO requestDTO) {
        return activityService.createActivity(requestDTO);
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT)
    public Response<Boolean> updateActivity(@RequestParam("activityId") Long activityId,
                                            @RequestBody ActivityUpdateRequestDTO requestDTO) {
        return activityService.updateActivity(activityId, requestDTO);
    }

    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    public Response<Boolean> deleteActivity(@RequestParam("activityId") Long activityId) {
        return activityService.deleteActivity(activityId);
    }

    @RequestMapping(value = "query", method = RequestMethod.GET)
    public Response<ActivityResponseDTO> queryActivity(@RequestParam("activityId") Long activityId) {
        return activityService.queryActivity(activityId);
    }

    @RequestMapping(value = "query_list", method = RequestMethod.GET)
    public Response<List<ActivityResponseDTO>> queryActivityList() {
        return activityService.queryActivityList();
    }

    @RequestMapping(value = "bind_sku_activity", method = RequestMethod.POST)
    public Response<Boolean> bindSkuActivity(@RequestBody SCSkuActivityBindRequestDTO requestDTO) {
        return activityService.bindSkuActivity(requestDTO);
    }

    @RequestMapping(value = "unbind_sku_activity", method = RequestMethod.DELETE)
    public Response<Boolean> unbindSkuActivity(@RequestParam("source") String source,
                                               @RequestParam("channel") String channel,
                                               @RequestParam("goodsId") String goodsId) {
        return activityService.unbindSkuActivity(source, channel, goodsId);
    }

    @RequestMapping(value = "query_sku_activity", method = RequestMethod.GET)
    public Response<SCSkuActivityResponseDTO> querySkuActivity(@RequestParam("source") String source,
                                                               @RequestParam("channel") String channel,
                                                               @RequestParam("goodsId") String goodsId) {
        return activityService.querySkuActivity(source, channel, goodsId);
    }

}
