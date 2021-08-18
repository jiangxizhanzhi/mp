package cn.cyberpeace.mp.devops.cluster.model;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 虚实结合交换机模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "虚实结合交换机模型，对应云平台接口返回信息")
public class VirtualRealSwitchCloudPlatformModel extends BaseCloudPlatformModel implements Serializable {

    /**
     * 集合
     */
    private List<Switch> switchs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Switch{

        /**
         * IP
         */
        private String name;

        /**
         * 管理IP
         */
        private String managementIp;

        /**
         * 节点管理
         */
        private String status;

        /**
         * 总端口数
         */
        private String interfacesSummary;

        /**
         * 已使用端口数
         */
        private String interfacesUsed;

        public String getInterfaces_summary() {
            return StringUtils.isBlank(interfacesSummary) ? "0" : interfacesSummary;
        }

        public String getInterfaces_used() {
            return StringUtils.isBlank(interfacesUsed) ? "0" : interfacesUsed;
        }
    }
}
