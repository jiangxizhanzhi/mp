package cn.cyberpeace.mp.flow.v2.client.ixia;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/11/4
 */
@Getter
@Setter
public class AddSecurityToTestData implements HttpData {
    private String name;
    private String component;
    private String type;
    private Boolean active;
}
