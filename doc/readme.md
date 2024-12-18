## 对接启动流量执行器的流程
1. 创建一个场景，场景中包含一个虚拟机，然后启动这个场景。拿到这个场景模板id ${cscene_id} 和 场景实例id ${cscene_instance_id} ；

2. 调用 `创建流量执行器(/api/mp/flow/actuator/create_actuator)` 接口参数是 ${cscene_id} 和 ${cscene_instance_id} ；

3. `创建流量执行器` 这个接口会创建一个 tgn-trex 镜像的虚拟机 ；

4. 执行器信息会写入 `mp_flow_actuator` 表 ， state = 0 ； 

5. 调用 `流量执行器列表` 接口可以查看到数据 ；

6. cn.cyberpeace.mp.flow.job.FlowActuatorStateJob#queryFlowActuator ；
这个定时任务，会使用ssh协议，更新 tgn-trex 虚拟机的代码，并启动trex服务 ；


## 对接创建流量任务的流程
1. 添加一条背景流量 ， postman里面有个接口 `流量信息 - 新增背景流量（/api/mp/flow/info/background）` ,pcap_file字段需要选择一个pcap文件 ，pcap文件在当前这个doc目录下面。
返回值里面有个 resource_id ， 这个就是后面要用的 ${flow_id} 

2. 创建一个场景模板，场景中包含一个虚拟机，拿到这个场景模板id ${cscene_id} ， 节点id ${node_id}

3. 向这个场景模板加流量任务 ， postman 里面有个接口 `业务接口 - 更新场景模板节点的流量任务（/api/mp/out/flow/cscene_config/update_tasks）` ， 参数id就是 ${cscene_id} ， node_id
就是 ${node_id} ， flow_id 就是  ${flow_id}

4. 启动场景，得到场景实例的id ${cscene_instance_id} 

5. 调用 `流量执行器 - 创建流量执行器 (/api/mp/flow/actuator/create_actuator)` 接口参数是 ${cscene_id} 和 ${cscene_instance_id} ；
这时候会返回一个流量执行器id ， ${actuator_id}

6. 30所的查询虚拟机ip的接口没好的话，先手动向 mp_flow_actuator 填入ip ;
ip写入之后，FlowActuatorStateJob定时任务会处理 新加的执行器 ， 正常的话会输出下面的日志

| 开始处理执行器 0bedd096aeff4197854c22bde9d4e720
| 检测ssh是否可用
| ssh 可用 ， 更新发生器代码

| open sftp channel
| put file [traffic_generators.zip] from [traffic_generators.zip] to [/opt/project_dirs] success
| start exec shell script: ./init.sh
| exec shell task result: traffic_generators: stopped
| traffic_generators: started
| exec shell end and close thread pool
| exec shell script success

