package cn.cyberpeace.mp.sceneplugincollection.service.collect.base;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WazuhResult<T> {
    private List<T> list;
    private long total;

    /**
     * WazuhResult
     */
    public WazuhResult() {

    }

    /**
     * WazuhResult
     *
     * @param list
     * @param total
     */
    public WazuhResult(List<T> list, long total) {
        this.list = list;
        this.total = total;
    }
}
