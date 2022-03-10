package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Network;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualRouter;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerGetData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineGetData;
import cn.hutool.core.collection.CollectionUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @Description:
 * @Author hewei
 * @Date：2022/3/9 16:37
 */
@Service
@Slf4j
public class ScenePluginCloudSceneSynchronizationService {
    public static final long REFRESH_RATE = 60 * 24;

    public static final long REFRESH_MINUTE = 60000;

    private static ExecutorService threadPool = null;

    private SceneElementMapper sceneElementMapper;

    private final Container containerClient = new Container();
    private final VirtualMachine vmClient = new VirtualMachine();
    private final Network netClient = new Network();
    private final VirtualRouter vrClient = new VirtualRouter();

    @Scheduled(initialDelay = REFRESH_RATE * REFRESH_MINUTE, fixedRate = REFRESH_RATE * REFRESH_MINUTE)
    public void scheduleNode() {
        if (threadPool == null) {
            threadPool = getSingletonThreadPool();
        }

        for (Integer offset = 1, limit = 10; ; offset++) {
            List<ContainerGetData> containers = containerClient.getAllContainer(limit, offset);
            Map<String, String> containerName2Id = new HashMap<>();
            if (CollectionUtil.isEmpty(containers)) {
                break;
            }
            containers.forEach(container -> {
                if (container.getName() != null && container.getId() != null && container.getName().startsWith("MP_SCENE")) {
                    String name = container.getName().replace("MP_SCENE_", StringUtils.EMPTY);
                    containerName2Id.put(name, container.getId());
                }
            });
            containerName2Id.forEach((name, id) -> {
                var condition = new HumanCondition().filterEqual(SceneElementModel::getId, name);
                SceneElementModel element = sceneElementMapper.list(condition).get(0);
                if (element == null) {
                    return;
                }
                if (element.getEventStatus() == EventStatus.OVER && element.getEvent() == SceneElementModel.Event.DELETE) {
                    containerClient.deleteContainerById(id);
                } else if (element.getEventStatus() == EventStatus.ABNORMAL && element.getEvent() == SceneElementModel.Event.CREATE) {
                    containerClient.deleteContainerById(id);
                }
            });
        }

        for (Integer offset = 1, limit = 10; ; offset++) {
            List<VirtualMachineGetData> vmData = vmClient.getAllInstance(limit, offset);
            Map<String, String> vmName2Id = new HashMap<>();
            if (CollectionUtil.isEmpty(vmData)) {
                break;
            }
            vmData.forEach(vm -> {
                if (vm.getName() != null && vm.getId() != null && vm.getName().startsWith("MP_SCENE")) {
                    String name = vm.getName().replace("MP_SCENE_", StringUtils.EMPTY);
                    vmName2Id.put(name, vm.getId());
                }
            });
            vmName2Id.forEach((name, id) -> {
                var condition = new HumanCondition().filterEqual(SceneElementModel::getId, name);
                SceneElementModel element = sceneElementMapper.list(condition).get(0);
                if (element == null) {
                    return;
                }
                if (element.getEventStatus() == EventStatus.OVER && element.getEvent() == SceneElementModel.Event.DELETE) {
                    vmClient.deleteInstanceById(id);
                } else if (element.getEventStatus() == EventStatus.ABNORMAL && element.getEvent() == SceneElementModel.Event.CREATE) {
                    vmClient.deleteInstanceById(id);
                }
            });
        }
    }

    /**
     * 静态内部类的单例模式
     *
     * @return
     */
    public static ExecutorService getSingletonThreadPool() {
        return ThreadPoolHolder.threadPoolExecutor;
    }

    private static class ThreadPoolHolder {
        static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("online-report-pool-%d").build();
        private static ExecutorService threadPoolExecutor = new ThreadPoolExecutor(20, 100, 20, TimeUnit.SECONDS,
                new ArrayBlockingQueue(100), namedThreadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
    }
}
