package cn.cyberpeace.mp.sceneplugincloud.init.jobs;

import cn.cyberpeace.mp.sceneplugincloud.service.CloudCommonConfigService;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudmessageconvert.CloudMessageConvertUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


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
