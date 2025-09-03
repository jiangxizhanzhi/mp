package cn.cyberpeace.mp.cscene.controller.ai;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.situationaware.utils.FileUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MessageBuilder {

    private static String json1 = null;

    private static String node_data = null;

    private static String topo_data = null;

    private static String config_data = null;

    public static void init() {
        org.springframework.core.io.Resource[] resources = null;
        try {
            resources = new PathMatchingResourcePatternResolver().getResources(ResourceUtils.CLASSPATH_URL_PREFIX + "config/topo1.json");
            json1 = FileUtils.readLine(resources);
            File file = new File("topo1.json");
            if (file.exists()) {
                json1 = org.apache.commons.io.FileUtils.readFileToString(file, "utf-8");
            }

            resources = new PathMatchingResourcePatternResolver().getResources(ResourceUtils.CLASSPATH_URL_PREFIX + "config/node_data.json");
            node_data = FileUtils.readLine(resources);
            file = new File("node_data.json");
            if (file.exists()) {
                node_data = org.apache.commons.io.FileUtils.readFileToString(file, "utf-8");
            }

            resources = new PathMatchingResourcePatternResolver().getResources(ResourceUtils.CLASSPATH_URL_PREFIX + "config/topo_data.json");
            topo_data = FileUtils.readLine(resources);
            file = new File("topo_data.json");
            if (file.exists()) {
                topo_data = org.apache.commons.io.FileUtils.readFileToString(file, "utf-8");
            }

            resources = new PathMatchingResourcePatternResolver().getResources(ResourceUtils.CLASSPATH_URL_PREFIX + "config/config_data.json");
            config_data = FileUtils.readLine(resources);
            file = new File("config_data.json");
            if (file.exists()) {
                config_data = org.apache.commons.io.FileUtils.readFileToString(file, "utf-8");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<AiController.Message> topo(String csceneId, String templateId, String configFileUrl) {
        // 每次都取一下最新的数据
        init();

        var list = JsonCommon.parse(topo_data, new TypeReference<List<AiController.Message>>() {
        });
        for (var m : list) {
            var url = StringUtils.replace(m.getUrl(), "{id}", csceneId);
            url = StringUtils.replace(url, "{template_id}", templateId);
            m.setUrl(url);

            var content = StringUtils.replace(m.getContent(), "{url}", configFileUrl);
            m.setContent(content);
        }
        return list;
    }

    public static SceneConfigDataBO getTopo() {
        // 每次都取一下最新的数据
        init();

        return JsonCommon.parse(json1, SceneConfigDataBO.class);
    }

    public static List<AiController.Message> node() {
        // 每次都取一下最新的数据
        init();

        var list = JsonCommon.parse(node_data, new TypeReference<List<AiController.Message>>() {
        });
        return list;
    }

    public static List<AiController.Message> config() {
        // 每次都取一下最新的数据
        init();

        var list = JsonCommon.parse(config_data, new TypeReference<List<AiController.Message>>() {
        });
        return list;
    }
}
