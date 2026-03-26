package cn.bugstack.api.dto.activity;

import lombok.Data;

import java.util.Date;

/**
 * @author needyou
 * @description 活动创建请求
 * @create 2026-01-13
 */
@Data
public class ActivityCreateRequestDTO {

    /** 活动ID（可选，不传则后端生成） */
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
}

