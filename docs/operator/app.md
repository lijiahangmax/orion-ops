### 环境管理

配置应用环境, 如: 开发环境、测试环境、生产环境, 从而做到应用在不同环境的有不同的配置, 操作相互隔离。

### 应用列表

管理和维护应用, 每个应用在不同的环境都可以有不同的配置。

* 配置: 配置应用的基本信息, 构建操作, 发布操作
* 构建: 执行应用构建操作
* 发布: 创建应用发布任务 `CI/CD` > `应用发布`
* 同步: 同步构建发布配置到其他环境
* 复制: 复制当前应用, 会复制应用的环境变量, 构建配置, 发布配置

### 应用配置

配置应用的基本信息, 以及不同环境下的构建与发布配置。  
配置时 `机器环境变量` `系统环境变量` `应用环境变量` 都可以在命令执行前进行替换。  
⚡ 注意: 如果不配置应用的构建与发布配置, 则不能进行构建 (CI) 和发布 (CD) 操作

应用在配置后系统会将部分配置信息存储到应用环境变量中。

| key                | 示例                      | 描述                                          |
| :----              | :---                     | :----                                         |
| bundle_path        | target/build.jar         | 宿主机构建产物路径 (绝对路径/基于版本仓库的相对路径) |
| transfer_path      | /data/projects/demo.jar  | 产物传输目标机器绝对路径                          |
| transfer_mode      | sftp                     | 产物传输方式 (sftp/scp)                         |
| transfer_file_type | normal                   | 产物传输文件类型 (normal/zip)                    |
| release_serial     | parallel                 | 发布序列方式 (serial/parallel)                  |
| exception_handler  | skip_all                 | 异常处理类型 (skip_all/skip_error)              |

<br/>  

> ##### 构建配置

设计思路: 应用在发布之前需要先由宿主机进行构建, 生成一个可发布的产物, 发布时需要基于该产物进行发布操作。

`构建产物路径` 就是构建生成的产物文件路径, 这个路径可以是宿主机的`绝对路径` 或 基于版本仓库的`相对路径` (如果配置了版本仓库)。

* vue 项目构建后会生成一个 dist 文件夹, 这个路径就应该是 `dist`  
  如果构建完成后执行用 tar 执行压缩命令, 将 dist 文件夹压缩为 dist.tar 这里的路径就是 `dist.tar`
* spring boot 项目构建后会生成一个可执行的 jar 文件, 这个路径就应该是 `target/xxx.jar`

* 可能还会有其他更复杂的情况, 如: 构建完成后会在 a 文件夹生成一个文件, 在 b 文件夹生成一个文件夹。  
  这之后就应该在构建完成后执行 `mv` 命令, 移动到同一个文件夹, `构建产物路径` 就应该设置为移动的这个文件夹

* 如果构建完成后不会生成产物, 或者已经将文件传输过了, 这时候这个路径就随便设置一个存在的文件即可

