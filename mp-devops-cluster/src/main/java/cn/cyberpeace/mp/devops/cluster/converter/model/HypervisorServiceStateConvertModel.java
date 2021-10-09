package cn.cyberpeace.mp.devops.cluster.converter.model;

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
}
