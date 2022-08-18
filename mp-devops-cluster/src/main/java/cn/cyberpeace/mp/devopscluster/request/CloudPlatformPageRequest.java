package cn.cyberpeace.mp.devopscluster.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: 云平台服务监控分页查询
 * @Author qiaokang
 * @Date：2022/7/15 14:56
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CloudPlatformPageRequest extends BasePageRequest implements Serializable {

    /**
     * 节点名称
     */
    private String hostName;

    /**
     * 如 +runtime or -runtime
     */
    private String sort;

}
