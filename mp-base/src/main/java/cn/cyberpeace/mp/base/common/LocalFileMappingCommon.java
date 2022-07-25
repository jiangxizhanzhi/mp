package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.event.LocalFileSyncEventPublisher;
import cn.cyberpeace.mp.base.event.LocalFileSyncSuccessEvent;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.base.service.LocalFileMappingService;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class LocalFileMappingCommon {

    public static LocalFileMappingService localFileMappingService;

    public static LocalFileSyncEventPublisher eventPublisher;

    @Autowired
    private LocalFileSyncEventPublisher publisher;

    @PostConstruct
    public void init() {
        eventPublisher = this.publisher;
    }

    private LocalFileMappingCommon(LocalFileMappingService localFileMappingService) {
        LocalFileMappingCommon.localFileMappingService = localFileMappingService;
    }

    public static LocalFileMappingModel getMapping(String group, String name) {
        return localFileMappingService.getMapping(group, name).getSource();
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
        List<LocalFileMappingModel> mappings = syncMap(group, namePathMap);
        eventPublisher.publishEvent(new LocalFileSyncSuccessEvent(mappings));
        return mappings;
    }

    public static int deleteMappings(String group) {
        return localFileMappingService.deleteMappings(group).getSource();
    }

}
