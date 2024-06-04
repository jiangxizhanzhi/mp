package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import lombok.Data;

@Data
public class VirtualNodePageParam extends BasePageParam {
    private String name;
    private String nodeType;
    private Boolean isAll;
}

