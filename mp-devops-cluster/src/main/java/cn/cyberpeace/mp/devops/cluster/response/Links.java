package cn.cyberpeace.mp.devops.cluster.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 关联信息
 * @Author qiaokang
 * @Date：2021/7/9 10:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("关联关系")
public class Links {

    @ApiModelProperty("分页信息")
    private Pagination pagination;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Pagination {

        /**
         * 数据总数
         */
        @ApiModelProperty("数据总数")
        private int total;
    }

}
