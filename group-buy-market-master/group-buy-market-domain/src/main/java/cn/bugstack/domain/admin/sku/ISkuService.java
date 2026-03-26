package cn.bugstack.domain.admin.sku;

import cn.bugstack.domain.admin.sku.model.entity.SkuInventoryEntity;
import cn.bugstack.domain.admin.sku.model.vo.SkuInfoVO;
import com.github.pagehelper.PageInfo;

import java.math.BigDecimal;

public interface ISkuService {

    String createSku(String source, String channel, String name, BigDecimal originalPrice);

    void createInventory(String goodsId, Long totalStock);

    String createSkuAndInventory(String source, String channel, String name, BigDecimal originalPrice, Long totalStock);

    /**
     * 分页查询商品信息（含库存与活动）
     */
    PageInfo<SkuInfoVO> querySkuPage(SkuInventoryEntity skuInventoryEntity);

    /**
     * 根据商品ID删除商品及其库存等关联信息
     */
    void deleteSku(String goodsId);

    /**
     * 更新商品与库存信息（同一事务内）
     */
    void updateSkuAndInventory(String goodsId, String name, BigDecimal originalPrice, Long totalStock);
}
