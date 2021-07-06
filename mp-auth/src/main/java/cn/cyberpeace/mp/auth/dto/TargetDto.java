package cn.cyberpeace.mp.auth.dto;

import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.OrgOperation;
import cn.cyberpeace.mp.auth.service.TargetOperation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TargetDto extends TargetModel {

    @ApiModelProperty(value = "上级组织")
    TargetDto parentTarget;

    @ApiModelProperty(value = "下级组织")
    List<TargetDto> children;

    public <T> T execute(TargetOperation<T> operation) {
        return operation.doExecute(this);
    }

}
