-- 节点信息增加漏洞信息记录
ALTER TABLE mp.mp_sceneplugincloud_scene_element ADD leaks LONGVARCHAR not null default '' ;
ALTER TABLE mp.mp_sceneplugincloud_scene_config_element ADD leaks LONGVARCHAR not null default '';
ALTER TABLE mp.mp_devops_device add leaks LONGVARCHAR not null default '';
ALTER TABLE mp.mp_devops_target add leaks LONGVARCHAR not null default '';

update mp.mp_devops_device set leaks = '[
                                     	{
                                     		"name": "CVE-2019-12860",
                                     		"version": "S-CMS PHP v3.0",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "Weblogic-SSRF漏洞",
                                     		"version": "10.0.2.0, 10.3.6.0",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "泛微E-Cology Xml外部实体注入漏洞",
                                     		"version": "泛微 EC 9.x (补丁版本 < 10.58.2) 和泛微 EC 8.x (补丁版本 < 10.58.2)",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "CVE-2018-8120",
                                     		"version": "Windows Server 2008 for x64-based Systems Service Pack 2",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "通达OA v11.9 getdata 任意命令执行漏洞",
                                     		"version": "通达OA v11.9",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "CVE-2023-42442 JumpServer未授权访问漏洞",
                                     		"version": "3.0.0 <= JumpServer <= 3.6.3",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "MongoDB 未授权访问漏洞",
                                     		"version": "所有版本",
                                     		"severity": "中等"
                                     	},
                                     	{
                                     		"name": "服务弱口令",
                                     		"version": "所有版本",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "口令爆破获取sqlserver账户，发现SQL Server上能启用CLR组件并可以创建自定义存储过程",
                                     		"version": "所有版本",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "CVE-2021-44228+脏牛提权，Apache Log4j远程代码执行漏洞",
                                     		"version": "Apache log4j2 2.0 - 2.14.1 版本 Centos7/RHEL7 3.10.0-327.36.3.el7",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "Apache Kafka 远程代码执行漏洞 CVE-2023-25194",
                                     		"version": "2.4.0 <= Apache kafka <= 3.3.2",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "僵尸网络内攻击者可以控制受害机发送大量数据包，形成拒绝服务攻击",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "拿到工控系统主机权限后由于工控系统未加密，篡改工程文件中的程序",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "利用探测工具未发现内网存在防火墙等网络安全设备",
                                     		"severity": "严重"
                                     	}
                                     ]'
                                     where name = '服务器';
update mp.mp_devops_device set leaks = '[
                                         {
                                             "name": "CNVD-2021-27989",
                                             "severity": "严重",
                                             "version": "Google Chrome <= 89.0.4389.114"
                                         },
                                         {
                                             "name": "主机未及时安装系统补丁，未开启防火墙，未启动Windows Defender",
                                             "severity": "严重",
                                             "version": ""
                                         },
                                         {
                                             "name": "网站服务器拿到命令执行权限后篡改首页信息",
                                             "severity": "严重",
                                             "version": ""
                                         },
                                         {
                                             "name": "内网主机未将网关254的地址与mac地址静态绑定，可进行arp欺骗",
                                             "severity": "严重",
                                             "version": ""
                                         },
                                         {
                                             "name": "通过抓包工具发现内网存在http请求，http协议未经过认真和加密，容易受到DNS毒化",
                                             "severity": "严重",
                                             "version": ""
                                         }
                                     ]'
                                     where name = 'PC';
update mp.mp_devops_device set leaks = '[
                                         {
                                             "name": "主机开放445端口",
                                             "severity": "严重",
                                             "version": ""
                                         },
                                         {
                                             "name": "MongoDB 未授权访问漏洞",
                                             "severity": "中等",
                                             "version": ""
                                         },
                                         {
                                             "name": "服务弱口令",
                                             "severity": "严重",
                                             "version": ""
                                         }
                                     ]'
                                     where name like '%数据库%';
update mp.mp_devops_device set leaks = '[
                                         {
                                             "name": "MongoDB 未授权访问漏洞",
                                             "severity": "中等",
                                             "version": ""
                                         }
                                     ]'
                                     where name like '%OPC%';



