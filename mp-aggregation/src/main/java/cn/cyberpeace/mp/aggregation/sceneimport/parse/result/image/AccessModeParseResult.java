package cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author maojun
 * @Date 2021/11/2
 */
@Getter
@Setter
public class AccessModeParseResult {
    private String protocol;
    private String port;
    private String username;
    private String password;
    private String desc;
    private String mode;
    private String isproxy;
}
