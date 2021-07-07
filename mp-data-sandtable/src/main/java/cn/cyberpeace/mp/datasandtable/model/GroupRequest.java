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

    private String groupId;

    /**
     * 物理机地址
     */
    @NotNull(message = "macIds不能为空")
    @ApiModelProperty("物理机地址")
    private List<String> macIds;
}
