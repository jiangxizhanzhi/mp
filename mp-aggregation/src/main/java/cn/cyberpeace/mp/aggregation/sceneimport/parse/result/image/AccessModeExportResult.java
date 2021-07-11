package cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AccessModeExportResult {
    private String protocol;
    private Integer port;
    private String path;
    private String username;
    private String password;
    private String privateKey;
    private String privateKeyPath;
    private String desc;
    private String mode;
    private Boolean isproxy;
}
