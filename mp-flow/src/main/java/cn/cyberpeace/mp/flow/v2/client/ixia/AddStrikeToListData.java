package cn.cyberpeace.mp.flow.v2.client.ixia;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/11/4
 */
@Getter
@Setter
public class AddStrikeToListData implements HttpData {
    private List<Strike> strike;

    @Getter
    @Setter
    public static class Strike {
        private String id;
    }
}
