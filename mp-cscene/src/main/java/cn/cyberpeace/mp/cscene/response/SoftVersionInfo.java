package cn.cyberpeace.mp.cscene.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoftVersionInfo {
    private String soft;
    private String originVersion;
    private String newVersion;
    private Boolean upgradeResult;
}
