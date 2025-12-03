# Ubuntu 编译与运行环境的设置

## 流媒体服务器
> 更新包列表
- `sudo apt-get update`
> 安装 gcc  
- `sudo apt-get install build-essential`  
> 安装 cmake  
- `sudo apt-get install cmake`  
> 安装依赖库
- `sudo apt-get install libssl-dev`  
- `sudo apt-get install ffmpeg`
> 开始构建和编译项目
- `git submodule update --init` // 拉取StreamServer的依赖
- `mkdir build`
- `cd build`
- `cmake ..` 或者 `cmake .. -DCMAKE_BUILD_TYPE=Release` 构建release版本
- `make -j4`
> 运行
- `cd streamserver/release/linux/Debug`
- 配置信息在/streamserver_config目录中，建议拷贝到Debug下并改名成config.ini，或者使用`-c`指定配置文件位置。几个端口为（**记得在防火墙中打开**）
  - rtmp: 1935  
  - rtp: 30000-35000  
  - rtsp: 554  
- `./StreamServer -h` // 通过-h可以了解启动参数
- `sudo ./StreamServer -d &` // 以守护进程模式启动

## 录像辅助
> 开始构建和编译项目
- 进入vbaseassist项目, 执行`mvn package`

## GB28181 平台
> 安装依赖  

| 依赖项 | 版本 | 开发环境需要 | 生产环境需要 |
| ----- | -----| :-----------: | :-----------: |
| jdk   | >= 1.8 | 是 | 是 |
| maven | >= 3.3 | 是 | 否 |
| intelij | lts  | 是 | 否 |
| nodejs | lts | 是 | 否 |
| npm | lts | 是 | 否 |
| mysql | lts | 是 | 是 |
| redis | lts | 是 | 是 |

> 编译前端页面
- 安装nodejs以及npm: `curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash - && sudo apt-get install -y nodejs`
- `cd gb28181/web_src`
- `npm --registry=https://registry.npm.taobao.org install`
- `npm run build`
- 编译完成后在src/main/resources下出现static目录

> 通过命令行编译后端
- `cd gb28181`
- 如果没有maven则先安装`sudo apt install maven`
- `mvn package`
- 编译完成后在target目录下出现vbase-gb28181.jar

> 配置 vbase  
- 基于spring boot的开发方式，配置文件的加载是很灵活的。默认在src/main/resources/application.yml，部分配置项是可选，你不需要全部配置在配置文件中， 完全的配置说明可以参看all-application.yml。
- 使用maven打包后的jar包里，已经存在了配置文件，但是每次打开jar包修改配置文件或者修改后再打包都是比较麻烦的，所以大家可通过指定配置文件路径(`java -jar vbase-gb28181.jar --spring.config.location={指定位置}`)来加载指定位置的配置文件。

> 配置 `mysql`
- 首先需要创建一个名为`vbase`的数据库，并使用sql/mysql.sql导入数据库，初始化数据库结构
- 然后在application.yml中配置数据库连接，包括数据库连接信息，密码。

> 配置 `redis`
- 在application.yml中配置`redis`的连接信息

