package cn.bugstack.api.dto.activity;

import lombok.Data;

import java.util.Date;

/**
 * @author needyou
 * @description 活动更新请求
 * @create 2026-01-13
 */
@Data
public class ActivityUpdateRequestDTO {

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

