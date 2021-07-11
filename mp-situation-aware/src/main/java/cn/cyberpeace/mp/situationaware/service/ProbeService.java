package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.situationaware.constant.SituationBase18nCode;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import cn.cyberpeace.mp.situationaware.model.ProbeParamModel;
import cn.cyberpeace.mp.situationaware.model.ProbeScriptModel;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbePageQueryRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeParamRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeSearchCondition;
import cn.cyberpeace.mp.situationaware.repository.ProbeMapper;
import cn.cyberpeace.mp.situationaware.repository.ProbeParamMapper;
import cn.cyberpeace.mp.situationaware.repository.ProbeScriptMapper;
import cn.cyberpeace.mp.situationaware.utils.MessageCommonUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ProbeService {

    @Autowired
    private ProbeMapper probeMapper;

    @Autowired
    private ProbeParamMapper probeParamMapper;

    @Autowired
    private ProbeScriptMapper probeScriptMapper;

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private ProbeParamService probeParamService;

    
    @Transactional(rollbackFor = Exception.class)
    public String addProbe(ProbeRequest probeRequest) {
        addProbeCheck(probeRequest);
        ProbeModel probeModel = new ProbeModel();
        BeanUtils.copyProperties(probeRequest, probeModel);
        
        probeModel.setType(ProbeModel.TYPE_CUSTOM);

        String osTypes = probeRequest.getOsTypes().stream().filter(Objects::nonNull).map(t -> String.valueOf(t)).collect(Collectors.joining(","));
        probeModel.setOsType(osTypes);
        probeModel.setCreator(UserUtils.getLoginUser() != null ? UserUtils.getLoginUser().getUsername() : "");

        String probeId = TextUtils.uuid();
        probeModel.setId(probeId);
        if (StringUtils.isBlank(probeModel.getOutputFormat())) {
            probeModel.setOutputFormat(null);
        }
        boolean createProbeSuccess = probeMapper.create(probeModel);

        boolean rollback = false;
        if (!createProbeSuccess) {
            log.error("create probe:{} failed!", probeId);
            rollback = true;
        }

        if (rollback) {
            throw new MpRuntimeException("create probe failed!");
        }

        if (StringUtils.isNotBlank(probeRequest.getScriptContent())) {
            ProbeScriptModel scriptModel = ProbeScriptModel.builder()
                    .probeId(probeId)
                    .scriptType(probeRequest.getScriptType())
                    .scriptContent(probeRequest.getScriptContent())
                    .scriptUrl(probeRequest.getScriptUrl())
                    .checksum(probeRequest.getScriptChecksum())
                    .build();
            boolean scriptCreateSuccess = probeScriptMapper.create(scriptModel);

            if (!scriptCreateSuccess) {
                log.error("create script:{} failed!", scriptModel);
                rollback = true;
            }
        }

        List<ProbeParamRequest> params = probeParamService.paramStr2paramsRequest(probeRequest.getParameter());

        if (CollectionUtil.isNotEmpty(params)) {
            List<ProbeParamModel> paramList =
                    params.stream().map(paramReq -> {
                        ProbeParamModel paramModel = ProbeParamModel.builder()
                                .probeId(probeId)
                                .templateId(null)
                                .name(paramReq.getName())
                                .defaultValue(paramReq.getDefaultValue())
                                .description(paramReq.getDescription())
                                .type(paramReq.getType())
                                .build();
                        return paramModel;
                    }).collect(Collectors.toList());

            int createCnt = probeParamMapper.bulkCreate(paramList).getSuccessCount();
            if (createCnt <= 0) {
                log.error("create probeparam cnt less than 1:{}", createCnt);
                rollback = true;
            }
        }

        if (rollback) {
            log.error("finally, add probe failed! rollback:{}", rollback);
            throw new MpRuntimeException("finally, add probe failed!");
        }

        return probeId;
    }


    @Transactional(rollbackFor = Exception.class)
    public void modifyProbe(ProbeRequest probeRequest) {
        modifyProbeCheck(probeRequest);
        String probeId = probeRequest.getId();
        ProbeModel probeModel = probeMapper.get(probeId);

        if (Objects.isNull(probeModel)) {
            log.error("probe not existed! probeId:{}", probeId);
            throw new MpRuntimeException(SituationalAwareErrorCode.PROBE_NOT_EXISTED);
        }

        ProbeModel probeModelToUpdate = new ProbeModel();
        BeanUtils.copyProperties(probeRequest, probeModelToUpdate);
        
        

        if (CollectionUtil.isNotEmpty(probeRequest.getOsTypes())) {
            String osTypes = probeRequest.getOsTypes().stream().filter(Objects::nonNull).map(t -> String.valueOf(t)).collect(Collectors.joining(","));
            probeModelToUpdate.setOsType(osTypes);
        }
        probeModelToUpdate.setId(probeId);
        boolean probeUpdateSuccess = probeMapper.update(probeModelToUpdate);

        boolean rollback = false;
        if (!probeUpdateSuccess) {
            log.error("update probe:{} failed!", probeId);
            rollback = true;
        }

        if (rollback) {
            throw new MpRuntimeException("modify probe failed!");
        }

        if (StringUtils.isNotBlank(probeRequest.getScriptContent())) {
            ProbeScriptModel scriptModel = ProbeScriptModel.builder()
                    .probeId(probeId)
                    .scriptType(probeRequest.getScriptType())
                    .scriptContent(probeRequest.getScriptContent())
                    .scriptUrl(probeRequest.getScriptUrl())
                    .checksum(probeRequest.getScriptChecksum())
                    .build();
            probeScriptMapper.deleteByProbeId(probeId);
            boolean scriptCreateSuccess = probeScriptMapper.create(scriptModel);
            if (!scriptCreateSuccess) {
                log.error("create script:{} failed!", scriptModel);
                rollback = true;
            }
        }


        List<ProbeParamRequest> params = probeParamService.paramStr2paramsRequest(probeRequest.getParameter());
        if (CollectionUtil.isNotEmpty(params)) {
            List<ProbeParamModel> paramList =
                    params.stream().map(paramReq -> {
                        ProbeParamModel paramModel = ProbeParamModel.builder()
                                .probeId(probeId)
                                .templateId(null)
                                .name(paramReq.getName())
                                .defaultValue(paramReq.getDefaultValue())
                                .description(paramReq.getDescription())
                                .type(paramReq.getType())
                                .build();
                        return paramModel;
                    }).collect(Collectors.toList());

            probeParamMapper.deleteByProbeIdAndTemplateId(probeId, null);
            int createCnt = probeParamMapper.bulkCreate(paramList).getSuccessCount();
            if (createCnt <= 0) {
                log.error("create probeparam cnt less than 1:{}", createCnt);
                rollback = true;
            }
        } else {
            probeParamMapper.deleteByProbeIdAndTemplateId(probeId, null);
        }

        if (rollback) {
            log.error("finally, modify probe failed! rollback:{}", rollback);
            throw new MpRuntimeException("finally, modify probe failed!");
        }
    }


    public ProbeDto getById(String id) {
        ProbeDto probeDto = probeMapper.getById(id);
        processProbeDtoOsTypes(probeDto);
        processProbeDtoParams(probeDto);
        return probeDto;
    }


    public boolean delete(String id) {
        return probeMapper.delete(id);
    }

    
    public PageInfo<ProbeDto> page(ProbePageQueryRequest pageQueryRequest) {
        pageQueryRequest.ensure();

        ProbeSearchCondition condition = new ProbeSearchCondition();
        BeanUtils.copyProperties(pageQueryRequest, condition);

        PageHelper.startPage(pageQueryRequest.getPage(), pageQueryRequest.getPageSize());
        List<ProbeDto> probeDtos = probeMapper.listProbeDtos(condition);
        probeDtos.forEach(p -> {
            processProbeDtoOsTypes(p);
            processProbeDtoParams(p);
            if (p.getType() == ProbeModel.TYPE_COMMON) {
                processInternationalMessage(p);
            }
        });
        
        PageInfo pageInfo = new PageInfo(probeDtos);
        return pageInfo;
    }

    
    public List<ProbeDto> listByIds(List<String> ids) {
        ProbeSearchCondition condition = new ProbeSearchCondition();
        condition.setIds(ids.toArray(new String[0]));
        condition.setNeedScriptContent(true);
        List<ProbeDto> probeDtos = probeMapper.listProbeDtos(condition);
        probeDtos.forEach(p ->
            processProbeDtoOsTypes(p)
        );
        probeDtos.forEach(p -> {
            processProbeDtoParams(p);
        });
        return probeDtos;
    }


    public List<ProbeDto> listByCondition(ProbeSearchCondition condition) {
        List<ProbeDto> probeDtos = probeMapper.listProbeDtos(condition);
        probeDtos.forEach(p -> {
            processProbeDtoOsTypes(p);
        });
        probeDtos.forEach(p -> {
            processProbeDtoParams(p);
        });
        return probeDtos;
    }

    
    public int deleteByIds(List<String> ids) {
        List<ProbeModel> probesToUpdate =
            ids.stream().map(id -> {
                ProbeModel probeModel = new ProbeModel();
                probeModel.setId(id);
                probeModel.setIsDeleted(1);
                return probeModel;
            }).collect(Collectors.toList());

        int cnt = probeMapper.bulkUpdate(probesToUpdate).getSuccessCount();
        return cnt;
    }


    
    public void cleanProbeData(List<String> agentNames, LocalDateTime sceneInstanceStartTime) {
        if (Objects.isNull(sceneInstanceStartTime)) {
            log.error("scene instance startTime should not be null!");
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setAgentNames(agentNames);
        wazuhEventRequest.setStartTime(sceneInstanceStartTime);
        wazuhEventRequest.setEndTime(LocalDateTime.now());

        
        Long deletedCount = dataCollectionService.deleteDocByAgentNames(wazuhEventRequest);
        log.info("deletedCount:{}", deletedCount);

        

        

        
    }

    public ProbeModel getByName(String name) {
        return probeMapper.getByName(name);
    }


    private void processProbeDtoOsTypes(ProbeDto probeDto) {
        if (StringUtils.isNotBlank(probeDto.getOsType())) {
            List<Integer> osTypes = new ArrayList<>();
            for (String osType : probeDto.getOsType().split(",")) {
                osTypes.add(Integer.valueOf(osType));
            }
            probeDto.setOsTypes(osTypes);
        }
    }

    private void processProbeDtoParams(ProbeDto probeDto) {
        String paramStr = probeParamService.getProbeParamAsJsonStr(probeDto.getId(), null);
        probeDto.setParameter(paramStr);
    }

    
    private void processInternationalMessage(ProbeDto dto) {
        String name = MessageCommonUtil.convertMessage(SituationBase18nCode.SITUATION_AWARE_I18N_NAME_PREFIX + dto.getId());
        String description = MessageCommonUtil.convertMessage(SituationBase18nCode.SITUATION_AWARE_I18N_DESCRIPTION_PREFIX + dto.getId());
        dto.setName(name);
        dto.setDescription(description);
    }

    private void check(ProbeRequest probeRequest) {
        if (StringUtils.isNotBlank(probeRequest.getName())) {
            int len = probeRequest.getName().length();
            if (len < 1 || len > 40) {
                throw new MpRuntimeException(SituationalAwareErrorCode.PROBE_NAME_LENGTH_INVALID);
            }
        }

        if (StringUtils.isNotBlank(probeRequest.getDescription())) {
            int len = probeRequest.getDescription().length();
            if (len > 500) {
                throw new MpRuntimeException(SituationalAwareErrorCode.PROBE_DESC_LENGTH_INVALID);
            }
        }
    }

    
    private void addProbeCheck(ProbeRequest addRequest) {
        check(addRequest);
        ProbeModel oldModel = getByName(addRequest.getName());
        if (Objects.nonNull(oldModel)) {
            log.error("probe name:{} already existed!", addRequest.getName());
            throw new MpRuntimeException(SituationalAwareErrorCode.PROBE_NAME_EXISTED);
        }
    }


    
    private void modifyProbeCheck(ProbeRequest modifyRequest) {
        check(modifyRequest);
        String probeId = modifyRequest.getId();
        ProbeModel probe = probeMapper.get(probeId);
        if (Objects.isNull(probe)) {
            log.error("probe not existed! id:{}", probeId);
            throw new MpRuntimeException(SituationalAwareErrorCode.PROBE_NOT_EXISTED);
        }

        ProbeModel oldModel = getByName(modifyRequest.getName());
        
        if (Objects.nonNull(oldModel) && !Objects.equals(oldModel.getId(), probeId)) {
            log.error("probe name:{} already existed!", modifyRequest.getName());
            throw new MpRuntimeException(SituationalAwareErrorCode.PROBE_NAME_EXISTED);
        }
    }


    

    



    
    


    
    

    
   

    
    

     

}
