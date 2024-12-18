## 对接启动流量执行器的流程
1. 创建一个场景，场景中包含一个虚拟机，然后启动这个场景。拿到这个场景模板id ${cscene_id} 和 场景实例id ${cscene_instance_id} ；

2. 调用 `创建流量执行器(/api/mp/flow/actuator/create_actuator)` 接口参数是 ${cscene_id} 和 ${cscene_instance_id} ；

3. `创建流量执行器` 这个接口会创建一个 tgn-trex 镜像的虚拟机 ；

4. 执行器信息会写入 `mp_flow_actuator` 表 ， state = 0 ； 

5. 调用 `流量执行器列表` 接口可以查看到数据 ；

6. cn.cyberpeace.mp.flow.job.FlowActuatorStateJob#queryFlowActuator ；
这个定时任务，会使用ssh协议，更新 tgn-trex 虚拟机的代码，并启动trex服务 ；


## 对接创建流量任务的流程
1. 添加一条背景流量 ， postman里面有个接口 `新增背景流量` ,pcap_file字段需要选择一个pcap文件 ，pcap文件在当前这个doc目录下面。
返回值里面有个 resource_id ， 这个就是后面要用的 ${flow_id} 

2. 创建一个场景模板，场景中包含一个虚拟机，拿到这个场景模板id ${cscene_id} ， 节点id ${node_id}

3. 向这个场景模板加流量任务 ， postman 里面有个接口 `更新场景模板节点的流量任务` ， 参数id就是 ${cscene_id} ， node_id
就是 ${node_id} ， flow_id 就是  ${flow_id}

4. 启动场景，得到场景实例的id ${cscene_instance_id} 

5. 调用 `创建流量执行器(/api/mp/flow/actuator/create_actuator)` 接口参数是 ${cscene_id} 和 ${cscene_instance_id} ；

6. 30所的查询虚拟机ip的接口没好的话，先手动向 mp_flow_actuator 填入ip ;

7. 


1. 创建一个场景，场景中包含一个虚拟机，拿到这个场景模板id ${cscene_id} 和 虚拟机的节点id ${cscene_node_id} ；

2. 调用 `创建流量执行器(/api/mp/flow/actuator/create_actuator)` ， 创建完成之后在 mp_flow_actuator 里会多一条数据 ;

3. 30所的查询虚拟机ip的接口没好的话，先手动向 mp_flow_actuator 填入ip ;


