package cn.cyberpeace.mp.flow.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 新增容器请求
 * @Author qiaokang
 * @Date：2021/8/23 10:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddContainerRequest {

    private String name;
    private String image;
    private int cpu = 2;
    private int ram = 6114;
    private int disk = 40;
    private boolean run = true;
    private boolean interactive = true;
    private boolean privileged = true;
    private List<Networks> networks;

    @Data
    public static final class Networks {
        private String networkId;
    }

}
