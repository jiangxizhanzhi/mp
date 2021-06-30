# mp

### 分层领域模型规约

- Model(Data Object):与数据库表结构一一对应，通过 DAO 层向上传输数据源对象。
- Request 数据查询对象，各层接收上层的查询请求。注:超过 2 个参数的查询封装，禁止 使用 Map 类来传输。
- Result 显示层对象，通常是 Web 向模板渲染引擎层传输的对象。

### 运行时异常规约
  遇到业务异常，捕获的其它Checked Exception,可以直接throw MpRuntimeException, MpRuntimeExceptionHandler会统一处理异常信息

示例

log.error 打出栈位置，具体栈信息也可以抛个前端方便后期定位问题，MpRuntimeExceptionHandler统一处理这类取消

```java
try {
    httpResponse =  HttpUtils.sendGetDownLoadWithDigestAuth(molochUrl,
        sandTableConfig.getUserName(), sandTableConfig.getPassword());
} catch (IOException e) {
    log.error("查询缓存异常 groupId {}" + e, groupId);
    throw new MpRuntimeException("查询缓存异常 groupId: "+ groupId);
}
```

[wazuh安装]: wazuh安装.md	"wazuh安装"

