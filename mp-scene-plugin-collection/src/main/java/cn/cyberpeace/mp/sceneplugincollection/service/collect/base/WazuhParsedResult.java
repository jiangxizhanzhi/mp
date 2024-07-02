package cn.cyberpeace.mp.sceneplugincollection.service.collect.base;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName WazuhParsedResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Setter
@Getter
public class WazuhParsedResult<T> {
    private List<T> data;
    private long total;
}
