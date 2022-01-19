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
    @ApiModelProperty(value = "每页条数",hidden = true)
    private Integer limit;

    /**
     * 页码，从1开始
     */
    @Min(1)
    @ApiModelProperty(value = "页码",hidden = true)
    private Integer offset;

    /**
     * 页码
     */
    @ApiModelProperty(value = "页码")
    private Integer page = 1;

    /**
     * 每页条数
     */
    @ApiModelProperty(value = "每页条数")
    private Integer perPage = 10;

    public Integer getLimit() {
        return perPage;
    }

    public Integer getOffset() {
        return page;
    }
}
