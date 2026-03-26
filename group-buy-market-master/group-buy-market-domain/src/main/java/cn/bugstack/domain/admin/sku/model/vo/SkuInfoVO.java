package cn.bugstack.domain.admin.sku.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkuInfoVO {

    /** 商品信息 */
    private Goods goods;

    /** 库存信息 */
    private Inventory inventory;

    /** 绑定活动列表 */
    private List<ActivityBinding> activityBindings;

    /**
     * 商品信息
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Goods {
        /** 商品ID */
        private String goodsId;
        /** 来源 */
        private String source;
        /** 渠道 */
        private String channel;

        /** 商品名称 */
        private String name;
        /** 商品类目 */
        private Long categoryId;
        /** 商品副标题 */
        private String subtitle;
        /** 商品描述/详情（富文本ID或HTML） */
        private String description;

        /** 商品封面图 */
        private String coverImage;
        /** 商品图片列表 */
        private List<String> imageList;

        /** 商品价格，单位：分，对应 sku.original_price */
        private Long price;

        /** 商品状态；ONLINE / OFFLINE */
        private String status;

        /** 创建时间 */
        private Date createTime;
        /** 更新时间 */
        private Date updateTime;
    }

    /**
     * 库存信息
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Inventory {
        /** 总库存，对应 activity_inventory.total_stock（全局库存时可不带 activityId） */
        private Long totalStock;
        /** 已锁单库存 */
        private Long reservedStock;
        /** 已售库存 */
        private Long soldStock;
        /** 可用库存；total - reserved - sold（后端算好给前端） */
        private Long availableStock;
    }

    /**
     * 活动绑定信息
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ActivityBinding {
        /** 活动ID */
        private Long activityId;
        /** 活动名称 */
        private String activityName;
        /** 活动状态；例如：ONLINE / OFFLINE */
        private String status;
        /** 拼团类型；例如：GROUP、AUTO 等 */
        private String groupType;
        /** 活动开始时间 */
        private Date startTime;
        /** 活动结束时间 */
        private Date endTime;
    }
}
