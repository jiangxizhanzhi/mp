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
public class ElasticIpConvertModel extends BasePageConvertModel{

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
         * 固定IP
         */
        private String fixedIpAddress;

        /**
         * 弹性IP
         */
        private String floatIpAddress;

        /**
         * 状态
         */
        private CloudPlatformEnum status;

        /**
         * 创建时间
         */
        private String createdAt;
    }


}
