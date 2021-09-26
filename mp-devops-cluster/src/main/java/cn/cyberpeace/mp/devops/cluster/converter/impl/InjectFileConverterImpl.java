package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.devops.cluster.converter.InjectFileConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.InjectFileConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.InjectFileListConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.request.ImageInjectFileDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.service.InjectFileService;
import cn.cyberpeace.mp.devops.cluster.service.ScenariosSnapshotService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class InjectFileConverterImpl implements InjectFileConverter {

    private InjectFileService injectFileService;

    private ScenariosSnapshotService scenariosSnapshotService;

    @Autowired
    public InjectFileConverterImpl(InjectFileService injectFileService,ScenariosSnapshotService scenariosSnapshotService) {
        this.injectFileService = injectFileService;
        this.scenariosSnapshotService = scenariosSnapshotService;
    }

    @Override
    public InjectFileListConvertModel list(String deviceUuid) {
        InjectFileCloudPlatformModel model = injectFileService.list(deviceUuid);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getItems())){
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

    @Override
    public Boolean create(SnapshotCreate snapshotCreate) {
        return null;
    }

    @Override
    public BaseCloudPlatformModel delete(ImageInjectFileDeleteRequest request) {
        return scenariosSnapshotService.deleteInjectFile(new DeviceInjectFileDelete(request.getDeviceUuid(),request.getInjectUuid()));
    }
}
