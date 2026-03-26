package cn.bugstack.api.dto;

import lombok.Data;

import java.util.List;

/**
 * @author needyou
 * @description 批量商品营销请求对象（用于 Index 列表聚合展示）
 * @create 2026-01-08
 */
@Data
public class GoodsMarketBatchRequestDTO {

    /** 用户ID */
    private String userId;
    /** 渠道 */
    private String source;
    /** 来源 */
    private String channel;

    /** 商品ID列表 */
    private List<String> goodsIdList;

    /** 是否返回组队明细列表（默认 false；Index 列表一般不需要） */
    private Boolean includeTeamList;

    /** 个人数量（includeTeamList=true 时生效；默认 1） */
    private Integer ownerCount;
    /** 随机数量（includeTeamList=true 时生效；默认 2） */
    private Integer randomCount;
}

