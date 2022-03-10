package cn.cyberpeace.mp.devops.cluster.model;

import lombok.*;

import java.util.List;

/**
 * @Description: 物理主机服务状态
 * @Author qiaokang
 * @Date：2022/3/9 14:38
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HypervisorServiceStatusModel extends BaseCloudPlatformModel {

    /**
     * 物理主机服务状态列表
     */
    private List<ServiceStatus> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ServiceStatus {

        /**
         * 节点名称
         */
        private String host;

        /**
         * 节点状态
         */
        private boolean status;

    }

}
