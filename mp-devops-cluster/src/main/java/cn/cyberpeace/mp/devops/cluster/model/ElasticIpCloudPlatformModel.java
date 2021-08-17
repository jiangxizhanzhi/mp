package cn.cyberpeace.mp.devops.cluster.model;

import lombok.*;

import java.util.List;

/**
 * 弹性IP分页返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ElasticIpCloudPlatformModel extends BaseCloudPlatformModel {

    /**
     * 弹性IP列表
     */
    private List<ElasticIpModel> floatingips;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ElasticIpModel{

        /**
         * ID
         */
        private String id;

        /**
         * 承租者ID
         */
        private String tenant_id;

        /**
         * 弹性IP地址
         */
        private String floating_ip_address;

        /**
         * 弹性网络ID
         */
        private String floating_network_id;

        /**
         * 路由ID
         */
        private String router_id;

        /**
         * 端口
         */
        private String port_id;

        /**
         * 固定IP地址
         */
        private String fixed_ip_address;

        /**
         * 状态
         */
        private String status;

        /**
         * 描述
         */
        private String description;

        /**
         * 端口详细
         */
        private String port_details;

        /**
         *
         */
        private List<String> tags;

        /**
         * 修订号
         */
        private Integer revision_number;

        /**
         * 项目ID
         */
        private String project_id;

        /**
         * 创建时间
         */
        private String created_at;
        /**
         * 更新时间
         */
        private String updated_at;
    }
}
