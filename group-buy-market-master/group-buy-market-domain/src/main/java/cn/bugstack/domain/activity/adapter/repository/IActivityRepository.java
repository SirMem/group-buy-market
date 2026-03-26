package cn.bugstack.domain.activity.adapter.repository;

import cn.bugstack.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import cn.bugstack.domain.activity.model.entity.GroupBuyActivityEntity;
import cn.bugstack.domain.activity.model.entity.SCSkuActivityEntity;
import cn.bugstack.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import cn.bugstack.domain.activity.model.valobj.SCSkuActivityVO;
import cn.bugstack.domain.activity.model.valobj.SkuVO;
import cn.bugstack.domain.activity.model.valobj.TeamStatisticVO;

import java.util.List;

/**
 * @author needyou
 * @description 活动仓储
 * @create 2024-12-21 10:06
 */
public interface IActivityRepository {

    boolean insertGroupBuyActivity(GroupBuyActivityEntity activityEntity);

    boolean updateGroupBuyActivity(GroupBuyActivityEntity activityEntity);

    boolean deleteGroupBuyActivity(Long activityId);

    GroupBuyActivityEntity queryGroupBuyActivityByActivityId(Long activityId);

    List<GroupBuyActivityEntity> queryGroupBuyActivityList();

    boolean bindSkuActivity(String source, String channel, String goodsId, Long activityId);

    boolean unbindSkuActivity(String source, String channel, String goodsId);

    SCSkuActivityEntity querySCSkuActivity(String source, String channel, String goodsId);

    GroupBuyActivityDiscountVO queryGroupBuyActivityDiscountVO(Long activityId);

    SkuVO querySkuByGoodsId(String goodsId);

    SCSkuActivityVO querySCSkuActivityBySCGoodsId(String source, String channel, String goodsId);

    boolean isTagCrowdRange(String tagId, String userId);

    boolean downgradeSwitch();

    boolean cutRange(String userId);

    List<UserGroupBuyOrderDetailEntity> queryInProgressUserGroupBuyOrderDetailListByOwner(Long activityId, String userId, Integer ownerCount);

    List<UserGroupBuyOrderDetailEntity> queryInProgressUserGroupBuyOrderDetailListByRandom(Long activityId, String userId, Integer randomCount);

    TeamStatisticVO queryTeamStatisticByActivityId(Long activityId);

}
