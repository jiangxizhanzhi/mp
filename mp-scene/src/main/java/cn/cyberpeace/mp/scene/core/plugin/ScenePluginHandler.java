package cn.cyberpeace.mp.scene.core.plugin;

import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.common.BasePluginCommonHandler;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import lombok.Data;

import java.util.List;



@Data
public abstract class ScenePluginHandler<S extends BasePluginSceneBO<?, S, SceneBO>,
        E extends BasePluginSceneElementBO<?, E, SceneElementBO>,
        P extends BasePluginSceneElementPortBO<?, P, SceneElementPortBO>,
        C> extends BasePluginCommonHandler<SceneBO, SceneElementBO, SceneElementPortBO, S, E, P, C, SceneHandler> {

    private SceneHandler sceneHandler;

    public void prepareSceneProcess(int event, List<EventProcess> eventProcesses) {
        getSceneHandler().prepareSceneProcess(event, eventProcesses);
    }

    public void prepareElementProcess(String nodeId, int event, List<EventProcess> eventProcesses) {
        getSceneHandler().prepareElementProcess(nodeId, event, eventProcesses);
    }

    public int progressSceneProcess(int event, String process) {
        return getSceneHandler().progressSceneProcess(event, process);
    }

    public int progressElementProcess(String nodeId, int event, String process) {
        return getSceneHandler().progressElementProcess(nodeId, event, process);
    }

    public int overSceneProcess(int event) {
        return getSceneHandler().overSceneProcess(event);
    }

    public int overElementProcess(String nodeId, int event) {
        return getSceneHandler().overElementProcess(nodeId, event);
    }

    public int errorScene(int event, String errorMessage) {
        return getSceneHandler().errorScene(event, errorMessage);
    }

    public int errorElement(String nodeId, int event, String errorMessage) {
        return getSceneHandler().errorElement(nodeId, event, errorMessage);
    }

    public void ackSceneChanged(boolean forceSave) {
        getSceneHandler().ackSceneChanged(forceSave, true);
    }

    public void ackElementChanged(String nodeId, boolean forceSave) {
        getSceneHandler().ackElementChanged(nodeId, forceSave, true);
    }

    public void asyncAckSceneChanged(boolean forceSave) {
        getSceneHandler().asyncAckSceneChanged(forceSave, true);
    }

    public void asyncAckElementChanged(String nodeId, boolean forceSave) {
        getSceneHandler().asyncAckElementChanged(nodeId, forceSave, true);
    }

    public void initiate() throws Exception {
    }

    public void prepare(boolean partialStart) {

    }

    public void create() throws Exception {

    }

    public void start() {

    }

    public void stop() {

    }

    public void restart() {

    }

    public void pause() {

    }

    public void unpause() {

    }

    public void suspend() {

    }

    public void resume() {

    }

    public void snapshot() {

    }

    public void delete() throws Exception {

    }

    
    public void modify(SceneHandler updatedSceneHandler, List<String> updateElementIds) {

    }

    public void reformTopo() {

    }

    public void recreateElement(String nodeId,String image) {

    }

    public void modifyElement(String nodeId, SceneHandler updatedSceneHandler) {

    }

    public void suspendElement(String nodeId) {

    }

    public void resumeElement(String nodeId) {

    }

    public void startElement(String nodeId) {

    }

    public void stopElement(String nodeId){

    }

    public void restartElement(String nodeId) {

    }

    public void initiateElement(String nodeId) {

    }

    public void createElement(String nodeId) {

    }

    public void pauseElement(String nodeId) {
    }

    public void unpauseElement(String nodeId) {
    }

    public void deleteElement(String nodeId) {
    }

    public void snapshotElement(String nodeId, String snapshotId, String snapshotName, String description) {

    }
}
