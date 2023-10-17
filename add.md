

## testAddReport


**接口地址**:`/api/mp/unauth/users/test_add_report`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
    "action": "add",
        "data": 
        {
            "pk": 111350,
            "id": "2ff778c6962e427e8bab8d2f7aee65a6",
            "createTime": "2023-10-16 11:52:16",
            "updateTime": "2023-10-16 11:52:16",
            "tenant": "default",
            "username": "test1109011",
            "tokenSecret": "0dff9c2ea7fe44ef90cae2c44bab551b",
            "name": "test1109o8",
            "nickname": "",
            "avatar": "/api/mp/file/953/304/c755d645862ae7c14850a33efebd24c4.png",
            "birthday": "2023-10-16",
            "email": "",
            "initCheck": 0,
            "description": "",
            "isEnabled": 1,
            "isAudited": 1,
            "dateJoined": "2023-10-16 11:52:16",
            "expiredTime": "2122-10-16 11:52:16",
            "lastLogin": "2023-10-16 11:52:16",
            "isExpired": 0,
            "isAdmin": 0,
            "isSuperUser": 0,
            "orgpaths": [
                [
                    "一级组织",
                    "对抗赛"
                ]
            ],
            "orgIds": [
                "61ef15b457fd459980b73db4d090de36"
            ],
            "orgs": [
                {
                    "pk": 2,
                    "id": "61ef15b457fd459980b73db4d090de36",
                    "createTime": "2023-05-06 16:05:19",
                    "updateTime": "2023-09-22 17:29:39",
                    "tenant": "default",
                    "parent": "a13262d600fe40c799d63dce5c1485af",
                    "identifier": "18d7842c73d1455da5b9d72801e7db68",
                    "name": "对抗赛",
                    "description": "",
                    "path": "一级组织/对抗赛"
                }
            ],
            "roles": [
                {
                    "pk": 48,
                    "id": "4199c0d5a3dd4a9592c92b0a4cddccf0",
                    "createTime": "2023-08-10 09:24:46",
                    "updateTime": "2023-08-10 09:24:46",
                    "tenant": "default",
                    "org": "default",
                    "identifier": "1689447664446705664",
                    "name": "11",
                    "description": ""
                }
            ]
        }
    
}
```

**请求参数**:


| 参数名称 | 参数说明 | 是否必须   | 数据类型 | schema |
| -------- | --------  |--------| -------- | ------ |
|&emsp;&emsp;action|动作, 值固定为 add|| true   |string||
|&emsp;&emsp;data|新增的用户列表|| true  |UserBo|UserBo|
|&emsp;&emsp;&emsp;&emsp;address|地址|| false  |string||
|&emsp;&emsp;&emsp;&emsp;avatar|头像|| false  |string||
|&emsp;&emsp;&emsp;&emsp;birthday|生日|| false  |string||
|&emsp;&emsp;&emsp;&emsp;create_time|创建时间|| true  |string||
|&emsp;&emsp;&emsp;&emsp;date_joined|加入时间|| true  |string||
|&emsp;&emsp;&emsp;&emsp;description|描述|| false  |string||
|&emsp;&emsp;&emsp;&emsp;email|邮箱|| false  |string||
|&emsp;&emsp;&emsp;&emsp;expired_time|过期时间|| false  |string||
|&emsp;&emsp;&emsp;&emsp;id|ID|| true  |string||
|&emsp;&emsp;&emsp;&emsp;id_card|身份证号|| false  |string||
|&emsp;&emsp;&emsp;&emsp;identifier|用户编号|| false  |string||
|&emsp;&emsp;&emsp;&emsp;init_check|是否首次登陆需要强制修改密码 0:否，1:是|| false  |integer||
|&emsp;&emsp;&emsp;&emsp;is_admin|是否是管理员 1：是 0：否|| false  |integer||
|&emsp;&emsp;&emsp;&emsp;is_audited|1：已审核 0：待审核|| false  |integer||
|&emsp;&emsp;&emsp;&emsp;is_enabled|1: 正常 0：禁用|| false  |integer||
|&emsp;&emsp;&emsp;&emsp;is_expired|1：已过期 0：未过期|| false  |integer||
|&emsp;&emsp;&emsp;&emsp;is_super_user|是否是超级用户 1：是 0：否|| false  |integer||
|&emsp;&emsp;&emsp;&emsp;last_login|最后登录时间|| false  |string||
|&emsp;&emsp;&emsp;&emsp;mobile|手机号|| false  |string||
|&emsp;&emsp;&emsp;&emsp;name|用户姓名|| true  |string||
|&emsp;&emsp;&emsp;&emsp;nickname|昵称|| true  |string||
|&emsp;&emsp;&emsp;&emsp;org_ids|组织id集合|| false  |array|string|
|&emsp;&emsp;&emsp;&emsp;orgpaths|组织路径|| false  |array|array|
|&emsp;&emsp;&emsp;&emsp;orgs|组织集合|| false  |array|OrgBO|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;create_time|创建时间|| false  |string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;description|描述|| false  |string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;id|ID|| true  |string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;identifier|编号|| false  |string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;name|名称|| false  |string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;parent|上级组织ID|| false  |string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;path|路径|| false  |string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;pk|自增ID(只读)|| true  |integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;tenant|租户|| false  |string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;update_time|更新时间|| false  |string||
|&emsp;&emsp;&emsp;&emsp;password|密码|| false  |string||
|&emsp;&emsp;&emsp;&emsp;pk|主键|| true  |integer||
|&emsp;&emsp;&emsp;&emsp;roles|角色集合|| false  |array|RoleModel|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;create_time|创建时间|| false  |string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;description|描述|| false  |string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;id|ID|| true  |string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;identifier|编号|| false  |string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;name|名称|| true  |string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;org|所属组织|| false  |string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;pk|自增ID(只读)|| true  |integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;tenant|租户|| false  |string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;update_time|更新时间|| false  |string||
|&emsp;&emsp;&emsp;&emsp;tenant|租户|| false  |string||
|&emsp;&emsp;&emsp;&emsp;update_time|更新时间|| false  |string||
|&emsp;&emsp;&emsp;&emsp;username|用户名|| false  |string||


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