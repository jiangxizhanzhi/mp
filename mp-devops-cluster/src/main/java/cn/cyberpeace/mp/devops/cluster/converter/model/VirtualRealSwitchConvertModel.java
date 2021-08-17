package cn.cyberpeace.mp.devops.cluster.converter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VirtualRealSwitchConvertModel {

    /**
     * 集合
     */
    private List<Model> modelList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static final class Model {

        /**
         * 名称
         */
        private String name;

        /**
         * 管理IP
         */
        private String managementIp;

        /**
         * 节点管理
         */
        private String state;

        /**
         * 端口总数
         */
        private Integer ports;

        /**
         * 已使用端口数
         */
        private Integer usedPorts;
    }

}
