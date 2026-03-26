# frontend-user 用户端开发文档

## 1. 项目概述

用户端前端（`frontend-user/`）是拼团营销系统面向 C 端用户的 Web 应用。用户可以浏览拼团商品、参与拼团下单、管理订单与退款。

### 1.1 核心功能

| 功能模块 | 说明 |
|----------|------|
| 登录 | 微信公众号扫码登录 + 用户名密码注册/登录，Tab 切换 |
| 商品首页 | 按 source/channel 筛选，分页展示拼团商品卡片 |
| 商品详情 | 商品价格、拼团优惠、现有团队列表、拼团购买/单独购买 |
| 订单管理 | 分页订单列表、订单状态跟踪、退款操作 |

### 1.2 技术栈

与 `frontend-merchant/` 保持一致：

- **Vue 3** + **TypeScript** + **Vite**
- **Element Plus** — UI 组件库
- **TailwindCSS v4** — 原子化样式（做移动端适配）
- **Pinia** — 状态管理
- **Vue Router** — 路由
- **Axios** — HTTP 请求

---

## 2. 后端服务依赖

用户端需要同时对接两个独立的后端服务：

| 服务 | 端口 | 环境变量 | 职责 |
|------|------|----------|------|
| group-buy-market (GBM) | 8091 | `VITE_GBM_API_BASE` | 商品查询、营销试算、账号注册/登录 |
| s-pay-mall-ddd (PAY) | 8080 | `VITE_PAY_API_BASE` | 微信登录、下单支付、订单查询、退款 |

前端创建两个独立的 Axios 实例，分别指向不同的 baseURL。

### 2.1 环境配置 (.env)

```env
VITE_GBM_API_BASE=http://localhost:8091
VITE_PAY_API_BASE=http://localhost:8080
```

---

## 3. 身份认证设计

### 3.1 统一身份方案

两种登录方式最终都产出一个 **userId** 字符串，前端统一存储在 Pinia + localStorage 中，调用两个后端服务时均使用同一个 userId。

| 登录方式 | 后端服务 | 接口 | 返回值 |
|----------|----------|------|--------|
| 微信扫码 | PAY (8080) | `GET /api/v1/login/weixin_qrcode_ticket` | ticket |
| | | `GET /api/v1/login/check_login?ticket=xxx` | openidToken |
| 账号登录 | GBM (8091) | `POST /api/v1/auth/login` | `{token, id, username, role}` |
| 账号注册 | GBM (8091) | `POST /api/v1/auth/register` | `{token, id, username, role}` |

### 3.2 微信扫码登录流程

```
前端                              PAY 后端                     微信
 │                                   │                          │
 ├─ GET /weixin_qrcode_ticket ──────►│                          │
 │◄──── ticket ─────────────────────│                          │
 │                                   │                          │
 │  展示二维码:                       │                          │
 │  <img src="https://mp.weixin.   │                          │
 │   qq.com/cgi-bin/showqrcode     │                          │
 │   ?ticket={ticket}">             │                          │
 │                                   │                          │
 │  每 2 秒轮询:                     │                          │
 ├─ GET /check_login?ticket=xxx ───►│                          │
 │◄──── code=0003 (未登录) ─────────│                          │
 │  ...                              │    用户扫码关注           │
 │                                   │◄─────────────────────────│
 ├─ GET /check_login?ticket=xxx ───►│                          │
 │◄──── openidToken ────────────────│                          │
 │                                   │                          │
 │  存储 token + userId              │                          │
 │  跳转首页                          │                          │
```

**轮询策略**：
- 间隔：2 秒
- 最大次数：60 次（总计 2 分钟）
- 超时处理：提示"登录超时，请刷新重试"

### 3.3 账号登录/注册流程

```
前端                              GBM 后端
 │                                   │
 ├─ POST /api/v1/auth/login ────────►│
 │  { username, password }           │
 │◄──── { token, id, username } ────│
 │                                   │
 │  存储 token + userId(=username)    │
 │  跳转首页                          │
```

### 3.4 Auth Store 结构

```typescript
interface AuthState {
  token: string          // JWT token (账号登录) 或 openidToken (微信登录)
  userId: string         // 统一用户标识
  loginType: 'wechat' | 'account'  // 登录方式
}
```

