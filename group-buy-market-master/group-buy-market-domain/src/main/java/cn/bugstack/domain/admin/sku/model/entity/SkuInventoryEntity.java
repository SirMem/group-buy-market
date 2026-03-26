package cn.bugstack.domain.admin.sku.model.entity;

import lombok.Data;

@Data
public class SkuInventoryEntity {

    private int page = 1;
    private int pageSize = 10;

    private String source;
    private String channel;

    private String goodsId;
    private String keyword;
}
