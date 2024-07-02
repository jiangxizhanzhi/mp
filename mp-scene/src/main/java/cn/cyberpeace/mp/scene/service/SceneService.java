package cn.cyberpeace.mp.scene.service;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.thread.ThreadUtils;
import cn.cyberpeace.mp.scene.constant.SceneErrorCode;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.event.SceneEventHandler;
import cn.cyberpeace.mp.scene.core.event.SceneStageRecorder;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.core.exception.SceneNotExistException;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.service.taskpool.ElementTaskPool;
import cn.cyberpeace.mp.scene.service.taskpool.SceneTaskPool;
import cn.cyberpeace.mp.scene.util.SceneEventUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@Slf4j
@Service
public class SceneService {

    @Resource
    private SceneConfigService sceneConfigService;

    /**
     * get
     *
     * @param id
     * @param isAreaMode
     * @param areaId
     * @param context
     * @return
     */
    public ServiceResult<SceneDataBO> get(String id, boolean isAreaMode, String areaId, SceneContextBO context) {
        SceneHandler sceneHandler;
        try {
            sceneHandler = new SceneHandler(id, areaId, isAreaMode, context);
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(null, SceneErrorCode.SCENE_NOT_EXIST);
        }
        return new ServiceResult<>(sceneHandler.toData());
    }

    /**
     * create
     *
     * @param sceneDataBO
     * @param context
     * @param partialStart
     * @return
     */
    public ServiceResult<SceneDataBO> create(SceneDataBO sceneDataBO, SceneContextBO context, boolean partialStart) {
        SceneHandler sceneHandler;
        try {
            sceneHandler = new SceneHandler(sceneDataBO, false, context);
            sceneHandler.setRealTimeUpdate(true);
            if (sceneHandler.getScene() != null) {
                sceneHandler.getScene().setCreateTime(null);
            }
            sceneHandler.reformTopo();
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(null, SceneErrorCode.SCENE_NOT_EXIST);
        }

        // 在prepare中预先判断下有多少启动流程
        sceneHandler.prepare(partialStart);
        sceneHandler.save();

        SceneStageRecorder.registerRecoder(sceneHandler.getSceneId(), sceneHandler.getSceneStageRecorder());
        SceneTaskPool.getInstance().executeTask(() -> {
            LocalDateTime starTime = LocalDateTime.now();
            if (!sceneHandler.initiate(false)) {
                return;
            }
            sceneHandler.create();

            LocalDateTime endTime = LocalDateTime.now();
            long consumeSeconds = Duration.between(starTime, endTime).toSeconds();
            sceneHandler.getScene().setCreateConsumeSeconds(consumeSeconds);
            sceneHandler.updateScene();
            SceneStageRecorder.removeRecorder(sceneHandler.getSceneId());
        });

        return new ServiceResult<>(sceneHandler.toData());
    }

    /**
     * createFromSceneConfig
     *
     * @param sceneConfigDataBO
     * @param context
     * @return
     */
    public ServiceResult<SceneDataBO> createFromSceneConfig(SceneConfigDataBO sceneConfigDataBO,
                                                            SceneContextBO context) {
        return create(sceneConfigDataBO.exportToScene(), context, false);
    }

    /**
     * createFromSceneConfig
     *
     * @param sceneConfigId
     * @param context
     * @return
     */
    public ServiceResult<SceneDataBO> createFromSceneConfig(String sceneConfigId, SceneContextBO context) {
        SceneConfigDataBO sceneConfigDataBO = sceneConfigService.get(sceneConfigId, false, null, null)
                .getSource();

        if (sceneConfigDataBO == null) {
            return new ServiceResult<>(null, SceneErrorCode.SCENE_NOT_EXIST);
        }

        return createFromSceneConfig(sceneConfigDataBO, context);
    }

