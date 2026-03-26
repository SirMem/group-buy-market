package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.ActivityInventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 */
@Mapper
public interface IActivityInventoryDao {

    Long queryTotalStockByGoodsId(String goodsId);

    int updateAddReserveStockByGoodsId(String goodsId);

    int updateSettleStockByGoodsId(String goodsId);

    int updateReleaseReserveByGoodsId(String goodsId);

    int updateRefundStockByGoodsId(String goodsId);

    int insert(ActivityInventory activityInventory);

    ActivityInventory queryByGoodsId(String goodsId);

    /**
     * 根据商品ID删除库存记录
     */
    int deleteByGoodsId(String goodsId);

    /**
     * 根据商品ID更新总库存
     */
    int updateTotalStockByGoodsId(@Param("goodsId") String goodsId, @Param("totalStock") Long totalStock);

}