---

## 4. 页面路由设计

### 4.1 路由表

| 路由 | 页面 | 组件路径 | 需要登录 |
|------|------|----------|----------|
| `/login` | 登录页 | `pages/login/index.vue` | 否 |
| `/` | 首页（商品列表） | `pages/home/index.vue` | 是 |
| `/goods/:goodsId` | 商品详情 | `pages/goods/detail.vue` | 是 |
| `/orders` | 订单列表 | `pages/orders/index.vue` | 是 |
| `/profile` | 个人中心 | `pages/profile/index.vue` | 是 |

### 4.2 导航结构

底部 Tab 导航栏（移动端风格）：

```
┌─────────────────────────────────────┐
│                                     │
│           页面内容区域                │
│                                     │
├───────────┬───────────┬─────────────┤
│   🏠 首页  │  📋 订单   │   👤 我的   │
└───────────┴───────────┴─────────────┘
```

### 4.3 路由守卫

- 未登录用户访问需要认证的页面 → 重定向到 `/login`
- 已登录用户访问 `/login` → 重定向到 `/`

---

## 5. API 接口详细定义

### 5.1 登录相关

#### 获取微信二维码 ticket

```
GET  PAY_BASE/api/v1/login/weixin_qrcode_ticket

Response: {
  code: "0000",
  info: "success",
  data: "gQHB8TwAAAAAAAAAAS5..."  // ticket 字符串
}

二维码图片 URL：https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket={encodeURIComponent(ticket)}
```

#### 轮询检测登录状态

```
GET  PAY_BASE/api/v1/login/check_login?ticket={ticket}

Response (未登录): {
  code: "0003",
  info: "未登录",
  data: null
}

Response (已登录): {
  code: "0000",
  info: "success",
  data: "openidToken_string"  // 作为 token 和 userId
}
```

#### 账号登录

```
POST  GBM_BASE/api/v1/auth/login
Body: { "username": "xxx", "password": "xxx" }

Response: {
  code: 0,
  data: {
    token: "jwt_string",
    id: 1,
    username: "xxx",
    role: "user",
    userStatus: 1
  }
}
```

#### 账号注册

```
POST  GBM_BASE/api/v1/auth/register
Body: { "username": "xxx", "password": "xxx" }

Response: 同上
```

### 5.2 商品相关

#### 商品列表（分页）

```
POST  GBM_BASE/api/v1/gbm/index/query_market_goods_page

Body: {
  "userId": "xxx",
  "source": "meituan",
  "channel": "app",
  "page": 1,
  "pageSize": 10,
  "keyword": "",               // 可选，模糊搜索
  "onlyEffective": true,       // 仅返回有效商品
  "includeInventory": true,    // 返回库存信息
  "includeTeamStatistic": true, // 返回拼团统计
  "includeTeamList": false      // 列表页不需要团队明细
}

Response: {
  code: "0000",
  info: "success",
  data: {
    pageNum: 1,
    pageSize: 10,
    total: 25,
    pages: 3,
    list: [
      {
        activityId: 100,
        goodsId: "9890001",
        goodsName: "《手写MyBatis：渐进式源码实践》",
        originalPrice: 100.00,
        deductionPrice: 20.00,
        payPrice: 80.00,
        visible: true,
        enable: true,
        effective: true,
        inventory: {
          totalStock: 1000,
          reservedStock: 50,
          soldStock: 200,
          availableStock: 750
        },
        teamStatistic: {
          allTeamCount: 15,         // 开团队伍数量
          allTeamCompleteCount: 8,  // 成团队伍数量
          allTeamUserCount: 42      // 参团总人数
        }
      }
    ]
  }
}
```

#### 商品详情（含拼团信息）

```
POST  GBM_BASE/api/v1/gbm/index/query_group_buy_market_config

Body: {
  "userId": "xxx",
  "source": "meituan",
  "channel": "app",
  "goodsId": "9890001"
}

Response: {
  code: "0000",
  info: "success",
  data: {
    activityId: 100,
    goods: {
      goodsId: "9890001",
      originalPrice: 100.00,
      deductionPrice: 20.00,
      payPrice: 80.00
    },
    teamList: [
      {
        userId: "user001",       // 团长
        teamId: "T20260324001",
        activityId: 100,
        targetCount: 3,          // 目标人数
        completeCount: 1,        // 已完成人数
        lockCount: 2,            // 已锁单人数
        validStartTime: "2026-03-24T10:00:00",
        validEndTime: "2026-03-25T10:00:00",
        validTimeCountdown: "18:30:00",  // 倒计时
        outTradeNo: "OT20260324001"
      }
    ],
    teamStatistic: {
      allTeamCount: 15,
      allTeamCompleteCount: 8,
      allTeamUserCount: 42
    }
  }
}
```

