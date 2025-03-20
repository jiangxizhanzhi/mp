package cn.cyberpeace.mp.sceneplugincloud.bo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlowMirrorIpsBO {
    private String[] ips ;

    public FlowMirrorIpsBO(String[] ips) {
        this.ips = ips;
    }
}
