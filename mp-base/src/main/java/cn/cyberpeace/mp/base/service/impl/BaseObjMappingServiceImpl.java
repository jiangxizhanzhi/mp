package cn.cyberpeace.mp.base.service.impl;

import cn.cyberpeace.mp.base.bo.DiffTargets;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.BaseObjMappingModel;
import cn.cyberpeace.mp.base.repository.BaseObjMappingMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.BaseObjMappingService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseObjMappingServiceImpl<M extends BaseObjMappingModel, P extends BaseObjMappingMapper<M>>
        implements BaseObjMappingService<M> {

    @Getter
    @Setter
    private P modelMapper;

    protected boolean judgeKeepObjMapping(M newObjMapping, M existObjMapping) {
        return existObjMapping.getSeq().equals(newObjMapping.getSeq());
    }

    protected DiffTargets<M> diffObjMappings(String group, List<M> objMappings) {
        List<M> existMappings = modelMapper.list(new HumanCondition()
                .filterEqual(BaseObjMappingModel::getGroup, group));
        Map<String, M> existNameMapping = new HashMap<>();
        for (M m : existMappings) {
            if (existNameMapping.put(m.getName(), m) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }

        Map<String, M> nameMapping = new HashMap<>();
        for (int i = 0; i < objMappings.size(); i++) {
            M objMapping = objMappings.get(i);
            objMapping.setGroup(group);
            objMapping.setSeq(i + 1);
            objMapping.initDefault(BaseObjMappingModel::getName);
            String name = objMapping.getName();
            nameMapping.put(name, objMapping);

            M existObjMapping = existNameMapping.getOrDefault(name, null);
            if (existObjMapping != null) {
                objMapping.setId(existObjMapping.getId());
            }
        }

        return DiffTargets.keyDiff(nameMapping, existNameMapping, this::judgeKeepObjMapping);
    }

    protected void saveDiffObjMappings(DiffTargets<M> diff) {
        try {
            log.info("save diff : {}" + JsonCommon.stringify(diff));
            List<String> list = new ArrayList<>();
            for (M m : diff.getRemovingTargets()) {
                String id = m.getId();
                list.add(id);
            }
            modelMapper.bulkDelete(list.toArray(new String[0]));
            modelMapper.bulkCreate(diff.getAddingTargets());
            modelMapper.bulkUpdate(diff.getUpdatingTargets());
        } catch (Exception e) {
            log.error("Sync save obj mapping error: ", e);
            throw e;
        }
    }

    protected List<M> collectDiffObjMappings(DiffTargets<M> diff) {
        List<M> resultObjMappings = new ArrayList<>();
        resultObjMappings.addAll(diff.getAddingTargets());
        resultObjMappings.addAll(diff.getUpdatingTargets());
        resultObjMappings.addAll(diff.getKeepTargets());
        List<M> list = new ArrayList<>();
        for (M resultObjMapping : resultObjMappings) {
            list.add(resultObjMapping);
        }
        list.sort(Comparator.comparing(BaseObjMappingModel::getSeq));
        resultObjMappings = list;
        return resultObjMappings;
    }

    protected M getObjMapping(String group, String name) {
        return modelMapper.first(new HumanCondition()
                .filterEqual(BaseObjMappingModel::getGroup, group)
                .filterEqual(BaseObjMappingModel::getName, name));
    }

    protected List<M> getObjMappings(String group) {
        return modelMapper.list(new HumanCondition()
                .filterEqual(BaseObjMappingModel::getGroup, group)
                .orderBy(BaseObjMappingModel::getSeq));
    }

    protected List<M> syncObjMappings(String group, List<M> objMappings) {
        DiffTargets<M> diff = diffObjMappings(group, objMappings);
        saveDiffObjMappings(diff);
        return collectDiffObjMappings(diff);
    }

    protected int deleteObjMappings(String group) {
        return modelMapper.filterDelete(new HumanCondition()
                .filterEqual(BaseObjMappingModel::getGroup, group));
    }

    public ServiceResult<M> getMapping(String group, String name) {
        return new ServiceResult<>(getObjMapping(group, name));
    }

    public ServiceResult<List<M>> getMappings(String group) {
        return new ServiceResult<>(getObjMappings(group));
    }

    public ServiceResult<Integer> deleteMappings(String group) {
        return new ServiceResult<>(deleteObjMappings(group));
    }

}