    /**
     * delete
     *
     * @param id
     * @param logic
     * @param context
     * @return
     */
    public ServiceResult<Boolean> delete(String id, boolean logic, SceneContextBO context) {
        SceneHandler sceneHandler;
        try {
//            sceneHandler = new SceneHandler(id, null, false, context);
//            sceneHandler.setRealTimeUpdate(true);
            sceneHandler = new SceneHandler(id, null, false, context);
            sceneHandler.setRealTimeUpdate(true);
            if (logic) {
                SceneTaskPool.getInstance().executeTask(() -> sceneHandler.logicDelete());
            } else {
                SceneTaskPool.getInstance().executeTask(() -> {
                    sceneHandler.delete();
                });
            }
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(false, SceneErrorCode.SCENE_NOT_EXIST);
        }

        return new ServiceResult<>(true);
    }

    /**
     * bulkDelete
     *
     * @param ids
     * @param logic
     * @param context
     * @return
     */
    public ServiceResult<Integer> bulkDelete(String[] ids, boolean logic, SceneContextBO context) {
        int result = 0;
        for (String id : ids) {
            if (Boolean.TRUE.equals(delete(id, logic, context).getSource())) {
                result += 1;
            }
        }

        if (result < ids.length) {
            log.error("Service bulk delete scene configs {} with some failed", Arrays.toString(ids));
            return new ServiceResult<>(result, BaseErrorCode.DATA_PARTIAL_SUCCESS);
        } else {
            return new ServiceResult<>(result);
        }
    }

    /**
     * modify
     *
     * @param sceneId
     * @param updatedSceneData
     * @param context
     * @param runner
     * @return
     */
    public ServiceResult<SceneDataBO> modify(String sceneId, SceneDataBO updatedSceneData, SceneContextBO context, Runnable runner) {
        SceneEventUtils.SceneEventInfo newEventInfo = this.createSceneEventInfo(sceneId, null, null, SceneModel.Event.MODIFY);
        SceneEventUtils.SceneEventInfo originLockInfo = SceneEventUtils.tryLockAndGetInfoIfLockFailed(sceneId, newEventInfo);
        if (originLockInfo != null) {
            // 更新曹邹只能有一个请求成功
            throw this.createOperateException(originLockInfo);
        }

        SceneHandler originSceneHandler = new SceneHandler(sceneId, null, false, context);
        // 场景信息目前只更新显示参数
        String newDisplayContext = updatedSceneData.getTopology().getScene().getDisplayContext();
        updatedSceneData.getTopology().setScene(originSceneHandler.getScene());
        updatedSceneData.getTopology().getScene().setDisplayContext(newDisplayContext);
        SceneHandler updatedSceneHandler = new SceneHandler(updatedSceneData, context);

        Queue<SceneElementBO> allElements = new ArrayDeque<>(updatedSceneHandler.getElementMap().values());
        while (!allElements.isEmpty()) {
            SceneElementBO element = allElements.poll();
            if (element != null) {
                List<SceneElementPortBO> connectPorts = updatedSceneHandler.getElementConnectedPorts(element.getId());
                if (CollectionUtils.isNotEmpty(connectPorts)) {
                    Set<String> ips = new HashSet<>();
                    for (SceneElementPortBO p : connectPorts) {
                        if (StringUtils.isNotEmpty(p.getIp())) {
                            if (ips.contains(p.getIp())) {
                                throw new MpRuntimeException(SceneErrorCode.IP_DUPLICATE.format(p.getIp()));
                            } else {
                                ips.add(p.getIp());
                            }
                        }
                    }
                }
            }
        }

        updatedSceneHandler.setRealTimeUpdate(true);
        if (updatedSceneHandler.getScene() != null) {
            updatedSceneHandler.getScene().setCreateTime(null);
        }
        updatedSceneHandler.save();
        // 获取配置更新的节点
        List<String> updateElementIds = new ArrayList<>();
        originSceneHandler.getElementMap().values().forEach(e -> {
            if (e.getEvent() == SceneModel.Event.MODIFY && e.getEventStatus() == EventStatus.ABNORMAL) {
                updateElementIds.add(e.getId());
            }
        });
        SceneTaskPool.getInstance().executeTask(() -> {
            originSceneHandler.getElementMap().values().forEach(e -> {
                if (e.getEvent() == SceneElementModel.Event.DEFAULT) {
                    updatedSceneHandler.addExcludeHandleNode(e.getId());
                    originSceneHandler.addExcludeHandleNode(e.getId());
                } else {
                    e.setEvent(SceneElementModel.Event.DEFAULT);
                    e.setEventStatus(EventStatus.INITIAL);
                }
            });

            for (SceneElementBO e : updatedSceneHandler.getElementMap().values()) {
                if (updatedSceneHandler.isHandleNode(e.getId())) {
                    e.setEvent(SceneElementModel.Event.DEFAULT);
                    e.setEventStatus(EventStatus.INITIAL);
                }
            }
            updatedSceneHandler.modify(originSceneHandler, updateElementIds);
            //W:Refresh scene after updating
            runner.run();
        });
        return new ServiceResult<>(updatedSceneHandler.toData());
    }

