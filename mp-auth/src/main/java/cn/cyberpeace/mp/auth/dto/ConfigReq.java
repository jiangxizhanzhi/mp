package cn.cyberpeace.mp.auth.dto;

import lombok.Data;

@Data
public class ConfigReq {

    private String keyName;

    private String value;

    private String tenant;

}
