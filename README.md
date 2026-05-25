# 差旅费用报销单项目

- 前端：Vue 3 + Vite + TypeScript + Element Plus
- 后端：Java 17 + Spring Boot 3.2 + MyBatis Plus 3.5.11
- 数据库：MySQL 8.x

## 目录结构

```text
web
├── frontend          # 前端项目
├── backend           # 后端项目
└── README.md
```

## 环境要求

- Node.js 18+
- JDK 17+
- MySQL 8.x

数据库默认配置：

```text
地址：localhost:3306
数据库：travel_reimbursement
用户名：root
密码：123456
```

配置文件位置：

```text
backend/src/main/resources/application.yml
```

## 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端默认地址：`http://localhost:8080`

启动时会自动完成：
- 创建数据库 `travel_reimbursement`
- 创建表结构（fk_reim_main、fk_reim_itinerary、fk_reim_subsidy、fk_subsidy_calendar）
- 自动迁移兼容旧表结构
- 首次启动自动填充示例数据

## 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认访问：`http://127.0.0.1:5173`

接口默认请求：`http://localhost:8080/api`

请先启动后端，再打开前端页面。

## 功能说明

- 报销单列表查询（分页、条件筛选）
- 报销单新增、编辑、提交、作废
- 基础信息维护
- 补录行程维护
- 行程人员 + 日期重复校验
- 自动生成补助信息和补助日历
- 餐费、交通、通讯补助选择与金额校验
- 费用合计自动计算
- 费用归属及分摊联动计算
- 备注信息维护

## 常用接口

```text
GET  /api/dict                         获取页面下拉数据
POST /api/reimbursements/page          分页查询报销单
GET  /api/reimbursements/{id}          查询报销单详情
POST /api/reimbursements/save          保存草稿
POST /api/reimbursements/submit        提交单据
POST /api/reimbursements/{id}/void     作废单据
```

## 构建前端

```bash
cd frontend
npm run build
```

构建产物位于 `frontend/dist`。

## 常见问题

### 数据库连接失败

确认 MySQL 已启动，账号密码与 `application.yml` 中配置一致。

### 前端页面没有数据

确认后端已启动，访问 `http://localhost:8080/api/dict` 验证。

### 旧表字段报错

删除旧库后重启即可自动重建：

```sql
DROP DATABASE travel_reimbursement;
```
