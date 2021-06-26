package cn.cyberpeace.mp.devops.cluster.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实装信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealEquipmentOpenStackModel extends BaseModel {

    /**
     * 关联信息
     */
    private Links links;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Links{
        private Pagination pagination;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static final class Pagination{

            /**
             * 当前页码
             */
            private Integer current_page;

            /**
             * 从第几条数据开始
             */
            private Integer from;

            /**
             * 总页数
             */
            private Integer last_page;

            /**
             * 到第几条数据结束
             */
            private Integer to;

            /**
             * 数据总大小
             */
            private Integer total;
        }
    }

    
}
