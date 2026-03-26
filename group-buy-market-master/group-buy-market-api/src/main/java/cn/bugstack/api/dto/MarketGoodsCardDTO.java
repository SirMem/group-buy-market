package cn.bugstack.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author needyou
 * @description Index 商品卡片信息（营销聚合结果）
 * @create 2026-01-11
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketGoodsCardDTO {

    private Long activityId;

    private String goodsId;
    private String goodsName;

    private BigDecimal originalPrice;
    private BigDecimal deductionPrice;
    private BigDecimal payPrice;

    /** 是否可见（人群/策略过滤后） */
    private Boolean visible;
    /** 是否可用（人群/策略过滤后） */
    private Boolean enable;
    /** 便于前端判断；visible && enable */
    private Boolean effective;

    private Inventory inventory;

    private GoodsMarketResponseDTO.TeamStatistic teamStatistic;

    /** 可选：组队明细列表（列表页一般不返回） */
    private List<GoodsMarketResponseDTO.Team> teamList;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Inventory {
        private Long totalStock;
        private Long reservedStock;
        private Long soldStock;
        private Long availableStock;
    }
}

