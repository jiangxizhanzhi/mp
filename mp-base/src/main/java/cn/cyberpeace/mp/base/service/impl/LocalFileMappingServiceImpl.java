package cn.cyberpeace.mp.base.service.impl;

import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.base.repository.LocalFileMappingMapper;
import cn.cyberpeace.mp.base.service.LocalFileMappingService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class LocalFileMappingServiceImpl
        extends BaseObjMappingServiceImpl<LocalFileMappingModel, LocalFileMappingMapper>
        implements LocalFileMappingService {

    @Autowired
    public LocalFileMappingServiceImpl(LocalFileMappingMapper modelMapper) {
        setModelMapper(modelMapper);
    }

    protected boolean judgeMergeObjChanged(LocalFileMappingModel newObjMapping, LocalFileMappingModel existObjMapping) {
        var flag = super.judgeMergeObjChanged(newObjMapping, existObjMapping);
        if (existObjMapping != null) {
            if (existObjMapping.getLocalFile()
                    .equals(newObjMapping.getLocalFile())
                    || existObjMapping.getLocalFileModifiedTime()
                    .equals(newObjMapping.getLocalFileModifiedTime())) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public ServiceResult<List<LocalFileMappingModel>> getMappings(String group) {
        return new ServiceResult<>(getObjMappings(group));
    }

    @Override
    public ServiceResult<List<LocalFileMappingModel>> syncMappings(String group, List<LocalFileMappingModel> mappings) {
        List<String> savedRemoteFiles = new ArrayList<>();
        for (var mapping : mappings) {
            var localFile = mapping.getLocalFile();
            var modifiedTime = new File(localFile).lastModified();
            mapping.setLocalFileModifiedTime(modifiedTime);
        }

        var resultObjMappings = syncObjMappings(group, mappings);
        return new ServiceResult<>(resultObjMappings);
    }
}
