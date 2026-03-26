# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Layout

This repo contains multiple projects, but the main working set here is:

- `group-buy-market-master/` — Spring Boot backend for the group-buy market domain
- `frontend-merchant/` — Merchant admin frontend built with Vue 3 + TypeScript
- `s-pay-mall-ddd-market-master/` — Related payment mall service with its own `CLAUDE.md`; treat it as a separate codebase unless a task explicitly crosses repositories

## Existing CLAUDE.md Improvements

The previous version was already useful. The main improvements in this version are:

- clarifies that `s-pay-mall-ddd-market-master/` is a separate codebase with its own operating instructions
- adds concrete frontend commands from `package.json`
- notes that the backend app module has `maven-surefire-plugin` configured with `skipTests=true`, so running tests may require overriding that flag explicitly
- makes the frontend architecture more explicit: router + Pinia auth store + Axios request layer + page/service split
- tightens the backend architecture summary around the main domain entry points and the async settlement / notification flow

---

## Backend (`group-buy-market-master/`)

### Common Commands

Run backend commands from `group-buy-market-master/`.

```bash
# Build all backend modules without tests
mvn clean package -DskipTests

# Run the Spring Boot application
mvn spring-boot:run -pl group-buy-market-app

# Run the packaged app jar after build
java -jar group-buy-market-app/target/group-buy-market-app.jar

# Run all tests in the app module (override the module's default skipTests=true)
mvn test -pl group-buy-market-app -DskipTests=false

# Run one test class
mvn test -pl group-buy-market-app -DskipTests=false -Dtest=IIndexGroupBuyMarketServiceTest

# Run one test method
mvn test -pl group-buy-market-app -DskipTests=false -Dtest=IIndexGroupBuyMarketServiceTest#test_indexMarketTrial
```

### Runtime Configuration

- Spring profile defaults to `dev` in `group-buy-market-app/src/main/resources/application.yml`
- Dev app port is `8091` in `group-buy-market-app/src/main/resources/application-dev.yml`
- Environment-specific config lives in `application-dev.yml`, `application-test.yml`, and `application-prod.yml`
- The checked-in `application-dev.yml` contains real-looking service credentials and endpoints for MySQL, RabbitMQ, Redis, dynamic config, and logstash; avoid copying these values into generated docs or code comments unless the task requires it

### Module Structure

| Module | Role |
|---|---|
| `group-buy-market-api` | Shared DTOs and API contract definitions |
| `group-buy-market-types` | Shared enums, exceptions, constants, and the imported `wrench-design` support abstractions |
| `group-buy-market-domain` | Core business logic and domain service interfaces / implementations |
| `group-buy-market-infrastructure` | Repository implementations, DAO/MyBatis integration, Redis, MQ, JWT, HTTP gateways, dynamic config |
| `group-buy-market-trigger` | Inbound adapters such as controllers, MQ listeners, and scheduled jobs |
| `group-buy-market-app` | Spring Boot bootstrap module, resource config, and integration-style tests |

### Big-Picture Architecture

The backend is a DDD-style multi-module Spring Boot application. The dependency direction is:

```text
trigger -> domain <- infrastructure
              ^
           api / types
```

Important implications:

- `domain` owns business interfaces such as repositories and ports; `infrastructure` provides the concrete implementations
- `trigger` should call domain services rather than infrastructure classes directly
- `group-buy-market-app` wires the system together and is also where the test suite lives
- cross-cutting integrations such as Redis, JWT, HTTP callbacks, MQ, and DCC are exposed to the domain through interfaces rather than being referenced ad hoc from controllers

### Core Business Flows

#### Activity / market trial flow

This is the main “homepage pricing / eligibility trial” flow.

- Main service interface: `cn.bugstack.domain.activity.service.IIndexGroupBuyMarketService`
- Primary method: `indexMarketTrial(MarketProductEntity)` returns `TrialBalanceEntity`
- Supporting query methods also expose in-progress team/order information and team statistics for an activity
- `AbstractGroupBuyMarketSupport` extends `AbstractMultiThreadStrategyRouter` from the `wrench-design` framework; this is the key architectural pattern behind the activity trial pipeline
- The activity trial logic is not a simple sequential service: it is organized as a tree/strategy router with a mutable `DefaultActivityStrategyFactory.DynamicContext` passed through the pipeline
- This design is intended for combining eligibility checks, crowd-tag filtering, discount selection, and related decision nodes, potentially in parallel via `CompletableFuture`

