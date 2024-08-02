package cn.cyberpeace.mp.agent.snmp.controller;

import cn.cyberpeace.mp.agent.snmp.constant.ControllerConstant;
import cn.cyberpeace.mp.agent.snmp.controller.request.SnmpAccessInfo;
import cn.cyberpeace.mp.agent.snmp.controller.request.SnmpAccessInfoTest;
import cn.cyberpeace.mp.agent.snmp.service.TaskRequest;
import cn.cyberpeace.mp.agent.snmp.service.TaskService;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;

@Api(value = "Snmp配置代理api", tags = "Snmp配置代理api")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH)
public class SnmpAgentController {
    @Resource(name = "snmpTaskService")
    private TaskService<SnmpAccessInfo> snmpAccessInfoTaskService;

    @ApiOperation("配置任务接收接口")
    @PostMapping("/receiveTask")
    public ServiceResult<Boolean> receiveTask(@RequestBody TaskRequest<SnmpAccessInfo> taskRequest, HttpServletRequest request) {
        log.info("接收配置数据:" + JsonCommon.stringify(taskRequest));

        TaskCommon.runTasks(Collections.singletonList(() -> {
            snmpAccessInfoTaskService.execTask(taskRequest);
        }));

        // 异步执行配置
        return new ServiceResult<>(true);
    }

    @ApiOperation("测试下发")
    @PostMapping("/test")
    public ServiceResult<String> test(@RequestBody SnmpAccessInfoTest taskRequest) throws Exception {

        String hostIp = taskRequest.getHostIp();
        String message = getMessageByIpAndOid(hostIp, taskRequest.getOid());
        log.info("=============snmp request success, message=[{}]", message);

        // 异步执行配置
        return new ServiceResult<>(message).asResult();
    }


    private String getMessageByIpAndOid(String ip, String oid) throws Exception {
        String variableString = "";
        try {
            //设定CommunityTarget
            CommunityTarget myTarget = new CommunityTarget();
            //机器的地址
            Address address = GenericAddress.parse("udp:" + ip + "/161");
            //设定地址
            myTarget.setAddress(address);
            //设置snmp共同体
            myTarget.setCommunity(new OctetString("private"));
            //设置超时重试次数
            myTarget.setRetries(3);
            //设置超时的时间
            myTarget.setTimeout(5 * 60);
            //设置使用的snmp版本
            myTarget.setVersion(SnmpConstants.version1);
            //设定采取的协议
            TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();
            //调用TransportMapping中的listen()方法，启动监听进程，接收消息，由于该监听进程是守护进程，最后应调用close()方法来释放该进程
            transport.listen();
            //创建SNMP对象，用于发送请求PDU
            Snmp protocol = new Snmp(transport);
            //创建请求pdu,获取mib
            PDU request = new PDU();
            //调用的add方法绑定要查询的OID
            request.add(new VariableBinding(new OID(oid)));
            //调用setType()方法来确定该pdu的类型
            request.setType(PDU.GET);
            //调用 send(PDU pdu,Target target)发送pdu，返回一个ResponseEvent对象
            ResponseEvent responseEvent = protocol.send(request, myTarget);
            //通过ResponseEvent对象来获得SNMP请求的应答pdu，方法：public PDU getResponse()
            PDU response = responseEvent.getResponse();
            //输出
            if (response != null) {
                //通过应答pdu获得mib信息（之前绑定的OID的值），方法：VaribleBinding get(int index)
                VariableBinding vb = response.get(0);
                variableString = String.valueOf(vb.getVariable());
                //调用close()方法释放该进程
                transport.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return variableString;
    }
}
