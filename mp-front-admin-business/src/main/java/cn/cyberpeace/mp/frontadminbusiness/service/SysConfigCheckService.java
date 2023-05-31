package cn.cyberpeace.mp.frontadminbusiness.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.frontadmin.constant.AdminErrorCode;
import cn.cyberpeace.mp.frontadmin.constant.SysConfigConstant;
import cn.cyberpeace.mp.frontadmin.constant.SysConfigKey;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;
import cn.cyberpeace.mp.frontadminbusiness.constant.AdminBusinessErrorCode;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/9 17:34
 */
@Slf4j
@Service
public class SysConfigCheckService {

    public void updateCheck(List<SysConfigModel> sysConfigs) {
        if (CollectionUtil.isEmpty(sysConfigs)) {
            throw new MpRuntimeException(AdminErrorCode.PARAMS_ERROR);
        }

        for (SysConfigModel sysConfig : sysConfigs) {
            if (Objects.equals(sysConfig.getKeyName(), SysConfigKey.SCENE_AUTO_CLEAN_INTERVAL_DAY)) {
                String value = sysConfig.getValue();
                Integer intVal = Integer.valueOf(value);
                if (intVal < 0) {
                    throw new MpRuntimeException(AdminBusinessErrorCode.SYS_CONFIG_ERROR);
                }
            }

            if (Objects.equals(sysConfig.getKeyName(), SysConfigConstant.LOG_FILE_MAX_HISTORY_KEY)) {
                String value = sysConfig.getValue();
                if (!NumberUtil.isInteger(value) || Integer.parseInt(value) < 0) {
                    throw new MpRuntimeException(AdminBusinessErrorCode.LOG_FILE_MAX_HISTORY);
                }
            }

            if (Objects.equals(sysConfig.getKeyName(), SysConfigConstant.SYSTEM_BACKUP_INTERVAL_KEY)) {
                String value = sysConfig.getValue();
                if (!NumberUtil.isInteger(value) || Integer.parseInt(value) < 0) {
                    throw new MpRuntimeException(AdminBusinessErrorCode.BACKUP_INTERVAL);
                }
            }
        }

    }

}
