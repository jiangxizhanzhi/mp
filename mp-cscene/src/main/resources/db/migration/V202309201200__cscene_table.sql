-- 节点信息增加漏洞信息记录
ALTER TABLE mp.mp_sceneplugincloud_scene_element ADD leaks LONGVARCHAR not null default '' ;
ALTER TABLE mp.mp_sceneplugincloud_scene_config_element ADD leaks LONGVARCHAR not null default '';
ALTER TABLE mp.mp_devops_device add leaks LONGVARCHAR not null default '';
ALTER TABLE mp.mp_devops_target add leaks LONGVARCHAR not null default '';


MERGE INTO mp.mp_devops_target T USING (SELECT '209' id, 'f7ebab70-ac8e-4aaf-b4b9-18a4d205b209' uuid, 'OPC-client' name FROM DUAL) S ON (T.id = S.id OR T.uuid = S.uuid OR T.name = S.name) WHEN MATCHED THEN UPDATE SET leaks = '[
                                         {
                                             "name": "MongoDB 未授权访问漏洞",
                                             "severity": "中等",
                                             "version": ""
                                         }
                                     ]' WHEN NOT MATCHED THEN INSERT (id, uuid, name, logo, role, main_category, sub_category, third_category, image_type, is_public, quick_create_support, in_position, reference_status, use_status, parent_system_type, system_type, system_architecure, language, image_status, base_image_support, init_support, create_time, update_time, image_source, is_import, is_hide, instance_state, is_init, instance_create_user, leaks) VALUES (209, 'f7ebab70-ac8e-4aaf-b4b9-18a4d205b209', 'OPC-client', '/api/mp/network-equipment/internet.png', 4, 2, 56, 0.0, 'vm', 2, 1, NULL, 0, 0, NULL, NULL, 64.0, 'cn', 2.0, 0.0, 1, '2022-11-09 11:38:30', '2022-11-09 11:38:30', 1.0, 0, 0, 0, 0, NULL, '[
                                         {
                                             "name": "MongoDB 未授权访问漏洞",
                                             "severity": "中等",
                                             "version": ""
                                         }
                                     ]'); COMMIT;
MERGE INTO mp.mp_devops_target T USING (SELECT '10927454' id, 'e85e65e5-73ee-44ba-a681-704f52f3e605' uuid, '数据库' name FROM DUAL) S ON (T.id = S.id OR T.uuid = S.uuid OR T.name = S.name) WHEN MATCHED THEN UPDATE SET leaks = '[
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
                                     ]' WHEN NOT MATCHED THEN INSERT (id, uuid, name, logo, role, main_category, sub_category, third_category, image_type, is_public, quick_create_support, in_position, reference_status, use_status, parent_system_type, system_type, system_architecure, language, image_status, base_image_support, init_support, create_time, update_time, image_source, is_import, is_hide, instance_state, is_init, instance_create_user, leaks) VALUES (10927454, 'e85e65e5-73ee-44ba-a681-704f52f3e605', '数据库', '/api/mp//application-system/db.png', 4, 2, 32, NULL, 'vm', 2, 0, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, 0, '2023-05-05 11:24:14', '2023-05-05 11:24:14', NULL, 0, 0, 0, 0, NULL, '[
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
                                     ]'); COMMIT;
MERGE INTO mp.mp_devops_target T USING (SELECT '10927506' id, '05bdd8c0-0660-41c3-bf93-feae912b5c1c' uuid, 'PC' name FROM DUAL) S ON (T.id = S.id OR T.uuid = S.uuid OR T.name = S.name) WHEN MATCHED THEN UPDATE SET leaks = '[
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
                                     ]' WHEN NOT MATCHED THEN INSERT (id, uuid, name, logo, role, main_category, sub_category, third_category, image_type, is_public, quick_create_support, in_position, reference_status, use_status, parent_system_type, system_type, system_architecure, language, image_status, base_image_support, init_support, create_time, update_time, image_source, is_import, is_hide, instance_state, is_init, instance_create_user, leaks) VALUES (10927506, '05bdd8c0-0660-41c3-bf93-feae912b5c1c', 'PC', '/api/mp//application-system/notebook.png', 4, 1, 1, 97.0, 'vm', 2, 0, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, 0, '2023-05-05 11:25:06', '2023-05-05 11:25:06', NULL, 0, 0, 0, 0, NULL, '[
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
                                     ]'); COMMIT;