update mp.mp_devops_target set leaks = '[
                                     	{
                                     		"name": "CVE-2019-12860",
                                     		"version": "S-CMS PHP v3.0",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "Weblogic-SSRF漏洞",
                                     		"version": "10.0.2.0, 10.3.6.0",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "泛微E-Cology Xml外部实体注入漏洞",
                                     		"version": "泛微 EC 9.x (补丁版本 < 10.58.2) 和泛微 EC 8.x (补丁版本 < 10.58.2)",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "CVE-2018-8120",
                                     		"version": "Windows Server 2008 for x64-based Systems Service Pack 2",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "通达OA v11.9 getdata 任意命令执行漏洞",
                                     		"version": "通达OA v11.9",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "CVE-2023-42442 JumpServer未授权访问漏洞",
                                     		"version": "3.0.0 <= JumpServer <= 3.6.3",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "MongoDB 未授权访问漏洞",
                                     		"version": "所有版本",
                                     		"severity": "中等"
                                     	},
                                     	{
                                     		"name": "服务弱口令",
                                     		"version": "所有版本",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "口令爆破获取sqlserver账户，发现SQL Server上能启用CLR组件并可以创建自定义存储过程",
                                     		"version": "所有版本",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "CVE-2021-44228+脏牛提权，Apache Log4j远程代码执行漏洞",
                                     		"version": "Apache log4j2 2.0 - 2.14.1 版本 Centos7/RHEL7 3.10.0-327.36.3.el7",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "Apache Kafka 远程代码执行漏洞 CVE-2023-25194",
                                     		"version": "2.4.0 <= Apache kafka <= 3.3.2",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "僵尸网络内攻击者可以控制受害机发送大量数据包，形成拒绝服务攻击",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "拿到工控系统主机权限后由于工控系统未加密，篡改工程文件中的程序",
                                     		"severity": "严重"
                                     	},
                                     	{
                                     		"name": "利用探测工具未发现内网存在防火墙等网络安全设备",
                                     		"severity": "严重"
                                     	}
                                     ]'
                                     where name = '服务器';
update mp.mp_devops_target set leaks = '[
                                         {
                                             "name": "CNVD-2021-27989",
                                             "severity": "严重",
                                             "version": "Google Chrome <= 89.0.4389.114"
                                         },
                                         {
                                             "name": "主机未及时安装系统补丁，未开启防火墙，未启动Windows Defender",
                                             "severity": "严重",
                                             "version": ""
                                         },
                                         {
                                             "name": "网站服务器拿到命令执行权限后篡改首页信息",
                                             "severity": "严重",
                                             "version": ""
                                         },
                                         {
                                             "name": "内网主机未将网关254的地址与mac地址静态绑定，可进行arp欺骗",
                                             "severity": "严重",
                                             "version": ""
                                         },
                                         {
                                             "name": "通过抓包工具发现内网存在http请求，http协议未经过认真和加密，容易受到DNS毒化",
                                             "severity": "严重",
                                             "version": ""
                                         }
                                     ]'
                                     where name = 'PC';
update mp.mp_devops_target set leaks = '[
                                         {
                                             "name": "主机开放445端口",
                                             "severity": "严重",
                                             "version": ""
                                         },
                                         {
                                             "name": "MongoDB 未授权访问漏洞",
                                             "severity": "中等",
                                             "version": ""
                                         },
                                         {
                                             "name": "服务弱口令",
                                             "severity": "严重",
                                             "version": ""
                                         }
                                     ]'
                                     where name like '%数据库%';
update mp.mp_devops_target set leaks = '[
                                         {
                                             "name": "MongoDB 未授权访问漏洞",
                                             "severity": "中等",
                                             "version": ""
                                         }
                                     ]'
                                     where name like '%OPC%';

