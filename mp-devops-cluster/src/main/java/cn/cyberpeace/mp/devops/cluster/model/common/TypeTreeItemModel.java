package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeTreeItemModel {

    /**
     * 1级类型ID
     */
    private Integer l1Id;

    /**
     * 名称
     */
    private String l1Name;

    /**
     * 二级类型集合
     */
    private List<L1Child> l1Child;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static final class L1Child implements Serializable {

        /**
         * 二级类型ID
         */
        private Integer l2Id;

        /**
         * 名称
         */
        private String l2Name;

        /**
         * 1级类型的ID
         */
        private Integer l1ParentId;

        /**
         * 三级类型集合
         */
        private List<L2Child> l2Child;

        @NoArgsConstructor
        @AllArgsConstructor
        @Data
        public static final class L2Child{

            /**
             * 三级类别ID
             */
            private Integer l3Id;

            /**
             * 名称
             */
            private String l3Name;

            /**
             * 2级类型ID
             */
            private Integer l2ParentId;

            /**
             * 1级类型ID
             */
            private Integer l1ParentId;
        }
    }
}
