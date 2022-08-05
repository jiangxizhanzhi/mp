package cn.cyberpeace.mp.sceneplugincloud.init.jobs;

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
public class CloudMessageConvertInitJob extends AbstractInitJob {
    @Resource
    private CloudCommonConfigService cloudCommonConfigService;

    @Override
    protected boolean doExecute() {
        CloudMessageConvertUtil.init(cloudCommonConfigService.getCloudMessageConvert().getSource());
        return true;
    }

    @Override
    protected boolean getSupportRefresh() {
        return false;
    }
}
