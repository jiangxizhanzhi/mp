 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudMessageConvertInitJob.java.
 // File size 905.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.sceneplugincloud.service.CloudCommonConfigService;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudmessageconvert.CloudMessageConvertUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName CloudMessageConvertInitJob
 * @Description 初始化云平台错误码转换的配置
 * @Author maojun
 * @Date 2022/8/5
 * @Version 1.0.0
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