MERGE INTO mp.mp_devops_target T USING (SELECT '10929298' id, '38d20bae-220b-4151-99d0-f6bf7332ac2c' uuid, '服务器' name FROM DUAL) S ON (T.id = S.id OR T.uuid = S.uuid OR T.name = S.name) WHEN MATCHED THEN UPDATE SET leaks = '[
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
                                     ]' WHEN NOT MATCHED THEN INSERT (id, uuid, name, logo, role, main_category, sub_category, third_category, image_type, is_public, quick_create_support, in_position, reference_status, use_status, parent_system_type, system_type, system_architecure, language, image_status, base_image_support, init_support, create_time, update_time, image_source, is_import, is_hide, instance_state, is_init, instance_create_user, leaks) VALUES (10929298, '38d20bae-220b-4151-99d0-f6bf7332ac2c', '服务器', '/api/mp//1_server/服务器.png', 4, 2, 30, NULL, 'vm', 2, 0, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, 0, '2023-05-05 11:54:58', '2023-05-05 11:54:58', NULL, 0, 0, 0, 0, NULL, '[
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
                                     ]');
                                     COMMIT;

MERGE INTO mp.mp_devops_device T USING (
    SELECT
        '209' id,
        'f7ebab70-ac8e-4aaf-b4b9-18a4d205b209' uuid,
        'OPC-client1111' name
    FROM
        DUAL
) S ON (
    T.id = S.id
    OR T.uuid = S.uuid
    OR T.name = S.name
)
WHEN MATCHED THEN
UPDATE
SET
    leaks = '[
                                         {
                                             "name": "MongoDB 未授权访问漏洞",
                                             "severity": "中等",
                                             "version": ""
                                         }
                                     ]'
    WHEN NOT MATCHED THEN
INSERT
    (
        id,
        uuid,
        updated_at,
        deleted,
        created_at,
        deleted_at,
        name,
        image,
        description,
        logo,
        image_type,
        main_type,
        role,
        category,
        second_category,
        l3_category,
        is_import,
        is_internal,
        is_init,
        ext_entity,
        ext_entity_access,
        ext_system_info,
        ext_gateway,
        ext_vm,
        ext_image_info,
        ext_docker,
        is_build_in,
        is_hide,
        default_launch_image_name,
        create_time,
        update_time,
        leaks
    )
VALUES
    (
        209,
        'f7ebab70-ac8e-4aaf-b4b9-18a4d205b209',
        '2022-12-29 11:15:48',
        0,
        '2022-10-10 11:09:05',
        '',
        'OPC-client1111',
        '',
        'OPC-client',
        '/api/mp/safety-equipment/vpn.png',
        'vm',
        0,
        4,
        3,
        56,
        0,
        0,
        0,
        0,
        '',
        '',
        '{"parentSystemType":null,"systemType":null,"systemArchitecture":"64","language":"cn","systemTypeName":null,"systemArchitectureName":null,"languageName":null}',
        '{"portConfigs":[],"consoleConfig":null}',
        '{"accessProtocols": [{"manageMode": false, "protocol": "ssh", "privateKeyFileName": "", "hostname": "", "description": "", "authProtocol": "", "username": "", "port": 22, "password": "123456", "privateKeyPath": "", "privateKeyEnable": false}], "instanceExist": false, "snapshotCount": 0, "disk": 0,  "extInfo": {}, "quickCreateSupport": true, "initSupport": true, "instanceUuid": "",  "flavor": "mx.1c-1m-1g", "instanceState": 0, "quickCreateParams": [], "ram": 0, "cpu": 0}',
        '{"imageStatus": 2,"deviceActions": "","userFilename": "","metaData": {"hwVifModel": "","hwDiskBus": "","hwVideoModel": ""},"error": "", "baseImageName": "linux_cloud_init", "baseImageSupport": false,"diskFormat": "","uuid": "dfdc24aa-3b28-40dc-84aa-15cba3ddeb77","imageSource": 1,"uploadType": ""}',
        '',
        0,
        0,
        'linux_cloud_init',
        '2022-10-10 11:09:05',
        '2022-12-29 11:15:49',
        '[
                                         {
                                             "name": "MongoDB 未授权访问漏洞",
                                             "severity": "中等",
                                             "version": ""
                                         }
                                     ]'
    );

