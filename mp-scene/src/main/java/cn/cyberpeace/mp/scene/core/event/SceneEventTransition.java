package cn.cyberpeace.mp.scene.core.event;

import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.core.event.base.EventTransition;
import cn.cyberpeace.mp.scene.model.SceneModel;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneEventTransition {

    public static final Map<String, List<Integer>> SCENE_EVENT_TRANSITION = MapUtils.putAll(
            new HashMap<>(),
            new Object[]{
                    EventTransition.genKey(SceneModel.Event.DEFAULT, EventStatus.INITIAL), List.of(
                            SceneModel.Event.DEFAULT, SceneModel.Event.INITIATE, SceneModel.Event.CREATE,
                            SceneModel.Event.START, SceneModel.Event.STOP, SceneModel.Event.RESTART,
                            SceneModel.Event.PAUSE, SceneModel.Event.UNPAUSE, SceneModel.Event.SNAPSHOT,
                            SceneModel.Event.DELETE, SceneModel.Event.MODIFY, SceneModel.Event.SUSPEND, SceneModel.Event.RESUME
                    ),
                    EventTransition.genKey(SceneModel.Event.DEFAULT, EventStatus.START), List.of(
                            SceneModel.Event.DEFAULT, SceneModel.Event.INITIATE, SceneModel.Event.CREATE,
                            SceneModel.Event.START, SceneModel.Event.STOP, SceneModel.Event.RESTART,
                            SceneModel.Event.PAUSE, SceneModel.Event.UNPAUSE, SceneModel.Event.SNAPSHOT,
                            SceneModel.Event.DELETE, SceneModel.Event.MODIFY, SceneModel.Event.SUSPEND, SceneModel.Event.RESUME
                    ),
                    EventTransition.genKey(SceneModel.Event.DEFAULT, EventStatus.IN_PROGRESS), List.of(
                            SceneModel.Event.DEFAULT, SceneModel.Event.INITIATE, SceneModel.Event.CREATE,
                            SceneModel.Event.START, SceneModel.Event.STOP, SceneModel.Event.RESTART,
                            SceneModel.Event.PAUSE, SceneModel.Event.UNPAUSE, SceneModel.Event.SNAPSHOT,
                            SceneModel.Event.DELETE, SceneModel.Event.MODIFY, SceneModel.Event.SUSPEND, SceneModel.Event.RESUME
                    ),
                    EventTransition.genKey(SceneModel.Event.DEFAULT, EventStatus.PAUSE), List.of(
                            SceneModel.Event.DEFAULT, SceneModel.Event.INITIATE, SceneModel.Event.CREATE,
                            SceneModel.Event.START, SceneModel.Event.STOP, SceneModel.Event.RESTART,
                            SceneModel.Event.PAUSE, SceneModel.Event.UNPAUSE, SceneModel.Event.SNAPSHOT,
                            SceneModel.Event.DELETE, SceneModel.Event.MODIFY, SceneModel.Event.SUSPEND, SceneModel.Event.RESUME
                    ),
                    EventTransition.genKey(SceneModel.Event.DEFAULT, EventStatus.OVER), List.of(
                            SceneModel.Event.DEFAULT, SceneModel.Event.INITIATE, SceneModel.Event.CREATE,
                            SceneModel.Event.START, SceneModel.Event.STOP, SceneModel.Event.RESTART,
                            SceneModel.Event.PAUSE, SceneModel.Event.UNPAUSE, SceneModel.Event.SNAPSHOT,
                            SceneModel.Event.DELETE, SceneModel.Event.MODIFY, SceneModel.Event.SUSPEND, SceneModel.Event.RESUME
                    ),
                    EventTransition.genKey(SceneModel.Event.DEFAULT, EventStatus.ABNORMAL), List.of(
                            SceneModel.Event.DEFAULT, SceneModel.Event.INITIATE, SceneModel.Event.CREATE,
                            SceneModel.Event.START, SceneModel.Event.STOP, SceneModel.Event.RESTART,
                            SceneModel.Event.PAUSE, SceneModel.Event.UNPAUSE, SceneModel.Event.SNAPSHOT,
                            SceneModel.Event.DELETE, SceneModel.Event.MODIFY, SceneModel.Event.SUSPEND, SceneModel.Event.RESUME
                    ),

                    // 初始化事件
                    EventTransition.genKey(SceneModel.Event.INITIATE, EventStatus.INITIAL), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.INITIATE, EventStatus.START), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.INITIATE, EventStatus.IN_PROGRESS), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.INITIATE, EventStatus.PAUSE), List.of(
                            SceneModel.Event.INITIATE, SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.INITIATE, EventStatus.OVER), List.of(
                            SceneModel.Event.CREATE, SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.INITIATE, EventStatus.ABNORMAL), List.of(
                            SceneModel.Event.INITIATE, SceneModel.Event.DELETE
                    ),

                    // 创建事件
                    EventTransition.genKey(SceneModel.Event.CREATE, EventStatus.INITIAL), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.CREATE, EventStatus.START), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.CREATE, EventStatus.IN_PROGRESS), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.CREATE, EventStatus.PAUSE), List.of(
                            SceneModel.Event.CREATE, SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.CREATE, EventStatus.OVER), List.of(
                            SceneModel.Event.STOP, SceneModel.Event.RESTART, SceneModel.Event.PAUSE,
                            SceneModel.Event.SNAPSHOT, SceneModel.Event.MODIFY, SceneModel.Event.DELETE,
                            SceneModel.Event.SUSPEND, SceneModel.Event.RESUME
                    ),
                    EventTransition.genKey(SceneModel.Event.CREATE, EventStatus.ABNORMAL), List.of(
                            SceneModel.Event.CREATE, SceneModel.Event.DELETE
                    ),

                    // 启动事件
                    EventTransition.genKey(SceneModel.Event.START, EventStatus.INITIAL), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.START, EventStatus.START), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.START, EventStatus.IN_PROGRESS), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.START, EventStatus.PAUSE), List.of(
                            SceneModel.Event.START, SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.START, EventStatus.OVER), List.of(
                            SceneModel.Event.STOP, SceneModel.Event.RESTART, SceneModel.Event.PAUSE,
                            SceneModel.Event.SNAPSHOT, SceneModel.Event.MODIFY, SceneModel.Event.DELETE,
                            SceneModel.Event.SUSPEND
                    ),
                    EventTransition.genKey(SceneModel.Event.START, EventStatus.ABNORMAL), List.of(
                            SceneModel.Event.START, SceneModel.Event.DELETE
                    ),

                    // 关闭事件
                    EventTransition.genKey(SceneModel.Event.STOP, EventStatus.INITIAL), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.STOP, EventStatus.START), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.STOP, EventStatus.IN_PROGRESS), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.STOP, EventStatus.PAUSE), List.of(
                            SceneModel.Event.STOP, SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.STOP, EventStatus.OVER), List.of(
                            SceneModel.Event.START, SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.STOP, EventStatus.ABNORMAL), List.of(
                            SceneModel.Event.STOP, SceneModel.Event.DELETE
                    ),

                    // 重启事件
                    EventTransition.genKey(SceneModel.Event.RESTART, EventStatus.INITIAL), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.RESTART, EventStatus.START), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.RESTART, EventStatus.IN_PROGRESS), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.RESTART, EventStatus.PAUSE), List.of(
                            SceneModel.Event.RESTART, SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.RESTART, EventStatus.OVER), List.of(
                            SceneModel.Event.STOP, SceneModel.Event.RESTART, SceneModel.Event.PAUSE,
                            SceneModel.Event.SNAPSHOT, SceneModel.Event.MODIFY, SceneModel.Event.DELETE,
                            SceneModel.Event.SUSPEND
                    ),
                    EventTransition.genKey(SceneModel.Event.RESTART, EventStatus.ABNORMAL), List.of(
                            SceneModel.Event.RESTART, SceneModel.Event.DELETE
                    ),

                    // 暂停事件
                    EventTransition.genKey(SceneModel.Event.PAUSE, EventStatus.INITIAL), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.PAUSE, EventStatus.START), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.PAUSE, EventStatus.IN_PROGRESS), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.PAUSE, EventStatus.PAUSE), List.of(
                            SceneModel.Event.PAUSE, SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.PAUSE, EventStatus.OVER), List.of(
                            SceneModel.Event.UNPAUSE, SceneModel.Event.DELETE, SceneModel.Event.PAUSE
                    ),
                    EventTransition.genKey(SceneModel.Event.PAUSE, EventStatus.ABNORMAL), List.of(
                            SceneModel.Event.PAUSE, SceneModel.Event.DELETE, SceneModel.Event.PAUSE
                    ),

                    // 暂停恢复事件
                    EventTransition.genKey(SceneModel.Event.UNPAUSE, EventStatus.INITIAL), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.UNPAUSE, EventStatus.START), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.UNPAUSE, EventStatus.IN_PROGRESS), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.UNPAUSE, EventStatus.PAUSE), List.of(
                            SceneModel.Event.UNPAUSE, SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.UNPAUSE, EventStatus.OVER), List.of(
                            SceneModel.Event.STOP, SceneModel.Event.RESTART, SceneModel.Event.PAUSE,
                            SceneModel.Event.SNAPSHOT, SceneModel.Event.MODIFY, SceneModel.Event.DELETE,
                            SceneModel.Event.SUSPEND, SceneModel.Event.UNPAUSE
                    ),
                    EventTransition.genKey(SceneModel.Event.UNPAUSE, EventStatus.ABNORMAL), List.of(
                            SceneModel.Event.UNPAUSE, SceneModel.Event.DELETE, SceneModel.Event.UNPAUSE
                    ),

                    // 挂起事件
                    EventTransition.genKey(SceneModel.Event.SUSPEND, EventStatus.INITIAL), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.SUSPEND, EventStatus.START), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.SUSPEND, EventStatus.IN_PROGRESS), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.SUSPEND, EventStatus.PAUSE), List.of(
                            SceneModel.Event.SUSPEND, SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.SUSPEND, EventStatus.OVER), List.of(
                            SceneModel.Event.RESUME, SceneModel.Event.DELETE, SceneModel.Event.SUSPEND
                    ),
                    EventTransition.genKey(SceneModel.Event.SUSPEND, EventStatus.ABNORMAL), List.of(
                            SceneModel.Event.SUSPEND, SceneModel.Event.DELETE, SceneModel.Event.SUSPEND
                    ),

                    // 挂起恢复事件
                    EventTransition.genKey(SceneModel.Event.RESUME, EventStatus.INITIAL), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.RESUME, EventStatus.START), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.RESUME, EventStatus.IN_PROGRESS), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.RESUME, EventStatus.PAUSE), List.of(
                            SceneModel.Event.RESUME, SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.RESUME, EventStatus.OVER), List.of(
                            SceneModel.Event.STOP, SceneModel.Event.RESTART, SceneModel.Event.SUSPEND,
                            SceneModel.Event.SNAPSHOT, SceneModel.Event.MODIFY, SceneModel.Event.DELETE,
                            SceneModel.Event.PAUSE, SceneModel.Event.RESUME
                    ),
                    EventTransition.genKey(SceneModel.Event.RESUME, EventStatus.ABNORMAL), List.of(
                            SceneModel.Event.RESUME, SceneModel.Event.DELETE, SceneModel.Event.RESUME
                    ),

                    // 快照事件
                    EventTransition.genKey(SceneModel.Event.SNAPSHOT, EventStatus.INITIAL), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.SNAPSHOT, EventStatus.START), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.SNAPSHOT, EventStatus.IN_PROGRESS), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.SNAPSHOT, EventStatus.PAUSE), List.of(
                            SceneModel.Event.SNAPSHOT, SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.SNAPSHOT, EventStatus.OVER), List.of(
                            SceneModel.Event.STOP, SceneModel.Event.RESTART, SceneModel.Event.PAUSE,
                            SceneModel.Event.SNAPSHOT, SceneModel.Event.MODIFY, SceneModel.Event.DELETE,
                            SceneModel.Event.SUSPEND
                    ),
                    EventTransition.genKey(SceneModel.Event.SNAPSHOT, EventStatus.ABNORMAL), List.of(
                            SceneModel.Event.SNAPSHOT, SceneModel.Event.DELETE
                    ),

                    // 删除事件
                    EventTransition.genKey(SceneModel.Event.DELETE, EventStatus.INITIAL), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.DELETE, EventStatus.START), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.DELETE, EventStatus.IN_PROGRESS), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.DELETE, EventStatus.PAUSE), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.DELETE, EventStatus.OVER), List.of(
                            SceneModel.Event.INITIATE, SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.DELETE, EventStatus.ABNORMAL), List.of(
                            SceneModel.Event.DELETE
                    ),

                    // 修改事件
                    EventTransition.genKey(SceneModel.Event.MODIFY, EventStatus.INITIAL), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.MODIFY, EventStatus.START), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.MODIFY, EventStatus.IN_PROGRESS), List.of(
                            SceneModel.Event.DELETE
                    ),
                    EventTransition.genKey(SceneModel.Event.MODIFY, EventStatus.PAUSE), List.of(
                            SceneModel.Event.MODIFY
                    ),
                    EventTransition.genKey(SceneModel.Event.MODIFY, EventStatus.OVER), List.of(
                            SceneModel.Event.STOP, SceneModel.Event.RESTART, SceneModel.Event.PAUSE,
                            SceneModel.Event.SNAPSHOT, SceneModel.Event.MODIFY, SceneModel.Event.DELETE,
                            SceneModel.Event.SUSPEND
                    ),
                    EventTransition.genKey(SceneModel.Event.MODIFY, EventStatus.ABNORMAL), List.of(
                            SceneModel.Event.MODIFY, SceneModel.Event.DELETE
                    )
            });
}
