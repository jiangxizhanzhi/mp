package cn.cyberpeace.mp.devops.cluster.converter.model;

import cn.cyberpeace.mp.devops.cluster.enums.CloudPlatformEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 路由分页返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouterPageConverterModel extends BasePageConvertModel {

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
         * 状态
         */
        private CloudPlatformEnum status;

        /**
         * 创建时间
         */
        private String createdAt;
    }

}
