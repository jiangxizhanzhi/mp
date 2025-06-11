 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ImageInfoParseResult.java.
 // File size 3146.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image;

 import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
 import lombok.Getter;
 import lombok.Setter;
 import lombok.extern.slf4j.Slf4j;
 import org.apache.commons.lang3.StringUtils;

 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

/**
 * @ClassName ImageInfoParseResult
 * @Description
 * @Author maojun
 * @Date 2021/11/2
 * @Version 1.0.0
 */
@Setter
@Getter
@Slf4j
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

    public List<AccessModeParseResult> getManageAccessModes() {
        return this.管理接入;
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

    public String getLanguage() {
        return this.系统语言;
    }

    public String getSystemArchitecture() {
        return this.系统架构;
    }

    /**
     * 97 - windowsXp
     * 98 - windows-server2016
     * 99 - windows8
     * 100 - windows-server 2008
     * 106 - windows10
     * 107: windows7
     *
     * 301 centos 7
     * 302 centos8
     * 303 redhat7
     * 304 redhat8
     * 305 中标麒麟
     * 306 银河麒麟
     * 307 ubuntu
     * 308 debain
     *
     * windowx默认是windowsXp
     * linux默认是centos7
     *
     * @return
     */
    public Integer getSystemType() {
        //windows
        if (getParentSystemType() == 1) {
            if (StringUtils.isEmpty(this.系统类型)) {
                return 97;
            } else {
                String lowerCaseSystemType = this.系统类型.toLowerCase();
                if (lowerCaseSystemType.endsWith("xp")) {
                    return 97;
                } else if (lowerCaseSystemType.contains("2016") && lowerCaseSystemType.contains("server")) {
                    return 98;
                } else if (lowerCaseSystemType.contains("2008") && lowerCaseSystemType.contains("server")) {
                    return 100;
                } else if (lowerCaseSystemType.equals("windows7")) {
                    return 107;
                } else if (lowerCaseSystemType.equals("windows8")) {
                    return 99;
                } else if (lowerCaseSystemType.equals("windows10")) {
                    return 106;
                } else {
                    return 97;
                }
            }
        } else {
            if (StringUtils.isEmpty(this.系统类型)) {
                return 301;
            } else {
                String lowerCaseSystemType = this.系统类型.toLowerCase();
                if (lowerCaseSystemType.contains("centos") && (lowerCaseSystemType.contains("7"))) {
                    return 301;
                } else if (lowerCaseSystemType.contains("centos") && lowerCaseSystemType.contains("8")) {
                    return 302;
                } else if (lowerCaseSystemType.contains("redhat") && lowerCaseSystemType.contains("7")) {
                    return 303;
                } else if (lowerCaseSystemType.contains("redhat") && lowerCaseSystemType.contains("8")) {
                    return 304;
                } else if (lowerCaseSystemType.contains("中标")) {
                    return 305;
                } else if (lowerCaseSystemType.contains("银河")) {
                    return 306;
                } else if (lowerCaseSystemType.contains("ubuntu")) {
                    return 307;
                } else if (lowerCaseSystemType.contains("debain")) {
                    return 308;
                } else {
                    return 301;
                }

            }

        }

    }

    /**
     * 1 -windows  2-linux
     * @return
     */
    public Integer getParentSystemType() {
        if (StringUtils.isEmpty(this.系统类型)) {
            return 2;
        } else {
            String lowerCaseSystemType = this.系统类型.toLowerCase();
            if (lowerCaseSystemType.contains("win")) {
                return 1;
            } else {
                return 2;
            }
        }
    }



    public Integer getRole() {
        String role = this.角色;
        if (StringUtils.isEmpty(role)) {
            return null;
        }
        return ROLE_MAP.get(role);
    }

    public Integer getCategory() {
        if (StringUtils.isEmpty(this.镜像分类)) {
            return 0;
        }
        var info = StringUtils.split(this.镜像分类, "/");
        if (CATEGORY_MAP.containsKey(info[0])) {
            return CATEGORY_MAP.get(info[0]);
        } else {
            return 0;
        }
    }

    public Integer getSecondCategory() {
        if (StringUtils.isEmpty(this.镜像分类)) {
            return 0;
        }
        var info = StringUtils.split(this.镜像分类, "/");
        if (info.length == 1) {
            return 0;
        }
        if (CATEGORY_MAP.containsKey(info[1])) {
            return CATEGORY_MAP.get(info[1]);
        } else {
            return 0;
        }
    }

    public Integer getThirdCategory() {
        if (StringUtils.isEmpty(this.镜像分类)) {
            return 0;
        }
        var info = StringUtils.split(this.镜像分类, "/");
        if (info.length < 3) {
            return 0;
        }
        if (CATEGORY_MAP.containsKey(info[2])) {
            return CATEGORY_MAP.get(info[2]);
        } else {
            return 0;
        }
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

    public Flavor calFlavor() {
        String flavorStr = this.get实例大小();
        if (StringUtils.isNotEmpty(flavorStr)) {
            String[] flavorArr = flavorStr.split("\\.");
            if (flavorArr.length == 2) {
                Flavor flavor = new Flavor();
                String infoStr = flavorArr[1];
                String[] infos = infoStr.split("-");
                Integer cpu = parseCpu(infos[0]);
                Integer ram = parseRam(infos[1]);
                Integer disk = parseDisk(infos[2]);
                flavor.setCpu(cpu);
                flavor.setRam(ram);
                flavor.setDisk(disk);
                return flavor;
            } else {
                log.error("failed to parse flavor {}", flavorStr);
            }
        }
        return new Flavor();
    }

    private Integer parseCpu(String cpuStr) {
        String cpu = StringUtils.replace(cpuStr, "c", "");
        cpu = StringUtils.replace(cpu, "C", "");
        try {
            return Integer.parseInt(cpu);
        } catch (Exception e) {
            return 1;
        }
    }

    private Integer parseDisk(String diskStr) {
        String cpu = StringUtils.replace(diskStr, "g", "");
        cpu = StringUtils.replace(cpu, "G", "");
        try {
            return Integer.parseInt(cpu);
        } catch (Exception e) {
            return 20;
        }
    }

    private Integer parseRam(String ramStr) {
        if (ramStr.contains("g")) {
            String cpu = StringUtils.replace(ramStr, "g", "");
            cpu = StringUtils.replace(cpu, "G", "");
            try {
                return Integer.parseInt(cpu) * 1024;
            } catch (Exception e) {
                log.error("failed to parse flavor-cpu {}", ramStr, e);
                return 2048;
            }
        } else {
            String cpu = StringUtils.replace(ramStr, "m", "");
            cpu = StringUtils.replace(cpu, "M", "");
            try {
                return Integer.parseInt(cpu);
            } catch (Exception e) {
                log.error("failed to parse flavor-cpu {}", ramStr, e);
                return 2048;
            }
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

    private static Map<String, Integer> CATEGORY_MAP = new HashMap<>();

    static {
        // 一级分类
        CATEGORY_MAP.put("操作系统", 1);
        CATEGORY_MAP.put("应用系统", 2);
        CATEGORY_MAP.put("网络设备", 3);
        CATEGORY_MAP.put("安全设备", 4);
        // 操作系统的二级分类
        {
            CATEGORY_MAP.put("WINDOWS", 1);
            CATEGORY_MAP.put("LINUX", 2);
            CATEGORY_MAP.put("UNIX", 3);
            CATEGORY_MAP.put("BSD", 4);
            CATEGORY_MAP.put("OTHER", 5);
            // 三级分类
            {
                // WINDOWS的三级分类
                {
                    CATEGORY_MAP.put("WindowsXP", 97);
                    CATEGORY_MAP.put("Windows-Server2016", 98);
                    CATEGORY_MAP.put("Windows8", 99);
                    CATEGORY_MAP.put("Windows-Server2008", 100);
                    CATEGORY_MAP.put("Windows-Server2003", 101);
                    CATEGORY_MAP.put("Windows Vista", 102);
                    CATEGORY_MAP.put("Windows-Server2019", 103);
                    CATEGORY_MAP.put("Windows-Server2012", 104);
                    CATEGORY_MAP.put("Windows-Server2010", 105);
                    CATEGORY_MAP.put("Windows10", 106);
                    CATEGORY_MAP.put("Windows7", 107);
                    CATEGORY_MAP.put("Windows-Server2000", 108);
                    CATEGORY_MAP.put("Windows11", 109);
                }
                // LINUX的三级分类
                {
                    CATEGORY_MAP.put("Gentoo", 301);
                    CATEGORY_MAP.put("Mint", 302);
                    CATEGORY_MAP.put("Arch", 303);
                    CATEGORY_MAP.put("Ubuntu", 304);
                    CATEGORY_MAP.put("Deepin", 305);
                    CATEGORY_MAP.put("CentOS", 306);
                    CATEGORY_MAP.put("Debian", 307);
                    CATEGORY_MAP.put("Rocky", 308);
                    CATEGORY_MAP.put("Kali", 309);
                    CATEGORY_MAP.put("Fedora", 310);
                    CATEGORY_MAP.put("Android", 311);
                    CATEGORY_MAP.put("Redhat", 312);
                    CATEGORY_MAP.put("SUSE", 313);
                    CATEGORY_MAP.put("Other", 314);
                }
                // UNIX的三级分类
                {
                    CATEGORY_MAP.put("AIX", 1);
                    CATEGORY_MAP.put("HP-UX", 2);
                    CATEGORY_MAP.put("Solaris", 3);
                    CATEGORY_MAP.put("IRIX", 4);
                    CATEGORY_MAP.put("A/UX", 5);
                    CATEGORY_MAP.put("Xenix", 6);
                }
                // BSD的三级分类
                {
                    CATEGORY_MAP.put("FreeBSD", 201);
                    CATEGORY_MAP.put("OpenBSD", 202);
                    CATEGORY_MAP.put("NetBSD", 203);
                    CATEGORY_MAP.put("DragonFly BSD", 204);
                }
                // OTHER的三级分类
                {
                    CATEGORY_MAP.put("IOS", 401);
                    CATEGORY_MAP.put("MacOS", 402);
                    CATEGORY_MAP.put("USB", 403);
                }
            }
        }
        // 应用系统的二级分类
        {
            CATEGORY_MAP.put("网络服务", 30);
            CATEGORY_MAP.put("中间件", 31);
            CATEGORY_MAP.put("数据库", 32);
            CATEGORY_MAP.put("软件开发", 33);
            CATEGORY_MAP.put("基础镜像", 34);
            CATEGORY_MAP.put("运维工具", 35);
            CATEGORY_MAP.put("邮件服务器", 36);
            CATEGORY_MAP.put("文件服务器", 37);
            CATEGORY_MAP.put("企业办公", 38);
            CATEGORY_MAP.put("统计分析", 39);
            CATEGORY_MAP.put("负载均衡", 40);
            CATEGORY_MAP.put("应用框架", 41);
            CATEGORY_MAP.put("Web服务器", 42);
            CATEGORY_MAP.put("其他", 43);
        }
        // 网络设备的二级分类
        {
            CATEGORY_MAP.put("交换机", 50);
            CATEGORY_MAP.put("路由器", 51);
            CATEGORY_MAP.put("防火墙", 52);
            CATEGORY_MAP.put("外部网络", 53);
        }
        // 安全设备的二级分类
        {
            CATEGORY_MAP.put("IDS", 500);
            CATEGORY_MAP.put("WAF", 501);
            CATEGORY_MAP.put("VPN", 502);
            CATEGORY_MAP.put("行为管理", 503);
            CATEGORY_MAP.put("安全审计", 504);
            CATEGORY_MAP.put("防火墙", 505);
            CATEGORY_MAP.put("IPS", 506);
            CATEGORY_MAP.put("SIEM", 507);
            CATEGORY_MAP.put("其他", 508);
        }

    }

    @Getter
    @Setter
    public static class Flavor {
        private Integer cpu = 1;
        private Integer ram = 2048;
        private Integer disk = 20;
    }
}
