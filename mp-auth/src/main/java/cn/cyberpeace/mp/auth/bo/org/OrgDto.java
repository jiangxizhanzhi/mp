package cn.cyberpeace.mp.auth.bo.org;

import cn.cyberpeace.mp.auth.service.OrgDtoOperation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class OrgDto extends OrgBO {

    @ApiModelProperty(value = "上级组织")
    OrgDto parentOrg;

    @ApiModelProperty(value = "下级组织")
    List<OrgDto> children;

    public <T> T execute(OrgDtoOperation<T> operation) {
        return operation.doExecute(this);
    }
}
