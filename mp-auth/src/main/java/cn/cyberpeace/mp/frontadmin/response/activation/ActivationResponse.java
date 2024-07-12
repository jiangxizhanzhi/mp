 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ActivationResponse.java.
 // File size 1197.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.response.activation;

import cn.cyberpeace.mp.frontadmin.request.activation.ActivationRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * @ClassName ActivationResponse
 * @Description 激活响应
 * @Author zhouxinghuai
 * @Date 2021/09/07 11:13
 * @Version 1.0.0
 * @see ActivationRequest
 */
@Data
public class ActivationResponse extends ActivationRequest {

    @ApiModelProperty(value = "过期日期")
    private String expiredDate;

    @ApiModelProperty(value = "是否是激活状态 1:是 0:否")
    private Integer isActived;

    @ApiModelProperty(value = "授权延期校验码，des加密")
    private List<String> delayVerifyCodeList;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("expiredDate", expiredDate)
                .append("isActived", isActived)
                .append("delayVerifyCodeList", delayVerifyCodeList)
                .append("activationRequest", super.toString())
                .toString();
    }
}
