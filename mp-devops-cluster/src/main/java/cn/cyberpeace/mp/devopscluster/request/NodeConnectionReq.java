package cn.cyberpeace.mp.devopscluster.request;


import lombok.Data;

@Data
public class NodeConnectionReq {

    private String nodeName;
    private String nodeIp;
}
