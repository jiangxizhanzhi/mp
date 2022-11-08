package cn.cyberpeace.mp.auth.dto.target;

import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.TargetOperation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
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
