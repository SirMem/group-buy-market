package cn.bugstack.api.admin;

import cn.bugstack.api.dto.activity.*;
import cn.bugstack.api.response.Response;

import java.util.List;

/**
 * @author needyou
 * @description 活动管理接口（后台管理）
 * @create 2026-01-13
 */
public interface IActivityApi {

    Response<Long> createActivity(ActivityCreateRequestDTO requestDTO);

    Response<Boolean> updateActivity(Long activityId, ActivityUpdateRequestDTO requestDTO);

    Response<Boolean> deleteActivity(Long activityId);

    Response<ActivityResponseDTO> queryActivity(Long activityId);

    Response<List<ActivityResponseDTO>> queryActivityList();

    /**
     * 绑定渠道商品与活动（source+channel+goodsId -> activityId）
     */
    Response<Boolean> bindSkuActivity(SCSkuActivityBindRequestDTO requestDTO);

    Response<Boolean> unbindSkuActivity(String source, String channel, String goodsId);

    Response<SCSkuActivityResponseDTO> querySkuActivity(String source, String channel, String goodsId);

    /**
     * 绑定折扣与活动
     */

}