When changing activity trial behavior, inspect the strategy/router factory and filter nodes rather than only the top-level service.

#### Trade / order settlement flow

The trade domain handles the order lifecycle after a user participates in a group buy.

Important services include:

- `TradeLockOrderService` — reserves stock and creates group-buy orders
- `TradeSettlementOrderService` — handles payment-success settlement
- `TradeRefundOrderService` — refund path
- `TradeReverseStockService` — stock rollback / cancellation path
- `TradeTaskService` — compensation / retry work for unfinished notifications

`TradeSettlementOrderService` is especially important for understanding async side effects:

- it first runs a settlement rule chain via `BusinessLinkedList<...>` from `TradeSettlementRuleFilterFactory`
- it builds a `GroupBuyTeamSettlementAggregate`
- it persists settlement through `ITradeRepository`
- it confirms reserved total stock
- if settlement produces a notify task, it dispatches notification execution asynchronously on the shared `ThreadPoolExecutor`
- notification failures are expected to be compensated later by scheduled task processing rather than blocking the main settlement response

So the settlement architecture mixes rule-chain filtering, aggregate persistence, and async eventual notification.

### Infrastructure Patterns

A few infrastructure pieces are central to how the system behaves:

- **MyBatis DAOs + repository implementations** back the domain repositories
- **Redis / Redisson** are used for distributed locking and caching; cache key definitions are centralized under Redis key utility classes
- **RabbitMQ** is used for asynchronous trade and team-completion events; listeners live in the trigger layer
- **`GroupBuyNotifyService`** is the outbound HTTP callback client for merchant/team-completion notifications, implemented with `OkHttpClient`
- **`DCCService`** provides runtime feature toggles and rollout controls (downgrade switch, cut-range gating, blacklist interception, cache switch)
- **JWT support** is provided from infrastructure via a domain-facing service interface

### Tests

Backend tests live under `group-buy-market-app/src/test/java` and are mostly integration-style tests covering:

- domain services (`IIndexGroupBuyMarketServiceTest`, `TradeSettlementOrderServiceTest`, etc.)
- trigger/controller entry points (`ActivityControllerTest`, `DiscountControllerTest`, `MarketTradeControllerTest`, etc.)
- infrastructure integrations (`GroupBuyNotifyServiceTest`, DAO tests)

Because the app module sets Surefire `skipTests=true` in its `pom.xml`, use `-DskipTests=false` when you actually want Maven to execute tests.

---

## Frontend (`frontend-merchant/`)

### Common Commands

Run frontend commands from `frontend-merchant/`.

```bash
npm install
npm run dev
npm run build
npm run preview
```

There is no dedicated lint or test script in `package.json` right now, so do not assume `npm run lint` or `npm test` exists unless you add it as part of a task.

### Stack

- Vue 3
- TypeScript
- Vite
- Element Plus
- TailwindCSS v4
- Pinia
- Vue Router
- Axios

### Big-Picture Frontend Structure

The merchant frontend is organized around a small app shell and a page/service split:

- `src/main.ts` bootstraps Vue, Pinia, Element Plus, and the router
- `src/router/router.ts` contains the actual route-to-page mapping and document-title handling
- `src/router/routes.ts` is a lighter route metadata list used for route definitions elsewhere in the UI
- `src/router/pinia.ts` contains the auth store and localStorage persistence for merchant auth state
- `src/request/request.ts` defines the shared Axios client, request/response interceptors, auth header injection, error normalization, and 401 redirect handling
- `src/services/*.ts` contains API wrappers grouped by business area (`activity`, `discount`, `sku`, `binding`)
- `src/pages/merchant/*` contains route-level merchant screens such as dashboard, goods, discount, activity, and binding management

### Frontend Request/Auth Flow

A few project conventions matter when changing frontend behavior:

- the Axios base URL comes from `import.meta.env.VITE_API_BASE` in `src/request/request.ts`
- auth state is persisted in localStorage under `gbm_auth_info`
- the request layer adds `Authorization: Bearer <token>` automatically when a token exists
- the response interceptor clears auth and redirects to `/` on `401`
- page components generally call the typed service modules rather than embedding raw Axios calls directly

If you change API contracts, update both the relevant service wrapper and the consuming merchant pages.

### Frontend Routing

Current route-level pages live under `src/pages/merchant/` and cover:

- dashboard
- goods list / edit
- discount list / edit
- activity list / edit
- binding

This is a route-driven admin app rather than a heavily nested component architecture.
