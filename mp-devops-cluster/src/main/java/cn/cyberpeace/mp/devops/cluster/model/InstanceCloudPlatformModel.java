package cn.cyberpeace.mp.devops.cluster.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 虚拟机模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "虚拟机模型，对应云平台接口返回信息")
public class InstanceCloudPlatformModel extends BaseCloudPlatformModel implements Serializable {

    /**
     * 实例列表
     */
    private List<InstanceModel> instances;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class InstanceModel implements Serializable{

        /**
         * ID
         */
        private String id;

        /**
         * UUID
         */
        private String uuid;

        /**
         * 系统名称
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
         * 云主机类型信息
         */
        private String flavor;

        /**
         * 地址信息
         */
        private List<Address> addresses;

        /**
         * 逻辑卷
         */
        private List<String> volumes;

        /**
         * 初始化脚本
         */
        private String userData;

        /**
         * 元数据信息
         */
        private Object metadata;

        /**
         * 虚拟机状态
         */
        private String status;

        /**
         * 节点名称
         */
        private String hostname;

        /**
         * 实例名称
         */
        private String instanceName;

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

            /**
             * IP地址
             */
            private String ip;

            /**
             * MAC地址
             */
            private String mac;
        }
    }
}
