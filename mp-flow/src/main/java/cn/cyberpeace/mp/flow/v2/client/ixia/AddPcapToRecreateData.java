package cn.cyberpeace.mp.flow.v2.client.ixia;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/11/1
 */
@Getter
@Setter
public class AddPcapToRecreateData implements HttpData {
    private String file;
    private String delayStart;
    private loadProfile loadprofile;

    @Getter
    @Setter
    public static class loadProfile {
        private String name;
    }
}
