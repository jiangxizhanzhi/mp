package cn.cyberpeace.mp.frontadminbusiness.init;

import cn.cyberpeace.mp.frontadmin.constant.SysConfigConstant;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;
import cn.cyberpeace.mp.frontadmin.service.sys.SysConfigService;
import cn.cyberpeace.mp.frontadmin.service.sys.SysLogConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

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


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        SysConfigModel sysConfigModel = sysConfigService.getByKeyName(SysConfigConstant.LOG_FILE_MAX_HISTORY_KEY);
        if (Objects.nonNull(sysConfigModel)) {
            sysLogConfigService.setLogBackInfo(Integer.valueOf(sysConfigModel.getValue()));
        }
    }
}
