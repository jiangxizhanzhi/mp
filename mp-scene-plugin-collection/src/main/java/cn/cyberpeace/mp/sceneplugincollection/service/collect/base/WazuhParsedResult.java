package cn.cyberpeace.mp.sceneplugincollection.service.collect.base;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class WazuhParsedResult<T> {
    private List<T> data;
    private long total;
}
