package cn.cyberpeace.mp.situationaware.bo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public  class AttackSource{

    /**
     * 源IP
     */
    private String ip;

    /**
     * mac地址
     */
    private List<String> mac;

}