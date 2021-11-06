package cn.cyberpeace.mp.front.admin.dto.activation;

import lombok.Data;

import java.util.Map;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
public class ActivationVerifyInfo {

    // 机器码
    private String machinecode;

    // 授权月份（多少个月）
    private Integer deadline;

    // 其他信息
    private Map<String, String> info;

}
