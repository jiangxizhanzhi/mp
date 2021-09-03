package cn.cyberpeace.mp.auth.dto;

import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
public class ConfigReq {

    private String keyName;

    private String value;

    private String tenant;

}