update mp.mp_sceneplugincloud_scene_config_element set leaks = '[
                                                                                                  	{
                                                                                                  		"name": "CVE-2019-12860",
                                                                                                  		"version": "S-CMS PHP v3.0",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "Weblogic-SSRF漏洞",
                                                                                                  		"version": "10.0.2.0, 10.3.6.0",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "泛微E-Cology Xml外部实体注入漏洞",
                                                                                                  		"version": "泛微 EC 9.x (补丁版本 < 10.58.2) 和泛微 EC 8.x (补丁版本 < 10.58.2)",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "CVE-2018-8120",
                                                                                                  		"version": "Windows Server 2008 for x64-based Systems Service Pack 2",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "通达OA v11.9 getdata 任意命令执行漏洞",
                                                                                                  		"version": "通达OA v11.9",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "CVE-2023-42442 JumpServer未授权访问漏洞",
                                                                                                  		"version": "3.0.0 <= JumpServer <= 3.6.3",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "MongoDB 未授权访问漏洞",
                                                                                                  		"version": "所有版本",
                                                                                                  		"severity": "中等"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "服务弱口令",
                                                                                                  		"version": "所有版本",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "口令爆破获取sqlserver账户，发现SQL Server上能启用CLR组件并可以创建自定义存储过程",
                                                                                                  		"version": "所有版本",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "CVE-2021-44228+脏牛提权，Apache Log4j远程代码执行漏洞",
                                                                                                  		"version": "Apache log4j2 2.0 - 2.14.1 版本 Centos7/RHEL7 3.10.0-327.36.3.el7",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "Apache Kafka 远程代码执行漏洞 CVE-2023-25194",
                                                                                                  		"version": "2.4.0 <= Apache kafka <= 3.3.2",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "僵尸网络内攻击者可以控制受害机发送大量数据包，形成拒绝服务攻击",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "拿到工控系统主机权限后由于工控系统未加密，篡改工程文件中的程序",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "利用探测工具未发现内网存在防火墙等网络安全设备",
                                                                                                  		"severity": "严重"
                                                                                                  	}
                                                                                                  ]'
                                                                                                   where scene_element in (
select node_id from mp.mp_scene_scene_config_element where name like '%服务器%');

update mp.mp_sceneplugincloud_scene_element set leaks = '[
                                                                                                  	{
                                                                                                  		"name": "CVE-2019-12860",
                                                                                                  		"version": "S-CMS PHP v3.0",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "Weblogic-SSRF漏洞",
                                                                                                  		"version": "10.0.2.0, 10.3.6.0",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "泛微E-Cology Xml外部实体注入漏洞",
                                                                                                  		"version": "泛微 EC 9.x (补丁版本 < 10.58.2) 和泛微 EC 8.x (补丁版本 < 10.58.2)",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "CVE-2018-8120",
                                                                                                  		"version": "Windows Server 2008 for x64-based Systems Service Pack 2",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "通达OA v11.9 getdata 任意命令执行漏洞",
                                                                                                  		"version": "通达OA v11.9",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "CVE-2023-42442 JumpServer未授权访问漏洞",
                                                                                                  		"version": "3.0.0 <= JumpServer <= 3.6.3",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "MongoDB 未授权访问漏洞",
                                                                                                  		"version": "所有版本",
                                                                                                  		"severity": "中等"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "服务弱口令",
                                                                                                  		"version": "所有版本",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "口令爆破获取sqlserver账户，发现SQL Server上能启用CLR组件并可以创建自定义存储过程",
                                                                                                  		"version": "所有版本",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "CVE-2021-44228+脏牛提权，Apache Log4j远程代码执行漏洞",
                                                                                                  		"version": "Apache log4j2 2.0 - 2.14.1 版本 Centos7/RHEL7 3.10.0-327.36.3.el7",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "Apache Kafka 远程代码执行漏洞 CVE-2023-25194",
                                                                                                  		"version": "2.4.0 <= Apache kafka <= 3.3.2",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "僵尸网络内攻击者可以控制受害机发送大量数据包，形成拒绝服务攻击",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "拿到工控系统主机权限后由于工控系统未加密，篡改工程文件中的程序",
                                                                                                  		"severity": "严重"
                                                                                                  	},
                                                                                                  	{
                                                                                                  		"name": "利用探测工具未发现内网存在防火墙等网络安全设备",
                                                                                                  		"severity": "严重"
                                                                                                  	}
                                                                                                  ]'
                                                                                                   where scene_element in (
select node_id from mp.mp_scene_scene_element where name like '%服务器%');


