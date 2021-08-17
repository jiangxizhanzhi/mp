package cn.cyberpeace.mp.devops.cluster.converter.model;

import cn.cyberpeace.mp.devops.cluster.enums.CloudPlatformEnum;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.Data;

@Data
public class HypervisorServiceStateConvertModel {

    /**
     * 服务
     */
    private String name;

    /**
     * 是否存活
     */
    private Boolean alive;

    /**
     * 状态
     */
    private String state;

    public HypervisorServiceStateConvertModel(String name, Boolean alive) {
        this.name = name;
        this.alive = alive;
    }

    public HypervisorServiceStateConvertModel(String name, String state) {
        this.name = name;
        this.state = state;
    }

    public String getState() {
        return StringUtils.isNotBlank(state) ?
                state.equals(CloudPlatformEnum.UP.getState()) ?
                        CloudPlatformEnum.UP.getDesc() : CloudPlatformEnum.DOWN.getDesc()
                : alive ? CloudPlatformEnum.UP.getDesc() : CloudPlatformEnum.DOWN.getDesc();
    }
}
