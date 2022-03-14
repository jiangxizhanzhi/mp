package cn.cyberpeace.mp.situationaware.model.wazuh.common;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/24 09:37
 */
@Data
@ApiModel("响应体")
public class BaseResponse<T> implements Serializable {

    @ApiModelProperty(value = "返回数据")
    private Data<T> data;

    @ApiModelProperty(value = "消息")
    private String message;

    @ApiModelProperty(value = "错误码")
    private Integer error;


    @lombok.Data
    @ApiModel("返回数据")
    public static class Data<T> {

        @ApiModelProperty(value = "影响的数据集合")
        @JSONField(name = "affected_items")
        private List<T> affectedItems;

        @ApiModelProperty(value = "失败的数据集合")
        @JSONField(name = "failed_items")
        private List<T> failedItems;

        @ApiModelProperty(value = "影响的数据数量")
        @JSONField(name = "total_affected_items")
        private Integer totalAffectedItems;

        @ApiModelProperty(value = "失败的数据数量")
        @JSONField(name = "total_failed_items")
        private Integer totalFailedItems;
    }

}