    /**
     * pause
     *
     * @param id
     * @param context
     * @return
     */
    public ServiceResult<Boolean> pause(String id, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        SceneEventUtils.SceneEventInfo newEventInfo = this.createSceneEventInfo(id, null, null, SceneModel.Event.PAUSE);
        SceneEventUtils.SceneEventInfo originLockInfo = SceneEventUtils.tryLockAndGetInfoIfLockFailed(id, newEventInfo);
        if (originLockInfo == null) {
            // 锁获取成功 没有其他线程执行操作
            SceneTaskPool.getInstance()
                    .executeTask(() -> {
                        try {
                            sceneHandler.pause();
                        } finally {
                            SceneEventUtils.unLock(id);
                        }
                    });
            return new ServiceResult<>(true);
        } else if (originLockInfo.getEvent() == SceneModel.Event.PAUSE) {
            // 锁获取失败, 其它线程正在执行相同操作 则直接返回成功
            return new ServiceResult<>(true);
        } else {
            // 其他线程执行场景的其他操作 则返回报错
            throw this.createOperateException(originLockInfo);
        }
    }

    /**
     * unpause
     *
     * @param id
     * @param context
     * @return
     */
    public ServiceResult<Boolean> unpause(String id, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        SceneEventUtils.SceneEventInfo newEventInfo = this.createSceneEventInfo(id, null, null, SceneModel.Event.UNPAUSE);
        SceneEventUtils.SceneEventInfo originLockInfo = SceneEventUtils.tryLockAndGetInfoIfLockFailed(id, newEventInfo);
        if (originLockInfo == null) {
            // 锁获取成功 没有其他线程执行操作
            SceneTaskPool.getInstance()
                    .executeTask(() -> {
                        try {
                            sceneHandler.unpause();
                        } finally {
                            SceneEventUtils.unLock(id);
                        }
                    });
            return new ServiceResult<>(true);
        } else if (originLockInfo.getEvent() == SceneModel.Event.UNPAUSE) {
            // 锁获取失败, 其它线程正在执行相同操作 则直接返回成功
            return new ServiceResult<>(true);
        } else {
            // 其他线程执行场景的其他操作 则返回报错
            throw this.createOperateException(originLockInfo);
        }
    }

    /**
     * suspend
     *
     * @param id
     * @param context
     * @return
     */
    public ServiceResult<Boolean> suspend(String id, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        SceneEventUtils.SceneEventInfo newEventInfo = this.createSceneEventInfo(id, null, null, SceneModel.Event.SUSPEND);
        SceneEventUtils.SceneEventInfo originLockInfo = SceneEventUtils.tryLockAndGetInfoIfLockFailed(id, newEventInfo);
        if (originLockInfo == null) {
            // 锁获取成功 没有其他线程执行操作
            SceneTaskPool.getInstance()
                    .executeTask(() -> {
                        try {
                            sceneHandler.suspend();
                        } finally {
                            SceneEventUtils.unLock(id);
                        }
                    });
            return new ServiceResult<>(true);
        } else if (originLockInfo.getEvent() == SceneModel.Event.SUSPEND) {
            // 锁获取失败, 其它线程正在执行相同操作 则直接返回成功
            return new ServiceResult<>(true);
        } else {
            // 其他线程执行场景的其他操作 则返回报错
            throw this.createOperateException(originLockInfo);
        }
    }

