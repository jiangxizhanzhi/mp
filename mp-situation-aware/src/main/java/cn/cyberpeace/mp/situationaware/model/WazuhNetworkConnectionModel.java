package cn.cyberpeace.mp.situationaware.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WazuhNetworkConnectionModel {

    @JSONField(name = "pid")
    private String pid;

    @JSONField(name = "status")
    private String status;

    @JSONField(name = "proto")
    private String proto;

    @JSONField(name = "laddr_ip")
    private String laddrIp;

    @JSONField(name = "laddr_port")
    private String laddrPort;

    @JSONField(name = "raddr_ip")
    private String raddrIp;

    @JSONField(name = "raddr_port")
    private String raddrPort;
}
