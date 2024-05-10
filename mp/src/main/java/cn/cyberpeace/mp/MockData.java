package cn.cyberpeace.mp;

import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.scene.core.bo.*;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneDataBO;
import org.apache.commons.io.FileUtils;
import org.elasticsearch.common.UUIDs;
import oshi.util.FileUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

public class MockData {

    static SceneConfigElementBO createVM() {
        SceneConfigElementBO elementTpl = new SceneConfigElementBO();
        elementTpl.setName("vm-cirros");
        elementTpl.setType(4);
        ;
        elementTpl.setIcon2("/api/mp/file/504/805/726bd707e8ebacd6d1147584f221a5e2.png");
        elementTpl.setSystemLanguage("cn");
        elementTpl.setSystemArchitecture("64");
        elementTpl.setSystemType("other");
        elementTpl.setMaxPortNumber(1);
        elementTpl.setCpu(1);
        elementTpl.setRam(256);
        elementTpl.setDisk(8);
        elementTpl.setFile("");
        elementTpl.setNodeCommand("");

        elementTpl.setEid(UUID.randomUUID().toString());
        elementTpl.setId(UUID.randomUUID().toString());

        SceneConfigElementPortBO port = new SceneConfigElementPortBO();
        port.setId("port_" + elementTpl.getId() + "_1");
        port.setName("eth0/1");

        elementTpl.setPorts(List.of(port));
        return elementTpl;
    }

    static SceneConfigElementBO createSwitch() {
        SceneConfigElementBO elementTpl = new SceneConfigElementBO();
        elementTpl.setName("交换机");
        elementTpl.setType(1);
        ;
        elementTpl.setIcon2("/api/mp/file/639/162/85ce8f4bcad3948c390f774d2916fee2.png");
        elementTpl.setSystemLanguage("other");
        elementTpl.setSystemArchitecture("other");
        elementTpl.setSystemType("other");
        elementTpl.setMaxPortNumber(0);
        elementTpl.setCpu(0);
        elementTpl.setRam(0);
        elementTpl.setDisk(0);
        elementTpl.setFile("");
        elementTpl.setNodeCommand("");

        elementTpl.setEid(UUID.randomUUID().toString());
        elementTpl.setId(UUID.randomUUID().toString());

        List<SceneConfigElementPortBO> ports = new ArrayList<>();
        for (int i = 1; i < 49; i++) {
            SceneConfigElementPortBO port = new SceneConfigElementPortBO();
            port.setId("port_" + elementTpl.getId() + "_electrical_" + i);
            port.setName("E0/" + i);
            ports.add(port);
        }
        elementTpl.setPorts(ports);
        return elementTpl;
    }

    static Map<String, Object> createVMPlugin() {
        Map<String, Object> map = new HashMap<>();
        map.put("role", 4);
        map.put("image_type", "vm");
        map.put("image_uuid", "968fe380-9c4b-4d66-b2e2-85d94a97c09c");
        map.put("is_build_in", false);
        map.put("image", "vm-cirros");
        map.put("electric_port_number", 0);
        map.put("electric_port_type", "E");
        map.put("optical_port_number", 0);
        map.put("optical_port_type", "sfp");
        map.put("init_support", false);
        map.put("category", 2);
        map.put("category2", 31);
        map.put("category3", 0);
        map.put("dhcp", true);
        map.put("wan_port_number", 0);
        map.put("wan_port_type", "E");
        map.put("lan_port_number", 0);
        map.put("lan_port_type", "E");
        return map;
    }

    static Map<String, Object> createSwitchPlugin() {
        Map<String, Object> map = new HashMap<>();
        map.put("resource_id", "");
        map.put("role", 22);
        map.put("image_type", "simple");
        map.put("image_uuid", "66e6244c-90d1-48d6-a615-0762af79e4f2");
        map.put("is_build_in", true);
        map.put("image", "Switch Simple");
        map.put("electric_port_number", 48);
        map.put("electric_port_type", "E");
        map.put("optical_port_number", 0);
        map.put("optical_port_type", "sfp");
        map.put("init_support", false);
        map.put("category", 3);
        map.put("category2", 50);
        map.put("category3", 0);
        map.put("cidr", "192.168.1.0/24");
        map.put("gateway", "192.168.1.254");
        map.put("dhcp", true);
        map.put("wan_port_number", 0);
        map.put("wan_port_type", "E");
        map.put("lan_port_number", 0);
        map.put("lan_port_type", "E");
        map.put("dns_nameservers", new String[]{"8.8.8.8", "114.114.114.114"});
        return map;
    }

    static Map<String, Object> createPluginPorts() {
        Map<String, Object> map = new HashMap<>();
        map.put("access_modes", new Object[]{});
        map.put("firewall_rules", new Object[]{});
        map.put("resource_id", "");
        return map;
    }