COMMIT;

MERGE INTO mp.mp_devops_device T USING (
    SELECT
        '10927454' id,
        'e85e65e5-73ee-44ba-a681-704f52f3e605' uuid,
        '数据库' name
    FROM
        DUAL
) S ON (
    T.id = S.id
    OR T.uuid = S.uuid
    OR T.name = S.name
)
WHEN MATCHED THEN
UPDATE
SET
    leaks = '[
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
    WHEN NOT MATCHED THEN
INSERT
    (
        id,
        uuid,
        updated_at,
        deleted,
        created_at,
        deleted_at,
        name,
        image,
        description,
        logo,
        image_type,
        main_type,
        role,
        category,
        second_category,
        l3_category,
        is_import,
        is_internal,
        is_init,
        ext_entity,
        ext_entity_access,
        ext_system_info,
        ext_gateway,
        ext_vm,
        ext_image_info,
        ext_docker,
        is_build_in,
        is_hide,
        default_launch_image_name,
        create_time,
        update_time,
        leaks
    )
VALUES
    (
        10927454,
        'e85e65e5-73ee-44ba-a681-704f52f3e605',
        '2023-05-05 11:24:14',
        0,
        '2023-05-05 11:24:14',
        '2022-12-23 16:46:00',
        '数据库',
        '',
        '数据库',
        '/api/mp//application-system/db.png',
        'vm',
        0,
        4,
        2,
        32,
        0,
        0,
        0,
        0,
        '',
        '',
        '{"parentSystemType":null,"systemType":null,"systemArchitecture":null,"language":null,"systemTypeName":null,"systemArchitectureName":null,"languageName":null}',
        '{"portConfigs":[],"consoleConfig":null}',
        '{"flavor":"m1.1c-512m-8g","initSupport":false,"quickCreateSupport":false,"accessProtocols":[],"quickCreateParams":null,"extInfo":null,"instanceExist":null,"snapshotCount":null,"instanceUuid":null,"instanceState":null,"cpu":1,"ram":512,"disk":8}',
        '{"uuid":null,"baseImageName":null,"baseImageSupport":null,"metaData":{"hwDiskBus":"","hwVifModel":"","hwVideoModel":""},"imageStatus":null,"error":null,"imageSource":null,"uploadType":"http","diskFormat":null,"tmpdirUuid":null,"ftpFilename":null,"userFilename":null,"deviceActions":null}',
        '',
        0,
        0,
        '',
        '2023-05-05 11:24:14',
        '2023-05-05 11:24:14',
        '[
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
    );

COMMIT;

MERGE INTO mp.mp_devops_device T USING (
    SELECT
        '10927506' id,
        '05bdd8c0-0660-41c3-bf93-feae912b5c1c' uuid,
        'PC' name
    FROM
        DUAL
) S ON (
    T.id = S.id
    OR T.uuid = S.uuid
    OR T.name = S.name
)
WHEN MATCHED THEN
UPDATE
SET
    leaks = '[
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
    WHEN NOT MATCHED THEN
INSERT
    (
        id,
        uuid,
        updated_at,
        deleted,
        created_at,
        deleted_at,
        name,
        image,
        description,
        logo,
        image_type,
        main_type,
        role,
        category,
        second_category,
        l3_category,
        is_import,
        is_internal,
        is_init,
        ext_entity,
        ext_entity_access,
        ext_system_info,
        ext_gateway,
        ext_vm,
        ext_image_info,
        ext_docker,
        is_build_in,
        is_hide,
        default_launch_image_name,
        create_time,
        update_time,
        leaks
    )
