# Activity 模块分层调用说明

## 目标

修复 `ActivityController` 直接依赖 DAO/Repository 实现的跨层调用问题，改为依赖倒置的调用链。

## 分层职责

- `trigger`（接口层）：只处理 HTTP 入参/出参和路由，不包含持久化细节。
- `api`（契约层）：定义 DTO、通用响应对象等对外协议模型。
- `domain`（领域层）：定义仓储抽象 `IActivityRepository` 与领域实体，表达业务能力和边界。
- `infrastructure`（基础设施层）：实现 `IActivityRepository`，负责 DAO、缓存、数据库读写。

## 调用链

```text
ActivityController
    -> IActivityService (domain)
        -> IActivityServiceImpl
        -> IActivityRepository
            -> ActivityRepository
                -> MyBatis DAO / Redis
```

## 本次重构点

1. `ActivityController` 改为纯委托：不再直接引用 DAO、PO、Redis。
2. 在 `domain` 层新增 `IActivityService` 与 `IActivityServiceImpl`，承载活动管理业务逻辑与参数校验。
3. 扩展 `IActivityRepository` 的后台管理方法（活动 CRUD、商品活动绑定）。
4. 在 `ActivityRepository` 中实现新增仓储方法，并统一处理活动缓存失效。
5. 新增领域实体：
   - `GroupBuyActivityEntity`
   - `SCSkuActivityEntity`

## 收益

- 满足依赖倒置原则：上层依赖抽象，不依赖基础设施实现。
- Controller 更轻，便于测试与演进。
- 业务逻辑集中在服务层，仓储职责集中在基础设施层。
