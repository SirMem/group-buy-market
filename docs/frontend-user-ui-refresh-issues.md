# frontend-user UI 改版 Issue 拆分草稿

> 对应 PRD：`docs/frontend-user-ui-refresh-prd.md`
>
> 说明：当前环境未连接可用的 GitHub CLI / 仓库上下文，因此先将 issue 草稿整理为文档，后续可手动创建为 GitHub issues。

## Issue 1 — 建立 Sirmem 全局视觉基础

### Parent PRD
`frontend-user-ui-refresh-prd.md`

### What to build
建立 `frontend-user` 的 Sirmem 全局视觉基础层，为后续首页、商品详情页、底部导航和次级页面改版提供统一的视觉系统与复用规范。

本 issue 交付的是“可复用的设计基础设施”，而不是单独某一个页面。重点是将目前分散、薄弱的样式变量升级为稳定的设计 token 和共享视觉约定，覆盖品牌色、文字层级、圆角、阴影、背景层级、标签与按钮状态、占位视觉风格等。

这一条完成后，后续页面不应继续依赖零散 one-off 样式，而应基于统一的 Sirmem 视觉语言实施改版。

### Acceptance criteria
- [ ] 建立一套可复用的 Sirmem 视觉 token，覆盖品牌色、语义色、背景层级、文本层级、圆角、阴影和间距节奏
- [ ] 定义并落地通用视觉规则，包括卡片壳、按钮层级、标签样式、空状态与骨架状态风格
- [ ] 定义高质量占位视觉策略，并保证未来接入真实商品图时无需重做页面结构

### Blocked by
None - can start immediately

### User stories addressed
- User story 25
- User story 26
- User story 27
- User story 29
- User story 30

---

## Issue 2 — 重构底部导航为正式品牌导航

### Parent PRD
`frontend-user-ui-refresh-prd.md`

### What to build
将当前使用 emoji 的底部导航升级为符合 Sirmem 品牌风格的正式移动端导航组件。

本 issue 应覆盖导航图标、布局、激活态、背景质感和安全区处理，使底部导航成为整个产品 shell 的稳定组成部分，而不是一个临时实现。完成后，用户应能明显感受到首页、订单、我的三个 Tab 在视觉上更专业、更统一、更符合正式上线产品的观感。

### Acceptance criteria
- [ ] 底部导航移除 emoji，替换为正式 UI 图标
- [ ] 导航 active / inactive 状态清晰、统一，并与 Sirmem 品牌色和整体视觉风格一致
- [ ] 导航在移动端安全区、固定底部显示和页面切换场景下表现稳定

### Blocked by
- Blocked by Issue 1

### User stories addressed
- User story 18
- User story 19
- User story 20
- User story 25

---

## Issue 3 — 升级商品卡片与占位主视觉

### Parent PRD
`frontend-user-ui-refresh-prd.md`

### What to build
将商品卡片从偏数据展示的列表卡升级为具备电商营销属性的商品卡片，并引入高质量占位主视觉策略。

本 issue 的目标是让商品列表在不新增接口的前提下，也能拥有“更像正式商品”的首屏观感。卡片需要强化价格层级、优惠表达、参与氛围与点击动机，同时保留未来接入真实商品图的结构兼容性。

### Acceptance criteria
- [ ] 商品卡片突出拼团价、原价、优惠额等核心商业信息，并形成明确的视觉主次
- [ ] 卡片支持高质量占位主视觉，在无真实图片数据时仍具备完整、可信的展示效果
- [ ] 卡片结构对未来接入真实商品图保持兼容，无需重新设计整体布局

### Blocked by
- Blocked by Issue 1

### User stories addressed
- User story 6
- User story 7
- User story 8
- User story 9
- User story 10
- User story 25
- User story 28

---

## Issue 4 — 首页改造成 Sirmem 活动型商品首页

### Parent PRD
`frontend-user-ui-refresh-prd.md`

### What to build
将首页从“筛选 + 列表工具页”升级为具备品牌感、活动氛围和商品转化导向的 Sirmem 商品首页。

本 issue 应围绕首页 hero/banner、筛选区、搜索区和商品列表整体层级展开，让用户进入首页后能够立即理解：这是一款在运营中的拼团商品产品，而不是一个面向内部人员的工具页。所有现有筛选、搜索、列表加载逻辑需保持可用，但呈现方式要显著升级。

### Acceptance criteria
- [ ] 首页新增品牌化 hero/banner 区域，并传达拼团活动与 Sirmem 的核心氛围
- [ ] source/channel 筛选与搜索区改造成更贴近移动端电商体验的交互与视觉结构
- [ ] 首页在保留现有业务逻辑的前提下，整体观感明显提升为正式产品首页

### Blocked by
- Blocked by Issue 1
- Blocked by Issue 3

