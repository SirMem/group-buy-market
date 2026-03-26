package cn.bugstack.domain.admin.sku.adapter.repository;

import cn.bugstack.domain.admin.sku.model.entity.SkuInventoryEntity;
import cn.bugstack.domain.admin.sku.model.vo.SkuInfoVO;
import com.github.pagehelper.PageInfo;

import java.math.BigDecimal;

/**
 * 商品仓储适配接口
 */
public interface ISkuRepository {

    /**
     * 创建商品并生成 goodsId
     */
    String insertSku(String source, String channel, String name, BigDecimal originalPrice);

    /**
     * 创建全局库存记录（无活动维度）
     */
    void insertInventoryWithoutActivityId(String goodsId, Long totalStock);

    /**
     * 在一个事务中创建商品和库存
     */
    String createSkuAndInventory(String source, String channel, String name, BigDecimal originalPrice, Long totalStock);

    /**
     * 分页查询商品信息（含库存、活动绑定），返回领域层 VO
     */
    PageInfo<SkuInfoVO> querySkuInfoPage(SkuInventoryEntity skuInventoryEntity);

    /**
     * 根据商品ID删除商品及其库存等关联数据
     */
    void deleteSkuAndInventory(String goodsId);

    /**
     * 更新商品与库存信息（同一事务内）
     */
    void updateSkuAndInventory(String goodsId, String name, BigDecimal originalPrice, Long totalStock);
}
