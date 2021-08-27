package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.scene.core.event.base.BaseEventModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_scene_scene")
public class SceneModel extends BaseSceneModel implements BaseEventModel {
    @DefaultInt
    private Integer event;
    @DefaultInt
    private Integer eventStatus;
    @DefaultInt
    private Integer process;
    @DefaultInt
    private Integer processStatus;
    @DefaultStr
    private String processMessage;

    @Override
    public String genTarget() {
        return getId();
    }

    public static class Event {
        public static final Integer DEFAULT = 0;
        public static final Integer INITIATE = 1;
        public static final Integer CREATE = 2;
        public static final Integer START = 3;
        public static final Integer STOP = 4;
        public static final Integer RESTART = 5;
        public static final Integer PAUSE = 6;
        public static final Integer UNPAUSE = 7;
        public static final Integer SNAPSHOT = 8;
        public static final Integer DELETE = 9;
        public static final Integer MODIFY = 10;

        private Event() {}
    }
}