### 5.3 订单相关

#### 创建订单（下单）

```
POST  PAY_BASE/api/v1/alipay/create_pay_order

Body: {
  "userId": "xxx",
  "productId": "9890001",      // 商品ID（即 goodsId）
  "teamId": "T20260324001",    // 加入已有团时传；拼团购买（开新团）不传；单独购买不传
  "activityId": 100,           // 拼团购买时传；单独购买不传
  "marketType": 1              // 1=拼团购买, 0=单独购买（无营销）
}

Response: {
  code: "0000",
  info: "success",
  data: "https://openapi.alipay.com/..."  // 支付宝支付链接 (payUrl)
}
```

**三种下单场景**：

| 场景 | marketType | activityId | teamId |
|------|-----------|------------|--------|
| 拼团购买（开新团） | 1 | 传 | 不传 |
| 加入已有团 | 1 | 传 | 传 |
| 单独购买 | 0 | 不传 | 不传 |

**支付流程**：下单成功后，新标签页打开 payUrl，用户在支付宝完成支付。

#### 查询订单列表

```
POST  PAY_BASE/api/v1/alipay/query_user_order_list

Body: {
  "userId": "xxx",
  "lastId": null,    // 首次查询传 null；翻页传上次返回的 lastId
  "pageSize": 10
}

Response: {
  code: "0000",
  info: "success",
  data: {
    orderList: [
      {
        id: 1,
        userId: "xxx",
        productId: "9890001",
        productName: "《手写MyBatis：渐进式源码实践》",
        orderId: "928263928388",
        orderTime: "2026-03-24T12:00:00",
        totalAmount: 100.00,
        status: "PAY_WAIT",          // 订单状态码
        payUrl: "https://...",
        marketType: 1,               // 0=无营销, 1=拼团
        marketDeductionAmount: 20.00,
        payAmount: 80.00,
        payTime: null
      }
    ],
    hasMore: true,
    lastId: 10
  }
}
```

**订单状态映射**：

| status | 中文显示 | 用户可操作 |
|--------|---------|-----------|
| `CREATE` | 创建中 | - |
| `PAY_WAIT` | 待支付 | 可去支付 |
| `PAY_SUCCESS` | 已支付 | - |
| `DEAL_DONE` | 已完成 | - |
| `CLOSE` | 已关闭 | - |
| `REFUND` | 已退款 | - |

#### 退款

```
POST  PAY_BASE/api/v1/alipay/refund_order

Body: {
  "userId": "xxx",
  "orderId": "928263928388"
}

Response: {
  code: "0000",
  info: "success",
  data: {
    success: true,
    orderId: "928263928388",
    message: "退单成功"
  }
}
```

#### 主动查询支付状态

```
POST  PAY_BASE/api/v1/alipay/active_pay_notify?outTradeNo=928263928388

Response: {
  code: "0000",
  info: "success",
  data: "交易成功，订单状态已更新"
}
```

---

## 6. source/channel 渠道配置

### 6.1 Hardcode 映射表

用户端前端 hardcode 以下映射，与商户端保持一致：

```typescript
// source 映射
const SOURCE_MAP: Record<string, string> = {
  meituan: '美团',
  douyin: '抖音',
  xiaohongshu: '小红书',
}

// channel 映射
const CHANNEL_MAP: Record<string, string> = {
  app: 'APP',
  miniapp: '小程序',
  h5: 'H5网页',
}
```

### 6.2 用户选择方式

首页顶部提供 source + channel 选择器（如 Tab 或下拉菜单），默认选中第一个组合。切换后重新请求 `queryMarketGoodsPage` 加载对应商品列表。

---

## 7. 项目目录结构

