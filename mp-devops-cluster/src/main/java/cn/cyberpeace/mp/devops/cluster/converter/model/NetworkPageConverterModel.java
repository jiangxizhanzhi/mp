package cn.cyberpeace.mp.devops.cluster.converter.model;

import cn.cyberpeace.mp.devops.cluster.enums.CloudPlatformEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 网络分页返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetworkPageConverterModel extends BasePageConvertModel {

    /**
     * 列表
     */
    private List<Model> modelList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static final class Model {

        /**
         * 网络名称
         */
        private String name;

        /**
         * 网络类型
         */
        private String networkType;

        /**
         * 状态
         */
        private CloudPlatformEnum status;

        /**
         * 已连接子网
         */
        private List<Subnet> subnets;

        /**
         * 创建时间
         */
        private String createdAt;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static final class Subnet {

            private String id;

            /**
             * 子网CIDR
             */
            private String cidr;

            /**
             * 网关ip
             */
            private String gatewayIp;
        }
    }
}
