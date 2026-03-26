package cn.bugstack.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author needyou
 * @description 活动实体
 * @create 2026-03-12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyActivityEntity {

    private Long id;
    private Long activityId;
    private String activityName;
    private String discountId;
    private Integer groupType;
    private Integer takeLimitCount;
    private Integer target;
    private Integer validTime;
    private Integer status;
    private Date startTime;
    private Date endTime;
    private String tagId;
    private String tagScope;
    private Date createTime;
    private Date updateTime;

}
