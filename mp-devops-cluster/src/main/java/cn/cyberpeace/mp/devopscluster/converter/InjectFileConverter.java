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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InjectFileConverter {

    private final InjectFileService injectFileService;

    private final ScenariosService scenariosService;

    
    public BasePageBO<InjectFileConvertModel> list(String deviceUuid) {
        CloudPlatformPageResult<InjectFile> model = injectFileService.list(deviceUuid);
        List<InjectFileConvertModel> list = model.getList().stream().map(item ->
                new InjectFileConvertModel(item.getUuid(), item.getName(), item.getDestPath(), item.getSize(),item.getStatus()))
                .collect(Collectors.toList());
        BasePageBO<InjectFileConvertModel> convertModel = new BasePageBO<>();
        convertModel.setList(list);
        convertModel.setSuccess(model.isSuccess());
        return convertModel;
    }

    
    public BaseCloudPlatformResult delete(ImageInjectFileDeleteDTO dto) {
        return scenariosService.deleteInjectFile(new DeviceInjectFileDelete(dto.getDeviceUuid(),dto.getInjectUuid()));
    }
}
