**统一代码规范的目的是为了方便后续维护, 也是为了写出优雅的代码**

### 前端代码结构

```code
src 
 ├── assets               静态文件文件夹
 ├── components           组件代码, 根据 views 的功能话费文件夹
 ├── css
 │    ├─ common.less      一些公共的样式
 │    ├─ component.less   定义组件的样式
 │    ├─ layout.less      布局的样式
 │    └─ table.less       表格组件的样式
 ├── lib
 │    ├─ api.js           后端 api
 │    ├─ directive.js     自定义指令
 │    ├─ enum.js          枚举配置, 以后端定义的应完全保持一致
 │    ├─ filters.js       vue 通用过滤器
 │    ├─ http.js          axios 封装
 │    ├─ storage.js       local storage 存储封装
 │    ├─ utils.js         通用工具
 │    └─ validate.js      通用表单验证
 ├── router
 │    └─ index.js         组件路由
 └── views                页面代码, 根据功能划分文件夹
```

### 前端代码规范

```code
template 代码中的 id, class 命名需要为脊柱命名法 如: `app-container` 
template 代码中组件的 props 传参 命名需要为小驼峰命名法 如: `okText`
template 使用自定义组件 需要为大驼峰命名法 如: `AppSelector`
template 中也需要加注释
template 代码禁止使用浮动布局
template 代码禁止使用魔法值判断, 需要在 data 中定义
template 避免一行代码过长, 属性过长需要换行

script 中的 name 必须要和文件名保持一致

data 中的属性必须为小驼峰命名法 如: `repoId`
data 中定义的查询对象需要吧参数声明出来并且定义为 undefined

method 中的方法必须为小驼峰命名法 如: `getAppList`
method 中对于状态, 类型等枚举字段的判断必须使用 enum.js 
method 中的方法逻辑需要加注释

enum.js 中对象必须为蛇形命名法且都是大写字母 如: `BUILD_STATUS` 

提交的表单需要使用 a-form 并且定义 decorator 
查询的表单需要使用 a-form-model 

尽量控制代码的行数以及重复代码的控制, 可以考虑抽象组件
逻辑复杂的地方需要添加注释
不要使用中文标点
注意方法和括号之间的空格
```

### 后端代码结构

```code
java com.orion.ops 
  ├── annotation           自定义注解
  ├── config               配置包
  ├── constant             常量包 根据不同的业务划分不同的子包, 通常存放枚举以及常量 
  ├── controller           contorller 层
  ├── dao                  dao 层
  ├── entity
  │    ├─ domain           数据库实体 其中的类需要与数据库字段一一对应 不允许多字段
  │    ├─ dto              业务实体 用于业务对象 如: redis 的 json 实体, 数据导入实体
  │    ├─ request          请求实体 用于前端请求参数
  │    └─ vo               展示实体 用于前端响应展示
  ├── handler              复杂业务逻辑包 如: 用于构建, 用于发布
  ├── interceptor          拦截器配置包
  ├── runner               系统启动 runner 配置, 如: 自动修改状态, 加载转换器, 清除 redis 无效 key
  ├── service
  │    ├─ api              应用服务接口定义
  │    └─ impl             业务服务接口实现
  ├── task
  │    ├─ fixed            固定定时任务 如: 数据统计
  │    └─ impl             自定义定时任务 如: 调度任务, 定时发布
  └── utils                工具包
  
resources
  ├── config                配置文件目录
  ├── mapper                mybatis mapper 文件存放目录
  ├── menu                  菜单 json 文件存放目录
  ├── static                静态文件存储目录
  └── templates         
       ├─ import            导入模板目录
       └─ push              推送模板目录
```

### 后端代码规范

```code
基本和阿里巴巴开发手册一致
class 命名必须为大驼峰命名法 如: AuthenticateInterceptor
普通字段命令必须为小驼峰命名法 如: reopId
静态常量字段命令必须为蛇形命名法 并且是全大写 如: REPO_PATH

禁止使用魔法值 
常量需要在 constant.Const 中配置
中文常量需要在 constant.CnConst 中配置
redis 的 key 以及过期时间需要在 constant.KeyConst 中配置
返回的错误信息需要在 constant.MessageConst 中配置
自定义返回响应码需要在 constant.ResultCode 中配置
自定义线程池需要在 constant.SchedulerPools 中配置
数据库枚举类型必须存在 constant 包下的业务子包内 并且需要定义 of(xx) 静态方法

推荐使用 orion-kit 工具类的代码, 如: Maps.newMap() Lists.newList() Strings.isBlank()

controller entity 必须补齐 swagger 注解
controller 中需要使用 @RestController @RestWrapper
controller 禁止使用 map 传参

注意业务是否需要写重启清理状态的 runner
使用 redis 时需要注意是否需要在 CacheKeyCleanRunner 删除

尽量控制代码的行数以及重复代码的控制
多表操作需要注意事务的控制

分页的请求对象必须继承 PageRequest
分页的响应类型必须为 DataGrid<T>
查询善用 DataQuery

参数校验需要使用 utils.Valid 方法
抛出异常需要用到 Exceptions 方法, 不要 new
普通类型的工具方法需要定义在 Utils 中

DAO DTO DO VO 对象的定义 后缀必须为纯大写
对象之间的转换需要 在对象静态代码块中定义  TypeStore.STORE.register

代码必须要有注释
如果字段是一个枚举时 需要使用 @see 枚举

CodeGenerator 为代码生成器
可以不用写单元测试
禁止使用 @Autowire 注入 bean, 只能使用 @Resource 注入
@RestWrapper 为自定义注解, 自动返回 HttpWrapper

不要使用中文标点
使用4个空格替代 tab
注意方法和括号之间的空格

代码文件头统一为
  /** 
   *
   * @author ${USER}
   * @version 1.0.0
   * @since ${DATE} ${TIME}
   */
```