update mp.mp_sceneplugincloud_scene_config_element set leaks = '[
                                                                                                      {
                                                                                                          "name": "CNVD-2021-27989",
                                                                                                          "severity": "严重",
                                                                                                          "version": "Google Chrome <= 89.0.4389.114"
                                                                                                      },
                                                                                                      {
                                                                                                          "name": "主机未及时安装系统补丁，未开启防火墙，未启动Windows Defender",
                                                                                                          "severity": "严重",
                                                                                                          "version": ""
                                                                                                      },
                                                                                                      {
                                                                                                          "name": "网站服务器拿到命令执行权限后篡改首页信息",
                                                                                                          "severity": "严重",
                                                                                                          "version": ""
                                                                                                      },
                                                                                                      {
                                                                                                          "name": "内网主机未将网关254的地址与mac地址静态绑定，可进行arp欺骗",
                                                                                                          "severity": "严重",
                                                                                                          "version": ""
                                                                                                      },
                                                                                                      {
                                                                                                          "name": "通过抓包工具发现内网存在http请求，http协议未经过认真和加密，容易受到DNS毒化",
                                                                                                          "severity": "严重",
                                                                                                          "version": ""
                                                                                                      }
                                                                                                  ]'
                                                                                                   where scene_element in (
select node_id from mp.mp_scene_scene_config_element where name like 'PC%');

update mp.mp_sceneplugincloud_scene_element set leaks = '[{
                                                                                                   "name": "CNVD-2021-27989",
                                                                                                   "severity": "严重",
                                                                                                   "version": "Google Chrome <= 89.0.4389.114"
                                                                                               },
                                                                                               {
                                                                                                   "name": "主机未及时安装系统补丁，未开启防火墙，未启动Windows Defender",
                                                                                                   "severity": "严重",
                                                                                                   "version": ""
                                                                                               },
                                                                                               {
                                                                                                   "name": "网站服务器拿到命令执行权限后篡改首页信息",
                                                                                                   "severity": "严重",
                                                                                                   "version": ""
                                                                                               },
                                                                                               {
                                                                                                   "name": "内网主机未将网关254的地址与mac地址静态绑定，可进行arp欺骗",
                                                                                                   "severity": "严重",
                                                                                                   "version": ""
                                                                                               },
                                                                                               {
                                                                                                   "name": "通过抓包工具发现内网存在http请求，http协议未经过认真和加密，容易受到DNS毒化",
                                                                                                   "severity": "严重",
                                                                                                   "version": ""
                                                                                               }
                                                                                           ]'
                                                                                                   where scene_element in (
select node_id from mp.mp_scene_scene_element where name like 'PC%');







update mp.mp_sceneplugincloud_scene_config_element set leaks = '[
                                                                                                      {
                                                                                                          "name": "主机开放445端口",
                                                                                                          "severity": "严重",
                                                                                                          "version": ""
                                                                                                      },
                                                                                                      {
                                                                                                          "name": "MongoDB 未授权访问漏洞",
                                                                                                          "severity": "中等",
                                                                                                          "version": ""
                                                                                                      },
                                                                                                      {
                                                                                                          "name": "服务弱口令",
                                                                                                          "severity": "严重",
                                                                                                          "version": ""
                                                                                                      }
                                                                                                  ]'
                                                                                                   where scene_element in (
select node_id from mp.mp_scene_scene_config_element where name like '%数据库%');

update mp.mp_sceneplugincloud_scene_element set leaks = '[
                                                                                               {
                                                                                                   "name": "主机开放445端口",
                                                                                                   "severity": "严重",
                                                                                                   "version": ""
                                                                                               },
                                                                                               {
                                                                                                   "name": "MongoDB 未授权访问漏洞",
                                                                                                   "severity": "中等",
                                                                                                   "version": ""
                                                                                               },
                                                                                               {
                                                                                                   "name": "服务弱口令",
                                                                                                   "severity": "严重",
                                                                                                   "version": ""
                                                                                               }
                                                                                           ]'
                                                                                                   where scene_element in (
select node_id from mp.mp_scene_scene_element where name like '%数据库%');




update mp.mp_sceneplugincloud_scene_config_element set leaks = '[
                                                                                                      {
                                                                                                          "name": "MongoDB 未授权访问漏洞",
                                                                                                          "severity": "中等",
                                                                                                          "version": ""
                                                                                                      }
                                                                                                  ]'
                                                                                                   where scene_element in (
select node_id from mp.mp_scene_scene_config_element where name like '%OPC%');

update mp.mp_sceneplugincloud_scene_element set leaks = '[
                                                                                               {
                                                                                                   "name": "MongoDB 未授权访问漏洞",
                                                                                                   "severity": "中等",
                                                                                                   "version": ""
                                                                                               }
                                                                                           ]'
                                                                                                   where scene_element in (
select node_id from mp.mp_scene_scene_element where name like '%OPC%');
COMMIT;