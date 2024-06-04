package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import cn.cyberpeace.mp.base.util.http.SnakeHttpParam;
import lombok.Data;

@Data
public class BasePageParam implements SnakeHttpParam {
    private Integer page;
    private Integer perPage;
}