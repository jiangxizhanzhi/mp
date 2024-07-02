package cn.cyberpeace.mp.auth.bo.org;

import cn.cyberpeace.mp.auth.service.OrgDtoOperation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName OrgDto
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see OrgBO
 */
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