VALUES
    (
        10927506,
        '05bdd8c0-0660-41c3-bf93-feae912b5c1c',
        '2023-05-05 11:25:06',
        0,
        '2023-05-05 11:25:06',
        '2022-12-23 16:46:00',
        'PC',
        '',
        'PC',
        '/api/mp//application-system/notebook.png',
        'vm',
        0,
        4,
        1,
        1,
        97,
        0,
        0,
        0,
        '',
        '',
        '{"parentSystemType":null,"systemType":null,"systemArchitecture":null,"language":null,"systemTypeName":null,"systemArchitectureName":null,"languageName":null}',
        '{"portConfigs":[],"consoleConfig":null}',
        '{"flavor":"m1.1c-512m-8g","initSupport":false,"quickCreateSupport":false,"accessProtocols":[],"quickCreateParams":null,"extInfo":null,"instanceExist":null,"snapshotCount":null,"instanceUuid":null,"instanceState":null,"cpu":1,"ram":512,"disk":8}',
        '{"uuid":null,"baseImageName":null,"baseImageSupport":null,"metaData":{"hwDiskBus":"","hwVifModel":"","hwVideoModel":""},"imageStatus":null,"error":null,"imageSource":null,"uploadType":"http","diskFormat":null,"tmpdirUuid":null,"ftpFilename":null,"userFilename":null,"deviceActions":null}',
        '',
        0,
        0,
        '',
        '2023-05-05 11:25:06',
        '2023-05-05 11:25:06',
        '[
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
    );

COMMIT;

MERGE INTO mp.mp_devops_device T USING (
    SELECT
        '10929298' id,
        '38d20bae-220b-4151-99d0-f6bf7332ac2c' uuid,
        '服务器' name
    FROM
        DUAL
) S ON (
    T.id = S.id
    OR T.uuid = S.uuid
    OR T.name = S.name
)
WHEN MATCHED THEN
UPDATE
SET
    leaks = '[
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
    WHEN NOT MATCHED THEN
INSERT
    (
        id,
        uuid,
        updated_at,
        deleted,
        created_at,
        deleted_at,
        name,
        image,
        description,
        logo,
        image_type,
        main_type,
        role,
        category,
        second_category,
        l3_category,
        is_import,
        is_internal,
        is_init,
        ext_entity,
        ext_entity_access,
        ext_system_info,
        ext_gateway,
        ext_vm,
        ext_image_info,
        ext_docker,
        is_build_in,
        is_hide,
        default_launch_image_name,
        create_time,
        update_time,
        leaks
    )
VALUES
    (
        10929298,
        '38d20bae-220b-4151-99d0-f6bf7332ac2c',
        '2023-09-20 20:03:51',
        0,
        '2023-05-05 11:54:58',
        '2022-12-23 16:46:00',
        '服务器',
        '',
        '服务器',
        '/api/mp//1_server/服务器.png',
        'vm',
        0,
        4,
        2,
        30,
        0,
        0,
        0,
        0,
        '',
        '',
        '{"parentSystemType":2,"systemType":302,"systemArchitecture":"32","language":"ru","systemTypeName":null,"systemArchitectureName":null,"languageName":null}',
        '{"portConfigs":[],"consoleConfig":null}',
        '{"flavor":"m1.1c-512m-8g","initSupport":false,"quickCreateSupport":false,"accessProtocols":[],"quickCreateParams":null,"extInfo":null,"instanceExist":null,"snapshotCount":null,"instanceUuid":null,"instanceState":null,"cpu":1,"ram":512,"disk":8}',
        '{"uuid":null,"baseImageName":null,"baseImageSupport":null,"metaData":{"hwDiskBus":"","hwVifModel":"","hwVideoModel":""},"imageStatus":null,"error":null,"imageSource":null,"uploadType":"http","diskFormat":null,"tmpdirUuid":null,"ftpFilename":null,"userFilename":null,"deviceActions":null}',
        '',
        0,
        0,
        '',
        '2023-05-05 11:54:59',
        '2023-09-20 20:03:52',
        '[
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
    );

COMMIT;



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