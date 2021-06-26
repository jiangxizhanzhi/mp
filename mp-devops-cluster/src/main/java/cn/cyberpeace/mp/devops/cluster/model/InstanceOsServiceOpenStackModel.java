package cn.cyberpeace.mp.devops.cluster.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 虚拟机OS服务返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "虚拟机OS服务返回信息")
public class InstanceOsServiceOpenStackModel extends BaseOpenStackModel implements Serializable {

    /**
     * OsService列表
     */
    private List<InstanceOsServiceModel> services;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class InstanceOsServiceModel implements Serializable{

        /**
         * id
         */
        private Integer id;

        /**
         * binary
         */
        private String binary;

        /**
         * host
         */
        private String host;

        /**
         * zone
         */
        private String zone;

        /**
         * status
         */
        private String status;

        /**
         * state
         */
        private String state;

        /**
         *
         */
        private String disabled_reason;

        /**
         *
         */
        private String updated_at;
    }
}
