 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LocalFileMappingCommon.java.
 // File size 3535.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.base.service.LocalFileMappingService;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName LocalFileMappingCommon
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Component
public class LocalFileMappingCommon {

    public static LocalFileMappingService localFileMappingService;

    /**
     * LocalFileMappingCommon
     *
     * @param localFileMappingService
     */
    private LocalFileMappingCommon(LocalFileMappingService localFileMappingService) {
        LocalFileMappingCommon.localFileMappingService = localFileMappingService;
    }

    /**
     * getMapping
     *
     * @param group
     * @param name
     * @return 本地文件和远程文件映射模型
     */
    public static LocalFileMappingModel getMapping(String group, String name) {
        return localFileMappingService.getMapping(group, name).getSource();
    }

    /**
     * getMappings
     *
     * @param group
     * @return
     */
    public static List<LocalFileMappingModel> getMappings(String group) {
        return localFileMappingService.getMappings(group).getSource();
    }

    /**
     * syncMappings
     *
     * @param group
     * @param mappings
     * @return
     */
    public static List<LocalFileMappingModel> syncMappings(String group, List<LocalFileMappingModel> mappings) {
        return localFileMappingService.syncMappings(group, mappings).getSource();
    }

    /**
     * syncMap
     *
     * @param group
     * @param namePathMap
     * @return
     */
    public static List<LocalFileMappingModel> syncMap(String group, Map<String, String> namePathMap) {
        List<LocalFileMappingModel> mappings = new ArrayList<>();
        for (Map.Entry<String, String> entry : namePathMap.entrySet()) {
            String name = entry.getKey();
            String filepath = entry.getValue();
            LocalFileMappingModel mapping = new LocalFileMappingModel();
            mapping.setName(name);
            mapping.setLocalFile(filepath);
            mappings.add(mapping);
        }
        return syncMappings(group, mappings);
    }

    /**
     * syncDir
     *
     * @param group
     * @param localDir
     * @return
     */
    public static List<LocalFileMappingModel> syncDir(String group, String localDir) {
        log.info("sync file begin, group is {},dir is {}", group, localDir);
        List<String> fileNames = FileUtil.listFileNames(localDir);
        Map<String, String> namePathMap = new LinkedHashMap<>();
        for (String fileName : fileNames) {
            namePathMap.put(fileName, StringUtils.joinWith(FileUtil.FILE_SEPARATOR, localDir, fileName));
        }
        List<LocalFileMappingModel> result = syncMap(group, namePathMap);
        log.info("sync file end, diff is {}", JsonCommon.stringify(result));
        return result;
    }

    /**
     * deleteMappings
     *
     * @param group
     * @return int
     */
    public static int deleteMappings(String group) {
        return localFileMappingService.deleteMappings(group).getSource();
    }

}
