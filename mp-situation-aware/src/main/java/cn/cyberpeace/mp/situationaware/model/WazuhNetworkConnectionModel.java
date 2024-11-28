package cn.cyberpeace.mp.situationaware.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WazuhNetworkConnectionModel {

    private String agentIp;
    private String agentName;

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

    private String description;

    private LocalDateTime time;

    public Output toOutput() {
        Output output = new Output();
        output.setLocal(new Local(laddrIp, laddrPort));
        output.setRemote(new Local(raddrIp, raddrPort));
        output.setState(this.status);
        output.setProcess(this.pid);
        output.setProtocol(this.proto);
        output.setAgentIp(this.agentIp);
        return output;
    }

    @Setter
    @Getter
    public static class Output {
        private String agentIp;
        private String protocol;
        private Local local;
        private Local remote;
        private String state;
        private String process;
    }

    @Setter
    @Getter
    public static class Local {
        private String ip;
        private String port;

        public Local(String ip, String port) {
            this.ip = ip;
            this.port = port;
        }
    }


}
