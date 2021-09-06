package cn.cyberpeace.mp.scene.core.event;

import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.core.event.base.EventTransition;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneElementEventTransition {

    public static final Map<String, List<Integer>> SCENE_ELEMENT_EVENT_TRANSITION = MapUtils.putAll(
            new HashMap<>(),
            new Object[]{
                    EventTransition.genKey(SceneElementModel.Event.DEFAULT, EventStatus.INITIAL), List.of(
                    SceneElementModel.Event.DEFAULT, SceneElementModel.Event.INITIATE, SceneElementModel.Event.CREATE,
                    SceneElementModel.Event.START, SceneElementModel.Event.STOP, SceneElementModel.Event.RESTART,
                    SceneElementModel.Event.PAUSE, SceneElementModel.Event.UNPAUSE, SceneElementModel.Event.SNAPSHOT,
                    SceneElementModel.Event.DELETE, SceneElementModel.Event.MODIFY
            ),
                    EventTransition.genKey(SceneElementModel.Event.DEFAULT, EventStatus.START), List.of(
                    SceneElementModel.Event.DEFAULT, SceneElementModel.Event.INITIATE, SceneElementModel.Event.CREATE,
                    SceneElementModel.Event.START, SceneElementModel.Event.STOP, SceneElementModel.Event.RESTART,
                    SceneElementModel.Event.PAUSE, SceneElementModel.Event.UNPAUSE, SceneElementModel.Event.SNAPSHOT,
                    SceneElementModel.Event.DELETE, SceneElementModel.Event.MODIFY
            ),
                    EventTransition.genKey(SceneElementModel.Event.DEFAULT, EventStatus.IN_PROGRESS), List.of(
                    SceneElementModel.Event.DEFAULT, SceneElementModel.Event.INITIATE, SceneElementModel.Event.CREATE,
                    SceneElementModel.Event.START, SceneElementModel.Event.STOP, SceneElementModel.Event.RESTART,
                    SceneElementModel.Event.PAUSE, SceneElementModel.Event.UNPAUSE, SceneElementModel.Event.SNAPSHOT,
                    SceneElementModel.Event.DELETE, SceneElementModel.Event.MODIFY
            ),
                    EventTransition.genKey(SceneElementModel.Event.DEFAULT, EventStatus.PAUSE), List.of(
                    SceneElementModel.Event.DEFAULT, SceneElementModel.Event.INITIATE, SceneElementModel.Event.CREATE,
                    SceneElementModel.Event.START, SceneElementModel.Event.STOP, SceneElementModel.Event.RESTART,
                    SceneElementModel.Event.PAUSE, SceneElementModel.Event.UNPAUSE, SceneElementModel.Event.SNAPSHOT,
                    SceneElementModel.Event.DELETE, SceneElementModel.Event.MODIFY
            ),
                    EventTransition.genKey(SceneElementModel.Event.DEFAULT, EventStatus.OVER), List.of(
                    SceneElementModel.Event.DEFAULT, SceneElementModel.Event.INITIATE, SceneElementModel.Event.CREATE,
                    SceneElementModel.Event.START, SceneElementModel.Event.STOP, SceneElementModel.Event.RESTART,
                    SceneElementModel.Event.PAUSE, SceneElementModel.Event.UNPAUSE, SceneElementModel.Event.SNAPSHOT,
                    SceneElementModel.Event.DELETE, SceneElementModel.Event.MODIFY
            ),
                    EventTransition.genKey(SceneElementModel.Event.DEFAULT, EventStatus.ABNORMAL), List.of(
                    SceneElementModel.Event.DEFAULT, SceneElementModel.Event.INITIATE, SceneElementModel.Event.CREATE,
                    SceneElementModel.Event.START, SceneElementModel.Event.STOP, SceneElementModel.Event.RESTART,
                    SceneElementModel.Event.PAUSE, SceneElementModel.Event.UNPAUSE, SceneElementModel.Event.SNAPSHOT,
                    SceneElementModel.Event.DELETE, SceneElementModel.Event.MODIFY
            ),

                    // 初始化事件
                    EventTransition.genKey(SceneElementModel.Event.INITIATE, EventStatus.INITIAL), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.INITIATE, EventStatus.START), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.INITIATE, EventStatus.IN_PROGRESS), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.INITIATE, EventStatus.PAUSE), List.of(
                    SceneElementModel.Event.INITIATE, SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.INITIATE, EventStatus.OVER), List.of(
                    SceneElementModel.Event.CREATE, SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.INITIATE, EventStatus.ABNORMAL), List.of(
                    SceneElementModel.Event.INITIATE, SceneElementModel.Event.DELETE
            ),

                    // 创建事件
                    EventTransition.genKey(SceneElementModel.Event.CREATE, EventStatus.INITIAL), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.CREATE, EventStatus.START), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.CREATE, EventStatus.IN_PROGRESS), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.CREATE, EventStatus.PAUSE), List.of(
                    SceneElementModel.Event.CREATE, SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.CREATE, EventStatus.OVER), List.of(
                    SceneElementModel.Event.STOP, SceneElementModel.Event.RESTART, SceneElementModel.Event.PAUSE,
                    SceneElementModel.Event.SNAPSHOT, SceneElementModel.Event.MODIFY, SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.CREATE, EventStatus.ABNORMAL), List.of(
                    SceneElementModel.Event.CREATE, SceneElementModel.Event.DELETE
            ),

                    // 启动事件
                    EventTransition.genKey(SceneElementModel.Event.START, EventStatus.INITIAL), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.START, EventStatus.START), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.START, EventStatus.IN_PROGRESS), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.START, EventStatus.PAUSE), List.of(
                    SceneElementModel.Event.START, SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.START, EventStatus.OVER), List.of(
                    SceneElementModel.Event.STOP, SceneElementModel.Event.RESTART, SceneElementModel.Event.PAUSE,
                    SceneElementModel.Event.SNAPSHOT, SceneElementModel.Event.MODIFY, SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.START, EventStatus.ABNORMAL), List.of(
                    SceneElementModel.Event.START, SceneElementModel.Event.DELETE
            ),

                    // 关闭事件
                    EventTransition.genKey(SceneElementModel.Event.STOP, EventStatus.INITIAL), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.STOP, EventStatus.START), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.STOP, EventStatus.IN_PROGRESS), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.STOP, EventStatus.PAUSE), List.of(
                    SceneElementModel.Event.STOP, SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.STOP, EventStatus.OVER), List.of(
                    SceneElementModel.Event.START, SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.STOP, EventStatus.ABNORMAL), List.of(
                    SceneElementModel.Event.STOP, SceneElementModel.Event.DELETE
            ),

                    // 重启事件
                    EventTransition.genKey(SceneElementModel.Event.RESTART, EventStatus.INITIAL), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.RESTART, EventStatus.START), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.RESTART, EventStatus.IN_PROGRESS), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.RESTART, EventStatus.PAUSE), List.of(
                    SceneElementModel.Event.RESTART, SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.RESTART, EventStatus.OVER), List.of(
                    SceneElementModel.Event.STOP, SceneElementModel.Event.RESTART, SceneElementModel.Event.PAUSE,
                    SceneElementModel.Event.SNAPSHOT, SceneElementModel.Event.MODIFY, SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.RESTART, EventStatus.ABNORMAL), List.of(
                    SceneElementModel.Event.RESTART, SceneElementModel.Event.DELETE
            ),

                    // 暂停事件
                    EventTransition.genKey(SceneElementModel.Event.PAUSE, EventStatus.INITIAL), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.PAUSE, EventStatus.START), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.PAUSE, EventStatus.IN_PROGRESS), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.PAUSE, EventStatus.PAUSE), List.of(
                    SceneElementModel.Event.PAUSE, SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.PAUSE, EventStatus.OVER), List.of(
                    SceneElementModel.Event.UNPAUSE, SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.PAUSE, EventStatus.ABNORMAL), List.of(
                    SceneElementModel.Event.PAUSE, SceneElementModel.Event.DELETE
            ),

                    // 暂停恢复事件
                    EventTransition.genKey(SceneElementModel.Event.UNPAUSE, EventStatus.INITIAL), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.UNPAUSE, EventStatus.START), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.UNPAUSE, EventStatus.IN_PROGRESS), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.UNPAUSE, EventStatus.PAUSE), List.of(
                    SceneElementModel.Event.UNPAUSE, SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.UNPAUSE, EventStatus.OVER), List.of(
                    SceneElementModel.Event.STOP, SceneElementModel.Event.RESTART, SceneElementModel.Event.PAUSE,
                    SceneElementModel.Event.SNAPSHOT, SceneElementModel.Event.MODIFY, SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.UNPAUSE, EventStatus.ABNORMAL), List.of(
                    SceneElementModel.Event.UNPAUSE, SceneElementModel.Event.DELETE
            ),

                    // 快照事件
                    EventTransition.genKey(SceneElementModel.Event.SNAPSHOT, EventStatus.INITIAL), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.SNAPSHOT, EventStatus.START), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.SNAPSHOT, EventStatus.IN_PROGRESS), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.SNAPSHOT, EventStatus.PAUSE), List.of(
                    SceneElementModel.Event.SNAPSHOT, SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.SNAPSHOT, EventStatus.OVER), List.of(
                    SceneElementModel.Event.STOP, SceneElementModel.Event.RESTART, SceneElementModel.Event.PAUSE,
                    SceneElementModel.Event.SNAPSHOT, SceneElementModel.Event.MODIFY, SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.SNAPSHOT, EventStatus.ABNORMAL), List.of(
                    SceneElementModel.Event.SNAPSHOT, SceneElementModel.Event.DELETE
            ),

                    // 删除事件
                    EventTransition.genKey(SceneElementModel.Event.DELETE, EventStatus.INITIAL), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.DELETE, EventStatus.START), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.DELETE, EventStatus.IN_PROGRESS), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.DELETE, EventStatus.PAUSE), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.DELETE, EventStatus.OVER), List.of(
                    SceneElementModel.Event.INITIATE, SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.DELETE, EventStatus.ABNORMAL), List.of(
                    SceneElementModel.Event.DELETE
            ),

                    // 修改事件
                    EventTransition.genKey(SceneElementModel.Event.MODIFY, EventStatus.INITIAL), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.MODIFY, EventStatus.START), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.MODIFY, EventStatus.IN_PROGRESS), List.of(
                    SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.MODIFY, EventStatus.PAUSE), List.of(
                    SceneElementModel.Event.MODIFY
            ),
                    EventTransition.genKey(SceneElementModel.Event.MODIFY, EventStatus.OVER), List.of(
                    SceneElementModel.Event.STOP, SceneElementModel.Event.RESTART, SceneElementModel.Event.PAUSE,
                    SceneElementModel.Event.SNAPSHOT, SceneElementModel.Event.MODIFY, SceneElementModel.Event.DELETE
            ),
                    EventTransition.genKey(SceneElementModel.Event.MODIFY, EventStatus.ABNORMAL), List.of(
                    SceneElementModel.Event.MODIFY, SceneElementModel.Event.DELETE
            )
            });
}
