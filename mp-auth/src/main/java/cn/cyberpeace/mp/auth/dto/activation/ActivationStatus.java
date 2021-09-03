package cn.cyberpeace.mp.auth.dto.activation;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
public class ActivationStatus {

    private Boolean isActivated;

    private LocalDateTime expiredDate;

    // todo 其他信息
    private String info;

}
