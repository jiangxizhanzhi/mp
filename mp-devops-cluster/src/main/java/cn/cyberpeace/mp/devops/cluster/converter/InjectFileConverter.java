package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.InjectFileService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.ScenariosService;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.converter.model.InjectFileConvertModel;
import cn.cyberpeace.mp.devops.cluster.dto.ImageInjectFileDeleteDTO;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.DeviceInjectFileDelete;
import cn.cyberpeace.mp.devops.cluster.model.common.InjectFile;
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

    /**
     * 删除注入文件
     * @param dto
     * @return
     */
    public BaseCloudPlatformResult delete(ImageInjectFileDeleteDTO dto) {
        return scenariosService.deleteInjectFile(new DeviceInjectFileDelete(dto.getDeviceUuid(),dto.getInjectUuid()));
    }
}
