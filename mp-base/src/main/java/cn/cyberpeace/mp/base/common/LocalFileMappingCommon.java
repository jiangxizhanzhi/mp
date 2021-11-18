package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.base.service.LocalFileMappingService;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.PathUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class LocalFileMappingCommon {

    public static LocalFileMappingService localFileMappingService;

    private LocalFileMappingCommon(LocalFileMappingService localFileMappingService) {
        LocalFileMappingCommon.localFileMappingService = localFileMappingService;
    }

    public static List<LocalFileMappingModel> getMappings(String group) {
        return localFileMappingService.getMappings(group).getSource();
    }

    public static List<LocalFileMappingModel> syncMappings(String group, List<LocalFileMappingModel> mappings) {
        return localFileMappingService.syncMappings(group, mappings).getSource();
    }

    public static List<LocalFileMappingModel> syncMap(String group, Map<String, String> namePathMap) {
        List<LocalFileMappingModel> mappings = new ArrayList<>();
        namePathMap.forEach((name, filepath) -> {
            var mapping = new LocalFileMappingModel();
            mapping.setName(name);
            mapping.setLocalFile(filepath);
            mappings.add(mapping);
        });
        return syncMappings(group, mappings);
    }

    public static List<LocalFileMappingModel> syncDir(String group, String localDir) {
        List<String> fileNames = FileUtil.listFileNames(localDir);
        Map<String, String> namePathMap = new LinkedHashMap<>();
        fileNames.forEach(fileName -> {
            namePathMap.put(fileName, StringUtils.joinWith(FileUtil.FILE_SEPARATOR, localDir, fileName));
        });
        return syncMap(group, namePathMap);
    }

}
