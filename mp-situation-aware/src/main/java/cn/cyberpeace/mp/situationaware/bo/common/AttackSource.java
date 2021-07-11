package cn.cyberpeace.mp.situationaware.bo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public  class AttackSource{

    
    private String ip;

    
    private Integer port;

    
    private List<String> mac;

}