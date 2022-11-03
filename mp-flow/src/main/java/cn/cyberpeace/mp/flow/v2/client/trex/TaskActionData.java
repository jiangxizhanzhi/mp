package cn.cyberpeace.mp.flow.v2.client.trex;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/27
 */
@Getter
@Setter
public class TaskActionData implements HttpData {
    private String id;
    private Integer action;

    public static class Action {
        public static final int START = 1;
        public static final int STOP = 2;
        public static final int PAUSE = 3;
        public static final int UNPAUSE = 4;
    }
}
