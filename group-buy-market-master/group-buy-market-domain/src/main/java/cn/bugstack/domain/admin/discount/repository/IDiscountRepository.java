package cn.bugstack.domain.admin.discount.repository;

import cn.bugstack.domain.admin.discount.model.entity.GroupBuyDiscountEntity;

import java.util.List;

public interface IDiscountRepository {

    boolean insertDiscount(GroupBuyDiscountEntity discountEntity);

    boolean updateDiscount(GroupBuyDiscountEntity discountEntity);

    boolean deleteDiscount(String discountId);

    GroupBuyDiscountEntity queryDiscount(String discountId);

    List<GroupBuyDiscountEntity> queryDiscountList();

}
