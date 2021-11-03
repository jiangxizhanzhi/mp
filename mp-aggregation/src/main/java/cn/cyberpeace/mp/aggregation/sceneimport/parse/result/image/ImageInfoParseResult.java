package cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image;

import lombok.Setter;

import java.util.Arrays;
import java.util.List;

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
}