    /**
     * resume
     *
     * @param id
     * @param context
     * @return
     */
    public ServiceResult<Boolean> resume(String id, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        SceneEventUtils.SceneEventInfo newEventInfo = this.createSceneEventInfo(id, null, null, SceneModel.Event.RESUME);
        SceneEventUtils.SceneEventInfo originLockInfo = SceneEventUtils.tryLockAndGetInfoIfLockFailed(id, newEventInfo);
        if (originLockInfo == null) {
            // 锁获取成功 没有其他线程执行操作
            SceneTaskPool.getInstance()
                    .executeTask(() -> {
                        try {
                            sceneHandler.resume();
                        } finally {
                            SceneEventUtils.unLock(id);
                        }
                    });
            return new ServiceResult<>(true);
        } else if (originLockInfo.getEvent() == SceneModel.Event.RESUME) {
            // 锁获取失败, 其它线程正在执行相同操作 则直接返回成功
            return new ServiceResult<>(true);
        } else {
            // 其他线程执行场景的其他操作 则返回报错
            throw this.createOperateException(originLockInfo);
        }
    }

    /**
     * @description: 启动节点
     *
     * @param id
     * @param nodeId
     * @param context
     * @return ServiceResult<Boolean>
     */
    public ServiceResult<Boolean> startElement(String id, String nodeId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);

