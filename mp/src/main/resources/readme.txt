1. application-std.yml 为标准配置模板，application-ext.yml为从std文件中提取得外部配置变量，如果需要为某一个变量配置外部变量，如：
   mp:
    log:
     log-db: ${MP_DEVOPS_LOG_DB:sql} #日志存储数据库可以配置：es、sql
   可以在log下面增加一行ext-var: 然后将需要绑定到外部得变量拷贝一份放置ext-var下一级，然后在原有变量中引用外部变量（解耦防腐作用），如下：
   mp:
     log:
       ext-var:
        log-db: ${MP_DEVOPS_LOG_DB:sql}
       log-db: ${mp.log.ext-var.log-db} #日志存储数据库可以配置：es、sql

2. 开发环境(dev)模板默认为 application-std.yml 和 application-ext.yml组合配置
3. application-prop.properties.template为外部短变量模板，application-ext.properties.template为yml外部变量properties格式
4. 对外部署启动时外部配置参数，可以此为模板设置实际参数（敏感信息最好不配置到外部），启动时加入spring.config.location=配置文件路径完整路径（文件名可以自定义命名）。如下：
   1).MP_ENV_PATH方式配置外部变量(文件模板为application-prop.properties.template)
      java -DMP_ENV_PATH=D:/WORKDIR/SNProject/mp.properties -jar mp-0.0.1.jar  --spring.profiles.active=prod

   2).application-ext.properties.template板配置外部变量（配置文件名字只能为application-ext.properties）, 该配置文件是application-ext.yml的properties形式
      java -jar mp-0.0.1.jar --spring.config.additional-location=D:/WORKDIR/SNProject/application-ext.properties --spring.profiles.active=prod

   3).application-ext.yml模板配置外部变量（配置问下名字只能为application-ext.yml）
      java -jar mp-0.0.1.jar --spring.config.additional-location=D:/WORKDIR/SNProject/application-ext.yml --spring.profiles.active=prod

5. 原有MP_ENV_PATH模式加载外部变量方式暂时保留一段时间，后续可能不再支持。