package cn.cyberpeace.mp.auth.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ActivationContent {

    private ActivationRequest activationRequest;

    @ApiModelProperty(value = "过期日期")
    private String expiredDate;

}
