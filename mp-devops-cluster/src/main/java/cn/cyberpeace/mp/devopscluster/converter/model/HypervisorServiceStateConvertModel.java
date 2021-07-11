package cn.cyberpeace.mp.devopscluster.converter.model;

import lombok.Data;

@Data
public class HypervisorServiceStateConvertModel {

    
    private String name;

    
    private Boolean alive;

    
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
