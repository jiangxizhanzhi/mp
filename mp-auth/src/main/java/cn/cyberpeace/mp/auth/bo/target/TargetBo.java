 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TargetBo.java.
 // File size 727.
 // File create date Fri Jan 17 09:57:14 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.bo.target;

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
public class TargetBo extends TargetModel {

    @ApiModelProperty(value = "英文名称")
    private String enName;

    @ApiModelProperty(value = "上级组织")
    TargetBo parentTarget;

    @ApiModelProperty(value = "下级组织")
    List<TargetBo> children;

    public <T> T execute(TargetOperation<T> operation) {
        return operation.doExecute(this);
    }

}
