package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.GroupBuyActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author needyou
 * @description 拼团活动Dao
 * @create 2024-12-07 10:10
 */
@Mapper
public interface IGroupBuyActivityDao {

    List<GroupBuyActivity> queryGroupBuyActivityList();

    GroupBuyActivity queryValidGroupBuyActivity(GroupBuyActivity groupBuyActivityReq);

    GroupBuyActivity queryValidGroupBuyActivityId(Long activityId);

    GroupBuyActivity queryGroupBuyActivityByActivityId(Long activityId);

    int insert(GroupBuyActivity groupBuyActivity);

    int updateByActivityId(@Param("activityId") Long activityId,
                           @Param("activityName") String activityName,
                           @Param("discountId") String discountId,
                           @Param("groupType") Integer groupType,
                           @Param("takeLimitCount") Integer takeLimitCount,
                           @Param("target") Integer target,
                           @Param("validTime") Integer validTime,
                           @Param("status") Integer status,
                           @Param("startTime") java.util.Date startTime,
                           @Param("endTime") java.util.Date endTime,
                           @Param("tagId") String tagId,
                           @Param("tagScope") String tagScope);

    int deleteByActivityId(Long activityId);

}
