package cn.cyberpeace.mp.auth.bo.org;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrgIdAndCount {
    @ApiModelProperty(value = "orgId")
    private String orgId;

    @ApiModelProperty(value = "组织人员数量")
    private Integer count;
}
