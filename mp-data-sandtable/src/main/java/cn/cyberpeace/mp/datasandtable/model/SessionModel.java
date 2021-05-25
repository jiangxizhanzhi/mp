package cn.cyberpeace.mp.datasandtable.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * es真是的bean
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionModel {

    /**
     * 物理索引
     */
    private String index;

    private String srcIp;

    private String srcMac;

    private String dstIp;

    private String dstMac;

    private String protocol;
}
