package cn.cyberpeace.mp.flow.v2.client.ixia;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/11/1
 */
@Getter
@Setter
public class UploadPcapResult extends IxiaResponse {
    private String result;
    private String name;
}
