package cn.cyberpeace.mp.flow.v2.client.ixia;

import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/11/4
 */
@Getter
@Setter
public class StartTestResult extends IxiaResponse{
    private String host;
    private String name;
    private Integer iteration;
    private Integer runid;
}
