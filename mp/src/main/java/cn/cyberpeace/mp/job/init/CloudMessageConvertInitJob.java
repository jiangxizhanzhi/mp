package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.sceneplugincloud.service.CloudCommonConfigService;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudmessageconvert.CloudMessageConvertUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description 初始化云平台错误码转换的配置
 * @Author maojun
 * @Date 2022/8/5
 */
@Component
public class CloudMessageConvertInitJob {
    @Resource
    private CloudCommonConfigService cloudCommonConfigService;

    @MpInitiate(description = "初始化云平台错误码转换的配置",id="cloud_message_convert_init")
    public void doExecute() {
        CloudMessageConvertUtil.init(cloudCommonConfigService.getCloudMessageConvert().getSource());
    }

}