| 准备创建任务
|  =======sendHttpGet url: http://10.100.27.1/api/mp/cscene/cscene_instances?id=7e6c8dbb20a5417fa07b4fae3d97b23c
|  =======sendHttpGet response: {"data":{"id":"7e6c8dbb20a5417fa07b4fae3d97b23c","create_user":"3f655d87112f4ae094bbaf70830e20aa","create_username":"admin","create_name":null,"roles":[],"name":"wazuhtest","enable_recording":false,"support_partial_start":false,"cscene":"c56698e8a41240a783b98b0f13e1b768","logo":"/api/mp/file/038/373/d5b5a517df85250c019abce6d6d37abf.png","type":2,"reference_type":236,"reference_meta_data":"{\"business_code\":236,\"business_id\":\"a563183787feb53c560a7b6e6d34f26c\",\"business_name\":\"wazuhtest\",\"create_user_id\":\"3f655d87112f4ae094bbaf70830e20aa\"}","scene":{"topology":{"scene":{"eid":"dec146464cc444d6888e995b5bb81309","name":"wazuhtest","description":"","file":"","display_context":"{\"active\":\"background\",\"enable_flow_collect\":true,\"enable_flow_mirror\":false,\"dns_nameservers\":[],\"showLineLabels\":false,\"backgroundList\":[{\"key\":\"background\",\"value\":\"\"},{\"key\":\"background2\",\"value\":\"\"},{\"key\":\"background3\",\"value\":\"\"}],\"simpleModeToEdge\":true,\"graphTranslate\":{\"tx\":0,\"ty\":0},\"graphZoom\":1,\"showGraphToolbar\":true,\"showNodeLimitTip\":true,\"layoutType\":\"none\",\"backgroundType\":\"background-image\",\"backgroundColor\":\"var(--bg-color-1)\",\"showGrid\":true}","create_time":"2024-12-12 17:26:26","update_time":"2024-12-12 17:27:18","event":2,"event_status":4,"process":"create_probe_router","process_status":4,"process_message":"场景 wazuhtest  创建 结束","progress_percentage":100,"create_consume_seconds":50,"stage":"create_data_collect","stage_name":"创建主机采集","stage_percentage":100,"stage_list":[{"stage":"init","icon":"xinxitongji","stage_name":"规划资源","stage_percentage":100},{"stage":"create_network","icon":"wangluoyu","stage_name":"创建网络","stage_percentage":100},{"stage":"create_gateway","icon":"wangluoshebei","stage_name":"创建网关","stage_percentage":100},{"stage":"create_instance","icon":"changjingmoban","stage_name":"创建实例","stage_percentage":100},{"stage":"create_flow_collect","icon":"tanzhenguanl
| 任务内容: {"c25227d1-e54a-4406-9c29-6887bf027073":{"tasks":[{"flowId":"e44e7451345a4f658675fb741934cfac","attackConfs":[]}],"actuatorId":"0bedd096aeff4197854c22bde9d4e720","networkId":"317cd83e-72f8-4d4e-8589-bddd1c7d5765","targetIp":"192.168.1.103"}}

|  =======sendHttpPost url: http://10.100.27.1:11111/v1/instances/3ed1a6e2-eccd-4064-9e73-c4a4e4b16869/interfaces,  body: {"network_id":"317cd83e-72f8-4d4e-8589-bddd1c7d5765"}
|  =======sendHttpPost callTime [5216] response success


|  请求地址================>http://10.100.27.150/api/v1/task/network/init
|  请求参数================>{"network_infos":[{"mac":"fa:16:3e:5d:48:57","task_id":"1079ace1c6644f06ad9611bceb1e1313","target_ip":"192.168.1.103"}]}
|  结果================>{"code": 1000, "msg": "\u6210\u529f"}

|  请求地址================>http://10.100.27.150/api/v1/resource/pcap/upload
|  请求参数================>{"protocolRate":{"tcp":87.03,"udp":0.37,"other":0.27,"DATA":0.21,"xml":0.19,"dns":0.74,"http":9.53,"tls":0.42,"ssdp":0.21,"icmp":0.9,"icmpv6":0.13},"fileName":"testx11734520662887.pcap","flow_id":"e44e7451345a4f658675fb741934cfac","file_name":"testx11734520662887.pcap","protocol_rate":{"$ref":"$.protocolRate"},"flowId":"e44e7451345a4f658675fb741934cfac"}
|  结果================>{"code": 1000, "msg": "\u6210\u529f", "data": {"uuid": "e44e7451345a4f658675fb741934cfac", "file_name": "testx11734520662887.pcap"}}

|  完成处理执行器 0bedd096aeff4197854c22bde9d4e720

7. 调用 `流量执行器 - 执行器任务列表（/api/mp/flow/actuator/task/list）` ，参数是 ${actuator_id} 
返回得到该流量执行器下的任务列表 ， 取出其中一个任务id， ${task_id}

8. 调用 `流量执行器 - 启动单个任务（/api/mp/flow/actuator/task/start）` ，参数值是  ${task_id}


