package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.Sku;
import cn.bugstack.infrastructure.persistent.po.join.SkuWithInventoryPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author needyou
 * @description 商品查询
 * @create 2024-12-21 10:48
 */
@Mapper
public interface ISkuDao {

    Sku querySkuByGoodsId(String goodsId);

    void insert(Sku sku);

    /**
     * 查询商品及其全局库存信息（activity_id = 0），支持简单条件过滤
     */
    List<SkuWithInventoryPO> querySkuWithInventory(@Param("source") String source,
                                                   @Param("channel") String channel,
                                                   @Param("goodsId") String goodsId,
                                                   @Param("keyword") String keyword);

    /**
     * 根据商品ID删除商品记录
     */
    int deleteByGoodsId(String goodsId);

    /**
     * 根据商品ID更新商品信息（仅更新当前表字段）
     */
    int updateByGoodsId(@Param("goodsId") String goodsId,
                        @Param("goodsName") String goodsName,
                        @Param("originalPrice") BigDecimal originalPrice);
}
