1. application-std.yml 为标准配置模板；
2. 开发环境(dev)模板默认为 application-std.yml
3. 对外部署启动时外部配置参数，可以此为模板设置实际参数（敏感信息最好不配置到外部），启动时加入spring.config.location=配置文件路径完整路径（文件名可以自定义命名）。如下：
   java -jar mp-0.0.1.jar --spring.config.location=D:\WORKDIR\SNProject\mp\mp\src\main\resources\application-std.yml --spring.profiles.active=std
4. 原有properties模式加载外部变量方式暂时保留一段时间，后续可能不再支持。