### User stories addressed
- User story 1
- User story 2
- User story 3
- User story 4
- User story 5
- User story 6
- User story 29
- User story 30

---

## Issue 5 — 升级拼团进度组件与详情页团队区

### Parent PRD
`frontend-user-ui-refresh-prd.md`

### What to build
重构拼团进度展示及商品详情页中的团队展示区，让团队列表更具社交感、紧迫感和参与氛围，而不再只是单纯数据呈现。

本 issue 主要聚焦团队进度组件的表达方式，包括剩余人数、倒计时、进度条、团队身份感和加入动机。完成后，商品详情页中的“正在拼团”区域应明显更有“可加入的活跃团队”感。

### Acceptance criteria
- [ ] 团队进度组件强化倒计时、剩余人数和进度展示，提升用户感知的紧迫性
- [ ] 团队区视觉表现更具社交参与感，但不依赖新增接口字段
- [ ] 详情页团队列表在空状态、普通状态和边界状态下都保持稳定与一致的展示效果

### Blocked by
- Blocked by Issue 1

### User stories addressed
- User story 13
- User story 14
- User story 15
- User story 25
- User story 26

---

## Issue 6 — 商品详情页改造成转化型落地页

### Parent PRD
`frontend-user-ui-refresh-prd.md`

### What to build
将商品详情页升级为具备明确转化导向的商品落地页，强化商品主视觉、价格利益点、拼团统计、团队参与区和底部购买条的协同关系。

本 issue 目标是让用户进入详情页后，能更快理解商品价值、拼团优势与下一步操作。当前页面已有完整业务动作，因此重点不在新增功能，而在于重构页面结构与视觉层级，让 CTA 更明确、购买动线更清晰。

### Acceptance criteria
- [ ] 商品详情页顶部区域强化商品价值表达、价格层级和核心利益点
- [ ] 拼团统计区、团队区与底部购买条形成统一的转化路径与视觉主次
- [ ] 单独购买、拼团购买、加入团队等既有业务动作全部保留且交互可用

### Blocked by
- Blocked by Issue 1
- Blocked by Issue 3
- Blocked by Issue 5

### User stories addressed
- User story 11
- User story 12
- User story 13
- User story 14
- User story 15
- User story 16
- User story 17
- User story 29

---

## Issue 7 — 登录页风格对齐 Sirmem 品牌体系

### Parent PRD
`frontend-user-ui-refresh-prd.md`

### What to build
在不改变登录逻辑的前提下，将登录页统一升级为符合 Sirmem 风格的品牌入口页。

本 issue 应让微信扫码登录与账号登录两种入口共享一致的视觉体系，使登录页在视觉上与首页、详情页、底部导航同属一个产品，而不是单独存在的默认样式页面。重点是品牌表达、卡片结构、tab 样式、二维码区观感和按钮层级。

### Acceptance criteria
- [ ] 登录页在品牌文案、配色、卡片结构和按钮层级上与 Sirmem 风格保持一致
- [ ] 微信扫码与账号登录 tab 视觉统一且切换体验清晰
- [ ] 不修改既有登录业务逻辑，保证二维码登录与账号登录流程可正常使用

### Blocked by
- Blocked by Issue 1

### User stories addressed
- User story 21
- User story 22
- User story 25
- User story 26

---

## Issue 8 — 次级页面一致性补齐：订单 / 个人中心 / 状态页

### Parent PRD
`frontend-user-ui-refresh-prd.md`

### What to build
对订单页、订单卡片、个人中心以及空状态 / 骨架 / 错误状态等次级界面进行统一性补齐，确保整个 app 不会只在首页和详情页好看，而其他页面维持旧风格。

本 issue 是对核心主链路改版后的系统性补尾，目标是让用户在完整使用流程中都感受到一致的产品质感。

### Acceptance criteria
- [ ] 订单页与订单卡片升级为更偏用户视角的展示风格，并与主链路视觉一致
- [ ] 个人中心从信息展示页提升为更完整的个人中心页面观感
- [ ] 空状态、骨架、反馈状态在次级页面中采用统一的 Sirmem 风格

### Blocked by
- Blocked by Issue 1

### User stories addressed
- User story 23
- User story 24
- User story 25
- User story 26
- User story 30

---

## 建议实施顺序

1. Issue 1 — 建立 Sirmem 全局视觉基础
2. Issue 2 — 重构底部导航为正式品牌导航
3. Issue 3 — 升级商品卡片与占位主视觉
4. Issue 5 — 升级拼团进度组件与详情页团队区
5. Issue 4 — 首页改造成 Sirmem 活动型商品首页
6. Issue 6 — 商品详情页改造成转化型落地页
7. Issue 7 — 登录页风格对齐 Sirmem 品牌体系
8. Issue 8 — 次级页面一致性补齐：订单 / 个人中心 / 状态页
