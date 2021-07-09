package cn.cyberpeace.mp.datasandtable.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel("流量场景分组")
public class GroupRequest {

    @NotNull(message = "groupId不能为空")
    @ApiModelProperty(value = "场景分组id", required = true)
    private String groupId;

    /**
     * 物理机地址
     */
    @NotNull(message = "macIds不能为空")
    @ApiModelProperty(value = "物理机地址", required = true)
    private List<String> macIds;
}
