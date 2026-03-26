package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.GroupBuyDiscount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author needyou
 * @description 折扣配置Dao
 * @create 2024-12-07 10:10
 */
@Mapper
public interface IGroupBuyDiscountDao {

    List<GroupBuyDiscount> queryGroupBuyDiscountList();

    GroupBuyDiscount queryGroupBuyActivityDiscountByDiscountId(String discountId);

    GroupBuyDiscount queryGroupBuyDiscountByDiscountId(String discountId);

    int insert(GroupBuyDiscount groupBuyDiscount);

    int updateByDiscountId(@Param("discountId") String discountId,
                           @Param("discountName") String discountName,
                           @Param("discountDesc") String discountDesc,
                           @Param("discountType") Integer discountType,
                           @Param("marketPlan") String marketPlan,
                           @Param("marketExpr") String marketExpr,
                           @Param("tagId") String tagId);

    int deleteByDiscountId(String discountId);

}
