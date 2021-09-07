package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.model.common.TargetType;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 类型模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class TypeL2CloudPlatformModel extends BaseCloudPlatformModel implements Serializable {

    /**
     * 数据列表
     */
    private List<TargetType> items;

//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Data
//    public static final class Item{
//
//        /**
//         * 1级类型ID
//         */
//       private Integer id;
//
//        /**
//         * 名称
//         */
//       private String name;
//
//        /**
//         * 二级类型集合
//         */
//       private List<Child> child;
//
//       @NoArgsConstructor
//       @AllArgsConstructor
//       @Data
//       public static final class Child{
//
//            /**
//             * 二级类型ID
//             */
//            private Integer id;
//
//            /**
//             * 名称
//             */
//            private String name;
//
//            /**
//             * 1级类型的ID
//             */
//            private Integer parentId;
//       }
//    }
}
