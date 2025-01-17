// Copyright (C) 2024
// All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OrgDto.java.
 // File size 581.
 // File create date Fri Jan 17 09:57:14 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.bo.org;

import cn.cyberpeace.mp.auth.service.OrgDtoOperation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

 /**
  * org dto
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Data
public class OrgDto extends OrgBO {

    @ApiModelProperty(value = "上级组织")
    OrgDto parentOrg;

    @ApiModelProperty(value = "下级组织")
    List<OrgDto> children;

     /**
      * 执行
      *
      * @param operation 操作
      * @return {@link T }
      */
     public <T> T execute(OrgDtoOperation<T> operation) {
        return operation.doExecute(this);
    }
}
