package cn.cyberpeace.mp.base.service.impl;

import cn.cyberpeace.mp.base.bo.DiffTargets;
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

    protected boolean judgeKeepObjMapping(LocalFileMappingModel newObjMapping, LocalFileMappingModel existObjMapping) {
        return super.judgeKeepObjMapping(newObjMapping, existObjMapping)
                && existObjMapping.getLocalFile().equals(newObjMapping.getLocalFile())
                && existObjMapping.getLocalFileModifiedTime().equals(newObjMapping.getLocalFileModifiedTime());
    }

    @Override
    public ServiceResult<List<LocalFileMappingModel>> getMappings(String group) {
        return new ServiceResult<>(getObjMappings(group));
    }

    /**
     * 同步本地文件和远程文件映射，本地文件有变化就更新，根据文件最后修改时间判断
     * @param group 文件映射组
     * @param mappings 文件映射本地文件信息
     * @return 最新的本地文件和远程文件映射关系
     */
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
                // TODO 上传远程文件
                remoteFile = "";
            } catch (Exception e) {
                log.error("Upload local file({}) error: {}", mapping.getLocalFile(), e);
                // TODO 删除上传的远程文件
                throw e;
            }
            savedRemoteFiles.add(remoteFile);
            mapping.setRemoteFile(remoteFile);
        }

        try {
            saveDiffObjMappings(diff);
        } catch (Exception e) {
            log.error("Save local file mappings error: ", e);
            // TODO 删除上传的远程文件
            throw e;
        }

        return new ServiceResult<>(collectDiffObjMappings(diff));
    }
}
