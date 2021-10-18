package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.scene.core.event.base.BaseEventModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_scene_scene_element")
public class SceneElementModel extends BaseSceneElementModel implements BaseEventModel {
    @DefaultInt
    private Integer event;
    @DefaultInt
    private Integer eventStatus;
    @DefaultStr
    private String process;
    @DefaultInt
    private Integer processStatus;
    @DefaultStr
    private String processMessage;

    @Override
    public String genTarget() {
        return getId();
    }

    public static class Event {
        public static final int DEFAULT = 0;
        public static final int INITIATE = 1;
        public static final int CREATE = 2;
        public static final int START = 3;
        public static final int STOP = 4;
        public static final int RESTART = 5;
        public static final int PAUSE = 6;
        public static final int UNPAUSE = 7;
        public static final int SNAPSHOT = 8;
        public static final int DELETE = 9;
        public static final int MODIFY = 10;

        private Event() {}
    }
}