⚡ 注意: 如果产物路径是一个文件夹, 构建完成后会自动将次文件夹进行 `zip` 压缩, 如设置的路径为 `dist` 则会生成一个 `dist.zip`  
⚡ 注意: 构建产物路径中不能包含 `\` 应该用 `/` 替换。  
⚡ 注意: 执行结果的成功与否是通过执行命令的 `exitcode` 是否为 `0` 来判断的。

如果应用配置了版本仓库, 会有 `添加检出操作` 这个选项 (git clone)。

构建时系统提供了几个默认的变量, 在构建操作的命令中可以使用 `@{build.xxx}` 来替换。

| key             | 示例                                     | 描述                                       |
| :----           | :---                                     | :----                                     |
| build_id        | 10                                       | 构建id (数据库自增)                         |
| build_seq       | 1                                        | 当前环境该应用的构建序列                     |
| vcs_home        | /orion/vcs/2/10                          | 当前应用配置的版本仓库的 `clone` 目录 (如果有) |
| vcs_event_home  | /orion/vcs/event/2                       | 版本仓库的 (获取分支/commit) 目录 (只有一个)  |
| branch          | origin/master                            | 构建所选的 `branch`                        |
| commit          | 8ab50bada8525f6670c36114ad46baa70efda820 | 构建所选的 `commit`                        |
| bundle_path     | /root/orion_ops/dist/build/128/dist      | 构建完成后产物存储路径                       |
| bundle_zip_path | /root/orion_ops/dist/build/128/dist.zip  | 构建完成后产物 zip文件 存储路径 (如果有)      |

<br/>   

> ##### 发布配置

设计思路: 发布的应用可能是以集群的形式发布的, 可能会有多台机器同时发布一个应用, 基于选择的构建版本, 进行发布操作。

`文件传输方式` 可选择使用 `sftp` / `scp` 来分发产物文件。      
`文件传输路径` 是将选择的构建版本生成的产物分发到发布机器后绝对路径。

如果构建产物是一个文件 `build.jar`  
文件传输类型选择 `文件/文件夹` 文件传输路径可以配置为 `/data/projects/demo.jar`

如果构建产物是一个文件夹 `dist`  
当 文件传输类型 选择 `文件/文件夹` 文件传输路径 可以配置为 `/data/projects/dist`     
当 文件传输类型 选择 `zip` 文件传输路径 可以配置为 `/data/projects/dist.zip` 然后在进行解压。

当 `产物传输方式` 选择 `scp` 时可配置 `scp 传输命令`  
默认命令为 `scp "@{bundle_path}" @{target_username}@@{target_host}:"@{transfer_path}"`, 使用 `@{xxx}` 替换变量。

| key             | 示例                                          | 描述                                 |
| :----           | :---                                         | :----                                |
| bundle_path     | /root/orion_ops/dist/build/128/build.jar     | 构建完成后产物存储路径                  |
| transfer_path   | /data/projects/demo.jar                      | 产物传输路径                           |  
| target_username | root                                         | 目标机器用户                           |
| target_host     | 192.168.5.65                                 | 目标机器主机                           |  

⚡ **这里一定要注意**: 文件传输方式选择 `SFTP` 后, 当执行传输操作时, 会先**删除**文件传输路径再进行传输操作  
**配置不正确会导致数据误删除!!!**  
这里更推荐使用 `scp` 的方式来传输产物文件, 速度更快, 以命令的形式配置, 更加灵活。

```
当然这里也可以写死, 以上述例子命令执行时会替换为
scp "/root/orion_ops/dist/build/128/build.jar" root@192.168.5.65:"/data/projects/demo.jar"
```

<br/>  

应用发布时系统提供了几个默认的变量, 在发布操作的命令中可以使用 `@{release.xxx}` 来替换。

| key            | 示例                                        | 描述                         | 
| :----          | :---                                       | :----                        |
| build_id       | 128                                        | 发布所选的构建版本的id          |
| build_seq      | 10                                         | 发布所选的构建版本的序列         |
| branch         | origin/master                              | 发布所选的构建版本的 `branch`   |
| commit         | 8ab50bada8525f6670c36114ad46baa70efda820   | 发布所选的构建版本的 `commit`   |
| bundle_path    | /root/orion_ops/dist/build/128/build.jar   | 发布所选的构建版本的产物路径     |
| release_id     | 10                                         | 发布id                        |
| release_title  | 发布应用                                    | 发布标题                       |
| transfer_path  | /data/projects/demo.jar                    | 产物传输路径                   |

<br/>


⚡ 注意: 产物传输路径中不能包含 `\` 应该用 `/` 替换。  
⚡ 注意: 产物传输方式选择 `scp` 需要建立宿主机与目标机器 `ssh` 免密登录。  
⚡ 注意: 执行 `scp` 命令 transfer_path 如果包含空格执行时会自动转义。  
⚡ 注意: 执行结果的成功与否是通过执行命令的 `exitcode` 是否为 `0` 来判断的。

### 流水线配置

设计思路: 以流水线的形式执行应用构建及发布, 一个流水线任务同时执行项目的所有模块的构建以及发布。

* 执行: 创建流水线任务  `CI/CD` > `流水线任务`
* 配置: 配置流水线执行操作

⚡ 只有 `已配置` 的应用才可以设置流水线操作。

### 环境变量

配置不同环境的应用环境变量, 不同环境之间的变量是相互隔离的。  
系统提供了几个默认的应用变量, 执行命令时使用 `@{app.xxx}` 来替换。

| key          | 示例       | 描述       |
| :----        | :---      | :----      |
| app_id       | 1         | 应用id     |
| app_name     | 订单服务   | 应用名称    |
| app_tag      | order     | 应用标签    |
| profile_id   | 1         | 应用环境id  |
| profile_name | 开发环境   | 应用环境名称 |
| profile_tag  | dev       | 应用环境标签 |

> ##### 示例

```
# 当前应用 名称: 订单服务
# 当前应用环境 名称: 开发环境 tag: dev
# 应用环境变量 xmx: 128m    xms: 64m

# 执行命令
echo @{app.app_name}
echo @{app.profile_name}
java -jar demo.jar --spring.profiles.active=@{app.profile_tag} -Xmx@{app.xmx} -Xms@{app.xms}

# 命令在执行前会被替换为
echo 订单服务
echo 开发环境
java -jar demo.jar --spring.profiles.active=dev  -Xmx128m -Xms64m
```

⚡ 注意: 切换视图后保存只会增量保存, 并不会删除变量

### 版本仓库

配置应用的版本仓库, 仅支持 `git`, 用于构建时选择构建的 `branch` 以及 `commit`。   
可以通过 `密码` 或 `令牌` 方式导入仓库。

* 初始化: 初始化版本仓库, 验证密码或者token `vcs_event_home`
* 重新初始化: 重新初始化版本仓库 `vcs_event_home`

[comment]: <> (* 清空: 清空应用构建历史版本, 会保留两个版本, 防止清空正在进行中的构建任务)
