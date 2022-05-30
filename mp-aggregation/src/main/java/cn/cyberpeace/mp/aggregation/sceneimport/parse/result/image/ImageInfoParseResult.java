package cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image;

import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2021/11/2
 */
@Setter
public class ImageInfoParseResult {
    private String 描述;
    private String 系统类型;
    private String 系统版本;
    private String 镜像分类;
    private String 系统语言;
    private String 系统架构;
    private String 实例大小;
    private String 镜像格式;
    private String 虚拟机文件;
    private String LOGO;
    private String 发布时间;
    private String 镜像来源;
    private String 系统服务;
    private String 安装软件;
    private String 授权类型;
    private String 备注;
    private String NAT到外网;
    private String 自定义命令;
    private String 角色;
    private String 虚拟类型;
    private List<AccessModeParseResult> 接入方式;
    private List<AccessModeParseResult> 管理接入;

    public Boolean getNat() {
        return Boolean.valueOf(this.NAT到外网);
    }

    public List<AccessModeParseResult> getAccessModes() {
        return this.接入方式;
    }

    public List<String> getInitCommands() {
        return Arrays.asList(this.自定义命令);
    }

    public Integer getVirtualType() {
        if (this.虚拟机文件.endsWith(".tar")) {
            return BaseCloudPluginSceneElementModel.VirtualType.CONTAINER;
        } else {
            return BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE;
        }
    }

    public Integer getRole() {
        String role = this.角色;
        if (StringUtils.isEmpty(role)) {
            return null;
        }
        return ROLE_MAP.get(role);
    }

    public String getImageType() {
        String imageType = this.虚拟类型;
        if ("虚拟机".equals(imageType)) {
            return "vm";
        } else if ("容器".equals(imageType)) {
            return "container";
        } else {
            return null;
        }
    }

    private static Map<String, Integer> ROLE_MAP = new HashMap<>();

    static {
        ROLE_MAP.put("网关节点-路由器", BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER);
        ROLE_MAP.put("网关节点-交换机", BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH);
        ROLE_MAP.put("网关节点-防火墙", BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL);
        ROLE_MAP.put("终端节点", BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL);
        ROLE_MAP.put("外部网络", BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK);
    }
}
