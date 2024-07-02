package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import lombok.Data;

/**
 * @ClassName VirtualNodePageParam
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePageParam
 */
@Data
public class VirtualNodePageParam extends BasePageParam {
    private String name;
    private String nodeType;
    private Boolean isAll;
}

