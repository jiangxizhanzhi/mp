package cn.cyberpeace.mp.cscene.controller.request;

import lombok.Data;

@Data
public class VendorNodeConnectionReq {
    private String nodeId;
    private String id;
    private String vmId;
    private Integer vendor;
}
