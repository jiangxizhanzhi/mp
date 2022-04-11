package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Description
 * @Author maojun
 * @Date 2022/4/1
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VlanRangeResult {
    private Boolean status;
    private String message;
    private Boolean success;
    private String requestId;
}
