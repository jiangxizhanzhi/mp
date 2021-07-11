package cn.cyberpeace.mp.base.service.impl;

import cn.cyberpeace.mp.base.bo.DiffTargets;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.base.repository.LocalFileMappingMapper;
import cn.cyberpeace.mp.base.service.LocalFileMappingService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class LocalFileMappingServiceImpl
        extends BaseObjMappingServiceImpl<LocalFileMappingModel, LocalFileMappingMapper>
        implements LocalFileMappingService {

    @Autowired
    public LocalFileMappingServiceImpl(LocalFileMappingMapper modelMapper) {
        setModelMapper(modelMapper);
    }

    private void removeRemoteFiles(List<String> remoteFiles) {
        for (var remoteFile : remoteFiles) {
            try {
                FsCommon.delete(remoteFile);
            } catch (Exception e) {
                log.error("Remove remote file error: ", e);
            }
        }
    }

    protected boolean judgeKeepObjMapping(LocalFileMappingModel newObjMapping, LocalFileMappingModel existObjMapping) {
        return super.judgeKeepObjMapping(newObjMapping, existObjMapping)
                && existObjMapping.getLocalFile().equals(newObjMapping.getLocalFile())
                && existObjMapping.getLocalFileModifiedTime().equals(newObjMapping.getLocalFileModifiedTime());
    }

    
    @Override
    public ServiceResult<List<LocalFileMappingModel>> syncMappings(String group, List<LocalFileMappingModel> mappings) {
        for (var mapping : mappings) {
            var localFile = mapping.getLocalFile();
            var modifiedTime = new File(localFile).lastModified();
            mapping.setLocalFileModifiedTime(modifiedTime);
        }

        DiffTargets<LocalFileMappingModel> diff = diffObjMappings(group, mappings);
        List<String> savedRemoteFiles = new ArrayList<>();
        for (var mapping : ListUtils.union(diff.getAddingTargets(), diff.getUpdatingTargets())) {
            var existMapping = diff.getExistTargetMap().getOrDefault(mapping.getName(), null);
            if (existMapping != null && existMapping.getLocalFile().equals(mapping.getLocalFile())
                    && existMapping.getLocalFileModifiedTime().equals(mapping.getLocalFileModifiedTime())) {
                continue;
            }

            String remoteFile;
            try {
                remoteFile = FsCommon.upload(mapping.getLocalFile());
            } catch (Exception e) {
                log.error("Upload local file({}) error: {}", mapping.getLocalFile(), e);
                removeRemoteFiles(savedRemoteFiles);
                throw e;
            }
            savedRemoteFiles.add(remoteFile);
            mapping.setRemoteFile(remoteFile);
        }

        try {
            saveDiffObjMappings(diff);
        } catch (Exception e) {
            log.error("Save local file mappings error: ", e);
            removeRemoteFiles(savedRemoteFiles);
            throw e;
        }

        return new ServiceResult<>(collectDiffObjMappings(diff));
    }
}
