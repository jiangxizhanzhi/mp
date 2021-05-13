package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_scene_scene_element")
public class SceneElementModel extends BaseSceneElementModel {
    @DefaultInt(1)
    private Integer event;
    @DefaultInt(1)
    private Integer eventStatus;
    @DefaultInt(1)
    private Integer process;
    @DefaultInt(1)
    private Integer processStatus;
    @DefaultStr
    private Integer processMessage;

    public static class Event {
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

    public static class EventStatus {
        public static final Integer START = 1;
        public static final Integer IN_PROGRESS = 2;
        public static final Integer PAUSE = 3;
        public static final Integer OVER = 4;
        public static final Integer ABNORMAL = 5;

        private EventStatus() {}
    }

    public static class ProcessStatus {
        public static final Integer START = 1;
        public static final Integer IN_PROGRESS = 2;
        public static final Integer PAUSE = 3;
        public static final Integer OVER = 4;
        public static final Integer ABNORMAL = 5;

        private ProcessStatus() {}
    }
}