```
frontend-user/
├── public/
├── src/
│   ├── main.ts                   # 应用入口
│   ├── App.vue                   # 根组件
│   ├── style.css                 # 全局样式 (TailwindCSS)
│   │
│   ├── config/
│   │   └── constants.ts          # source/channel 映射、订单状态映射等常量
│   │
│   ├── request/
│   │   ├── gbm.ts                # GBM 服务 Axios 实例 (8091)
│   │   └── pay.ts                # PAY 服务 Axios 实例 (8080)
│   │
│   ├── services/
│   │   ├── auth.ts               # 登录/注册 API 封装
│   │   ├── goods.ts              # 商品列表/详情 API 封装
│   │   └── order.ts              # 订单/退款/支付查询 API 封装
│   │
│   ├── store/
│   │   └── auth.ts               # Pinia auth store
│   │
│   ├── router/
│   │   └── index.ts              # Vue Router 配置 + 路由守卫
│   │
│   ├── components/
│   │   ├── BottomNav.vue         # 底部导航栏
│   │   ├── GoodsCard.vue         # 商品卡片组件
│   │   ├── TeamProgress.vue      # 拼团进度条组件
│   │   └── OrderCard.vue         # 订单卡片组件
│   │
│   └── pages/
│       ├── login/
│       │   └── index.vue         # 登录页（微信扫码 Tab + 账号登录 Tab）
│       ├── home/
│       │   └── index.vue         # 首页（source/channel 选择 + 商品列表）
│       ├── goods/
│       │   └── detail.vue        # 商品详情（拼团信息 + 下单按钮）
│       ├── orders/
│       │   └── index.vue         # 订单列表（状态标签 + 退款按钮）
│       └── profile/
│           └── index.vue         # 个人中心（退出登录）
│
├── .env                          # 环境变量
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
└── tailwind.config.ts
```

---

## 8. 页面 UI 设计要点

### 8.1 登录页 `/login`

```
┌─────────────────────────────┐
│        拼团商城               │
│                              │
│  ┌──────────┬──────────┐     │
│  │ 微信扫码  │ 账号登录  │     │
│  └──────────┴──────────┘     │
│                              │
│  [微信扫码 Tab]               │
│  ┌───────────────────┐       │
│  │                   │       │
│  │    微信二维码       │       │
│  │    (img tag)      │       │
│  │                   │       │
│  └───────────────────┘       │
│    请使用微信扫码关注登录       │
│                              │
│  [账号登录 Tab]               │
│  用户名 [_______________]     │
│  密码   [_______________]     │
│  [登录]  还没有账号？去注册     │
│                              │
└─────────────────────────────┘
```

### 8.2 首页 `/`

```
┌─────────────────────────────┐
│  🔍 搜索商品...               │
│                              │
│  source:  [美团] [抖音] [小红书]│
│  channel: [APP] [小程序] [H5] │
│                              │
│  ┌───────────────────────┐   │
│  │ 📦 商品名称             │   │
│  │ ¥80.00  原价¥100.00    │   │
│  │ 省¥20.00               │   │
│  │ 🔥 15个团 · 42人参与     │   │
│  │ 库存: 750              │   │
│  └───────────────────────┘   │
│  ┌───────────────────────┐   │
│  │ 📦 商品名称             │   │
│  │ ...                    │   │
│  └───────────────────────┘   │
│                              │
├───────────┬──────┬───────────┤
│   🏠 首页  │📋 订单│  👤 我的   │
└───────────┴──────┴───────────┘
```

### 8.3 商品详情 `/goods/:goodsId`

```
┌─────────────────────────────┐
│  ← 返回                      │
│                              │
│  商品名称                     │
│  ¥80.00  原价 ¥100.00        │
│  拼团优惠 -¥20.00            │
│                              │
│  ── 拼团统计 ──               │
│  15个团 · 8个已成团 · 42人参与  │
│                              │
│  ── 正在拼团 ──               │
│  ┌─────────────────────┐     │
│  │ user001 的团          │     │
│  │ ██░░░ 1/3   ⏱ 18:30  │     │
│  │         [加入此团]     │     │
│  └─────────────────────┘     │
│  ┌─────────────────────┐     │
│  │ user002 的团          │     │
│  │ ████░ 2/3   ⏱ 05:15  │     │
│  │         [加入此团]     │     │
│  └─────────────────────┘     │
│                              │
│  ┌────────────┬─────────┐    │
│  │ 单独购买¥100│拼团¥80  │    │
│  └────────────┴─────────┘    │
└─────────────────────────────┘
```

