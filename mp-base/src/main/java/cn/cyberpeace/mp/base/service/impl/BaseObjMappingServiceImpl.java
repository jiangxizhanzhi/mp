package cn.cyberpeace.mp.base.service.impl;

import cn.cyberpeace.mp.base.bo.DiffTargets;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.BaseObjMappingModel;
import cn.cyberpeace.mp.base.repository.BaseObjMappingMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.BaseObjMappingService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseObjMappingServiceImpl<M extends BaseObjMappingModel, P extends BaseObjMappingMapper<M>>
        implements BaseObjMappingService<M> {

    @Getter
    @Setter
    private P modelMapper;

    protected boolean judgeMergeObjChanged(M newObjMapping, M existObjMapping) {
        var flag = false;
        if (existObjMapping == null) {
            return true;
        }

        if (!existObjMapping.getSeq().equals(newObjMapping.getSeq())) {
            flag = true;
            existObjMapping.setSeq(newObjMapping.getSeq());
        }
        return flag;
    }

    protected DiffTargets<M> diffObjMappings(List<M> objMappings, List<M> existObjMappings) {
        Map<String, M> existNameMapping = existObjMappings.stream()
                .collect(Collectors.toMap(BaseObjMappingModel::getName, m -> m));

        DiffTargets<M> diff = new DiffTargets<>();
        List<M> addingObjMappings = diff.getAddingTargets();
        List<M> updatingObjMappings = diff.getUpdatingTargets();
        List<M> removingTargets = diff.getRemovingTargets();
        for (var i = 0; i < objMappings.size(); i++) {
            var objMapping = objMappings.get(i);
            objMapping.setSeq(i + 1);
            objMapping.initDefault(BaseObjMappingModel::getName);

            var name = objMapping.getName();
            if (existNameMapping.containsKey(name)) {
                var existMapping = existNameMapping.remove(name);
                if (judgeMergeObjChanged(objMapping, existMapping)) {
                    updatingObjMappings.add(existMapping);
                }
            } else {
                judgeMergeObjChanged(objMapping, null);
                addingObjMappings.add(objMapping);
            }
        }
        removingTargets.addAll(existNameMapping.values());
        return diff;
    }

    protected List<M> getObjMappings(String group) {
        return modelMapper.list(new HumanCondition()
                .filterEqual(BaseObjMappingModel::getGroup, group)
                .orderBy(BaseObjMappingModel::getSeq));
    }

    protected List<M> syncObjMappings(String group, List<M> objMappings) {
        List<M> existMappings = modelMapper.list(new HumanCondition()
                .filterEqual(BaseObjMappingModel::getGroup, group));
        Map<String, M> existNameMapping = existMappings.stream()
                .collect(Collectors.toMap(BaseObjMappingModel::getName, m -> m));

        List<M> resultObjMappings = new ArrayList<>();
        List<M> savingObjMappings = new ArrayList<>();
        List<M> updatingObjMappings = new ArrayList<>();
        for (var i = 0; i < objMappings.size(); i++) {
            var objMapping = objMappings.get(i);
            objMapping.setSeq(i + 1);
            objMapping.initDefault(BaseObjMappingModel::getName);

            var name = objMapping.getName();
            if (existNameMapping.containsKey(name)) {
                var existMapping = existNameMapping.remove(name);
                resultObjMappings.add(existMapping);
                if (judgeMergeObjChanged(objMapping, existMapping)) {
                    updatingObjMappings.add(existMapping);
                }
            } else {
                judgeMergeObjChanged(objMapping, null);
                resultObjMappings.add(objMapping);
                savingObjMappings.add(objMapping);
            }
        }
        List<String> deletingIds = existNameMapping.values().stream()
                .map(BaseModel::getId).collect(Collectors.toList());
        try {
            modelMapper.bulkDelete(deletingIds.toArray(new String[0]));
            modelMapper.bulkCreate(savingObjMappings);
            modelMapper.bulkUpdate(updatingObjMappings);
        } catch (Exception e) {
            log.error("Sync save obj mapping error: ", e);
            throw e;
        }

        return resultObjMappings;
    }
}
