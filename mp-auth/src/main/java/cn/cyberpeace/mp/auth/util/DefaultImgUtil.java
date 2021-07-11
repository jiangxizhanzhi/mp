package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.DefaultImgDirEnum;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.base.repository.LocalFileMappingMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Component
@Slf4j
public class DefaultImgUtil {

    private static LocalFileMappingMapper localFileMappingMapper;

    public DefaultImgUtil(LocalFileMappingMapper localFileMappingMapper) {
        DefaultImgUtil.localFileMappingMapper = localFileMappingMapper;
    }

    
    public static String pathToFdfsUrl(String path) {
        if (StringUtils.isNotBlank(path) && !FsCommon.isFileServerOuterUrl(path)) {



            HumanCondition humanCondition = new HumanCondition();
            humanCondition.filterEqual(LocalFileMappingModel::getGroup, splitToGroup(path))
                    .filterEqual(LocalFileMappingModel::getName, splitToName(path));
            List<LocalFileMappingModel> fileMappingModels = localFileMappingMapper.list(humanCondition);
            if (CollectionUtils.isNotEmpty(fileMappingModels)) {
                path = FsCommon.genHttpUrl(fileMappingModels.get(0).getRemoteFile());
            }
        }
        if (!FsCommon.isFileServerOuterUrl(path)) {
            path = FsCommon.genHttpUrl(path);
        }

        return path;
    }

    
    public static String pickPathToFdfsUrl(String path, List<LocalFileMappingModel> mappingModelList) {
        if (StringUtils.isNotBlank(path) && !FsCommon.isFileServerOuterUrl(path) &&
                CollectionUtils.isNotEmpty(mappingModelList)) {
            String mayDefaultPath = path ;
            if (!path.startsWith(AuthConstant.DEFAULT_IMAGE_PREFIX)) {
                mayDefaultPath = AuthConstant.DEFAULT_IMAGE_PREFIX.concat(path);
            }
            String group = splitToGroup(mayDefaultPath);
            String name = splitToName(mayDefaultPath);
            Optional<LocalFileMappingModel> mappingModelOptional =
                    mappingModelList.stream().filter(model -> model.getGroup().equals(group) && model.getName().equals(name)).findFirst();

            if (mappingModelOptional.isPresent()) {
                path = FsCommon.genHttpUrl(mappingModelOptional.get().getRemoteFile());
            }
        }
        if (!FsCommon.isFileServerOuterUrl(path)) {
            path = FsCommon.genHttpUrl(path);
        }

        return path;
    }

    
    public static String fdfsUrlToPath(String url) {
        if (StringUtils.isNotBlank(url) && url.startsWith(AuthConstant.FDFS_PREFIX)) {
            String tmpUrl = url.substring(6);

            HumanCondition humanCondition = new HumanCondition();
            humanCondition.filterEqual(LocalFileMappingModel::getRemoteFile, tmpUrl);
            List<LocalFileMappingModel> mappingModels = localFileMappingMapper.list(humanCondition);
            if (CollectionUtils.isNotEmpty(mappingModels)) {
                url = getPathByGroup(mappingModels.get(0).getGroup()).concat(FileUtil.FILE_SEPARATOR).concat(mappingModels.get(0).getName());
            }
        }

        return url;
    }

    
    public static List<LocalFileMappingModel> listByGroups(List<String> groupList) {
        List<LocalFileMappingModel> modelList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(groupList)) {
            if (DefaultImgDirEnum.TARGET.getGroup().equals(groupList.get(0))) {
                groupList = Arrays.asList(DefaultImgDirEnum.APPLICATION_SYSTEM.getGroup(),
                        DefaultImgDirEnum.NETWORK_EQUIPMENT.getGroup(),DefaultImgDirEnum.OPERATION_SYSTEM.getGroup(),
                        DefaultImgDirEnum.SAFETY_EQUIPMENT.getGroup(), DefaultImgDirEnum.COMMON_TARGET.getGroup());
            }

            HumanCondition humanCondition = new HumanCondition();
            humanCondition.filterIn(LocalFileMappingModel::getGroup, groupList.toArray());
            List<LocalFileMappingModel> fileMappingModels = localFileMappingMapper.list(humanCondition);
            if (CollectionUtils.isNotEmpty(fileMappingModels)) {
                modelList = fileMappingModels;
            }
        }

        return modelList;
    }

    
    public static String getPathByGroup(String group) {
        for (DefaultImgDirEnum imgDirEnum : DefaultImgDirEnum.values()) {
            if (imgDirEnum.getGroup().equals(group)) {
                return AuthConstant.DEFAULT_IMAGE_PREFIX.concat(imgDirEnum.getEndPathName());
            }
        }

        return StringUtils.EMPTY;
    }

    
    public static String getGroupByPath(String path) {
        for (DefaultImgDirEnum imgDirEnum : DefaultImgDirEnum.values()) {
            if (imgDirEnum.getCurrentPath().equals(path) || imgDirEnum.getEndPathName().equals(path)) {
                return imgDirEnum.getGroup();
            }
        }

        return StringUtils.EMPTY;
    }

    
    public static String splitToGroup(String path) {
        path = path.substring(0, path.lastIndexOf(AuthConstant.ORG_PATH_SEPARATOR));
        return getGroupByPath(path);
    }

    
    public static String splitToName(String path) {
        return path.substring(path.lastIndexOf(AuthConstant.ORG_PATH_SEPARATOR) + 1);
    }

}
