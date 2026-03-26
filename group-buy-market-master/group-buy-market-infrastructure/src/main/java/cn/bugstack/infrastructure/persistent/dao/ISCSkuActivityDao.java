package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.SCSkuActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author needyou
 * @description 渠道商品活动配置关联表Dao
 * @create 2025-01-01 09:30
 */
@Mapper
public interface ISCSkuActivityDao {

    /**
     * 多对一：在 source+channel+goodsId 维度下，只返回一个绑定活动
     */
    SCSkuActivity querySCSkuActivityBySCGoodsId(SCSkuActivity scSkuActivity);

    /**
     * 根据商品ID删除渠道商品活动关联记录
     */
    int deleteByGoodsId(String goodsId);

    int deleteBySCGoodsId(@Param("source") String source, @Param("channel") String channel, @Param("goodsId") String goodsId);

    /**
     * 绑定（upsert）：source+channel+goodsId 唯一
     */
    int upsert(@Param("source") String source,
               @Param("channel") String channel,
               @Param("goodsId") String goodsId,
               @Param("activityId") Long activityId);

}
