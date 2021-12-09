package cn.cyberpeace.mp.front.admin.business.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.front.admin.constant.AdminErrorCode;
import cn.cyberpeace.mp.front.admin.constant.SysConfigKey;
import cn.cyberpeace.mp.front.admin.model.SysConfigModel;
import cn.hutool.core.collection.CollectionUtil;
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
            throw new MpRuntimeException("更新的系统配置不能为空！");
        }

        for (SysConfigModel sysConfig : sysConfigs) {
            if (Objects.equals(sysConfig.getKeyName(), SysConfigKey.SCENE_AUTO_CLEAN_INTERVAL_DAY)) {
                String value = sysConfig.getValue();
                Integer intVal = Integer.valueOf(value);
                if (intVal < 0) {
                    throw new MpRuntimeException(AdminErrorCode.SYS_CONFIG_ERROR, "场景数据自动清除天数不能小于0！");
                }
            }
        }

    }

}
