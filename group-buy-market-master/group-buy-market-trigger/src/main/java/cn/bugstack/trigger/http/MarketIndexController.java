package cn.bugstack.trigger.http;

import cn.bugstack.api.IMarketIndexService;
import cn.bugstack.api.dto.GoodsMarketBatchRequestDTO;
import cn.bugstack.api.dto.GoodsMarketBatchResponseDTO;
import cn.bugstack.api.dto.GoodsMarketRequestDTO;
import cn.bugstack.api.dto.GoodsMarketResponseDTO;
import cn.bugstack.api.dto.MarketGoodsCardDTO;
import cn.bugstack.api.dto.MarketGoodsPageRequestDTO;
import cn.bugstack.api.response.Response;
import cn.bugstack.domain.activity.model.entity.MarketProductEntity;
import cn.bugstack.domain.activity.model.entity.TrialBalanceEntity;
import cn.bugstack.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import cn.bugstack.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import cn.bugstack.domain.activity.model.valobj.TeamStatisticVO;
import cn.bugstack.domain.activity.service.IIndexGroupBuyMarketService;
import cn.bugstack.domain.admin.sku.ISkuService;
import cn.bugstack.domain.admin.sku.model.entity.SkuInventoryEntity;
import cn.bugstack.domain.admin.sku.model.vo.SkuInfoVO;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.wrench.rate.limiter.types.annotations.RateLimiterAccessInterceptor;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * @author needyou
 * @description 营销首页服务
 * @create 2025-02-02 16:03
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/gbm/index/")
public class MarketIndexController implements IMarketIndexService {

    @Resource
    private IIndexGroupBuyMarketService indexGroupBuyMarketService;
    @Resource
    private ISkuService merchantSkuService;

