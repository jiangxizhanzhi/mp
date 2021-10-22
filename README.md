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



### 注释

1. 类的注释模板
```java
/**
* @Description 
* @Author 提交人名字拼音
* @Date ${DATE}
*/
```
2. 字段注释模板
3. 方法注释模板

### 错误码统一格式
1. 错误码由前缀 MP- 加6位数字组成，形如 MP-123456。
2. 通用错误码为 MP-0***** ,其中 MP-000000 表示成功， MP-099999 表示未知异常。 
3. 其余错误码必须以 MP-11 开头。为方便各模块自定义错误码，约定从MP-110000开始每100个错误码为一个区间，每个模块的错误码按照区间进行划分。 
目前划分的情况如下：

   | 错误码实现类           | 区间       | 备注       |
   | ----------           | ----      | ---        |
   | BaseErrorCode        | MP-0***** |  通用错误码  |
   | AuthErrorCode        | MP-1100** |            |
   | FastDfsErrorCode     | MP-1101** |            |
   | CsceneErrorCode      | MP-1102** |            |
   | FlowErrorCode        | MP-1103** |            |
   | SceneErrorCode       | MP-1104** |            |
   | SituationalAwareErrorCode       | MP-1105** |            |
   | DevopsClusterErrorCode       | MP-1106** |            |
4. 建议错误码的定义可以尽量连续。如果部分模块错误码不足，可以再分配一个区间。
   

