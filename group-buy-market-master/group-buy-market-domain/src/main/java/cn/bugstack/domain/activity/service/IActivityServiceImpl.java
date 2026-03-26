package cn.bugstack.domain.activity.service;

import cn.bugstack.api.dto.activity.ActivityCreateRequestDTO;
import cn.bugstack.api.dto.activity.ActivityResponseDTO;
import cn.bugstack.api.dto.activity.ActivityUpdateRequestDTO;
import cn.bugstack.api.dto.activity.SCSkuActivityBindRequestDTO;
import cn.bugstack.api.dto.activity.SCSkuActivityResponseDTO;
import cn.bugstack.api.response.Response;
import cn.bugstack.domain.activity.adapter.repository.IActivityRepository;
import cn.bugstack.domain.activity.model.entity.GroupBuyActivityEntity;
import cn.bugstack.domain.activity.model.entity.SCSkuActivityEntity;
import cn.bugstack.types.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author needyou
 * @description 活动领域服务实现
 * @create 2026-03-12
 */
@Slf4j
@Service
public class IActivityServiceImpl implements IActivityService {

    @Resource
    private IActivityRepository activityRepository;

    @Override
    public Response<Long> createActivity(ActivityCreateRequestDTO requestDTO) {
        try {
            if (requestDTO == null
                    || StringUtils.isBlank(requestDTO.getActivityName())
                    || StringUtils.isBlank(requestDTO.getDiscountId())
                    || requestDTO.getGroupType() == null
                    || requestDTO.getTakeLimitCount() == null
                    || requestDTO.getTarget() == null
                    || requestDTO.getValidTime() == null
                    || requestDTO.getStatus() == null
                    || requestDTO.getStartTime() == null
                    || requestDTO.getEndTime() == null) {
                return Response.<Long>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

            Long activityId = requestDTO.getActivityId();
            boolean autoGenerateActivityId = activityId == null;
            if (autoGenerateActivityId) {
                activityId = Long.valueOf(RandomStringUtils.randomNumeric(6));
            }

            GroupBuyActivityEntity entity = GroupBuyActivityEntity.builder()
                    .activityId(activityId)
                    .activityName(requestDTO.getActivityName())
                    .discountId(requestDTO.getDiscountId())
                    .groupType(requestDTO.getGroupType())
                    .takeLimitCount(requestDTO.getTakeLimitCount())
                    .target(requestDTO.getTarget())
                    .validTime(requestDTO.getValidTime())
                    .status(requestDTO.getStatus())
                    .startTime(requestDTO.getStartTime())
                    .endTime(requestDTO.getEndTime())
                    .tagId(requestDTO.getTagId())
                    .tagScope(requestDTO.getTagScope())
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();

            try {
                activityRepository.insertGroupBuyActivity(entity);
            } catch (DuplicateKeyException e) {
                if (autoGenerateActivityId) {
                    activityId = Long.valueOf(RandomStringUtils.randomNumeric(6));
                    entity.setActivityId(activityId);
                    activityRepository.insertGroupBuyActivity(entity);
                } else {
                    throw e;
                }
            }

            return Response.<Long>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(activityId)
                    .build();
        } catch (Exception e) {
            log.error("创建活动失败 req:{}", requestDTO, e);
            return Response.<Long>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    public Response<Boolean> updateActivity(Long activityId, ActivityUpdateRequestDTO requestDTO) {
        try {
            if (activityId == null || requestDTO == null) {
                return Response.<Boolean>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .data(Boolean.FALSE)
                        .build();
            }

            boolean updateSuccess = activityRepository.updateGroupBuyActivity(GroupBuyActivityEntity.builder()
                    .activityId(activityId)
                    .activityName(requestDTO.getActivityName())
                    .discountId(requestDTO.getDiscountId())
                    .groupType(requestDTO.getGroupType())
                    .takeLimitCount(requestDTO.getTakeLimitCount())
                    .target(requestDTO.getTarget())
                    .validTime(requestDTO.getValidTime())
                    .status(requestDTO.getStatus())
                    .startTime(requestDTO.getStartTime())
                    .endTime(requestDTO.getEndTime())
                    .tagId(requestDTO.getTagId())
                    .tagScope(requestDTO.getTagScope())
                    .updateTime(new Date())
                    .build());

            if (!updateSuccess) {
                return Response.<Boolean>builder()
                        .code(ResponseCode.UPDATE_ZERO.getCode())
                        .info(ResponseCode.UPDATE_ZERO.getInfo())
                        .data(Boolean.FALSE)
                        .build();
            }

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(Boolean.TRUE)
                    .build();
        } catch (Exception e) {
            log.error("更新活动失败 activityId:{} req:{}", activityId, requestDTO, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(Boolean.FALSE)
                    .build();
        }
    }

    @Override
    public Response<Boolean> deleteActivity(Long activityId) {
        try {
            if (activityId == null) {
                return Response.<Boolean>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .data(Boolean.FALSE)
                        .build();
            }

            boolean deleteSuccess = activityRepository.deleteGroupBuyActivity(activityId);
            if (!deleteSuccess) {
                return Response.<Boolean>builder()
                        .code(ResponseCode.UPDATE_ZERO.getCode())
                        .info(ResponseCode.UPDATE_ZERO.getInfo())
                        .data(Boolean.FALSE)
                        .build();
            }

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(Boolean.TRUE)
                    .build();
        } catch (Exception e) {
            log.error("删除活动失败 activityId:{}", activityId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(Boolean.FALSE)
                    .build();
        }
    }

    @Override
    public Response<ActivityResponseDTO> queryActivity(Long activityId) {
        try {
            if (activityId == null) {
                return Response.<ActivityResponseDTO>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

            ActivityResponseDTO dto = toDTO(activityRepository.queryGroupBuyActivityByActivityId(activityId));
            return Response.<ActivityResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(dto)
                    .build();
        } catch (Exception e) {
            log.error("查询活动失败 activityId:{}", activityId, e);
            return Response.<ActivityResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    public Response<List<ActivityResponseDTO>> queryActivityList() {
        try {
            List<GroupBuyActivityEntity> list = activityRepository.queryGroupBuyActivityList();
            List<ActivityResponseDTO> dtoList = list.stream().map(this::toDTO).collect(Collectors.toList());
            return Response.<List<ActivityResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(dtoList)
                    .build();
        } catch (Exception e) {
            log.error("查询活动列表失败", e);
            return Response.<List<ActivityResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    public Response<Boolean> bindSkuActivity(SCSkuActivityBindRequestDTO requestDTO) {
        try {
            if (requestDTO == null
                    || StringUtils.isBlank(requestDTO.getSource())
                    || StringUtils.isBlank(requestDTO.getChannel())
                    || StringUtils.isBlank(requestDTO.getGoodsId())
                    || requestDTO.getActivityId() == null) {
                return Response.<Boolean>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .data(Boolean.FALSE)
                        .build();
            }

            boolean bindSuccess = activityRepository.bindSkuActivity(
                    requestDTO.getSource(),
                    requestDTO.getChannel(),
                    requestDTO.getGoodsId(),
                    requestDTO.getActivityId()
            );

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(bindSuccess)
                    .build();
        } catch (Exception e) {
            log.error("绑定商品活动失败 req:{}", requestDTO, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(Boolean.FALSE)
                    .build();
        }
    }

    @Override
    public Response<Boolean> unbindSkuActivity(String source, String channel, String goodsId) {
        try {
            if (StringUtils.isBlank(source) || StringUtils.isBlank(channel) || StringUtils.isBlank(goodsId)) {
                return Response.<Boolean>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .data(Boolean.FALSE)
                        .build();
            }

            boolean unbindSuccess = activityRepository.unbindSkuActivity(source, channel, goodsId);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(unbindSuccess)
                    .build();
        } catch (Exception e) {
            log.error("解绑商品活动失败 source:{} channel:{} goodsId:{}", source, channel, goodsId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(Boolean.FALSE)
                    .build();
        }
    }

    @Override
    public Response<SCSkuActivityResponseDTO> querySkuActivity(String source, String channel, String goodsId) {
        try {
            if (StringUtils.isBlank(source) || StringUtils.isBlank(channel) || StringUtils.isBlank(goodsId)) {
                return Response.<SCSkuActivityResponseDTO>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

            SCSkuActivityEntity entity = activityRepository.querySCSkuActivity(source, channel, goodsId);
            SCSkuActivityResponseDTO dto = entity == null ? null : SCSkuActivityResponseDTO.builder()
                    .id(entity.getId())
                    .source(entity.getSource())
                    .channel(entity.getChannel())
                    .activityId(entity.getActivityId())
                    .goodsId(entity.getGoodsId())
                    .createTime(entity.getCreateTime())
                    .updateTime(entity.getUpdateTime())
                    .build();

            return Response.<SCSkuActivityResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(dto)
                    .build();
        } catch (Exception e) {
            log.error("查询商品活动绑定失败 source:{} channel:{} goodsId:{}", source, channel, goodsId, e);
            return Response.<SCSkuActivityResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    private ActivityResponseDTO toDTO(GroupBuyActivityEntity entity) {
        if (entity == null) return null;
        return ActivityResponseDTO.builder()
                .id(entity.getId())
                .activityId(entity.getActivityId())
                .activityName(entity.getActivityName())
                .discountId(entity.getDiscountId())
                .groupType(entity.getGroupType())
                .takeLimitCount(entity.getTakeLimitCount())
                .target(entity.getTarget())
                .validTime(entity.getValidTime())
                .status(entity.getStatus())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .tagId(entity.getTagId())
                .tagScope(entity.getTagScope())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }

}
