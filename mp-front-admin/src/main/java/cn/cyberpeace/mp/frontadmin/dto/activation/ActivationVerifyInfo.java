package cn.cyberpeace.mp.frontadmin.dto.activation;

import lombok.Data;

import java.util.Map;


@Data
public class ActivationVerifyInfo {

    
    private String machinecode;

    
    private Integer deadline;

    
    private Map<String, String> info;

}
