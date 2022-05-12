package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainerModel implements Serializable {

    /**
     * ID
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 镜像信息
     */
    private String image;

    /**
     * 镜像驱动
     */
    private String imageDriver;

    /**
     * 主机名/节点名称
     */
    private String host;

    /**
     * 地址信息
     */
    private HashMap<String, Object> addresses;

    /**
     * 逻辑卷
     */
    private List<String> volumes;

    /**
     * 主机名
     */
    private String hostname;

    /**
     * CPU核数
     */
    private Integer cpu;

    /**
     * 内存大小
     */
    private Integer ram;

    /**
     * 磁盘空间
     */
    private Integer disk;

    /**
     * 是否维持STDIN
     */
    private Boolean interactive;

    /**
     * 命令
     */
    private Object command;

    /**
     * 工作目录
     */
    private String workdir;

    /**
     * 状态
     */
    private String status;


    private String runtime;

    /**
     * 环境变量
     */
    private Object environment;

    /**
     * 标签
     */
    private Object labels;

    /**
     * 端口
     */
    private List<String> ports;

    /**
     * 是否私有
     */
    private Boolean privileged;

    /**
     * 用户
     */
    private String user;

    /**
     * 项目
     */
    private String project;

    /**
     * 创建时间
     */
    private String createdAt;
    /**
     * 更新时间
     */
    private String updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Address{

        private String addr;

        private Integer version;

        private String port;

        private String subnetId;

        private Boolean preserveOnDelete;

        private Integer vlanId;

    }
}
