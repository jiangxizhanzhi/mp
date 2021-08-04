package cn.cyberpeace.mp.devops.cluster.converter.model;

import cn.cyberpeace.mp.devops.cluster.enums.CloudPlatformEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ContainerConvertModel extends BasePageConvertModel{

    /**
     * 列表
     */
    private List<Model> modelList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static final class Model{
        /**
         * 容器名称
         */
        private String name;

        /**
         * 镜像名称
         */
        private String image;

        /**
         * 节点名称
         */
        private String node;

        /**
         * IP地址
         */
        private List<String> addressList;

        /**
         * 状态
         */
        private CloudPlatformEnum status;
    }


}
