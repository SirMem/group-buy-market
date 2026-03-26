# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Layout

This repo contains two independently deployable projects:

- `group-buy-market-master/` — Main backend: Spring Boot DDD group-buy marketing service
- `frontend-merchant/` — Merchant admin frontend: Vue 3 + TypeScript
- `s-pay-mall-ddd-market-master/` — Related payment mall service (has its own CLAUDE.md)

---

## Backend (`group-buy-market-master/`)

### Build & Run

```bash
# Build all modules (skip tests)
mvn clean package -DskipTests

# Run the application
mvn spring-boot:run -pl group-buy-market-app

# Run all tests
mvn test -pl group-buy-market-app

# Run a specific test class
mvn test -pl group-buy-market-app -Dtest=IIndexGroupBuyMarketServiceTest

# Run a specific test method
mvn test -pl group-buy-market-app -Dtest=IIndexGroupBuyMarketServiceTest#test_indexMarketTrial
```

App runs on **port 8091**. Active Spring profile is `dev` (set in `group-buy-market-app/src/main/resources/application.yml`); environment-specific config lives in `application-dev.yml` / `application-prod.yml`.

### Module Structure

| Module | Role |
|---|---|
| `group-buy-market-api` | Shared DTOs and API interface definitions |
| `group-buy-market-types` | Common enums, exceptions, base classes, design-pattern framework (`wrench-design`) |
| `group-buy-market-domain` | All business logic, split into sub-domains |
| `group-buy-market-infrastructure` | Persistence (MyBatis + MySQL), Redis (Redisson), RabbitMQ, JWT, external gateways |
| `group-buy-market-trigger` | Inbound adapters: HTTP controllers, MQ listeners, scheduled jobs |
| `group-buy-market-app` | Spring Boot entry point, wiring config, and all integration tests |

### DDD Architecture

The project follows strict DDD layering:

```
trigger → domain ← infrastructure
               ↑
              api / types
```

- `domain` defines repository/port **interfaces** (`IActivityRepository`, `ITradeRepository`, etc.); `infrastructure` implements them.
- `trigger` injects domain services and calls them; it never touches infrastructure directly.
- Cross-cutting concerns (events, Redis, JWT) are accessed by domain via injected interfaces from `infrastructure`.

### Domain Breakdown

**`activity` domain** — core group-buy trial logic:
- Entry point: `IIndexGroupBuyMarketService` (`indexMarketTrial`, `queryInProgressUserGroupBuyOrderDetailList`)
- `AbstractGroupBuyMarketSupport` extends `AbstractMultiThreadStrategyRouter` (from `wrench-design` types module), implementing a **multi-threaded tree strategy** pipeline that runs filter nodes (e.g., activity eligibility, crowd tag, discount node) in parallel via `CompletableFuture`.
- `DefaultActivityStrategyFactory.DynamicContext` carries per-request mutable state through the pipeline.
- `MarketProductEntity` is the input; `TrialBalanceEntity` is the output.

**`trade` domain** — order lifecycle:
- `TradeLockOrderService` — locks stock and creates group-buy order on purchase
- `TradeSettlementOrderService` — settles order after payment confirmation (uses `SettableRuleFilter` chain)
- `TradeRefundOrderService` — handles refunds
- `TradeReverseStockService` — reverses stock on order cancellation
- `TradeTaskService` — scheduled job for compensating unfinished MQ notifications

**`tag` domain** — crowd segmentation: batch jobs write user sets used by activity eligibility filters.

**`discount` domain** — discount calculation utilities.

### Infrastructure Key Points

- **Redis**: Redisson (`IRedisService` / `RedissonService`) used for distributed locks and caching. Cache keys centralized in `infrastructure/utils/redis/keys/`.
- **Events**: `EventPublisher` wraps Spring `ApplicationEventPublisher`; MQ listeners in `trigger/listener/` consume RabbitMQ messages for async settlement callbacks.
- **External notify**: `GroupBuyNotifyService` sends HTTP callbacks to merchants when group-buy teams complete.
- **DCC**: `DCCService` provides dynamic configuration (toggling features at runtime via Nacos or equivalent).
- **JWT**: `JwtService` in infrastructure implements `IJwtService` from domain.

---

## Frontend (`frontend-merchant/`)

### Build & Run

```bash
cd frontend-merchant
npm install
npm run dev      # development server
npm run build    # production build (vue-tsc + vite)
npm run preview  # preview production build
```

### Stack & Structure

- **Vue 3** + **TypeScript** + **Vite**
- **Element Plus** for UI components; **TailwindCSS v4** for utility styles
- **Pinia** for state; **Vue Router v5** for routing
- API calls via **Axios** (`src/request/request.ts`)
- Pages under `src/pages/merchant/`: `dashboard`, `activity`, `discount`, `goods`, `binding`
- Service modules under `src/services/`: `activity.ts`, `discount.ts`, `sku.ts`, `binding.ts` — each wraps Axios calls to the backend API
- Backend base URL configured in `frontend-merchant/.env`
