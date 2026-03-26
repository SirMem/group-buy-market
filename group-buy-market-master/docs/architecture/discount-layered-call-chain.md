# Discount 模块分层调用说明

## 调用链

```text
DiscountController
    -> IDiscountService
        -> DiscountService
            -> IDiscountRepository
                -> DiscountRepository
                    -> IGroupBuyDiscountDao / Redis
```

## 本次重构点

1. `DiscountController` 改为纯路由与转发，不再直接访问 DAO/Redis。
2. 在 `domain` 层完善 `IDiscountService` / `DiscountService` 业务逻辑。
3. 在 `domain` 层完善 `IDiscountRepository` 仓储抽象。
4. 在 `infrastructure` 层实现 `DiscountRepository`，统一处理 DB 与缓存失效。
5. 新增领域实体 `GroupBuyDiscountEntity` 作为服务层与仓储层传输对象。
