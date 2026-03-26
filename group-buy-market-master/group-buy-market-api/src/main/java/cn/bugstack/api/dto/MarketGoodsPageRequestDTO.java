package cn.bugstack.api.dto;

import lombok.Data;

/**
 * @author needyou
 * @description 用户侧 Index 聚合分页查询请求
 * @create 2026-01-11
 */
@Data
public class MarketGoodsPageRequestDTO {

    /** 用户ID */
    private String userId;
    /** 渠道 */
    private String source;
    /** 来源 */
    private String channel;

    private int page = 1;
    private int pageSize = 10;

    /** 模糊搜索关键词（goodsName 等） */
    private String keyword;

    /** 仅返回有效商品（默认 true） */
    private Boolean onlyEffective = Boolean.TRUE;

    /** 是否返回库存信息（默认 true） */
    private Boolean includeInventory = Boolean.TRUE;

    /** 是否返回团队统计（默认 true） */
    private Boolean includeTeamStatistic = Boolean.TRUE;

    /** 是否返回组队明细列表（默认 false；列表页一般不需要） */
    private Boolean includeTeamList = Boolean.FALSE;

    /** 个人数量（includeTeamList=true 时生效；默认 1） */
    private Integer ownerCount = 1;
    /** 随机数量（includeTeamList=true 时生效；默认 2） */
    private Integer randomCount = 2;
}

