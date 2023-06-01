package cn.cyberpeace.mp.frontadminbusiness.init;

import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.frontadmin.config.FrontAdminConfig;
import cn.cyberpeace.mp.frontadmin.constant.SysConfigConstant;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;
import cn.cyberpeace.mp.frontadmin.model.SysDeployLogModel;
import cn.cyberpeace.mp.frontadmin.service.sys.SysConfigService;
import cn.cyberpeace.mp.frontadmin.service.sys.SysDeployLogService;
import cn.cyberpeace.mp.frontadmin.service.sys.SysLogConfigService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2023/05/31 09:22
 */
@Component
@Slf4j
public class FrontAdminInitListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private SysLogConfigService sysLogConfigService;

    @Autowired
    private FrontAdminConfig frontAdminConfig;

    @Autowired
    private SysDeployLogService deployLogService;


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        SysConfigModel sysConfigModel = sysConfigService.getByKeyName(SysConfigConstant.LOG_FILE_MAX_HISTORY_KEY);
        if (Objects.nonNull(sysConfigModel)) {
            sysLogConfigService.setLogBackInfo(Integer.valueOf(sysConfigModel.getValue()));
        }

        TaskCommon.executeTask(() -> {
            String filePath = frontAdminConfig.getDeployLogPath();
            if (!FileUtil.exist(filePath)) {
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                final List<SysDeployLogModel> logModels = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    // 处理每一行记录
                    String[] strArr = StringUtils.split(line, "###");
                    if (strArr.length != 6) {
                        log.error("deploy log format error:{}", line);
                        throw new MpRuntimeException("deploy log format error");
                    }

                    SysDeployLogModel logModel = new SysDeployLogModel();
                    logModel.setId(strArr[0]);
                    logModel.setSystem(strArr[1]);
                    logModel.setVersion(strArr[2]);
                    logModel.setDescription(strArr[3]);
                    logModel.setStatus(Integer.parseInt(strArr[4]));
                    logModel.setCreateTime(TimeUtils.stringToLocalDateTime(strArr[5]));
                    logModel.setUpdateTime(TimeUtils.stringToLocalDateTime(strArr[5]));
                    logModels.add(logModel);
                }

                if (CollectionUtil.isNotEmpty(logModels)) {
                    var ids = logModels.stream().map(SysDeployLogModel::getId).collect(Collectors.toList());
                    String[] idArr = ids.toArray(new String[0]);
                    QueryParams queryParams = new QueryParams();
                    queryParams.setIds(idArr);

                    var result = deployLogService.list(queryParams);
                    var existedLogs = result.getSource().getObjs();
                    List<SysDeployLogModel> logsToSave = logModels;
                    if (CollectionUtil.isNotEmpty(existedLogs)) {
                        var existedIds = existedLogs.stream().map(SysDeployLogModel::getId).collect(Collectors.toList());
                        logsToSave = logModels.stream().filter(logModel -> !existedIds.contains(logModel.getId())).collect(Collectors.toList());
                    }

                    if (CollectionUtil.isNotEmpty(logsToSave)) {
                        deployLogService.bulkCreate(logsToSave);
                    }
                }

            } catch (IOException e) {
                log.error("read deploy log error:{}", ExceptionUtil.stacktraceToOneLineString(e));
            }
        });
    }
}
