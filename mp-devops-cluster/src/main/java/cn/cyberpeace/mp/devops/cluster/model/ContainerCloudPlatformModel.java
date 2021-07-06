package cn.cyberpeace.mp.devops.cluster.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 容器模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "容器模型，对应云平台接口返回信息")
public class ContainerCloudPlatformModel extends BaseCloudPlatformModel implements Serializable {

    /**
     * 容器列表
     */
    private List<ContainerModel> containers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ContainerModel {

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
        private String image_driver;

        /**
         * 主机名/节点名称
         */
        private String host;

        /**
         * 地址信息
         */
        private Map<String, Object> addresses;

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
        private List<String> command;

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
        private String created_at;
        /**
         * 更新时间
         */
        private String updated_at;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static final class Address{

            private String addr;

            private Integer version;

            private String port;

            private String subnet_id;

            private Boolean preserve_on_delete;

        }
    }
}
