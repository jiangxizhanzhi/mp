package cn.cyberpeace.mp.auth.dto;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.service.OrgOperation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrgDto extends OrgModel {

    @ApiModelProperty(value = "上级组织")
    OrgDto parentOrg;

    @ApiModelProperty(value = "下级组织")
    List<OrgDto> children;

    public <T> T execute(OrgOperation<T> operation) {
        return operation.doExecute(this);
    }
}
