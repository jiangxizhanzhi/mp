package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.InjectFileService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.ScenariosService;
import cn.cyberpeace.mp.devops.cluster.converter.model.InjectFileConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.InjectFileListConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.DeviceInjectFileDelete;
import cn.cyberpeace.mp.devops.cluster.model.common.InjectFile;
import cn.cyberpeace.mp.devops.cluster.request.ImageInjectFileDeleteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public InjectFileListConvertModel list(String deviceUuid) {
        CloudPlatformPageResult<InjectFile> model = injectFileService.list(deviceUuid);
        List<InjectFileConvertModel> list = model.getList().stream().map(item ->
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
    public BaseCloudPlatformResult delete(ImageInjectFileDeleteRequest request) {
        return scenariosService.deleteInjectFile(new DeviceInjectFileDelete(request.getDeviceUuid(),request.getInjectUuid()));
    }
}