    @RateLimiterAccessInterceptor(key = "userId", fallbackMethod = "queryGroupBuyMarketConfigFallBack", permitsPerSecond = 1.0d, blacklistCount = 1)
    @RequestMapping(value = "query_group_buy_market_config", method = RequestMethod.POST)
    @Override
    public Response<GoodsMarketResponseDTO> queryGroupBuyMarketConfig(@RequestBody GoodsMarketRequestDTO requestDTO) {
        try {
            log.info("查询拼团营销配置开始:{} goodsId:{}", requestDTO.getUserId(), requestDTO.getGoodsId());

            if (StringUtils.isBlank(requestDTO.getUserId()) || StringUtils.isBlank(requestDTO.getSource()) || StringUtils.isBlank(requestDTO.getChannel()) || StringUtils.isBlank(requestDTO.getGoodsId())) {
                return Response.<GoodsMarketResponseDTO>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

            // 1. 营销优惠试算
            TrialBalanceEntity trialBalanceEntity = indexGroupBuyMarketService.indexMarketTrial(MarketProductEntity.builder()
                    .userId(requestDTO.getUserId())
                    .source(requestDTO.getSource())
                    .channel(requestDTO.getChannel())
                    .goodsId(requestDTO.getGoodsId())
                    .build());


            GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = trialBalanceEntity.getGroupBuyActivityDiscountVO();
            Long activityId = groupBuyActivityDiscountVO.getActivityId();

            // 2. 查询拼团组队
            List<UserGroupBuyOrderDetailEntity> userGroupBuyOrderDetailEntities = indexGroupBuyMarketService.queryInProgressUserGroupBuyOrderDetailList(activityId, requestDTO.getUserId(), 1, 2);

            // 3. 统计拼团数据
            TeamStatisticVO teamStatisticVO = indexGroupBuyMarketService.queryTeamStatisticByActivityId(activityId);

            GoodsMarketResponseDTO.Goods goods = GoodsMarketResponseDTO.Goods.builder()
                    .goodsId(trialBalanceEntity.getGoodsId())
                    .originalPrice(trialBalanceEntity.getOriginalPrice())
                    .deductionPrice(trialBalanceEntity.getDeductionPrice())
                    .payPrice(trialBalanceEntity.getPayPrice())
                    .build();

            List<GoodsMarketResponseDTO.Team> teams = new ArrayList<>();
            if (null != userGroupBuyOrderDetailEntities && !userGroupBuyOrderDetailEntities.isEmpty()) {
                for (UserGroupBuyOrderDetailEntity userGroupBuyOrderDetailEntity : userGroupBuyOrderDetailEntities) {
                    GoodsMarketResponseDTO.Team team = GoodsMarketResponseDTO.Team.builder()
                            .userId(userGroupBuyOrderDetailEntity.getUserId())
                            .teamId(userGroupBuyOrderDetailEntity.getTeamId())
                            .activityId(userGroupBuyOrderDetailEntity.getActivityId())
                            .targetCount(userGroupBuyOrderDetailEntity.getTargetCount())
                            .completeCount(userGroupBuyOrderDetailEntity.getCompleteCount())
                            .lockCount(userGroupBuyOrderDetailEntity.getLockCount())
                            .validStartTime(userGroupBuyOrderDetailEntity.getValidStartTime())
                            .validEndTime(userGroupBuyOrderDetailEntity.getValidEndTime())
                            .validTimeCountdown(GoodsMarketResponseDTO.Team.differenceDateTime2Str(new Date(), userGroupBuyOrderDetailEntity.getValidEndTime()))
                            .outTradeNo(userGroupBuyOrderDetailEntity.getOutTradeNo())
                            .build();
                    teams.add(team);
                }
            }

            GoodsMarketResponseDTO.TeamStatistic teamStatistic = GoodsMarketResponseDTO.TeamStatistic.builder()
                    .allTeamCount(teamStatisticVO.getAllTeamCount())
                    .allTeamCompleteCount(teamStatisticVO.getAllTeamCompleteCount())
                    .allTeamUserCount(teamStatisticVO.getAllTeamUserCount())
                    .build();

            Response<GoodsMarketResponseDTO> response = Response.<GoodsMarketResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(GoodsMarketResponseDTO.builder()
                            .activityId(activityId)
                            .goods(goods)
                            .teamList(teams)
                            .teamStatistic(teamStatistic)
                            .build())
                    .build();

            log.info("查询拼团营销配置完成:{} goodsId:{} response:{}", requestDTO.getUserId(), requestDTO.getGoodsId(), JSON.toJSONString(response));

            return response;
        } catch (Exception e) {
            log.error("查询拼团营销配置失败:{} goodsId:{}", requestDTO.getUserId(), requestDTO.getGoodsId(), e);
            return Response.<GoodsMarketResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    public Response<GoodsMarketResponseDTO> queryGroupBuyMarketConfigFallBack(@RequestBody GoodsMarketRequestDTO requestDTO) {
        log.error("查询拼团营销配置限流:{}", requestDTO.getUserId());
        return Response.<GoodsMarketResponseDTO>builder()
                .code(ResponseCode.RATE_LIMITER.getCode())
                .info(ResponseCode.RATE_LIMITER.getInfo())
                .build();
    }

    /**
     * 批量查询拼团营销配置（用于 Index 列表聚合展示）
     */
    @RequestMapping(value = "query_group_buy_market_config_batch", method = RequestMethod.POST)
    @Override
    public Response<GoodsMarketBatchResponseDTO> queryGroupBuyMarketConfigBatch(@RequestBody GoodsMarketBatchRequestDTO requestDTO) {
        try {
            if (requestDTO == null
                    || StringUtils.isBlank(requestDTO.getUserId())
                    || StringUtils.isBlank(requestDTO.getSource())
                    || StringUtils.isBlank(requestDTO.getChannel())
                    || requestDTO.getGoodsIdList() == null
                    || requestDTO.getGoodsIdList().isEmpty()) {
                return Response.<GoodsMarketBatchResponseDTO>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

            // 简单防护：避免一次拉取过多导致服务压力过大
            if (requestDTO.getGoodsIdList().size() > 50) {
                return Response.<GoodsMarketBatchResponseDTO>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info("goodsIdList size must <= 50")
                        .build();
            }

            boolean includeTeamList = Boolean.TRUE.equals(requestDTO.getIncludeTeamList());
            int ownerCount = requestDTO.getOwnerCount() == null ? 1 : requestDTO.getOwnerCount();
            int randomCount = requestDTO.getRandomCount() == null ? 2 : requestDTO.getRandomCount();

            // 1. 逐个商品做营销试算（失败不影响整体）
            Map<String, TrialBalanceEntity> trialBalanceMap = new LinkedHashMap<>();
            List<String> invalidGoodsIdList = new ArrayList<>();

            for (String goodsId : requestDTO.getGoodsIdList()) {
                if (StringUtils.isBlank(goodsId)) continue;
                try {
                    TrialBalanceEntity trialBalanceEntity = indexGroupBuyMarketService.indexMarketTrial(MarketProductEntity.builder()
                            .userId(requestDTO.getUserId())
                            .source(requestDTO.getSource())
                            .channel(requestDTO.getChannel())
                            .goodsId(goodsId)
                            .build());
                    if (trialBalanceEntity == null || trialBalanceEntity.getGroupBuyActivityDiscountVO() == null) {
                        invalidGoodsIdList.add(goodsId);
                        continue;
                    }
                    trialBalanceMap.put(goodsId, trialBalanceEntity);
                } catch (Exception e) {
                    log.warn("批量查询拼团营销配置-试算失败 userId:{} goodsId:{} msg:{}",
                            requestDTO.getUserId(), goodsId, e.getMessage());
                    invalidGoodsIdList.add(goodsId);
                }
            }

            // 2. 按活动ID聚合查询 teamStatistic（以及可选 teamList）
            Set<Long> activityIdSet = new HashSet<>();
            for (TrialBalanceEntity trialBalanceEntity : trialBalanceMap.values()) {
                GroupBuyActivityDiscountVO vo = trialBalanceEntity.getGroupBuyActivityDiscountVO();
                if (vo != null && vo.getActivityId() != null) {
                    activityIdSet.add(vo.getActivityId());
                }
            }

            Map<Long, TeamStatisticVO> teamStatisticMap = new LinkedHashMap<>();
            Map<Long, List<UserGroupBuyOrderDetailEntity>> teamListMap = new LinkedHashMap<>();

            for (Long activityId : activityIdSet) {
                try {
                    TeamStatisticVO teamStatisticVO = indexGroupBuyMarketService.queryTeamStatisticByActivityId(activityId);
                    teamStatisticMap.put(activityId, teamStatisticVO);
                } catch (Exception e) {
                    log.warn("批量查询拼团营销配置-查询拼团统计失败 activityId:{} msg:{}", activityId, e.getMessage());
                    teamStatisticMap.put(activityId, new TeamStatisticVO(0, 0, 0));
                }

                if (includeTeamList) {
                    try {
                        List<UserGroupBuyOrderDetailEntity> teamList =
                                indexGroupBuyMarketService.queryInProgressUserGroupBuyOrderDetailList(activityId, requestDTO.getUserId(), ownerCount, randomCount);
                        teamListMap.put(activityId, teamList);
                    } catch (Exception e) {
                        log.warn("批量查询拼团营销配置-查询拼团组队失败 activityId:{} msg:{}", activityId, e.getMessage());
                        teamListMap.put(activityId, new ArrayList<>());
                    }
                }
            }

            // 3. 组装返回 DTO
            List<GoodsMarketResponseDTO> goodsMarketList = new ArrayList<>(trialBalanceMap.size());
            Date now = new Date();

            for (Map.Entry<String, TrialBalanceEntity> entry : trialBalanceMap.entrySet()) {
                TrialBalanceEntity trialBalanceEntity = entry.getValue();
                GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = trialBalanceEntity.getGroupBuyActivityDiscountVO();
                Long activityId = groupBuyActivityDiscountVO.getActivityId();

                GoodsMarketResponseDTO.Goods goods = GoodsMarketResponseDTO.Goods.builder()
                        .goodsId(trialBalanceEntity.getGoodsId())
                        .originalPrice(trialBalanceEntity.getOriginalPrice())
                        .deductionPrice(trialBalanceEntity.getDeductionPrice())
                        .payPrice(trialBalanceEntity.getPayPrice())
                        .build();

                List<GoodsMarketResponseDTO.Team> teams = new ArrayList<>();
                if (includeTeamList) {
                    List<UserGroupBuyOrderDetailEntity> userGroupBuyOrderDetailEntities = teamListMap.get(activityId);
                    if (userGroupBuyOrderDetailEntities != null && !userGroupBuyOrderDetailEntities.isEmpty()) {
                        for (UserGroupBuyOrderDetailEntity userGroupBuyOrderDetailEntity : userGroupBuyOrderDetailEntities) {
                            GoodsMarketResponseDTO.Team team = GoodsMarketResponseDTO.Team.builder()
                                    .userId(userGroupBuyOrderDetailEntity.getUserId())
                                    .teamId(userGroupBuyOrderDetailEntity.getTeamId())
                                    .activityId(userGroupBuyOrderDetailEntity.getActivityId())
                                    .targetCount(userGroupBuyOrderDetailEntity.getTargetCount())
                                    .completeCount(userGroupBuyOrderDetailEntity.getCompleteCount())
                                    .lockCount(userGroupBuyOrderDetailEntity.getLockCount())
                                    .validStartTime(userGroupBuyOrderDetailEntity.getValidStartTime())
                                    .validEndTime(userGroupBuyOrderDetailEntity.getValidEndTime())
                                    .validTimeCountdown(GoodsMarketResponseDTO.Team.differenceDateTime2Str(now, userGroupBuyOrderDetailEntity.getValidEndTime()))
                                    .outTradeNo(userGroupBuyOrderDetailEntity.getOutTradeNo())
                                    .build();
                            teams.add(team);
                        }
                    }
                }

                TeamStatisticVO teamStatisticVO = teamStatisticMap.get(activityId);
                GoodsMarketResponseDTO.TeamStatistic teamStatistic = GoodsMarketResponseDTO.TeamStatistic.builder()
                        .allTeamCount(teamStatisticVO == null ? 0 : teamStatisticVO.getAllTeamCount())
                        .allTeamCompleteCount(teamStatisticVO == null ? 0 : teamStatisticVO.getAllTeamCompleteCount())
                        .allTeamUserCount(teamStatisticVO == null ? 0 : teamStatisticVO.getAllTeamUserCount())
                        .build();

                goodsMarketList.add(GoodsMarketResponseDTO.builder()
                        .activityId(activityId)
                        .goods(goods)
                        .teamList(teams)
                        .teamStatistic(teamStatistic)
                        .build());
            }

            return Response.<GoodsMarketBatchResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(GoodsMarketBatchResponseDTO.builder()
                            .goodsMarketList(goodsMarketList)
                            .invalidGoodsIdList(invalidGoodsIdList)
                            .build())
                    .build();
        } catch (Exception e) {
            log.error("批量查询拼团营销配置失败 userId:{}", requestDTO == null ? null : requestDTO.getUserId(), e);
            return Response.<GoodsMarketBatchResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * 用户侧 Index 聚合分页查询：SKU 分页 + 营销试算 +（可选）库存/团队统计
     */
    @RequestMapping(value = "query_market_goods_page", method = RequestMethod.POST)
    @Override
    public Response<PageInfo<MarketGoodsCardDTO>> queryMarketGoodsPage(@RequestBody MarketGoodsPageRequestDTO requestDTO) {
        try {
            if (requestDTO == null
                    || StringUtils.isBlank(requestDTO.getUserId())
                    || StringUtils.isBlank(requestDTO.getSource())
                    || StringUtils.isBlank(requestDTO.getChannel())) {
                return Response.<PageInfo<MarketGoodsCardDTO>>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

            boolean onlyEffective = requestDTO.getOnlyEffective() == null || Boolean.TRUE.equals(requestDTO.getOnlyEffective());
            boolean includeInventory = requestDTO.getIncludeInventory() == null || Boolean.TRUE.equals(requestDTO.getIncludeInventory());
            boolean includeTeamStatistic = requestDTO.getIncludeTeamStatistic() == null || Boolean.TRUE.equals(requestDTO.getIncludeTeamStatistic());
            boolean includeTeamList = Boolean.TRUE.equals(requestDTO.getIncludeTeamList());

            int ownerCount = requestDTO.getOwnerCount() == null ? 1 : requestDTO.getOwnerCount();
            int randomCount = requestDTO.getRandomCount() == null ? 2 : requestDTO.getRandomCount();

            // 1) 分页查询 SKU（按 source/channel 过滤）
            SkuInventoryEntity skuInventoryEntity = new SkuInventoryEntity();
            skuInventoryEntity.setPage(requestDTO.getPage());
            skuInventoryEntity.setPageSize(requestDTO.getPageSize());
            skuInventoryEntity.setKeyword(requestDTO.getKeyword());
            skuInventoryEntity.setSource(requestDTO.getSource());
            skuInventoryEntity.setChannel(requestDTO.getChannel());

            PageInfo<SkuInfoVO> skuInfoVOPage = merchantSkuService.querySkuPage(skuInventoryEntity);

            // 2) 逐个 goodsId 做营销试算（无营销配置/异常则跳过）
            List<MarketGoodsCardDTO> cardList = new ArrayList<>();
            Map<Long, List<MarketGoodsCardDTO>> cardByActivityId = new LinkedHashMap<>();

            if (skuInfoVOPage.getList() != null && !skuInfoVOPage.getList().isEmpty()) {
                for (SkuInfoVO skuInfoVO : skuInfoVOPage.getList()) {
                    if (skuInfoVO == null || skuInfoVO.getGoods() == null) continue;
                    String goodsId = skuInfoVO.getGoods().getGoodsId();
                    if (StringUtils.isBlank(goodsId)) continue;

                    TrialBalanceEntity trialBalanceEntity;
                    try {
                        trialBalanceEntity = indexGroupBuyMarketService.indexMarketTrial(MarketProductEntity.builder()
                                .userId(requestDTO.getUserId())
                                .source(requestDTO.getSource())
                                .channel(requestDTO.getChannel())
                                .goodsId(goodsId)
                                .build());
                    } catch (Exception e) {
                        // 无营销配置/降级/切量/异常：列表聚合直接跳过（避免影响整体）
                        continue;
                    }

                    if (trialBalanceEntity == null || trialBalanceEntity.getGroupBuyActivityDiscountVO() == null) {
                        continue;
                    }

                    boolean visible = Boolean.TRUE.equals(trialBalanceEntity.getIsVisible());
                    boolean enable = Boolean.TRUE.equals(trialBalanceEntity.getIsEnable());
                    boolean effective = visible && enable;
                    if (onlyEffective && !effective) {
                        continue;
                    }

                    Long activityId = trialBalanceEntity.getGroupBuyActivityDiscountVO().getActivityId();

                    MarketGoodsCardDTO.Inventory inventory = null;
                    if (includeInventory && skuInfoVO.getInventory() != null) {
                        inventory = MarketGoodsCardDTO.Inventory.builder()
                                .totalStock(skuInfoVO.getInventory().getTotalStock())
                                .reservedStock(skuInfoVO.getInventory().getReservedStock())
                                .soldStock(skuInfoVO.getInventory().getSoldStock())
                                .availableStock(skuInfoVO.getInventory().getAvailableStock())
                                .build();
                    }

                    MarketGoodsCardDTO card = MarketGoodsCardDTO.builder()
                            .activityId(activityId)
                            .goodsId(trialBalanceEntity.getGoodsId())
                            .goodsName(trialBalanceEntity.getGoodsName())
                            .originalPrice(trialBalanceEntity.getOriginalPrice())
                            .deductionPrice(trialBalanceEntity.getDeductionPrice())
                            .payPrice(trialBalanceEntity.getPayPrice())
                            .visible(visible)
                            .enable(enable)
                            .effective(effective)
                            .inventory(inventory)
                            .build();

                    cardList.add(card);

                    if (activityId != null && (includeTeamStatistic || includeTeamList)) {
                        cardByActivityId.computeIfAbsent(activityId, k -> new ArrayList<>()).add(card);
                    }
                }
            }

            // 3) 聚合补齐团队统计/组队列表
            Date now = new Date();
            for (Map.Entry<Long, List<MarketGoodsCardDTO>> entry : cardByActivityId.entrySet()) {
                Long activityId = entry.getKey();
                List<MarketGoodsCardDTO> cards = entry.getValue();

                GoodsMarketResponseDTO.TeamStatistic teamStatisticDTO = null;
                if (includeTeamStatistic) {
                    TeamStatisticVO teamStatisticVO = indexGroupBuyMarketService.queryTeamStatisticByActivityId(activityId);
                    teamStatisticDTO = GoodsMarketResponseDTO.TeamStatistic.builder()
                            .allTeamCount(teamStatisticVO == null ? 0 : teamStatisticVO.getAllTeamCount())
                            .allTeamCompleteCount(teamStatisticVO == null ? 0 : teamStatisticVO.getAllTeamCompleteCount())
                            .allTeamUserCount(teamStatisticVO == null ? 0 : teamStatisticVO.getAllTeamUserCount())
                            .build();
                }

                List<GoodsMarketResponseDTO.Team> teamListDTO = null;
                if (includeTeamList) {
                    List<UserGroupBuyOrderDetailEntity> userGroupBuyOrderDetailEntities =
                            indexGroupBuyMarketService.queryInProgressUserGroupBuyOrderDetailList(activityId, requestDTO.getUserId(), ownerCount, randomCount);
                    teamListDTO = new ArrayList<>();
                    if (userGroupBuyOrderDetailEntities != null && !userGroupBuyOrderDetailEntities.isEmpty()) {
                        for (UserGroupBuyOrderDetailEntity userGroupBuyOrderDetailEntity : userGroupBuyOrderDetailEntities) {
                            teamListDTO.add(GoodsMarketResponseDTO.Team.builder()
                                    .userId(userGroupBuyOrderDetailEntity.getUserId())
                                    .teamId(userGroupBuyOrderDetailEntity.getTeamId())
                                    .activityId(userGroupBuyOrderDetailEntity.getActivityId())
                                    .targetCount(userGroupBuyOrderDetailEntity.getTargetCount())
                                    .completeCount(userGroupBuyOrderDetailEntity.getCompleteCount())
                                    .lockCount(userGroupBuyOrderDetailEntity.getLockCount())
                                    .validStartTime(userGroupBuyOrderDetailEntity.getValidStartTime())
                                    .validEndTime(userGroupBuyOrderDetailEntity.getValidEndTime())
                                    .validTimeCountdown(GoodsMarketResponseDTO.Team.differenceDateTime2Str(now, userGroupBuyOrderDetailEntity.getValidEndTime()))
                                    .outTradeNo(userGroupBuyOrderDetailEntity.getOutTradeNo())
                                    .build());
                        }
                    }
                }

                for (MarketGoodsCardDTO card : cards) {
                    if (includeTeamStatistic) {
                        card.setTeamStatistic(teamStatisticDTO);
                    }
                    if (includeTeamList) {
                        card.setTeamList(teamListDTO);
                    }
                }
            }

            PageInfo<MarketGoodsCardDTO> dtoPage = new PageInfo<>();
            BeanUtils.copyProperties(skuInfoVOPage, dtoPage, "list");
            dtoPage.setList(cardList);

            return Response.<PageInfo<MarketGoodsCardDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(dtoPage)
                    .build();
        } catch (Exception e) {
            log.error("用户侧Index聚合分页查询失败 userId:{}", requestDTO == null ? null : requestDTO.getUserId(), e);
            return Response.<PageInfo<MarketGoodsCardDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

}
