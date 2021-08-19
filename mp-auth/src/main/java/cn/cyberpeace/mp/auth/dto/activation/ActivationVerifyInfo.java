package cn.cyberpeace.mp.auth.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
public class ActivationVerifyInfo {

    // 机器码
    private String machinecode;

    // 授权月份（多少个月）
    private Integer deadline;

    // 其他信息
    private Map<String, String> info;

}
