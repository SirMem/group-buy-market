package cn.bugstack.api.dto;

import lombok.Data;

@Data
public class SkuInventoryPageReq {

    private int page = 1;
    private int pageSize = 10;

    private String goodsId;
    private String keyword;
}
