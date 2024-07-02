package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName CreateFlavorResult
 * @Description
 * @Author maojun
 * @Date 2022/4/1
 * @Version 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateFlavorResult {
    private String id;
    private String name;
    private Integer ram;
    private Integer cpu;
    private Integer disk;
}


