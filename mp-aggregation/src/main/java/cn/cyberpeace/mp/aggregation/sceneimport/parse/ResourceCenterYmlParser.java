package cn.cyberpeace.mp.aggregation.sceneimport.parse;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.YmlParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.ImageInfoParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.ImageParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.v2.YmlParseResultV2;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public final class ResourceCenterYmlParser {
    private static String FILE_PATH_SEPARATOR = "/";

    private static Logger log = LoggerFactory.getLogger(ResourceCenterYmlParser.class);

    public static YmlParseResult parseTopoYml(String filePath) {
        YmlParseResult result = null;
        File topoV2 = new File(filePath + FILE_PATH_SEPARATOR + "topo2.yml");
        if (topoV2.exists()) {
            try {
                result = new FilePathYmlParser<>(filePath + FILE_PATH_SEPARATOR + "topo2.yml", YmlParseResult.class).parse();
            } catch (Exception e) {
                YmlParseResultV2 resultV2 = new FilePathYmlParser<>(filePath + FILE_PATH_SEPARATOR + "topo2.yml", YmlParseResultV2.class).parse();
                result = resultV2.convert();
            }
        } else {
            File topoV1 = new File(filePath + FILE_PATH_SEPARATOR + "topo.yml");
            if (topoV1.exists()) {
                try {
                    result = new FilePathYmlParser<>(filePath + FILE_PATH_SEPARATOR + "topo.yml", YmlParseResult.class).parse();
                } catch (Exception e) {
                    YmlParseResultV2 resultV2 = new FilePathYmlParser<>(filePath + FILE_PATH_SEPARATOR + "topo.yml", YmlParseResultV2.class).parse();
                    result = resultV2.convert();
                }
            } else {
                String detail = "[" + filePath + FILE_PATH_SEPARATOR + "topo.yml]-文件不存在";
                log.error(detail);
                throw new MpRuntimeException(BaseErrorCode.ERROR, detail);
            }
        }

        if (CollectionUtils.isNotEmpty(result.getNodes())) {
            result.getNodes().forEach(node -> {
                if (StringUtils.isNotEmpty(node.getFile())) {
                    node.setFile(filePath + FILE_PATH_SEPARATOR + node.getFile());
                }
            });
        }
        return result;
    }

    public static List<ImageParseResult> parseImageServicesYml(String filePath, String imageFilePath) {
        List<ImageParseResult> results = new ArrayList<>();
        
        results.addAll(parseServicesYml(filePath + FILE_PATH_SEPARATOR + "images"));

        
        if (!StringUtils.isEmpty(imageFilePath)) {
            results.addAll(parseServicesYml(imageFilePath));
        }

        
        String envServerYmlPath = filePath + FILE_PATH_SEPARATOR + "servers.yml";
        File file = new File(envServerYmlPath);
        if (file.exists()) {
            ImageParseResult imageParseResult = new FilePathYmlParser<>(envServerYmlPath, ImageParseResult.class).parse();
            var servers = imageParseResult.getServers();
            addAllPrivateKey(servers, envServerYmlPath);
            results.add(imageParseResult);
        }
        return results;
    }

    public static List<ImageParseResult> parseImageServicesYml(String filePath, List<String> imageFilePathList) {
        List<ImageParseResult> results = new ArrayList<>();
        
        results.addAll(parseServicesYml(filePath + FILE_PATH_SEPARATOR + "images"));

        
        if (CollectionUtils.isNotEmpty(imageFilePathList)) {
            imageFilePathList.forEach(imageFilePath -> results.addAll(parseServicesYml(imageFilePath)));
        }

        
        String envServerYmlPath = filePath + FILE_PATH_SEPARATOR + "servers.yml";
        File file = new File(envServerYmlPath);
        if (file.exists()) {
            ImageParseResult imageParseResult = new FilePathYmlParser<>(envServerYmlPath, ImageParseResult.class).parse();
            var servers = imageParseResult.getServers();
            addAllPrivateKey(servers, envServerYmlPath);
            results.add(imageParseResult);
        }
        return results;
    }

    private static List<ImageParseResult> parseServicesYml(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory() || file.listFiles() == null || file.listFiles().length == 0) {
            return new ArrayList<>();
        }
        File[] imageFiles = file.listFiles();
        
        List<String> serversYmlPaths = Arrays.asList(imageFiles).stream()
                .map(imageFile -> imageFile.getPath() + FILE_PATH_SEPARATOR + "servers.yml")
                .filter(serversYmlPath -> new File(serversYmlPath).exists())
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(serversYmlPaths)) {
            return new ArrayList<>();
        }
        
        var imageParseResult = serversYmlPaths.stream()
                .map(serversYmlPath -> new FilePathYmlParser<>(serversYmlPath, ImageParseResult.class).parse())
                .collect(Collectors.toList());

        for (int i = 0; i < imageParseResult.size(); i++) {
            var servers = imageParseResult.get(i).getServers();
            var path = serversYmlPaths.get(i);
            addAllPrivateKey(servers, path);
        }

        return imageParseResult;
    }

    private static void addAllPrivateKey(Map<String, ImageInfoParseResult> servers, String path) {
        if (servers != null && !servers.isEmpty()) {
            var accessModes = servers.values().stream().findFirst().get().getAccessModes();
            var manageAccessModes = servers.values().stream().findFirst().get().getManageAccessModes();

            if (CollectionUtils.isNotEmpty(accessModes)) {
                for (var accessMode : accessModes) {
                    if (StringUtils.isNotEmpty(accessMode.getPrivateKey())) {
                        
                        accessMode.setPrivateKeyPath(StringUtils.replace(path, "servers.yml", "") + accessMode.getPrivateKey());
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(manageAccessModes)) {
                for (var manageAccessMode : manageAccessModes) {
                    if (StringUtils.isNotEmpty(manageAccessMode.getPrivateKey())) {
                        
                        manageAccessMode.setPrivateKeyPath(StringUtils.replace(path, "servers.yml", "") + manageAccessMode.getPrivateKey());
                    }
                }
            }
        }
    }
}
