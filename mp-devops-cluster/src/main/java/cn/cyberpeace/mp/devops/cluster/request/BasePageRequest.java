package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 基础的分页参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasePageRequest extends BaseRequest implements Serializable {

    /**
     * 每页条数
     */
    @Min(1)
    @ApiModelProperty(value = "每页条数",required = false)
    private Integer limit = 10;

    /**
     * 页码，从1开始
     */
    @Min(1)
    @ApiModelProperty(value = "页码",required = false)
    private Integer offset = 1;

    public Integer getLimit() {
        return limit;
    }

    public Integer getOffset() {
        return offset;
    }
}