        SceneEventUtils.SceneEventInfo newEventInfo = this.createSceneEventInfo(id, nodeId, sceneHandler.getElement(nodeId)
                .getName(), SceneElementModel.Event.START);
        SceneEventUtils.SceneEventInfo originLockInfo = SceneEventUtils.tryLockAndGetInfoIfLockFailed(id, newEventInfo);
        if (originLockInfo == null) {
            // 锁获取成功 没有其他线程执行操作
            ElementTaskPool.getInstance()
                    .executeTask(ThreadUtils.format(() -> {
                        try {
                            sceneHandler.startElement(nodeId);
                        } finally {
                            SceneEventUtils.unLock(id);
                        }
                    }));
            return new ServiceResult<>(true);
        } else if (originLockInfo.getEvent() == SceneElementModel.Event.START) {
            // 锁获取失败, 其它线程正在执行相同操作 则直接返回成功
            return new ServiceResult<>(true);
        } else {
            // 其他线程执行场景的其他操作 则返回报错
            throw this.createOperateException(originLockInfo);
        }
    }

    /**
     * @description: 关闭节点
     *
     * @param id
     * @param nodeId
     * @param context
     * @return ServiceResult<Boolean>
     */
    public ServiceResult<Boolean> stopElement(String id, String nodeId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        SceneEventUtils.SceneEventInfo newEventInfo = this.createSceneEventInfo(id, nodeId, sceneHandler.getElement(nodeId)
                .getName(), SceneElementModel.Event.STOP);
        SceneEventUtils.SceneEventInfo originLockInfo = SceneEventUtils.tryLockAndGetInfoIfLockFailed(id, newEventInfo);
        if (originLockInfo == null) {
            // 锁获取成功 没有其他线程执行操作
            ElementTaskPool.getInstance()
                    .executeTask(ThreadUtils.format(() -> {
                        try {
                            sceneHandler.stopElement(nodeId);
                        } finally {
                            SceneEventUtils.unLock(id);
                        }
                    }));
            return new ServiceResult<>(true);
        } else if (originLockInfo.getEvent() == SceneElementModel.Event.STOP) {
            // 锁获取失败, 其它线程正在执行相同操作 则直接返回成功
            return new ServiceResult<>(true);
        } else {
            // 其他线程执行场景的其他操作 则返回报错
            throw this.createOperateException(originLockInfo);
        }
    }

    /**
     * pauseElement
     *
     * @param id
     * @param nodeId
     * @param context
     * @return
     */
    public ServiceResult<Boolean> pauseElement(String id, String nodeId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        SceneEventUtils.SceneEventInfo newEventInfo = this.createSceneEventInfo(id, nodeId, sceneHandler.getElement(nodeId)
                .getName(), SceneElementModel.Event.PAUSE);
        SceneEventUtils.SceneEventInfo originLockInfo = SceneEventUtils.tryLockAndGetInfoIfLockFailed(id, newEventInfo);
        if (originLockInfo == null) {
            // 锁获取成功 没有其他线程执行操作
            ElementTaskPool.getInstance()
                    .executeTask(ThreadUtils.format(() -> {
                        try {
                            sceneHandler.pauseElement(nodeId);
                        } finally {
                            SceneEventUtils.unLock(id);
                        }
                    }));
            return new ServiceResult<>(true);
        } else if (originLockInfo.getEvent() == SceneElementModel.Event.PAUSE) {
            // 锁获取失败, 其它线程正在执行相同操作 则直接返回成功
            return new ServiceResult<>(true);
        } else {
            // 其他线程执行场景的其他操作 则返回报错
            throw this.createOperateException(originLockInfo);
        }
    }

    /**
     * unpauseElement
     *
     * @param id
     * @param nodeId
     * @param context
     * @return
     */
    public ServiceResult<Boolean> unpauseElement(String id, String nodeId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);

        SceneEventUtils.SceneEventInfo newEventInfo = this.createSceneEventInfo(id, nodeId, sceneHandler.getElement(nodeId)
                .getName(), SceneElementModel.Event.UNPAUSE);
        SceneEventUtils.SceneEventInfo originLockInfo = SceneEventUtils.tryLockAndGetInfoIfLockFailed(id, newEventInfo);
        if (originLockInfo == null) {
            // 锁获取成功 没有其他线程执行操作
            ElementTaskPool.getInstance()
                    .executeTask(ThreadUtils.format(() -> {
                        try {
                            sceneHandler.unpauseElement(nodeId);
                        } finally {
                            SceneEventUtils.unLock(id);
                        }
                    }));
            return new ServiceResult<>(true);
        } else if (originLockInfo.getEvent() == SceneElementModel.Event.UNPAUSE) {
            // 锁获取失败, 其它线程正在执行相同操作 则直接返回成功
            return new ServiceResult<>(true);
        } else {
            // 其他线程执行场景的其他操作 则返回报错
            throw this.createOperateException(originLockInfo);
        }
    }

    /**
     * restartElement
     *
     * @param id
     * @param nodeId
     * @param context
     * @return
     */
    public ServiceResult<Boolean> restartElement(String id, String nodeId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);

        SceneEventUtils.SceneEventInfo newEventInfo = this.createSceneEventInfo(id, nodeId, sceneHandler.getElement(nodeId)
                .getName(), SceneElementModel.Event.RESTART);
        SceneEventUtils.SceneEventInfo originLockInfo = SceneEventUtils.tryLockAndGetInfoIfLockFailed(id, newEventInfo);
        if (originLockInfo == null) {
            var restartTask = ThreadUtils.format(() -> {
                try {
                    sceneHandler.restartElement(nodeId);
                } finally {
                    SceneEventUtils.unLock(id);
                }
            });
            // 锁获取成功 没有其他线程执行操作
            ElementTaskPool.getInstance().executeTask(restartTask);
            return new ServiceResult<>(true);
        } else if (originLockInfo.getEvent() == SceneElementModel.Event.RESTART) {
            // 锁获取失败, 其它线程正在执行相同操作 则直接返回成功
            return new ServiceResult<>(true);
        } else {
            // 其他线程执行场景的其他操作 则返回报错
            throw this.createOperateException(originLockInfo);
        }
    }

    /**
     * createElement
     *
     * @param id
     * @param nodeId
     * @param context
     * @return
     */
    public ServiceResult<Boolean> createElement(String id, String nodeId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);

        SceneEventUtils.SceneEventInfo newEventInfo = this.createSceneEventInfo(id, nodeId, sceneHandler.getElement(nodeId)
                .getName(), SceneElementModel.Event.CREATE);
        SceneEventUtils.SceneEventInfo originLockInfo = SceneEventUtils.tryLockAndGetInfoIfLockFailed(id, newEventInfo);
        if (originLockInfo == null) {
            // 锁获取成功 没有其他线程执行操作
            ElementTaskPool.getInstance()
                    .executeTask(ThreadUtils.format(() -> {
                        try {
                            sceneHandler.initiateElement(nodeId);
                            sceneHandler.createElement(nodeId);
                        } finally {
                            SceneEventUtils.unLock(id);
                        }
                    }));
            return new ServiceResult<>(true);
        } else if (originLockInfo.getEvent() == SceneElementModel.Event.CREATE) {
            // 锁获取失败, 其它线程正在执行相同操作 则直接返回成功
            return new ServiceResult<>(true);
        } else {
            // 其他线程执行场景的其他操作 则返回报错
            throw this.createOperateException(originLockInfo);
        }
    }

    /**
     * recreateElement
     *
     * @param id
     * @param nodeId
     * @param image
     * @param context
     * @return
     */
    public ServiceResult<Boolean> recreateElement(String id, String nodeId,String image, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);

        SceneEventUtils.SceneEventInfo newEventInfo = this.createSceneEventInfo(id, nodeId, sceneHandler.getElement(nodeId)
                .getName(), SceneElementModel.Event.RECREATE);
        SceneEventUtils.SceneEventInfo originLockInfo = SceneEventUtils.tryLockAndGetInfoIfLockFailed(id, newEventInfo);
        if (originLockInfo == null) {
            // 锁获取成功 没有其他线程执行操作
            ElementTaskPool.getInstance()
                    .executeTask(ThreadUtils.format(() -> {
                        try {
                            sceneHandler.recreateElement(nodeId, image);
                        } finally {
                            SceneEventUtils.unLock(id);
                        }
                    }));
            return new ServiceResult<>(true);
        } else if (originLockInfo.getEvent() == SceneElementModel.Event.RECREATE) {
            // 锁获取失败, 其它线程正在执行相同操作 则直接返回成功
            return new ServiceResult<>(true);
        } else {
            // 其他线程执行场景的其他操作 则返回报错
            throw this.createOperateException(originLockInfo);
        }
    }

    /**
     * deleteElement
     *
     * @param id
     * @param nodeId
     * @param context
     * @return
     */
    public ServiceResult<Boolean> deleteElement(String id, String nodeId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);

        SceneEventUtils.SceneEventInfo newEventInfo = this.createSceneEventInfo(id, nodeId, sceneHandler.getElement(nodeId)
                .getName(), SceneElementModel.Event.DELETE);
        SceneEventUtils.SceneEventInfo originLockInfo = SceneEventUtils.tryLockAndGetInfoIfLockFailed(id, newEventInfo);
        if (originLockInfo == null) {
            // 锁获取成功 没有其他线程执行操作
            ElementTaskPool.getInstance()
                    .executeTask(ThreadUtils.format(() -> {
                        try {
                            sceneHandler.deleteElement(nodeId);
                        } finally {
                            SceneEventUtils.unLock(id);
                        }
                    }));
            return new ServiceResult<>(true);
        } else if (originLockInfo.getEvent() == SceneElementModel.Event.DELETE) {
            // 锁获取失败, 其它线程正在执行相同操作 则直接返回成功
            return new ServiceResult<>(true);
        } else {
            // 其他线程执行场景的其他操作 则返回报错
            throw this.createOperateException(originLockInfo);
        }
    }

    /**
     * modifyElement
     *
     * @param id
     * @param nodeId
     * @param newDataBO
     * @param context
     * @return
     */
    public ServiceResult<Boolean> modifyElement(String id, String nodeId, SceneDataBO newDataBO, SceneContextBO context) {
        SceneHandler originHandler = new SceneHandler(id, null, false, context);

        SceneEventUtils.SceneEventInfo newEventInfo = this.createSceneEventInfo(id, nodeId, originHandler.getElement(nodeId)
                .getName(), SceneElementModel.Event.MODIFY);
        SceneEventUtils.SceneEventInfo originLockInfo = SceneEventUtils.tryLockAndGetInfoIfLockFailed(id, newEventInfo);
        if (originLockInfo != null) {
            throw this.createOperateException(originLockInfo);
        }

        newDataBO.getTopology().setScene(originHandler.getScene());
        SceneHandler updateHandler = new SceneHandler(newDataBO, context);

        SceneElementBO originElement = originHandler.getElement(nodeId);
        SceneElementBO newElement = updateHandler.getElement(nodeId);
        newElement.setEid(originElement.getEid());
        newElement.setEvent(originElement.getEvent());
        newElement.setEventStatus(originElement.getEventStatus());
        newElement.setProcess(originElement.getProcess());
        newElement.setProcessMessage(originElement.getProcessMessage());
        newElement.setProcessStatus(originElement.getProcessStatus());
        if (CollectionUtils.isNotEmpty(newElement.getPorts())) {
            for (SceneElementPortBO newPort : newElement.getPorts()) {
                SceneElementPortBO originPort = originHandler.getPort(newPort.getId());
                if (originPort != null) {
                    newPort.setEid(originPort.getEid());
                }
            }
        }

        ElementTaskPool.getInstance().executeTask(ThreadUtils.format(() -> {
            updateHandler.modifyElement(nodeId, originHandler);
        }));
        return new ServiceResult<>(true);
    }

    /**
     * suspendElement
     *
     * @param id
     * @param nodeId
     * @param context
     * @return
     */
    public ServiceResult<Boolean> suspendElement(String id, String nodeId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);

        SceneEventUtils.SceneEventInfo newEventInfo = this.createSceneEventInfo(id, nodeId, sceneHandler.getElement(nodeId)
                .getName(), SceneElementModel.Event.SUSPEND);
        SceneEventUtils.SceneEventInfo originLockInfo = SceneEventUtils.tryLockAndGetInfoIfLockFailed(id, newEventInfo);
        if (originLockInfo == null) {
            // 锁获取成功 没有其他线程执行操作
            ElementTaskPool.getInstance()
                    .executeTask(ThreadUtils.format(() -> {
                        try {
                            sceneHandler.suspendElement(nodeId);
                        } finally {
                            SceneEventUtils.unLock(id);
                        }
                    }));
            return new ServiceResult<>(true);
        } else if (originLockInfo.getEvent() == SceneElementModel.Event.SUSPEND) {
            // 锁获取失败, 其它线程正在执行相同操作 则直接返回成功
            return new ServiceResult<>(true);
        } else {
            // 其他线程执行场景的其他操作 则返回报错
            throw this.createOperateException(originLockInfo);
        }
    }

    /**
     * resumeElement
     *
     * @param id
     * @param nodeId
     * @param context
     * @return
     */
    public ServiceResult<Boolean> resumeElement(String id, String nodeId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);

        SceneEventUtils.SceneEventInfo newEventInfo = this.createSceneEventInfo(id, nodeId, sceneHandler.getElement(nodeId)
                .getName(), SceneElementModel.Event.RESUME);
        SceneEventUtils.SceneEventInfo originLockInfo = SceneEventUtils.tryLockAndGetInfoIfLockFailed(id, newEventInfo);
        if (originLockInfo == null) {
            // 锁获取成功 没有其他线程执行操作
            ElementTaskPool.getInstance()
                    .executeTask(ThreadUtils.format(() -> {
                        try {
                            sceneHandler.resumeElement(nodeId);
                        } finally {
                            SceneEventUtils.unLock(id);
                        }
                    }));
            return new ServiceResult<>(true);
        } else if (originLockInfo.getEvent() == SceneElementModel.Event.RESUME) {
            // 锁获取失败, 其它线程正在执行相同操作 则直接返回成功
            return new ServiceResult<>(true);
        } else {
            // 其他线程执行场景的其他操作 则返回报错
            throw this.createOperateException(originLockInfo);
        }
    }

    /**
     * snapshotElementSync
     *
     * @param id
     * @param nodeId
     * @param snapshotId
     * @param snapshotName
     * @param description
     * @param context
     * @return
     */
    public ServiceResult<Boolean> snapshotElementSync(String id, String nodeId, String snapshotId, String snapshotName, String description, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);

        SceneEventUtils.SceneEventInfo newEventInfo = this.createSceneEventInfo(id, nodeId, sceneHandler.getElement(nodeId)
                .getName(), SceneElementModel.Event.SNAPSHOT);
        SceneEventUtils.SceneEventInfo originLockInfo = SceneEventUtils.tryLockAndGetInfoIfLockFailed(id, newEventInfo);
        if (originLockInfo == null) {
            // 锁获取成功 没有其他线程执行操作
            sceneHandler.snapshotElement(nodeId, snapshotId, snapshotName, description);
            return new ServiceResult<>(true);
        } else if (originLockInfo.getEvent() == SceneElementModel.Event.SNAPSHOT) {
            // 锁获取失败, 其它线程正在执行相同操作 则直接返回成功
            return new ServiceResult<>(true);
        } else {
            // 其他线程执行场景的其他操作 则返回报错
            throw this.createOperateException(originLockInfo);
        }
    }

    /**
     * snapshotElementSyncV2
     *
     * @param id
     * @param nodeId
     * @param snapshotId
     * @param snapshotName
     * @param description
     * @param context
     * @return
     */
    public ServiceResult<Boolean> snapshotElementSyncV2(String id, String nodeId, String snapshotId, String snapshotName, String description, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);

        SceneEventUtils.SceneEventInfo newEventInfo = this.createSceneEventInfo(id, nodeId, sceneHandler.getElement(nodeId)
                .getName(), SceneElementModel.Event.SNAPSHOT);
        SceneEventUtils.SceneEventInfo originLockInfo = SceneEventUtils.tryLockAndGetInfoIfLockFailed(id, newEventInfo);
        if (originLockInfo == null) {
            // 锁获取成功 没有其他线程执行操作
            try {
                sceneHandler.snapshotElementV2(nodeId, snapshotId, snapshotName, description);
            } finally {
                SceneEventUtils.unLock(id);
            }
            return new ServiceResult<>(true);
        } else if (originLockInfo.getEvent() == SceneElementModel.Event.SNAPSHOT) {
            // 锁获取失败, 其它线程正在执行相同操作 则直接返回成功
            return new ServiceResult<>(true);
        } else {
            // 其他线程执行场景的其他操作 则返回报错
            throw this.createOperateException(originLockInfo);
        }
    }

    /**
     * deleteElementSnapshot
     *
     * @param id
     * @param nodeId
     * @param snapshotId
     * @param context
     * @return
     */
    public ServiceResult<Boolean> deleteElementSnapshot(String id, String nodeId, String snapshotId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        sceneHandler.deleteElementSnapshot(nodeId, snapshotId);
        return new ServiceResult<>(true);
    }

    /**
     * elementSnapshotAction
     *
     * @param id
     * @param nodeId
     * @param snapshotId
     * @param action
     * @param context
     * @return
     */
    public ServiceResult<Boolean> elementSnapshotAction(String id, String nodeId, String snapshotId, String action, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        sceneHandler.elementSnapshotAction(nodeId, snapshotId, action);
        return new ServiceResult<>(true);
    }

    /**
     * createSceneEventInfo
     *
     * @param id
     * @param nodeId
     * @param nodeName
     * @param event
     * @return
     */
    private SceneEventUtils.SceneEventInfo createSceneEventInfo(String id, String nodeId, String nodeName, int event) {
        SceneEventUtils.SceneEventInfo eventInfo = new SceneEventUtils.SceneEventInfo();
        String action = MessageCommon.get(SceneEventHandler.EVENT_NAME_MAP.get(event));
        eventInfo.setSceneId(id);
        eventInfo.setEventName(action);
        eventInfo.setEvent(event);
        eventInfo.setNodeId(nodeId);
        eventInfo.setNodeName(nodeName);
        return eventInfo;
    }

    /**
     * createOperateException
     *
     * @param originLockInfo
     * @return 监控运行时异常
     */
    private MpRuntimeException createOperateException(SceneEventUtils.SceneEventInfo originLockInfo) {
        if (originLockInfo.getNodeId() == null) {
            return new MpRuntimeException(SceneErrorCode.SCENE_EVENT_OPERATE_CONFLICT.format(originLockInfo.getEventName()));
        } else {
            return new MpRuntimeException(SceneErrorCode.SCENE_NODE_EVENT_OPERATE_CONFLICT.format(originLockInfo.getNodeName(), originLockInfo.getEventName()));
        }
    }
}
