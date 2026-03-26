package cn.bugstack.domain.activity.service;

import cn.bugstack.api.dto.activity.ActivityCreateRequestDTO;
import cn.bugstack.api.dto.activity.ActivityResponseDTO;
import cn.bugstack.api.dto.activity.ActivityUpdateRequestDTO;
import cn.bugstack.api.dto.activity.SCSkuActivityBindRequestDTO;
import cn.bugstack.api.dto.activity.SCSkuActivityResponseDTO;
import cn.bugstack.api.response.Response;

import java.util.List;

/**
 * @author needyou
 * @description 活动领域服务
 * @create 2026-03-12
 */
public interface IActivityService {

    Response<Long> createActivity(ActivityCreateRequestDTO requestDTO);

    Response<Boolean> updateActivity(Long activityId, ActivityUpdateRequestDTO requestDTO);

    Response<Boolean> deleteActivity(Long activityId);

    Response<ActivityResponseDTO> queryActivity(Long activityId);

    Response<List<ActivityResponseDTO>> queryActivityList();

    Response<Boolean> bindSkuActivity(SCSkuActivityBindRequestDTO requestDTO);

    Response<Boolean> unbindSkuActivity(String source, String channel, String goodsId);

    Response<SCSkuActivityResponseDTO> querySkuActivity(String source, String channel, String goodsId);

}