    static void createNetwork(List<SceneConfigElementBO> elements, List<SceneConfigElementPortConnectionBO> connections, Map<String, Map<String, Object>> pluginElementsMap, Map<String, Map<String, Object>> pluginElementPortsMap) {
        SceneConfigElementBO aSwitch = createSwitch();
        elements.add(aSwitch);
        pluginElementsMap.put(aSwitch.getId(), createSwitchPlugin());
        aSwitch.getPorts().forEach(port -> {
            // 添加插件端口
            pluginElementPortsMap.put(port.getId(), createPluginPorts());
        });
        // 每个组成单元有1个交换机和48和虚拟机
        for (int i = 0; i < 48; i++) {
            SceneConfigElementBO element = createVM();
            elements.add(element);
            pluginElementsMap.put(element.getId(), createVMPlugin());

            // 生成connection
            SceneConfigElementPortConnectionBO connection = new SceneConfigElementPortConnectionBO();
            connection.setType("straight");
            connection.setSource(element.getPorts().get(0).getId());
            connection.setTarget(aSwitch.getPorts().get(i).getId());
            connection.setName(element.getPorts().get(0).getName() + "_" + aSwitch.getPorts().get(i).getName());
            connections.add(connection);

            // 添加插件端口
            pluginElementPortsMap.put(element.getPorts().get(0).getId(), createPluginPorts());
        }
    }

    static Map<String, Object> createCrData(SceneConfigDataBO bo, String sceneName) {
        Map<String, Object> tpl = new HashMap<>();
        tpl.put("resource_id", "");
        tpl.put("event_data", new HashMap<>() {
            {
                put("logo", "");
                put("name", sceneName);
                put("description", "性能测试");
                put("category", 1);
                put("pre_skill", new String[]{"test"});
                put("train_skill", new String[]{"test"});
                put("difficult", 1);
                put("duration_time", 1);
                put("scene_config_data", new HashMap<>() {{
                    put("roles", new Object[]{});
                    put("logo", "/api/mp/file/987/065/c0a2879e39a2265e5ed673a39b3dbf23.png");
                    put("scene_config", bo);
                }});
            }
        });
        tpl.put("missions_scheme", new HashMap<>() {
            {
                put("color", 1);
                put("template", 1);
                put("scheme_data", new Object[]{});
            }
        });
        return tpl;
    }

    public static void main(String[] args) throws Exception {
        String sceneName = "十万性能测试x";

        SceneConfigDataBO bo = new SceneConfigDataBO();

        SceneConfigTopologyBO topologyBO = new SceneConfigTopologyBO();

        List<SceneConfigElementBO> elements = new ArrayList<>();
        List<SceneConfigElementPortConnectionBO> connections = new ArrayList<>();

        Map<String, Map<String, Object>> pluginElementsMap = new HashMap<>();
        Map<String, Map<String, Object>> pluginElementPortsMap = new HashMap<>();

        for (int i = 0; i < 1; i++) {
            // 创建一个网络（包含一个交换机和48个虚拟机）
            createNetwork(elements, connections, pluginElementsMap, pluginElementPortsMap);
        }

        topologyBO.setElements(elements);
        topologyBO.setConnections(connections);
        SceneConfigBO scene = new SceneConfigBO();
        scene.setName(sceneName);
        scene.setDescription("");
        scene.setFile("");
        scene.setDisplayContext("{\"active\":\"background\",\"enable_flow_collect\":false,\"enable_flow_mirror\":false,\"showLineLabels\":false,\"backgroundList\":[{\"key\":\"background\",\"value\":\"\"},{\"key\":\"background2\",\"value\":\"\"},{\"key\":\"background3\",\"value\":\"\"}],\"simpleModeToEdge\":true,\"graphTranslate\":{\"tx\":0,\"ty\":0},\"graphZoom\":1}");
        topologyBO.setScene(scene);
        topologyBO.setAreas(new ArrayList<>());
        topologyBO.setPieces(new ArrayList<>());
        bo.setTopology(topologyBO);

        ScenePluginDataBO cloudPlugin = new ScenePluginDataBO();
        cloudPlugin.setElements(pluginElementsMap);
        cloudPlugin.setElementPorts(pluginElementPortsMap);
        Map<String, ScenePluginDataBO> plugins = new HashMap<>();
        plugins.put("cpcloud", cloudPlugin);
        bo.setPlugins(plugins);

        // 组装外部结构（模拟调用靶场接口创建）
        Map<String, Object> tpl = createCrData(bo, sceneName);

        FileUtils.write(new File("perf_mock.json"), JsonUtils.stringify(tpl), Charset.defaultCharset());
    }
}
