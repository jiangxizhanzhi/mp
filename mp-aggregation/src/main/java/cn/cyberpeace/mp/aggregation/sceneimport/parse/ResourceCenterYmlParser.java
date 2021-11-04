package cn.cyberpeace.mp.aggregation.sceneimport.parse;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.YmlParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.ImageParseResult;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 解析资管的yml
 * @Author maojun
 * @Date 2021/10/12
 */
public final class ResourceCenterYmlParser {
    private static String FILE_PATH_SEPARATOR = "/";

    private static Logger log = LoggerFactory.getLogger(ResourceCenterYmlParser.class);

    public static YmlParseResult parseTopoYml(String filePath) {
        return new FilePathYmlParser<>(filePath + FILE_PATH_SEPARATOR + "topo.yml", YmlParseResult.class).parse();
    }

    public static List<ImageParseResult> parseImageServicesYml(String filePath) {
        File file = new File(filePath + FILE_PATH_SEPARATOR + "images");
        if (!file.exists() || !file.isDirectory() || file.listFiles() == null || file.listFiles().length == 0) {
            return new ArrayList<>();
        }
        File[] imageFiles = file.listFiles();
        // 取images目录下存在的servers.yml文件路径
        List<String> serversYmlPaths = Arrays.asList(imageFiles).stream()
                .map(imageFile -> imageFile.getPath() + FILE_PATH_SEPARATOR + "servers.yml")
                .filter(serversYmlPath -> new File(serversYmlPath).exists())
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(serversYmlPaths)) {
            return new ArrayList<>();
        }
        // 解析所有的 servers.yml
        return serversYmlPaths.stream()
                .map(serversYmlPath -> new FilePathYmlParser<>(serversYmlPath, ImageParseResult.class).parse())
                .collect(Collectors.toList());
    }
}
