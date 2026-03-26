# Sirmem frontend-user UI 视觉升级与轻交互重构 PRD

## Problem Statement

当前 `frontend-user` 已具备基础业务能力，包含登录、首页商品浏览、商品详情、下单、订单查看、个人中心等核心链路，但整体视觉表现仍偏向“内部演示页 / 功能验证页”，与面向真实用户的移动端拼团商城存在明显差距。

从用户视角看，当前产品主要存在以下问题：

1. 首页、商品详情页、底部导航等核心界面缺乏统一的视觉品牌表达，页面虽然可用，但不够像正式上线产品。
2. 商品卡片、团队进度、订单卡片等核心组件更偏数据展示，缺少电商营销场景下应有的转化氛围与情绪引导。
3. 底部导航使用 emoji 图标，降低了整体质感与一致性。
4. 全局视觉 token 体系较弱，颜色、阴影、圆角、间距、状态样式尚未形成稳定规则，导致页面间统一性不足。
5. 商品图资源当前不足，页面缺乏主视觉支撑，使首页列表和详情页不够吸引用户点击与购买。
6. 登录页、订单页、个人页虽功能完整，但与首页和商品详情未来的升级风格若不统一，整体产品会出现明显割裂感。

本次需求并非新增业务能力，而是围绕现有业务链路，进行一轮 **UI 视觉升级 + 轻交互重构**，目标是在 **不改接口** 的前提下，让 `frontend-user` 从“可用 demo”升级为“具备正式产品感的移动端拼团商城”。

## Solution

为 `frontend-user` 建立一套轻量级品牌化设计体系，并以首页、商品详情页、底部导航为 P0 核心改造对象，完成一次面向 C 端用户的视觉升级与轻交互重构。

整体方案采用以下原则：

- 风格定位为：**电商营销风**
- 视觉参考方向为：**美团 / 瑞幸式活动页**
- 品牌表达采用：**轻量品牌化 Sirmem**
- 改造方式为：**视觉 + 轻交互重构**
- 范围边界为：**允许新增轻量展示模块，但不改动后端接口**
- 商品图片策略为：**本次先采用高质量占位视觉，并预留未来接入真实商品图的结构**
- 产品目标优先级为：**正式产品感 > 转化感 > 单纯好看**

面向用户的最终体验应表现为：

- 首页更像“正在运营中的拼团商城首页”，而不是筛选+列表页
- 商品详情页更像“会促成下单的商品转化页”，而不是信息堆叠页
- 底部导航更像成熟移动端产品的正式导航，而非临时实现
- 登录页、订单页、个人页与主链路风格统一，不再割裂
- 页面在没有真实商品图片时，仍具备体面、可信、统一的视觉观感

## User Stories

