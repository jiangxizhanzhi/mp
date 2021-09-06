package cn.cyberpeace.mp.devops.cluster.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 类型模型，仅支持installer_platforms, image_type
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class TypeIdStringCloudPlatformModel extends BaseCloudPlatformModel implements Serializable {

    /**
     * 数据列表
     */
    private List<Item> items;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static final class Item{

        /**
         * ID
         */
       private String id;

        /**
         * 名称
         */
       private String name;
    }
}
