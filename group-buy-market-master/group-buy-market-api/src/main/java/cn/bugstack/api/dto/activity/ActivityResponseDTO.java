package cn.bugstack.api.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author needyou
 * @description 活动返回对象
 * @create 2026-01-13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityResponseDTO {

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