1. As a first-time mobile shopper, I want the homepage to feel like a real shopping product, so that I trust the product before interacting further.
2. As a returning user, I want the homepage to immediately show a branded and polished experience, so that I feel the product is stable and intentional.
3. As a shopper browsing group-buy goods, I want the homepage hero area to communicate value and activity atmosphere, so that I understand this is a promotional group-buy experience.
4. As a shopper, I want the homepage filters to feel mobile-friendly and lightweight, so that choosing source/channel feels easy rather than tool-like.
5. As a shopper, I want search and filter controls to visually match the overall product style, so that the product feels coherent.
6. As a shopper browsing goods, I want each goods card to highlight the most important purchase signals first, so that I can quickly judge whether to click.
7. As a shopper, I want to see prominent group-buy price, original price, and discount value, so that I can immediately understand the deal.
8. As a shopper, I want goods cards to visually imply popularity and urgency, so that I can identify attractive offers quickly.
9. As a shopper, I want goods cards to remain visually appealing even when no product image is available, so that the app still feels polished.
10. As a shopper, I want goods cards to reserve space for future product images, so that the experience can evolve without reworking the page structure.
11. As a shopper entering the goods detail page, I want the page to feel like a purchase-focused landing page, so that I am naturally guided toward buying.
12. As a shopper, I want the goods detail page to surface core benefits near the top, so that I do not need to scan the whole page to understand the offer.
13. As a shopper, I want group-buy statistics to look meaningful and easy to scan, so that the page feels active and trustworthy.
14. As a shopper, I want active teams to look socially alive rather than purely numeric, so that joining a team feels engaging.
15. As a shopper, I want the countdown and remaining-person signals to stand out on team cards, so that I understand urgency.
16. As a shopper, I want the bottom purchase bar to clearly distinguish single-buy and group-buy actions, so that I can decide quickly.
17. As a shopper, I want the primary call-to-action to be visually stronger than secondary actions, so that the recommended action is obvious.
18. As a shopper, I want the bottom nav to look modern and consistent with the rest of the app, so that the product feels professionally built.
19. As a shopper, I want navigation icons to be formal UI icons instead of emoji, so that the product feels higher quality.
20. As a returning user, I want the active tab state to be clear and branded, so that I always know where I am.
21. As a login user, I want the login page to visually match the rest of the app, so that my first impression aligns with the product experience.
22. As a user choosing between WeChat and account login, I want the tab switch to feel modern and polished, so that login feels trustworthy.
23. As an order viewer, I want order cards to look user-facing instead of operational, so that order management feels simple and clear.
24. As a user in profile center, I want my profile page to feel like a personal center rather than a debug info page, so that the product feels complete.
25. As a user, I want pages to share the same colors, spacing, shadows, and component language, so that the app feels like one system.
26. As a user, I want loading, empty, and feedback states to look intentional, so that the app feels polished even outside happy paths.
27. As a future maintainer, I want the UI to be built on reusable visual tokens and shared component conventions, so that future pages can follow the same style.
28. As a future maintainer, I want the app to support real product images later without redesigning all cards and detail pages, so that future evolution is low-cost.
29. As a product owner, I want the upgrade to avoid backend API changes, so that delivery remains fast and low-risk.
30. As a designer/developer, I want the refresh to focus first on core user paths, so that visible impact is high even if the work lands incrementally.

## Implementation Decisions

### 1. Overall product direction
- The refresh will target a **mobile-first group-buy shopping experience** rather than a generic admin-like interface.
- The chosen visual direction is **lightweight e-commerce marketing**, inspired by **Meituan / Luckin activity pages**, while avoiding noisy “festival campaign” aesthetics.
- The product brand will be lightly updated to **Sirmem**, used primarily in visible brand surfaces such as login, homepage hero language, and global title treatment.

### 2. Scope and priorities
- **P0** focuses on the highest-visibility, highest-impact conversion path:
  - Homepage
  - Goods detail page
  - Bottom navigation
  - Supporting shared visual system and core components used by these pages
- **P1** ensures surrounding pages do not visually fall behind:
  - Login page
  - Order page
  - Profile page
  - Order card refinement where needed for overall coherence

### 3. No backend/API changes
- The implementation must not require new interfaces or schema changes.
- All visual improvement must come from:
  - layout restructuring
  - copy refinement
  - token system
  - presentation hierarchy
  - placeholder assets
  - more deliberate state design
- Existing service contracts and business actions remain intact.

### 4. Homepage redesign decisions
- The homepage should move from “filter + list utility page” to a **shopping homepage with activity framing**.
- A new lightweight **hero/banner section** will be introduced at the top without requiring backend data changes.
- This hero section will use brand copy, current user context, and activity-oriented messaging to create a stronger first impression.
- Source/channel filters will remain functionally unchanged, but will be visually reframed as a mobile-friendly selection panel rather than a backend-like control strip.
- The search bar will be integrated into the new visual hierarchy and treated as a branded shopping control.
- Goods list presentation will emphasize shopping signals:
  - group-buy price
  - original price
  - savings
  - stock / popularity / team stats
  - click motivation

### 5. Goods detail page decisions
- The detail page should become a **conversion-oriented landing page** for a single product.
- The upper portion of the page should establish a clear primary narrative:
  - product value
  - group-buy pricing advantage
  - current participation / group activity
  - contextual trust