> 配置服务启动端口（可直接使用默认配置）
```
# [可选] Vbase监听的HTTP端口, 网页和接口调用都是这个端口
server:
  port: 18080
```
> 配置 `GB28181` 相关信息（可直接使用默认配置）
```
# 作为28181服务器的配置
sip:
    # [必须修改] 本机的IP
    ip: 192.168.1.3
    # [可选] 28181服务监听的端口，建议更换，否则在公网容易被攻击
    port: 5060
    # 根据国标6.1.2中规定，domain宜采用ID统一编码的前十位编码。国标附录D中定义前8位为中心编码（由省级、市级、区级、基层编号组成，参照GB/T 2260-2007）
    # 后两位为行业编码，定义参照附录D.3
    # [可选]
    domain: 3101100099
    # [可选]
    id: 31011000992016000022
    # [可选] 默认设备认证密码，后续扩展使用设备单独密码, 移除密码将不进行校验
    password: 12345678
```
> 配置StreamServer连接信息
```
#StreamServer 默认服务器配置
media:
    # ZStreamServer的服务ID，必须配置
    id: AD2SS7wC99ek4Mdr
    # [必须修改] StreamServer服务器的内网IP，sdp-ip与stream-ip使用默认值的情况下，这里不要使用127.0.0.1/0.0.0.0
    ip: 192.168.1.3
    # [必须修改] StreamServer服务器的http.port
    http-port: 6080
    # [可选] StreamServer服务器的hook.admin_params=secret
    secret: ec241e72-1f0b-4a79-a4e8-f5cd01495da7
    # 启用多端口模式, 多端口模式使用端口区分每路流，兼容性更好。 单端口使用流的ssrc区分， 点播超时建议使用多端口测试
    rtp:
        # [可选] 是否启用多端口模式, 开启后会在portRange范围内选择端口用于媒体流传输
        enable: true
        # [可选] 在此范围内选择端口用于媒体流传输,
        port-range: 30000,30500 # 端口范围
        # [可选] 国标级联在此范围内选择端口发送媒体流,
        send-port-range: 30000,30500 # 端口范围
    # 录像辅助服务， 部署此服务可以实现StreamServer录像的管理与下载， 0 表示不使用
    record-assist-port: 18081
```
> 个性化定制信息配置
```
# [根据业务需求配置]
user-settings:
    # [可选] 服务ID，不写则为000000
    server-id:
    # [可选] 自动点播， 使用固定流地址进行播放时，如果未点播则自动进行点播, 需要rtp.enable=true
    auto-apply-play: false
    # [可选] 部分设备需要扩展SDP，需要打开此设置
    senior-sdp: false
    # 保存移动位置历史轨迹：true:保留历史数据，false:仅保留最后的位置(默认)
    save-position-history: false
    # 点播等待超时时间,单位：毫秒
    play-timeout: 3000
    # 等待音视频编码信息再返回， true： 可以根据编码选择合适的播放器，false： 可以更快点播
    wait-track: false
    # 是否开启接口鉴权
    interface-authentication: true
    # 自动配置redis 可以过期事件
    redis-config: true
    # 接口鉴权例外的接口, 即不进行接口鉴权的接口,尽量详细书写，尽量不用/**，至少两级目录
    interface-authentication-excludes:
        - /api/v1/**
    # 推流直播是否录制
    record-push-live: true
    # 国标是否录制
    record-sip: true
    # 是否将日志存储进数据库
    logInDatebase: true
    # 第三方匹配，用于从stream钟获取有效信息
    thirdPartyGBIdReg: [\s\S]*
```
> 产线运行
- ~~`nohup java -jar vbase-gb28181.jar --spring.config.location={指定位置} &`~~
- 安装PM2 `npm install -g pm2`
- 安装PM2 logrorate `pm2 install pm2-logrotate`, 修改相应配置文件`/root/.pm2/module_conf.json`
- 确保jar包和配置文件都在/gb28181/target目录中
- 启动vbase
    - 根据实际情况，修改`vbase-pm2.json`中的参数，主要是路径和文件名
    - 运行`pm2 start .\vbase-pm2.json`
- 启动StreamServer
    - 根据实际情况，修改`streamserver-pm2.json`中的参数，主要是路径和文件名
    - 运行`pm2 start .\streamserver-pm2.json`
- 启动vbase-assist
    - vbase-assist必须和StreamServer跑在同一环境（OS）中
    - 根据实际情况，修改`vbase-assist-pm2.json`以及`application.yml`中的配置，主要是userSettings.record，填写正确的录像文件目录
    - 运行`pm2 start .\vbase-assist-pm2.json`
- 停止服务 `pm2 stop {name}`
> 本地调试
- 使用`intelij`启动`vbase`服务器端, 记得指定`-Dspring.profiles.active=dev`或者通过`--spring.config.location={指定位置}`直接指定配置文件
- 进入`/gb28181/web_src`目录，命令行众执行`npm run dev`, 如果遇到错误，试一下`npm update`更新调试开发依赖包
- 通过`localhost:8080`访问页面，后端请求会proxy到`18080`端口
> Swagger
- url: http://localhost:18080/doc.html
- 视频播放: http://localhost:18080/doc.html#/1.%20%E5%85%A8%E9%83%A8/%E5%9B%BD%E6%A0%87%E8%AE%BE%E5%A4%87%E7%82%B9%E6%92%AD/play_1
- 录像列表: http://localhost:18080/doc.html#/1.%20%E5%85%A8%E9%83%A8/%E5%9B%BD%E6%A0%87%E5%BD%95%E5%83%8F/recordinfo
- 录像播放: http://localhost:18080/doc.html#/1.%20%E5%85%A8%E9%83%A8/%E8%A7%86%E9%A2%91%E5%9B%9E%E6%94%BE/play
- 已拉流列表: http://localhost:18080/doc.html#/1.%20%E5%85%A8%E9%83%A8/%E6%9C%8D%E5%8A%A1%E6%8E%A7%E5%88%B6/getMediaLoad
- 云台控制: http://localhost:18080/doc.html#/1.%20%E5%85%A8%E9%83%A8/%E4%BA%91%E5%8F%B0%E6%8E%A7%E5%88%B6/ptz

> 模拟器操作指南
```
The GB28181 device is a console application.
Windows: to run the server, simply type "gb28181device".
Linux: to run the server, type "./start.sh", on linux platform, GB28181 device run as deamon by default.

GB28181 device supports the following command line options:
-c config specify the configuration file
-c option specifies the configuration file,if not specified, the default configuration config.xml is used.
-l [device|videodevice|audiodevice|window]
-l device list available video and audio capture device
-l videodevice list available video capture device
-l audiodevice list available audio capture device
-l window list available application window
```