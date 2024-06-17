package cn.cyberpeace.mp.scenepluginconfig.bo;

import lombok.Data;

import java.util.List;

@Data
public class Custom {
    private Boolean enable;
    private String description;
    private List<Param> params;
}
