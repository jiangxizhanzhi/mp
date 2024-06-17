package cn.cyberpeace.mp.scenepluginconfig.bo;

import lombok.Data;

@Data
public class Param {
    private String code;
    private String name;
    private Boolean required;
    private String type;
    private String rule;
    private String value;
    private String filename;
}
