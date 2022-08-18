package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.situationaware.BO.HostBO;
import cn.cyberpeace.mp.situationaware.BO.ProcessBO;
import cn.cyberpeace.mp.situationaware.BO.SystemUserBO;
import cn.cyberpeace.mp.situationaware.model.FileEventModel;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Scan;
import cn.cyberpeace.mp.situationaware.request.PageQueryRequest;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/wazuh")
@Api(value = "态势感知-事件查询接口", tags = "态势感知-事件查询接口")
public class DataCollectionController {

    @Resource
    private DataCollectionService dataCollectionService;

    @GetMapping("/file_events")
    @ApiOperation(value = "查询文件事件", notes = "")
    public ListObjServiceResult<FileEventModel> queryFileEvents(PageQueryRequest request) {
        request.ensure();
        var fileEventModel = new FileEventModel();
        fileEventModel.setAction("modified");
        fileEventModel.setAgentId("021");
        fileEventModel.setAgentName("a57f4ac737c7453ba14ada24e5a959e1");
        JSONObject jsonObject = new JSONObject();
        com.alibaba.fastjson.JSONObject ruleObj = new JSONObject();
        ruleObj.put("firedtimes", 248);
        ruleObj.put("description", "Integrity checksum changed");
        ruleObj.put("groups", List.of("ossec", "syscheck", "syscheck_entry_modified", "syscheck_file"));
        ruleObj.put("id", "550");
        jsonObject.put("rule", ruleObj);
        jsonObject.put("full_log", "File '/var/ossec/logs/ossec.log' modified\\nMode: realtime\\nChanged attributes: size,md5,sha1,sha256\\nSize changed from '257992' to '258081'\\nOld md5sum was: '6436abe963f1e3ae990dd12d0df0b785'\\nNew md5sum is : 'aac19c2fcc2b34b343b18c241029f7b1'\\nOld sha1sum was: 'e4df7d6433e90fbb1498e921ffb4548883b55087'\\nNew sha1sum is : '63fd558695791b7c13d5a67ad9091b96ca87e193'\\nOld sha256sum was: '32526a8662987518aed3235dcf950ee2ed1204a051d74bee842aa0b69ec12980'\\nNew sha256sum is : 'f5e39a7b72819d970bd21ddb14d57cd8cca0f52fe2c39fbdabdaa0558e8f2370'\\n");
        fileEventModel.setContent(jsonObject);
        fileEventModel.setDescription("Integrity checksum changed");
        fileEventModel.setFilePath("/var/ossec/logs/ossec.log");
        fileEventModel.setLevel(7);
        fileEventModel.setRuleId("550");
        fileEventModel.setUpdateTime(LocalDateTime.now());
        fileEventModel.setSize("258081");
        fileEventModel.setMd5Before("6436abe963f1e3ae990dd12d0df0b785");
        fileEventModel.setMd5After("aac19c2fcc2b34b343b18c241029f7b1");
        fileEventModel.setAgentIp("192.168.1.167");
        return new ListObjServiceResult<>(new ListObjResult<>(List.of(fileEventModel), 1)).asResult();
    }

    @GetMapping("/process_info")
    @ApiOperation(value = "查询进程信息", notes = "")
    public ListObjServiceResult<ProcessBO> queryProcessInfo(PageQueryRequest request) {
        request.ensure();
        var process = new ProcessBO();
        process.setCmd("/usr/lib/systemd/systemd --switched-root --system --deserialize 22");
        process.setCreateTime(LocalDateTime.now().plusHours(-2).toString());
        process.setTime(LocalDateTime.now());
        process.setName("systemd");
        process.setNumThreads("100");
        process.setParentId("2");
        process.setParentName("aaa");
        process.setPid("1");
        process.setPriority("0");
        process.setVmSize("128068");
        process.setEuser("root");
        process.setStatus("running");
        process.setAgentIp("10.100.7.1");
        return new ListObjServiceResult<>(new ListObjResult<>(List.of(process), 1)).asResult();
    }

    @GetMapping("/host_info")
    @ApiOperation(value = "查询主机信息", notes = "")
    public ListObjServiceResult<HostBO> queryHostInfo(PageQueryRequest request) {
        var host = new HostBO();
        host.setCPUPercent(10.1);
        host.setDiskUsePercent(20.2);
        host.setMemoryUsePercent(30.3);
        host.setAgentIp("10.100.7.1");
        return new ListObjServiceResult<>(new ListObjResult<>(List.of(host), 1)).asResult();
    }

    @GetMapping("/port_info")
    @ApiOperation(value = "查询端口信息", notes = "")
    public ListObjServiceResult<Port> queryPortInfo(PageQueryRequest request) {
        request.ensure();
        var port = new Port();
        port.setAgentId("021");
        port.setInode(23190);
        Port.Local local = new Port.Local();
        local.setIp("127.0.0.1");
        local.setPort(3350);
        port.setLocal(local);
        port.setPid(14);
        port.setProcess("bin");
        port.setRxQueue(0);
        port.setTxQueue("0");
        port.setProtocol("tcp6");
        port.setState("listening");
        Port.Remote remote = new Port.Remote();
        remote.setIp("0.0.0.0");
        remote.setPort(0);
        port.setRemote(remote);
        Scan scan = new Scan();
        scan.setId(0L);
        scan.setTime(LocalDateTime.now());
        port.setScan(scan);
        port.setAgentIp("10.100.7.1");
        return new ListObjServiceResult<>(new ListObjResult<>(List.of(port), 1)).asResult();
    }

    @GetMapping("/system_user")
    @ApiOperation(value = "查询用户信息", notes = "")
    public ListObjServiceResult<SystemUserBO> querySystemUser(PageQueryRequest request) {
        request.ensure();
        var systemUser = new SystemUserBO();
        systemUser.setAction("modified");
        systemUser.setAgentIp("192.168.1.167");
        systemUser.setComment("root");
        systemUser.setDir("/root");
        systemUser.setGroupId("0");
        systemUser.setGroupName("root");
        systemUser.setId("0");
        systemUser.setShell("/usr/bin");
        systemUser.setTime(LocalDateTime.now());
        systemUser.setName("root");
        return new ListObjServiceResult<>(new ListObjResult<>(List.of(systemUser), 1)).asResult();
    }
}
