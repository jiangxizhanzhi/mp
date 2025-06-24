 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LocalFileMappingServiceImpl.java.
 // File size 4308.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
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


/**
 * @ClassName LocalFileMappingServiceImpl
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseObjMappingServiceImpl
 * @see LocalFileMappingService
 */
@Slf4j
@Service
public class LocalFileMappingServiceImpl
        extends BaseObjMappingServiceImpl<LocalFileMappingModel, LocalFileMappingMapper>
        implements LocalFileMappingService {

    /**
     * LocalFileMappingServiceImpl
     *
     * @param modelMapper
     */
    @Autowired
    public LocalFileMappingServiceImpl(LocalFileMappingMapper modelMapper) {
        setModelMapper(modelMapper);
    }

    /**
     * removeRemoteFiles
     *
     * @param remoteFiles
     */
    private void removeRemoteFiles(List<String> remoteFiles) {
        for (String remoteFile : remoteFiles) {
            try {
                log.info("delete remoteFile");
            } catch (Exception e) {
                log.error("Remove remote file error: ", e);
            }
        }
    }

    /**
     * judgeKeepObjMapping
     *
     * @param newObjMapping
     * @param existObjMapping
     * @return boolean
     */
    protected boolean judgeKeepObjMapping(LocalFileMappingModel newObjMapping, LocalFileMappingModel existObjMapping) {
        return super.judgeKeepObjMapping(newObjMapping, existObjMapping)
                && existObjMapping.getLocalFile().equals(newObjMapping.getLocalFile())
                && existObjMapping.getLocalFileModifiedTime().equals(newObjMapping.getLocalFileModifiedTime());
    }

    /**
     * 同步本地文件和远程文件映射，本地文件有变化就更新，根据文件最后修改时间判断
     * @param group 文件映射组
     * @param mappings 文件映射本地文件信息
     * @return 最新的本地文件和远程文件映射关系
     */
    @Override
    public ServiceResult<List<LocalFileMappingModel>> syncMappings(String group, List<LocalFileMappingModel> mappings) {
        for (LocalFileMappingModel mapping : mappings) {
            String localFile = mapping.getLocalFile();
            long modifiedTime = new File(localFile).lastModified();
            mapping.setLocalFileModifiedTime(modifiedTime);
        }

        DiffTargets<LocalFileMappingModel> diff = diffObjMappings(group, mappings);
        List<String> savedRemoteFiles = new ArrayList<>();
        for (LocalFileMappingModel mapping : ListUtils.union(diff.getAddingTargets(), diff.getUpdatingTargets())) {
            LocalFileMappingModel existMapping = diff.getExistTargetMap().getOrDefault(mapping.getName(), null);
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