### 8.4 订单列表 `/orders`

```
┌─────────────────────────────┐
│  我的订单                     │
│                              │
│  ┌───────────────────────┐   │
│  │ 商品名称      [待支付]  │   │
│  │ 订单号: 928263928388   │   │
│  │ ¥80.00 (优惠¥20.00)   │   │
│  │ 2026-03-24 12:00      │   │
│  │ [去支付]  [退款]       │   │
│  └───────────────────────┘   │
│  ┌───────────────────────┐   │
│  │ 商品名称      [已完成]  │   │
│  │ ...                    │   │
│  └───────────────────────┘   │
│                              │
│  [加载更多]                   │
│                              │
├───────────┬──────┬───────────┤
│   🏠 首页  │📋 订单│  👤 我的   │
└───────────┴──────┴───────────┘
```

---

## 9. 关键交互流程

### 9.1 拼团购买流程

```
用户浏览首页商品列表
    │
    ▼
点击商品卡片 → 进入商品详情页
    │
    ├─ 点击「拼团购买 ¥80」(底部按钮)
    │      │
    │      ▼ 调用 createPayOrder:
    │        marketType=1, activityId=活动ID, 不传teamId (开新团)
    │      │
    │      ▼ 返回 payUrl → window.open(payUrl) 新标签页支付
    │
    ├─ 点击某个团的「加入此团」
    │      │
    │      ▼ 调用 createPayOrder:
    │        marketType=1, activityId=活动ID, teamId=该团ID
    │      │
    │      ▼ 返回 payUrl → window.open(payUrl) 新标签页支付
    │
    └─ 点击「单独购买 ¥100」(底部按钮)
           │
           ▼ 调用 createPayOrder:
             marketType=0, 不传activityId/teamId
           │
           ▼ 返回 payUrl → window.open(payUrl) 新标签页支付
```

### 9.2 支付结果确认流程

```
用户在支付宝标签页完成支付 → 关闭标签页 → 回到前端

前端策略（订单列表页）:
    ├─ 用户手动点击「刷新」按钮
    │      └─ 调用 queryUserOrderList 重新加载
    │
    └─ 对于 PAY_WAIT 状态的订单，提供「已支付？点击确认」按钮
           └─ 调用 activePayNotify(outTradeNo) 主动查询支付宝
           └─ 刷新订单列表
```

### 9.3 退款流程

```
用户在订单列表页
    │
    ▼ 点击「退款」按钮
    │
    ▼ 弹出确认对话框 "确定要申请退款吗？"
    │
    ├─ 确认 → 调用 refundOrder({ userId, orderId })
    │         │
    │         ├─ success=true → 提示"退款成功" → 刷新列表
    │         └─ success=false → 提示错误信息
    │
    └─ 取消 → 关闭对话框
```

---

## 10. 状态管理

### 10.1 Auth Store

```typescript
// store/auth.ts
interface AuthState {
  token: string
  userId: string
  username: string
  loginType: 'wechat' | 'account'
}

// Actions:
// - setWechatAuth(openidToken: string)     微信登录成功后
// - setAccountAuth(response: AuthResponse) 账号登录成功后
// - clear()                                 退出登录
// - persist() / loadFromStorage()          持久化到 localStorage
```

### 10.2 localStorage Key

```
user_auth_info  →  { token, userId, username, loginType }
```

---

## 11. 错误处理规范

### 11.1 HTTP 层

- **网络错误**：显示 "网络连接失败，请检查网络"
- **401 Unauthorized**：清除 auth store，跳转 `/login`
- **后端 code !== "0000"**：显示后端返回的 `info` 信息
- **请求超时**：显示 "请求超时，请稍后重试"

### 11.2 业务层

- **rate limiter**（code="0004"）：显示 "操作太频繁，请稍后再试"
- **下单失败**：显示具体错误原因，不跳转
- **退款失败**：显示 `data.message` 中的失败原因

---

## 12. 分步开发计划

> 每完成一步，从本节中移除对应 Step。全部移除即开发完毕。

