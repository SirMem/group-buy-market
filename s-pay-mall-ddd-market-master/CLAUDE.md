# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build all modules
mvn clean package -DskipTests

# Run the application
mvn spring-boot:run -pl s-pay-mall-ddd-app

# Run all tests
mvn test -pl s-pay-mall-ddd-app

# Run a single test class
mvn test -pl s-pay-mall-ddd-app -Dtest=OrderServiceTest

# Run a single test method
mvn test -pl s-pay-mall-ddd-app -Dtest=OrderServiceTest#test_createOrder_NO_MARKET
```

Start local infrastructure (MySQL on 13306, Redis on 16379, RabbitMQ on 5672):
```bash
cd docs/dev-ops
docker-compose -f docker-compose-environment.yml up -d
```

## Architecture

DDD multi-module Maven project (`groupId: cn.bugstack`, Java 8, Spring Boot). Module dependency flow:

```
trigger → domain ← infrastructure
    ↓         ↑
   api      types
    ↑
   app (entry point)
```

| Module | Role |
|---|---|
| `s-pay-mall-ddd-types` | Shared constants, base event types, common utilities |
| `s-pay-mall-ddd-api` | Public API interfaces and DTOs for HTTP consumers |
| `s-pay-mall-ddd-domain` | Core business logic; defines adapter interfaces that infrastructure must implement |
| `s-pay-mall-ddd-infrastructure` | Implements domain adapter interfaces; owns DB (MyBatis), Redis, and outbound HTTP (Retrofit2) |
| `s-pay-mall-ddd-trigger` | HTTP controllers, RabbitMQ listeners, and scheduled jobs; calls domain services |
| `s-pay-mall-ddd-app` | Spring Boot entry point, configuration, and integration tests |

### Domain Bounded Contexts

Three bounded contexts under `s-pay-mall-ddd-domain/src/main/java/cn/bugstack/domain/`:

- **`order`** – core order lifecycle: creation, payment, closure, refund. Uses `IOrderService` / `AbstractOrderService` / `OrderService` template pattern.
- **`goods`** – post-payment delivery/settlement via `IGoodsService`. Called by `OrderPaySuccessListener` after payment success.
- **`auth`** – WeChat login via `ILoginService` / `WeixinLoginService`.

Each context defines its own adapter interfaces (under `adapter/repository/` and `adapter/port/`) that the infrastructure layer implements.

### Order Domain Service Pattern

- `IOrderService` – interface consumed by the trigger layer
- `AbstractOrderService` – implements the shared `createOrder` template: idempotency check → lock market discount → persist order (CREATE status) → call Alipay prepay (PAY_WAIT status)
- `OrderService` – Spring bean; implements the abstract methods for Alipay API calls, market lock, and repository persistence

Domain adapter interfaces in `domain/order/adapter/`:
- `IOrderRepository` – implemented by `infrastructure/.../OrderRepository`
- `IProductPort` – implemented by `infrastructure/.../ProductPort` (calls group-buy-market service via Retrofit2)

### Key Flows

**Order creation** (`AbstractOrderService.createOrder`):
1. Query for existing unpaid order; return early if `PAY_WAIT` (idempotency)
2. If order is in `CREATE` status (save succeeded but prepay failed), skip to prepay only
3. For new orders: call `lockMarketPayOrder` (if `MarketTypeVO.GROUP_BUY_MARKET`) → persist via `doSaveOrder` → call Alipay prepay via `doPrepayOrder`

**Payment callback** (`AliPayController`): Alipay async notify → verify signature → `changeOrderPaySuccess` → publish `PaySuccessMessageEvent` to RabbitMQ → `OrderPaySuccessListener` consumes → `IGoodsService.changeOrderDealDone` (delivery & settlement)

**Group-buy settlement** (`TeamSuccessTopicListener`): RabbitMQ topic for team completion → `orderService.changeOrderMarketSettlement`

**Refund** (`OrderService.refundPayOrder`): query order → call Alipay refund API → `repository.refundOrder` → `RefundSuccessTopicListener` handles downstream

**Scheduled jobs** (in `trigger/job/`):
- `TimeoutCloseOrderJob` – runs every 30 minutes; closes orders unpaid for >30 minutes
- `NoPayNotifyOrderJob` – re-notifies users with pending unpaid orders

### External Integration: Group-Buy Market Service

Configured in `application-dev.yml` under `app.config.group-buy-market` (default URL: `http://localhost:8091`). HTTP client is `IGroupBuyMarketService` (Retrofit2). Three operations:
1. `lockMarketPayOrder` – locks a group-buy discount during order creation
2. `settlementMarketPayOrder` – called after Alipay payment success
3. `refundMarketPayOrder` – called on order refund

### Profile Configuration

`application.yml` sets `spring.profiles.active: dev`. Environment-specific config in `s-pay-mall-ddd-app/src/main/resources/application-dev.yml` (and `application-prod.yml`). The dev profile contains real credentials for a remote MySQL and RabbitMQ instance.
