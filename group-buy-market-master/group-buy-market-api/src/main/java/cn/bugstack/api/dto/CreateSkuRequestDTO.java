package cn.bugstack.api.dto;

import lombok.Data;

import java.util.List;

/**
 * @author needyou
 * @description 商家创建商品请求对象
 * @create 2025-12-02 10:00
 */
@Data
public class CreateSkuRequestDTO {

    /** 渠道 */
    private String source;
    /** 来源 */
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

    /** 总库存 */
    private Long totalStock;

    /** 商品状态；ONLINE / OFFLINE */
    private String status;

}
