package cn.cyberpeace.mp.aggregation.sceneimport.parse;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NodeElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.YmlParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.AccessModeParseResult;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 解析资管的yml
 * @Author maojun
 * @Date 2021/10/12
 */
public final class ResourceCenterYmlParser {
    private static String FILE_PATH_SEPARATOR = "/";

    private static Logger log = LoggerFactory.getLogger(ResourceCenterYmlParser.class);

    /**
     * parseTopoYml
     *
     * @param filePath
     * @return
     */
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
            for (NodeElementParseResult node : result.getNodes()) {
                if (StringUtils.isNotEmpty(node.getFile())) {
                    node.setFile(filePath + FILE_PATH_SEPARATOR + node.getFile());
                }
            }
        }
        return result;
    }

    /**
     * parseImageServicesYml
     *
     * @param filePath
     * @param imageFilePath
     * @return
     */
    public static List<ImageParseResult> parseImageServicesYml(String filePath, String imageFilePath) {
        List<ImageParseResult> results = new ArrayList<>();
        // env/images 下的目录
        results.addAll(parseServicesYml(filePath + FILE_PATH_SEPARATOR + "images"));

        // imageFilePath 下的目录
        if (!StringUtils.isEmpty(imageFilePath)) {
            results.addAll(parseServicesYml(imageFilePath));
        }

        // env 下的server.yml
        String envServerYmlPath = filePath + FILE_PATH_SEPARATOR + "servers.yml";
        File file = new File(envServerYmlPath);
        if (file.exists()) {
            ImageParseResult imageParseResult = new FilePathYmlParser<>(envServerYmlPath, ImageParseResult.class).parse();
            Map<String, ImageInfoParseResult> servers = imageParseResult.getServers();
            addAllPrivateKey(servers, envServerYmlPath);
            results.add(imageParseResult);
        }
        return results;
    }

    /**
     * parseImageServicesYml
     *
     * @param filePath
     * @param imageFilePathList
     * @return
     */
    public static List<ImageParseResult> parseImageServicesYml(String filePath, List<String> imageFilePathList) {
        List<ImageParseResult> results = new ArrayList<>();
        // env/images 下的目录
        results.addAll(parseServicesYml(filePath + FILE_PATH_SEPARATOR + "images"));

        // imageFilePath 下的目录
        if (CollectionUtils.isNotEmpty(imageFilePathList)) {
            for (String imageFilePath : imageFilePathList) {
                results.addAll(parseServicesYml(imageFilePath));
            }
        }

        // env 下的server.yml
        String envServerYmlPath = filePath + FILE_PATH_SEPARATOR + "servers.yml";
        File file = new File(envServerYmlPath);
        if (file.exists()) {
            ImageParseResult imageParseResult = new FilePathYmlParser<>(envServerYmlPath, ImageParseResult.class).parse();
            Map<String, ImageInfoParseResult> servers = imageParseResult.getServers();
            addAllPrivateKey(servers, envServerYmlPath);
            results.add(imageParseResult);
        }
        return results;
    }

    /**
     * parseServicesYml
     *
     * @param dirPath
     * @return
     */
    private static List<ImageParseResult> parseServicesYml(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory() || file.listFiles() == null || file.listFiles().length == 0) {
            return new ArrayList<>();
        }
        File[] imageFiles = file.listFiles();
        // 取images目录下存在的servers.yml文件路径
        List<String> serversYmlPaths = new ArrayList<>();
        for (File imageFile : Arrays.asList(imageFiles)) {
            String s = imageFile.getPath() + FILE_PATH_SEPARATOR + "servers.yml";
            if (new File(s).exists()) {
                serversYmlPaths.add(s);
            }
        }
        if (CollectionUtils.isEmpty(serversYmlPaths)) {
            return new ArrayList<>();
        }
        // 解析所有的 servers.yml
        List<ImageParseResult> imageParseResult = new ArrayList<>();
        for (String serversYmlPath : serversYmlPaths) {
            ImageParseResult parse = new FilePathYmlParser<>(serversYmlPath, ImageParseResult.class).parse();
            imageParseResult.add(parse);
        }

        for (int i = 0; i < imageParseResult.size(); i++) {
            Map<String, ImageInfoParseResult> servers = imageParseResult.get(i).getServers();
            String path = serversYmlPaths.get(i);
            addAllPrivateKey(servers, path);
        }

        return imageParseResult;
    }

    /**
     * addAllPrivateKey
     *
     * @param servers
     * @param path
     */
    private static void addAllPrivateKey(Map<String, ImageInfoParseResult> servers, String path) {
        if (servers != null && !servers.isEmpty()) {
            Optional<ImageInfoParseResult> result = Optional.empty();
            for (ImageInfoParseResult infoParseResult : servers.values()) {
                result = Optional.of(infoParseResult);
                break;
            }
            List<AccessModeParseResult> accessModes = result.get().getAccessModes();
            Optional<ImageInfoParseResult> found = Optional.empty();
            for (ImageInfoParseResult imageInfoParseResult : servers.values()) {
                found = Optional.of(imageInfoParseResult);
                break;
            }
            List<AccessModeParseResult> manageAccessModes = found.get().getManageAccessModes();

            if (CollectionUtils.isNotEmpty(accessModes)) {
                for (AccessModeParseResult accessMode : accessModes) {
                    if (StringUtils.isNotEmpty(accessMode.getPrivateKey())) {
                        //上传了秘钥,保存秘钥文件地址
                        accessMode.setPrivateKeyPath(StringUtils.replace(path, "servers.yml", "") + accessMode.getPrivateKey());
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(manageAccessModes)) {
                for (AccessModeParseResult manageAccessMode : manageAccessModes) {
                    if (StringUtils.isNotEmpty(manageAccessMode.getPrivateKey())) {
                        //上传了秘钥,保存秘钥文件地址
                        manageAccessMode.setPrivateKeyPath(StringUtils.replace(path, "servers.yml", "") + manageAccessMode.getPrivateKey());
                    }
                }
            }
        }
    }
}
