package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.InjectFileService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.ScenariosService;
import cn.cyberpeace.mp.devops.cluster.converter.model.InjectFileConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.InjectFileListConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.DeviceInjectFileDelete;
import cn.cyberpeace.mp.devops.cluster.model.InjectFileCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.ImageInjectFileDeleteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class InjectFileConverter {

    private InjectFileService injectFileService;

    private ScenariosService scenariosService;

    @Autowired
    public InjectFileConverter(InjectFileService injectFileService, ScenariosService scenariosService) {
        this.injectFileService = injectFileService;
        this.scenariosService = scenariosService;
    }

    /**
     * 查询注入文件列表
     * @param deviceUuid
     * @return
     */
    public InjectFileListConvertModel list(String deviceUuid) {
        InjectFileCloudPlatformModel model = injectFileService.list(deviceUuid);
        if (Objects.isNull(model)){
            return null;
        }
        List<InjectFileConvertModel> list = model.getItems().stream().map(item ->
                new InjectFileConvertModel(item.getUuid(), item.getName(), item.getDestPath(), item.getSize(),item.getStatus()))
                .collect(Collectors.toList());
        InjectFileListConvertModel convertModel = new InjectFileListConvertModel();
        convertModel.setModelList(list);
        convertModel.setSuccess(model.isSuccess());
        return convertModel;
    }

    /**
     * 删除注入文件
     * @param request
     * @return
     */
    public BaseCloudPlatformModel delete(ImageInjectFileDeleteRequest request) {
        return scenariosService.deleteInjectFile(new DeviceInjectFileDelete(request.getDeviceUuid(),request.getInjectUuid()));
    }
}
