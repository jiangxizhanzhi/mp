package cn.cyberpeace.mp.flow.v2.client.ixia;

import lombok.Data;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/31
 */
@Data
public class CreateTestResult extends IxiaResponse {
    private String otalUniqueStrikes;
    private String requiredMTU;
    private String totalUniqueSuperflows;
    private String contentType;
    private String totalSubnets;
    private String totalBandwidth;
    private String network;
    private String samplePeriod;
    private Long createdOn;
    private String author;
    private Integer duration;
    private String maximumConcurrentFlows;
    private String totalAttacks;
    private String totalAddresses;
    private String createdBy;
    private String component;
    private String name;
    private String maxFlowCreationRate;
    private String totalMacAddresses;
}
