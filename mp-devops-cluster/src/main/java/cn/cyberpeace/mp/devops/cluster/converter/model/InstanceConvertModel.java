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
public class InstanceConvertModel extends BasePageConvertModel{

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
         * 实例名称
         */
        private String name;

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

        /**
         * 创建时间
         */
        private String createTime;
    }
}
