package cn.bugstack.infrastructure.adapter.repository;

import cn.bugstack.domain.admin.discount.model.entity.GroupBuyDiscountEntity;
import cn.bugstack.domain.admin.discount.repository.IDiscountRepository;
import cn.bugstack.infrastructure.persistent.dao.IGroupBuyDiscountDao;
import cn.bugstack.infrastructure.persistent.po.GroupBuyDiscount;
import cn.bugstack.infrastructure.utils.redis.IRedisService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DiscountRepository implements IDiscountRepository {

    @Resource
    private IGroupBuyDiscountDao groupBuyDiscountDao;
    @Resource
    private IRedisService redisService;

    @Override
    public boolean insertDiscount(GroupBuyDiscountEntity discountEntity) {
        GroupBuyDiscount discount = GroupBuyDiscount.builder()
                .discountId(discountEntity.getDiscountId())
                .discountName(discountEntity.getDiscountName())
                .discountDesc(discountEntity.getDiscountDesc())
                .discountType(discountEntity.getDiscountType())
                .marketPlan(discountEntity.getMarketPlan())
                .marketExpr(discountEntity.getMarketExpr())
                .tagId(discountEntity.getTagId())
                .createTime(discountEntity.getCreateTime())
                .updateTime(discountEntity.getUpdateTime())
                .build();
        return groupBuyDiscountDao.insert(discount) > 0;
    }

    @Override
    public boolean updateDiscount(GroupBuyDiscountEntity discountEntity) {
        int count = groupBuyDiscountDao.updateByDiscountId(
                discountEntity.getDiscountId(),
                discountEntity.getDiscountName(),
                discountEntity.getDiscountDesc(),
                discountEntity.getDiscountType(),
                discountEntity.getMarketPlan(),
                discountEntity.getMarketExpr(),
                discountEntity.getTagId()
        );
        if (count > 0) {
            redisService.remove(GroupBuyDiscount.cacheRedisKey(discountEntity.getDiscountId()));
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteDiscount(String discountId) {
        int count = groupBuyDiscountDao.deleteByDiscountId(discountId);
        if (count > 0) {
            redisService.remove(GroupBuyDiscount.cacheRedisKey(discountId));
            return true;
        }
        return false;
    }

    @Override
    public GroupBuyDiscountEntity queryDiscount(String discountId) {
        GroupBuyDiscount discount = groupBuyDiscountDao.queryGroupBuyDiscountByDiscountId(discountId);
        return toEntity(discount);
    }

    @Override
    public List<GroupBuyDiscountEntity> queryDiscountList() {
        List<GroupBuyDiscount> list = groupBuyDiscountDao.queryGroupBuyDiscountList();
        if (null == list || list.isEmpty()) return Collections.emptyList();
        return list.stream().map(this::toEntity).collect(Collectors.toList());
    }

    private GroupBuyDiscountEntity toEntity(GroupBuyDiscount discount) {
        if (null == discount) return null;
        return GroupBuyDiscountEntity.builder()
                .id(discount.getId())
                .discountId(discount.getDiscountId())
                .discountName(discount.getDiscountName())
                .discountDesc(discount.getDiscountDesc())
                .discountType(discount.getDiscountType())
                .marketPlan(discount.getMarketPlan())
                .marketExpr(discount.getMarketExpr())
                .tagId(discount.getTagId())
                .createTime(discount.getCreateTime())
                .updateTime(discount.getUpdateTime())
                .build();
    }
}
