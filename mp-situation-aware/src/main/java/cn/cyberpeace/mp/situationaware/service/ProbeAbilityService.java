package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.situationaware.constant.SituationBase18nCode;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeAbilityDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbePageQueryRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeSearchCondition;
import cn.cyberpeace.mp.situationaware.repository.ProbeAbilityMapper;
import cn.cyberpeace.mp.situationaware.utils.MessageCommonUtil;
import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@Slf4j
public class ProbeAbilityService {

    @Autowired
    private ProbeAbilityMapper probeAbilityMapper;

    
    public PageInfo<ProbeAbilityDto> page(ProbePageQueryRequest pageRequest) {
        pageRequest.ensure();
        if (StringUtils.isNotBlank(pageRequest.getAbilityName())) {
            List<String> ids = MessageCommonUtil.getProbeIdsByNameBaseLanguage(pageRequest.getAbilityName());
            if (CollUtil.isEmpty(ids)) {
                return new PageInfo<>(new ArrayList<>());
            }
            pageRequest.setAbilityIds(ids.toArray(new String[0]));
            pageRequest.setAbilityName(null);
        }
        ProbeSearchCondition condition = new ProbeSearchCondition();
        BeanUtils.copyProperties(pageRequest, condition);
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getPageSize());
        List<ProbeAbilityDto> dtos = probeAbilityMapper.listProbeAbilityDtos(condition);
        PageInfo<ProbeAbilityDto> pageInfo = new PageInfo(dtos);
        dtos.forEach(dto -> {
            processProbeDtoOsTypes(dto);
            processAbilityEngine(dto);
            processInternationalMessage(dto);
        });
        pageInfo.setList(dtos);
        return pageInfo;
    }

    private void processInternationalMessage(ProbeAbilityDto dto) {
        String probeName = MessageCommonUtil.convertMessage(SituationBase18nCode.SITUATION_AWARE_I18N_NAME_PREFIX + dto.getProbeId());
        String name = MessageCommonUtil.convertMessage(SituationBase18nCode.SITUATION_AWARE_I18N_PROBE_ABILITY_NAME_PREFIX + dto.getId());
        String description = MessageCommonUtil.convertMessage(SituationBase18nCode.SITUATION_AWARE_I18N_PROBE_ABILITY_DESCRIPTION_PREFIX + dto.getId());
        dto.setProbeName(probeName);
        dto.setName(name);
        dto.setDescription(description);
    }

    private void processAbilityEngine(ProbeAbilityDto dto) {
        String msg;
        if (Objects.equals(dto.getEngine(), "Y")) {
            msg = MessageCommon.get(SituationBase18nCode.ABILITY_ENGINE_Y);
        } else {
            msg = MessageCommon.get(SituationBase18nCode.ABILITY_ENGINE_N);
        }
        dto.setEngine(msg);
    }

    private void processProbeDtoOsTypes(ProbeAbilityDto dto) {
        if (StringUtils.isNotBlank(dto.getOsType())) {
            List<Integer> osTypes = new ArrayList<>();
            for (String osType : dto.getOsType().split(",")) {
                osTypes.add(Integer.valueOf(osType));
            }
            dto.setOsTypes(osTypes);
        }
    }

}
