package cn.cyberpeace.mp.cscene.dto.event;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import cn.cyberpeace.mp.scene.config.MultiSceneConfig;
import cn.cyberpeace.mp.scene.core.bo.MultiSceneRegisterBo;
import cn.cyberpeace.mp.scene.service.ManageCenterService;
import cn.cyberpeace.mp.scene.util.MultiSceneClient;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Getter
public abstract class MultiSceneBaseEvent<T extends SnakeHttpData, R> {

    private static final MultiSceneClient CLIENT = new MultiSceneClient();

    public static final String BROADCAST_SAVE_SCENE_CONFIG = "00";
    public static final String BROADCAST_DELETE_SCENE_CONFIG = "01";
    public static final String BROADCAST_START_SCENE_INSTANCE = "02";
    public static final String BROADCAST_CLOSE_SCENE_INSTANCE = "03";
    public static final String START_PROCESS = "10";

    private static final String POST = "post";
    private static final String DELETE = "delete";

    private static final Map<String, String> typeToPath = new HashMap<>(8);
    private static final Map<String, String> typeToMethod = new HashMap<>(8);
    static {
        typeToPath.put(BROADCAST_SAVE_SCENE_CONFIG, "/api/mp/cscene/cscenes/sync");
        typeToPath.put(BROADCAST_DELETE_SCENE_CONFIG, "/api/mp/cscene/cscenes");
        typeToPath.put(BROADCAST_START_SCENE_INSTANCE, "/api/mp/cscene/cscenes/test_instance");
        typeToPath.put(BROADCAST_CLOSE_SCENE_INSTANCE, "/api/mp/cscene/cscene_instances");

        typeToMethod.put(BROADCAST_SAVE_SCENE_CONFIG, POST);
        typeToMethod.put(BROADCAST_DELETE_SCENE_CONFIG, DELETE);
        typeToMethod.put(BROADCAST_START_SCENE_INSTANCE, POST);
        typeToMethod.put(BROADCAST_CLOSE_SCENE_INSTANCE, DELETE);
    }


    private final String messageType;
    private final T message;

    public MultiSceneBaseEvent(String messageType, T message) {
        this.message = message;
        this.messageType = messageType;
    }


    public void publish() {
        //给子靶场广播的消息
        MultiSceneConfig multiSceneConfig = BeanCommon.getBean(MultiSceneConfig.class);

        if (this.getMessageType().startsWith("0")) {
            if (multiSceneConfig.isManagerCenterFlag()) {
                List<MultiSceneRegisterBo> multiSceneRegisterBoList = BeanCommon.getBean(ManageCenterService.class).getAllOnlineCyberRangeWithoutSelf();

                List<Runnable> runnableList = multiSceneRegisterBoList.stream().map(range -> (Runnable)(() -> {
                    String server = String.format("%s:%s", range.getRangeIp(), range.getRangePort());
                    log.info("send {} to {}", this.messageType, server);


                    R returnData;
                    if (POST.equals(typeToMethod.get(this.getMessageType()))) {
                        returnData = CLIENT.commonSnakePost(server+typeToPath.get(this.getMessageType()),
                                this.message, null, this.doGetReturnTypeReference());
                    } else {
                        returnData = CLIENT.commonSnakeDelete(server+typeToPath.get(this.getMessageType()),
                                this.message, null, this.doGetReturnTypeReference());
                    }

                    doValidateReturnMessage(returnData);
                })).collect(Collectors.toList());

                TaskCommon.runTasks(runnableList);
            } else {
                throw new MpRuntimeException("非联管中心, 不能发送广播消息");
            }
        } else if (this.getMessageType().startsWith("1")) {
            //子靶场给联管中心的单播消息
            List<String> manageServers = multiSceneConfig.getManageServersWithoutSelf();
            List<Runnable> runnableList = manageServers.stream().map(manageServer -> (Runnable)(() -> {

                R returnData;
                if (POST.equals(typeToMethod.get(this.getMessageType()))) {
                    returnData = CLIENT.commonSnakePost(manageServer+typeToPath.get(this.getMessageType()), this.message, null, this.doGetReturnTypeReference());
                } else {
                    returnData = CLIENT.commonSnakeDelete(manageServer+typeToPath.get(this.getMessageType()), this.message, null, this.doGetReturnTypeReference());
                }


                doValidateReturnMessage(returnData);
            })).collect(Collectors.toList());
            TaskCommon.runTasks(runnableList);
        }
    }

    public abstract void doValidateReturnMessage(R returnData);

    public abstract TypeReference<R> doGetReturnTypeReference();

}

