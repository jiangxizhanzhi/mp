package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.CscenePluginDataRecordService;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.logfile.LogFileDownloadUrlDto;
import cn.cyberpeace.mp.cscene.dto.logfile.LogFileVO;
import cn.cyberpeace.mp.cscene.request.logfile.LogFileDownloadUrlRequest;
import cn.cyberpeace.mp.cscene.request.logfile.LogFileSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.LogFileService;
import cn.cyberpeace.mp.cscene.service.cron.UnixHostJob;
import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneDataModel;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/6 15:27
 */
@Api(value = "节点日志文件Api", tags = "节点日志文件Api")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/logfile")
public class LogFileController {

    @Autowired
    private CsceneInstanceService instanceService;

    @Autowired
    private LogFileService logFileService;

    @Autowired
    private CscenePluginDataRecordService dataRecordService;

    private String[] getData(String scriptPath, String host, Integer port, String username, String password) {
        BufferedReader input = null;
        try {

            String os = System.getProperty("os.name").toLowerCase();
            String python_exec = "python3 %s %s %s %s %s";
            if (os.contains("win")) {
                python_exec = "C:\\python3\\python.exe %s %s %s %s %s";
            }

            String pythonCmd = String.format(python_exec, scriptPath, host, port, username, password);
            log.info("============python analyse cmd: {}", pythonCmd);
            Process process = Runtime.getRuntime().exec(pythonCmd);
            process.waitFor(5, TimeUnit.SECONDS);
            String res = "";
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = input.readLine()) != null) {
                res = line;
            }
            log.info("result:{}", res);
            Map<String,Object> da = JsonCommon.snakeParse(res, Map.class);
            var x = ((Map)da.get("ls")).get("data").toString().split("\n");
            return x;
        } catch (Exception e) {
            log.error("exec error:", e);
            return new String[]{};
        }
    }

    private void getData2(String scriptPath, String host, Integer port, String username, String password,String path1,String path2) {
        BufferedReader input = null;
        try {

            String os = System.getProperty("os.name").toLowerCase();
            String python_exec = "python3 %s %s %s %s %s %s %s";
            if (os.contains("win")) {
                python_exec = "C:\\python3\\python.exe %s %s %s %s %s %s %s";
            }

            String pythonCmd = String.format(python_exec, scriptPath, host, port, username, password,path1,path2);
            log.info("============python analyse cmd: {}", pythonCmd);
            Process process = Runtime.getRuntime().exec(pythonCmd);
            process.waitFor(5, TimeUnit.SECONDS);
            String res = "";
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = input.readLine()) != null) {
                res = line;
            }
            log.info("result:{}", res);
        } catch (Exception e) {
            log.error("exec error:", e);

        }
    }

    /**
     * elementIds不能为空，如果为多个值，默认只取第一个
     *
     * @param pageRequest
     * @return
     */
    @ApiOperation(value = "分页查询日志文件")
    @GetMapping("/page")
    public ListObjServiceResult<LogFileVO> pageLogFiles(LogFileSceneElementPageRequest pageRequest) {

        CsceneInstanceService instanceService = BeanCommon.getBean(CsceneInstanceService.class);
        String sceneId = instanceService.getSceneId(pageRequest.getSceneId());
        var s = new SceneHandler(sceneId, null, false, new SceneContextBO());
        SceneElementBO elementBO = null;
        for (var e : s.getElementMap().values()) {
            String type = SystemTypeUtils.getStandardSystemType(e.getSystemType());
            if (BaseSceneElementModel.SystemType.UNIX.equals(type)) {
                elementBO = e;
                break;
            }
        }
        if (elementBO != null) {
            // 使用ssh 查 /var/log/下的日志文件
            CloudScenePluginHandler pluginHandler = s.getPluginHandler(PluginConstant.PLUGIN_KEY);
            String ip = null;
            Integer port = null;
            String usename = null;
            String password = null;
            String portIp = null;
            for (var p : elementBO.getPorts()) {
                if (StringUtils.isNotEmpty(ip)) {
                    break;
                }
                var pluginPort = pluginHandler.getPort(p.getId());
                if (pluginPort.getAccessModes() == null) {
                    continue;
                }
                for (var a : pluginPort.getAccessModes()) {
                    if (AccessMode.Protocol.SSH.equals(a.getProtocol())) {
                        ip = a.getAccessIp();
                        port = a.getAccessPort();
                        usename = a.getUsername();
                        password = a.getPassword();
                        portIp = p.getIp();
                        break;
                    }
                }
            }
            if (StringUtils.isNotEmpty(ip)) {
                String dir = BeanCommon.getBean(BaseAppConfig.class).getResourcesDir();
                var info = getData(dir + "/ssh_run_cmd3.py", ip, port, usename, password);
                int start = (pageRequest.getPage() - 1) * pageRequest.getPerPage();
                int end = start + pageRequest.getPerPage();
                if (start > info.length) {
                    List res = new ArrayList<LogFileVO>();
                    return new ListObjServiceResult<>(new ListObjResult<>(res, 0));
                }
                end = Math.min(end, info.length);
                List<String> xxx = new ArrayList<>();
                for (var in : info) {
                    String[] inf = in.split("_");
                    if (inf.length != 2) {
                        continue;
                    }
                    xxx.add(in);
                }
                var t2 = xxx.subList(start, end);
                List<LogFileVO> res = new ArrayList<>();
                for (var tt2 : t2) {
                    String[] inf = tt2.split("_");
                    var logFileVO = new LogFileVO();
                    logFileVO.setName(inf[1]);
                    logFileVO.setPath("/var/log/" + inf[1]);
                    logFileVO.setSize(Long.valueOf(inf[0]));
                    logFileVO.setAccessTime(LocalDateTime.now());
                    logFileVO.setCreateTime(LocalDateTime.now());
                    logFileVO.setModifyTime(LocalDateTime.now());
                    logFileVO.setElementId(elementBO.getId());
                    logFileVO.setAgentName(elementBO.getEid());
                    logFileVO.setOsType("unix");
                    res.add(logFileVO);
                }
                return new ListObjServiceResult<>(new ListObjResult<>(res, xxx.size())).asResult();
            }
        }


        log.info("{}", pageRequest);
        if (StringUtils.isNotEmpty(pageRequest.getStartTime()) || StringUtils.isNotEmpty(pageRequest.getEndTime())) {
            var instance = instanceService.get(pageRequest.getSceneId()).getSource().getObj();
            LocalDateTime startTime = null;
            if (StringUtils.isNotEmpty(pageRequest.getStartTime())) {
                startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
            } else {
                startTime = instance.getCreateTime();
            }
            LocalDateTime endTime = null;
            if (StringUtils.isNotEmpty(pageRequest.getEndTime())) {
                endTime = TimeUtils.stringToLocalDateTime(pageRequest.getEndTime());
            } else {
                endTime = LocalDateTime.now();
            }
            TimeListPageInfoQueryParams params = new TimeListPageInfoQueryParams();
            params.setSceneIds(List.of(instance.getScene()));
            params.setNodeIds(pageRequest.getElementIds());
            var dataModels = dataRecordService.getData(params, CollectionPluginSceneDataModel.Type.LOGFile, startTime,
                    endTime);
            var logs = dataModels.stream().map(d -> {
                return JsonCommon.parse(d.getData(), LogFileVO.class);
            }).collect(Collectors.toList());
            Set<String> exists = new HashSet<>();
            List<LogFileVO> finalResult = new ArrayList<>();
            for (var log : logs) {
                if (!exists.contains(log.getPath())) {
                    exists.add(log.getPath());
                    finalResult.add(log);
                }
            }
            return new ListObjServiceResult(new ListObjResult(finalResult, finalResult.size()).asResult()).asResult();
        } else {
            ListObjServiceResult<LogFileVO> result = logFileService.pageByElementId(pageRequest);
            return result;
        }
    }


    /**
     * elementIds不能为空，如果为多个值，默认只取第一个
     *
     * @return
     */
    @ApiOperation(value = "查询日志原始文件下载地址")
    @GetMapping("/download_urls")
    public ListObjServiceResult<LogFileDownloadUrlDto> getDownloadUrl(LogFileDownloadUrlRequest request) {

        SceneElementMapper elementMapper = BeanCommon.getBean(SceneElementMapper.class);
        var em = elementMapper.first(new HumanCondition().filterEqual(SceneElementModel::getId,request.getAgentName()));
        if(em != null){
            String sceneId= em.getScene();
            var s = new SceneHandler(sceneId, null, false, new SceneContextBO());
            SceneElementBO elementBO = null;
            for (var e : s.getElementMap().values()) {
                String type = SystemTypeUtils.getStandardSystemType(e.getSystemType());
                if (BaseSceneElementModel.SystemType.UNIX.equals(type)) {
                    elementBO = e;
                    break;
                }
            }
            if (elementBO != null) {
                CloudScenePluginHandler pluginHandler = s.getPluginHandler(PluginConstant.PLUGIN_KEY);
                String ip = null;
                Integer port = null;
                String usename = null;
                String password = null;
                String portIp = null;
                for (var p : elementBO.getPorts()) {
                    if (StringUtils.isNotEmpty(ip)) {
                        break;
                    }
                    var pluginPort = pluginHandler.getPort(p.getId());
                    if (pluginPort.getAccessModes() == null) {
                        continue;
                    }
                    for (var a : pluginPort.getAccessModes()) {
                        if (AccessMode.Protocol.SSH.equals(a.getProtocol())) {
                            ip = a.getAccessIp();
                            port = a.getAccessPort();
                            usename = a.getUsername();
                            password = a.getPassword();
                            portIp = p.getIp();
                            break;
                        }
                    }
                }
                if (StringUtils.isNotEmpty(ip)) {
                    String dir = BeanCommon.getBean(BaseAppConfig.class).getResourcesDir();
                    String path1 = request.getPaths().get(0);
                    String path2 = "/tmp/" + TextUtils.uuid() + "_" + path1.substring(path1.lastIndexOf("/") + 1);
                    getData2(dir + "/ssh_run_cmd2.py", ip, port, usename, password,path1,path2);
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    var url = FsCommon.upload(path2);
                    List<LogFileDownloadUrlDto> res = new ArrayList<>();
                    LogFileDownloadUrlDto dto = new LogFileDownloadUrlDto();
                    dto.setDownloadUrl("/api/mp/file/"+url);
                    dto.setPath(request.getPaths().get(0));
                    dto.setAgentName(elementBO.getEid());
                    dto.setName(path1.substring(path1.lastIndexOf("/") + 1));
                    dto.setOsType("unix");
                    res.add(dto);
                    return new ListObjServiceResult(new ListObjResult(res, 1)).asResult();
                }
            }
        }

        List<LogFileDownloadUrlDto> downloadUrlDtos = logFileService.getDownloadUrls(request);
        return new ListObjServiceResult(new ListObjResult(downloadUrlDtos, downloadUrlDtos.size())).asResult();

    }

}
