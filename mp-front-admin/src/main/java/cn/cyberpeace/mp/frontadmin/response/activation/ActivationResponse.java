// Copyright (C) 2024
// All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ActivationResponse.java.
 // File size 908.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.response.activation;

import cn.cyberpeace.mp.frontadmin.request.activation.ActivationRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

 /**
  * 激活反应
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Data
public class ActivationResponse extends ActivationRequest {

    @ApiModelProperty(value = "过期日期")
    private String expiredDate;

    @ApiModelProperty(value = "是否是激活状态 1:是 0:否")
    private Integer isActived;

     /**
      * 字符串
      *
      * @return {@link String }
      */
     @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("expiredDate", expiredDate)
                .append("isActived", isActived)
                .append("activationRequest", super.toString())
                .toString();
    }
}
