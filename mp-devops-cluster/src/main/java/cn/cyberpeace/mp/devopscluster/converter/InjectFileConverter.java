 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name InjectFileConverter.java.
 // File size 2315.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.InjectFileService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.ScenariosService;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.converter.model.InjectFileConvertModel;
import cn.cyberpeace.mp.devopscluster.dto.ImageInjectFileDeleteDTO;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.DeviceInjectFileDelete;
import cn.cyberpeace.mp.devopscluster.model.common.InjectFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName InjectFileConverter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class InjectFileConverter {

    private final InjectFileService injectFileService;

    private final ScenariosService scenariosService;

    /**
     * 查询注入文件列表
     * @param deviceUuid
     * @return
     */
    public BasePageBO<InjectFileConvertModel> list(String deviceUuid) {
        CloudPlatformPageResult<InjectFile> model = injectFileService.list(deviceUuid);
        List<InjectFileConvertModel> list = new ArrayList<>();
        for (InjectFile item : model.getList()) {
            InjectFileConvertModel injectFileConvertModel = new InjectFileConvertModel(item.getUuid(), item.getName(), item.getDestPath(), item.getSize(), item.getStatus());
            list.add(injectFileConvertModel);
        }
        BasePageBO<InjectFileConvertModel> convertModel = new BasePageBO<>();
        convertModel.setList(list);
        convertModel.setSuccess(model.isSuccess());
        return convertModel;
    }

    /**
     * 删除注入文件
     * @param dto
     * @return
     */
    public BaseCloudPlatformResult delete(ImageInjectFileDeleteDTO dto) {
        return scenariosService.deleteInjectFile(new DeviceInjectFileDelete(dto.getDeviceUuid(),dto.getInjectUuid()));
    }
}