- Statistics should be presented as concise, high-readability visual cards rather than flat data blocks.
- Active team entries should feel socially alive and time-sensitive.
- The bottom purchase area should remain fixed, but become more deliberate in hierarchy:
  - supporting price and purchase explanation on the left
  - clearer distinction between single purchase and group-buy CTA
  - stronger emphasis on the primary conversion action

### 6. Bottom navigation decisions
- Emoji icons will be removed and replaced with formal UI icons.
- The bottom navigation should visually match a polished mobile commerce product:
  - better iconography
  - clearer active state
  - more cohesive shape and spacing
  - optional subtle background elevation / soft translucency if it remains readable and stable
- The nav should not become overly decorative; it should strengthen product quality without stealing attention from page content.

### 7. Shared component redesign decisions
#### Goods card
- The goods card should shift from “data summary card” to **shopping card with conversion cues**.
- A placeholder hero/image region should be introduced or reserved to support future product imagery.
- Price hierarchy should be made visually dominant.
- Discount information should be converted into clearer promotional language.
- Secondary signals such as participation, stock, or team activity should support the conversion story rather than compete with price.

#### Team progress
- The team progress component should better express urgency and social participation.
- Countdown, remaining slots, and team progress should be visually prioritized.
- The component may introduce lightweight avatar placeholders or social grouping motifs, but must not depend on new data fields.

#### Bottom nav
- Active/inactive states, icon style, and spacing should be standardized and brand-aligned.
- The navigation should become a reusable shell-level component rather than a one-off visual patch.

#### Order card
- The order card is not a P0 centerpiece, but may need refinement in P1 so the surrounding pages feel consistent.
- The card should remain operationally clear but more user-facing in tone and layout.

### 8. Login page decisions
- Login is included as a **secondary priority** to maintain brand continuity.
- It does not need a heavy redesign, but must no longer look disconnected from the rest of the app.
- The login card, tabs, QR section, buttons, and heading area should adopt the same Sirmem visual language as P0 pages.

### 9. Global visual system decisions
A reusable visual system should be defined before or alongside page work, including:

- Brand palette
- Text hierarchy
- Surface/background hierarchy
- Radius system
- Shadow/elevation system
- Border treatments
- State semantics
- Button hierarchy
- Section spacing rhythm
- Reusable pill/tag conventions
- Empty/loading state conventions

This system should be lightweight but explicit enough that all updated pages can share the same look without one-off styling.

### 10. Brand strategy decisions
- The brand expression should be **lightweight, modern, warm, and trustworthy**.
- “Sirmem” should feel like a subtle product identity, not a full marketing rebrand program.
- Brand expression is primarily achieved through:
  - color tone
  - copy tone
  - hero messaging
  - icon language
  - surface consistency

### 11. Placeholder visual strategy
- Because real product images are not currently a stable input, the redesign must use a **high-quality placeholder visual strategy**.
- Placeholder visuals should feel deliberate and premium rather than like missing content.
- The structure should be future-proof, allowing real product images to replace placeholders later without layout redesign.

### 12. Deep module opportunities
The redesign should avoid scattering hardcoded visual choices throughout pages. The implementation should extract stable, reusable modules around:

- global design tokens
- branded section/header patterns
- card shell patterns
- action bar/button priority patterns
- placeholder media presentation
- state badges/tags
- team progress display conventions

These modules should expose simple, stable interfaces and centralize the visual language of the app.

## Testing Decisions

### What makes a good test
Good tests should verify **user-visible behavior** and stable outputs rather than internal implementation details. In this UI refresh, a good test should focus on:

- whether key UI states render correctly
- whether important calls to action remain present and distinguishable
- whether page structure preserves required functionality
- whether navigation and primary user flows still work after layout changes
- whether placeholder/image fallback behavior remains stable
- whether active/inactive states and conditional rendering still match user expectations

Tests should avoid coupling to:
- exact class strings when those classes are incidental
- internal ref names or component-local implementation details
- visual minutiae that are likely to evolve during polish

### Which modules should be tested
Priority test coverage should focus on modules whose behavior matters across the redesign:

1. Homepage page behavior
   - filters still trigger list refresh
   - search still works
   - goods item click still navigates to detail page

