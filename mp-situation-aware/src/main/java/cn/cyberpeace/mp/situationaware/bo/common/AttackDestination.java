package cn.cyberpeace.mp.situationaware.bo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttackDestination{

    /**
     * 目标IP
     */
    private String ip;

    /**
     * 端口
     */
    private Integer port;

    /**
     * mac地址
     */
    private List<String> mac;

}