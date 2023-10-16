

## testLogoutReport


**接口地址**:`/api/mp/unauth/users/test_logout_report`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
    "action": "logout", 
    "data": "test1109010"
}
```


**请求参数**:


**请求参数**:


| 参数名称 | 参数说明            | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- |-----------------| ----- | -------- | -------- | ------ |
|info| info            |body|true|用户退出登录通知|用户退出登录通知|
|&emsp;&emsp;action| 动作, 值固定为 logout ||false|string||
|&emsp;&emsp;data| 退出登录的用户名        ||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ServiceResult|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|错误码|string||
|data|响应数据|object||
|detail|错误详细说明|string||
|message|错误说明|string||
|success||boolean||
|timestamp|服务器时间UTC毫秒|integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": "",
	"data": {},
	"detail": "",
	"message": "",
	"success": true,
	"timestamp": 0
}
```