2. Goods detail page behavior
   - data loads and renders correctly
   - join team / buy alone / buy by group actions still map to the correct purchase behaviors
   - empty/error states still render safely

3. Bottom navigation behavior
   - active tab state tracks the route correctly
   - clicking a tab navigates to the expected page

4. Goods card component
   - key commercial information still renders in correct hierarchy
   - fallback placeholder media state renders when no image exists

5. Team progress component
   - countdown/progress/remaining slots render coherently
   - zero/edge states do not visually collapse

6. Login page behavior
   - login mode switching still works
   - QR loading state, QR success state, and account login state remain intact

### Prior art for tests
Tests should follow existing frontend patterns in the repository if present; if frontend automated UI tests are currently thin or absent, the initial suite should prioritize **behavioral component/page tests** that validate routing, rendering, and action availability.

Where prior art is limited, new tests should be designed around:
- route-driven rendering
- props-driven component states
- user-triggered button actions
- safe rendering for loading/empty/error cases

### Manual testing expectations
Because this is a visual refresh, manual QA remains essential. Manual review should cover:

- mobile viewport appearance
- spacing and typography consistency
- fixed bottom purchase bar behavior
- bottom nav safe area behavior
- empty/loading states
- login page visual alignment with main product
- continuity between homepage → detail → order flow

## Out of Scope

The following items are out of scope for this PRD:

1. Backend API changes, schema changes, or new service contracts
2. New business capabilities such as recommendations, coupon issuance, or real-time group-buy systems
3. A full product rebranding program beyond lightweight Sirmem visual identity usage
4. New authenticated flows or major login logic changes
5. Complete redesign of order center and profile center as primary deliverables
6. Addition of real product image management or media upload pipelines
7. Major information architecture changes to routing or core business flow
8. Animation-heavy visual redesign that increases complexity without clear user value
9. Desktop-first or tablet-specific redesign work
10. Rewriting the app into a new design framework or replacing the existing tech stack

## Further Notes

### User experience goals
The redesign should be evaluated against the following UX goals:

- The app should look like a **real, shippable consumer product**
- The homepage should create immediate shopping context and brand confidence
- The detail page should make the user feel guided toward action
- The product should feel **more premium without becoming visually noisy**
- The system should be visually consistent enough that future pages can follow the same style

### Proposed visual language
The Sirmem visual language should be warm, contemporary, and commerce-oriented. Suggested characteristics:

- warm orange-red primary family instead of harsh red
- creamy or softly tinted backgrounds instead of plain gray utility backgrounds
- large but controlled corner radius
- soft layered shadows rather than hard borders everywhere
- strong primary CTA hierarchy
- restrained badge/tag usage for price and activity signals
- polished placeholder illustration blocks for product/media areas

### P0 success criteria
P0 is successful when:
- homepage, goods detail page, and bottom nav visibly feel like one branded system
- the app no longer relies on emoji navigation
- the homepage clearly communicates shopping and activity context
- goods cards feel like purchase-oriented product cards
- goods detail page feels conversion-focused and modern
- no API change is required to deliver the redesign

### P1 success criteria
P1 is successful when:
- login page visually aligns with Sirmem branding
- order/profile pages no longer feel disconnected from the refreshed core path
- card and state styling are consistent across secondary pages

### Suggested milestone plan
#### Milestone 1: Visual foundation
- define Sirmem token system
- define icon strategy
- define placeholder visual rules
- define card/action/header patterns

#### Milestone 2: P0 delivery
- homepage redesign
- goods card redesign
- goods detail redesign
- team progress redesign
- bottom nav redesign

#### Milestone 3: P1 alignment
- login page alignment
- order card and order page polish
- profile page polish
- empty/loading/feedback state cleanup

### Design acceptance checklist
The final implementation should satisfy all of the following:
- consistent Sirmem brand language across updated pages
- consistent spacing and hierarchy across sections
- formal icons replacing emoji
- placeholder media that feels intentional
- stronger CTA hierarchy on detail page
- shopping-first card design on homepage
- preserved existing business functionality and route behavior
- no regressions in login, browsing, or purchase flow
