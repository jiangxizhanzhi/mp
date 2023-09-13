package cn.cyberpeace.mp.scene.core.event;

import cn.cyberpeace.mp.scene.constant.SceneStage;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.core.event.base.SceneEventProcess;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class SceneStageRecorder {

    private Map<String, SceneEventProcess> processMap;

    private Map<String, BigDecimal> percentageMap;

    private Map<String, List<SceneEventProcess>> stageProcessMap;

    private SceneBO sceneBO;

    private List<SceneStageRecord> stages;

    private Map<String, SceneStageRecord> stagesMap;

    public SceneStageRecorder(SceneBO sceneBO) {
        this.processMap = new ConcurrentHashMap<>();
        this.percentageMap = new ConcurrentHashMap<>();
        this.stages = new Vector<>();
        this.sceneBO = sceneBO;
        this.stageProcessMap = new ConcurrentHashMap<>();
        this.stagesMap = new ConcurrentHashMap<>();
    }

    public void addStage(SceneStage stage) {
        if (this.stagesMap.containsKey(stage.getStage())) {
            return;
        }
        var stageInfo = new SceneStageRecord(stage);
        this.stages.add(stageInfo);
        this.stagesMap.put(stage.getStage(), stageInfo);
        sceneBO.setStageList(stages);
    }

    public void startStage(SceneStage stage) {
        if (this.stagesMap.containsKey(stage.getStage())) {
            var stageRecord = this.stagesMap.get(stage.getStage());
            stageRecord.setStagePercentage(0);
            this.refreshSceneBO(stageRecord, this.sceneBO);
        }
    }

    public void endStage(SceneStage stage) {
        if (this.stagesMap.containsKey(stage.getStage())) {
            var stageRecord = this.stagesMap.get(stage.getStage());
            stageRecord.setStagePercentage(100);
            this.refreshSceneBO(stageRecord, this.sceneBO);
        }
    }

    public void submitProcess(List<EventProcess> processes) {
        if (CollectionUtils.isEmpty(processes)) {
            return;
        }
        List<SceneEventProcess> temp = new ArrayList<>();
        processes.forEach(p -> {
            if (p instanceof SceneEventProcess) {
                temp.add((SceneEventProcess) p);
            }
        });
        Collections.sort(temp, Comparator.comparingInt(SceneEventProcess::getOrder));
        for (var sep : temp) {
            if (!this.stageProcessMap.containsKey(sep.getStage())) {
                this.stageProcessMap.put(sep.getStage(), new ArrayList<>());
            }
            this.stageProcessMap.get(sep.getStage()).add(sep);
        }

        for (var entry : this.stageProcessMap.entrySet()) {
            var allProcess = entry.getValue();

            BigDecimal step = BigDecimal.valueOf(100).divide(BigDecimal.valueOf(allProcess.size() + 1), 4, RoundingMode.DOWN);
            BigDecimal halfStep = step.divide(BigDecimal.valueOf(2), 4, RoundingMode.DOWN);
            for (int i = 0; i < allProcess.size(); i++) {
                var p = allProcess.get(i);
                var mid = step.multiply(BigDecimal.valueOf(i + 1));
                percentageMap.put(p.getProcess() + "_start", mid.subtract(halfStep));
                percentageMap.put(p.getProcess() + "_end", mid.add(halfStep));
                processMap.put(p.getProcess(), p);
            }
        }
    }

    public void progress(String progress) {
        if (this.processMap.containsKey(progress)) {
            var stage = this.processMap.get(progress).getStage();
            var percentage = this.percentageMap.get(progress + "_start").intValue();

            if (percentage > this.sceneBO.getStagePercentage()) {
                var stageRecord = this.stagesMap.get(stage);
                stageRecord.setStagePercentage(percentage);
                this.refreshSceneBO(stageRecord, this.sceneBO);
            }
        }
    }

    public void overProgress(String progress) {
        if (this.processMap.containsKey(progress)) {
            var stage = this.processMap.get(progress).getStage();
            var percentage = this.percentageMap.get(progress + "_end").intValue();

            if (percentage > this.sceneBO.getStagePercentage()) {
                var stageRecord = this.stagesMap.get(stage);
                stageRecord.setStagePercentage(percentage);
                this.refreshSceneBO(stageRecord, this.sceneBO);
            }
        }
    }

    private void refreshSceneBO(SceneStageRecord record, SceneBO sceneBO) {
        sceneBO.setStage(record.getStage());
        sceneBO.setStageName(record.getStageName());
        sceneBO.setStagePercentage(record.getStagePercentage());
        BigDecimal total = BigDecimal.valueOf(0);
        BigDecimal current = BigDecimal.valueOf(0);
        for (var stage : stages) {
            total = total.add(BigDecimal.valueOf(100));
            current = current.add(BigDecimal.valueOf(stage.stagePercentage));
        }
        sceneBO.setProgressPercentage(current.divide(total, 4, RoundingMode.DOWN).multiply(BigDecimal.valueOf(100)).intValue());
        sceneBO.setStageList(stages);
    }

    @Getter
    @Setter
    public static class SceneStageRecord extends SceneStage {

        private String stageName;

        private volatile int stagePercentage;

        public SceneStageRecord() {
            super(StringUtils.EMPTY, StringUtils.EMPTY);
        }

        public SceneStageRecord(SceneStage stage) {
            super(stage.getStage(), stage.getIcon());
            this.stagePercentage = 0;
            this.stageName = stage.getStageName();
        }

        @Override
        public String getStageName() {
            return this.stageName;
        }
    }
}