package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import cn.cyberpeace.mp.base.util.http.SnakeHttpParam;
import lombok.Data;

/**
 * @ClassName BasePageParam
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see SnakeHttpParam
 */
@Data
public class BasePageParam implements SnakeHttpParam {
    private Integer page;
    private Integer perPage;
}