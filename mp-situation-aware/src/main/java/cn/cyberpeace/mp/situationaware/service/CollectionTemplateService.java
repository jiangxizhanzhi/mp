package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.situationaware.constant.SituationBase18nCode;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.model.CollectionTemplateModel;
import cn.cyberpeace.mp.situationaware.model.CollectionTemplateProbeModel;
import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import cn.cyberpeace.mp.situationaware.model.ProbeParamModel;
import cn.cyberpeace.mp.situationaware.model.dto.collection.*;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeSearchCondition;
import cn.cyberpeace.mp.situationaware.repository.CollectionTemplateMapper;
import cn.cyberpeace.mp.situationaware.repository.CollectionTemplateProbeMapper;
import cn.cyberpeace.mp.situationaware.repository.ProbeMapper;
import cn.cyberpeace.mp.situationaware.repository.ProbeParamMapper;
import cn.cyberpeace.mp.situationaware.utils.MessageCommonUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Slf4j
public class CollectionTemplateService {

    public static final String[] BASIC_INFO_PROBE_IDS = {"001", "002", "003", "004", "005"};

    @Autowired
    private CollectionTemplateMapper templateMapper;

    @Autowired
    private CollectionTemplateProbeMapper templateProbeMapper;

    @Autowired
    private ProbeParamMapper probeParamMapper;

    @Autowired
    private ProbeMapper probeMapper;

    @Autowired
    private ProbeParamService probeParamService;

    @Autowired
    private ProbeService probeService;

    
    public PageInfo<CollectionTemplatePageItemDto> page(CollectionTemplatePageQueryRequest pageQueryRequest) {
        pageQueryRequest.ensure();

        CollectionTemplateSearchCondition condition = new CollectionTemplateSearchCondition();
        BeanUtils.copyProperties(pageQueryRequest, condition);

        PageHelper.startPage(pageQueryRequest.getPage(), pageQueryRequest.getPageSize());
        List<CollectionTemplatePageItemDto> templateDtos = templateMapper.listTemplateItemDtos(condition);
        templateDtos.forEach(dto -> {
            dto.setProbeCounts(templateProbeMapper.countByTemplateId(dto.getId(), null));
        });
        PageInfo pageInfo = new PageInfo(templateDtos);

        return pageInfo;
    }


    
    public List<CollectionTemplateDto> listByIds(String[] ids) {
        CollectionTemplateSearchCondition condition = new CollectionTemplateSearchCondition();
        condition.setIds(ids);
        List<CollectionTemplatePageItemDto> templatePageItemDtos = templateMapper.listTemplateItemDtos(condition);

        return templatePageItemDtos.stream().map(itemDto -> {
            CollectionTemplateDto templateDto = new CollectionTemplateDto();
            BeanUtils.copyProperties(itemDto, templateDto);

            List<CollectionTemplateProbeModel> basicProbeModels = templateProbeMapper.listCollectionTemplateProbeModels(itemDto.getId(), generateBasicInfoProbesCondition());
            List<CollectionTemplateProbeModel> eventProbeModels = templateProbeMapper.listCollectionTemplateProbeModels(itemDto.getId(), generateEventProbesCondition());
            List<CollectionTemplateProbeModel> logProbeModels = templateProbeMapper.listCollectionTemplateProbeModels(itemDto.getId(), generateLogProbesCondition());
            List<CollectionTemplateProbeModel> customProbeModels = templateProbeMapper.listCollectionTemplateProbeModels(itemDto.getId(), generateCustomProbesCondition());

           

            
            List<ColTempCommonProbeDto> basicProbeResps =
                    basicProbeModels.stream().map(dto -> {
                                ColTempCommonProbeDto commonDto = new ColTempCommonProbeDto();
                                BeanUtils.copyProperties(dto, commonDto);
                                commonDto.setId(dto.getProbeId());
                                return commonDto;
                            }
                    ).collect(Collectors.toList());

            
            List<ColTempCommonProbeDto> eventProbeResps =
                    eventProbeModels.stream().map(dto -> {
                                ColTempCommonProbeDto commonDto = new ColTempCommonProbeDto();
                                BeanUtils.copyProperties(dto, commonDto);
                                commonDto.setId(dto.getProbeId());
                                commonDto.setPaths(processColTempProbePaths(dto.getConfig()));
                                return commonDto;
                            }
                    ).collect(Collectors.toList());

            
            List<ColTempCommonProbeDto> logProbeResps =
                    logProbeModels.stream().map(dto -> {
                                ColTempCommonProbeDto commonDto = new ColTempCommonProbeDto();
                                BeanUtils.copyProperties(dto, commonDto);
                                commonDto.setId(dto.getProbeId());
                                commonDto.setPaths(processColTempProbePaths(dto.getConfig()));
                                return commonDto;
                            }
                    ).collect(Collectors.toList());

            
            List<CollectionTemplateProbeDto> customProbeResps =
                    customProbeModels.stream().map(p -> {
                        CollectionTemplateProbeDto customDto = new CollectionTemplateProbeDto();
                                BeanUtils.copyProperties(p, customDto);
                                customDto.setId(p.getProbeId());
                                return customDto;
                            }
                    ).collect(Collectors.toList());


            
            basicProbeResps.forEach(probeDto -> processProbeDtoInternational(probeDto));
            eventProbeResps.forEach(probeDto -> processProbeDtoInternational(probeDto));
            logProbeResps.forEach(probeDto -> processProbeDtoInternational(probeDto));

            templateDto.setBasicInfoProbes(basicProbeResps);
            templateDto.setEventProbes(eventProbeResps);
            templateDto.setLogProbes(logProbeResps);
            templateDto.setCustomProbes(customProbeResps);

            return templateDto;
        }).collect(Collectors.toList());
    }

    
    public CollectionTemplateProbeDto getTemplateProbeDtoWithScriptContent(String templateId, String probeId) {
        ProbeSearchCondition probeSearchCondition = generateCustomProbesCondition();
        probeSearchCondition.setIds(ArrayUtils.toArray(probeId));
        List<CollectionTemplateProbeDto> customProbeDtos = templateProbeMapper.listCollectionTemplateProbeDtos(templateId, probeSearchCondition);
        if (CollectionUtil.isNotEmpty(customProbeDtos)) {
            CollectionTemplateProbeDto colTempProbeDto = customProbeDtos.get(0);
            if (StringUtils.isNotBlank(colTempProbeDto.getScriptUrl())) {
                log.info("Render Script Content...");
                
                byte[] scriptByte = FsCommon.downloadRawContent(colTempProbeDto.getScriptUrl());
                String scriptContent = new String(scriptByte, StandardCharsets.UTF_8);
                colTempProbeDto.setScriptContent(scriptContent);
            }
            
            processParamsForColTempCustomProbeDto(colTempProbeDto);
            return colTempProbeDto;
        }

        return null;
    }


    
    public List<ProbeDto> listCustomProbesForTemplate(String templateId, Integer catetory, String name) {
        ProbeSearchCondition condition = new ProbeSearchCondition();
        condition.setTypes(ArrayUtils.toArray(ProbeModel.TYPE_CUSTOM));
        condition.setProbeCategories(ArrayUtils.toArray(catetory));
        condition.setName(name);
        List<ProbeDto> probeDtos = probeMapper.listProbeDtos(condition);

        
        probeDtos.forEach(p -> {
                    String paramStr = probeParamService.getProbeParamAsJsonStr(p.getId(), null);
                    p.setParameter(paramStr);
                }
        );
        return probeDtos;
    }


    
    @Transactional(rollbackFor = Exception.class)
    public String addTemplate(CollectionTemplateRequest addRequest) {
        addTemplateCheck(addRequest);

        CollectionTemplateModel colTempModelToCreate = new CollectionTemplateModel();
        BeanUtils.copyProperties(addRequest, colTempModelToCreate);

        String templateId = TextUtils.uuid();
        colTempModelToCreate.setId(templateId);
        colTempModelToCreate.setCreator(UserUtils.getLoginUser() != null ? UserUtils.getLoginUser().getUsername() : "");

        List<CollectionTemplateProbeModel> allColTempProbeList = new ArrayList<>();
        List<ProbeParamModel> probeParamList = new ArrayList<>();
        processColTempRequestForAddTemplate(addRequest, templateId, allColTempProbeList, probeParamList);

        boolean createSuccess = templateMapper.create(colTempModelToCreate);

        boolean rollback = false;
        if (!createSuccess) {
            log.error("create template:{} failed!", templateId);
            rollback = true;
        }

        if (rollback) {
            throw new MpRuntimeException("create template failed!");
        }

        if (CollectionUtil.isNotEmpty(allColTempProbeList)) {
            int cnt = templateProbeMapper.bulkCreate(allColTempProbeList).getSuccessCount();
            if (cnt <= 0) {
                log.error("create template-probe count less than 1:{}", cnt);
                rollback = true;
            }
        }

        if (CollectionUtil.isNotEmpty(probeParamList)) {
            int cnt = probeParamMapper.bulkCreate(probeParamList).getSuccessCount();
            if (cnt <= 0) {
                log.error("create probe-param count less than 1:{}", cnt);
                rollback = true;
            }
        }

        if (rollback) {
            log.error("finally, add template failed! rollback:{}", rollback);
            throw new MpRuntimeException("finally, add template failed!");
        }

        return templateId;
    }


    
    @Transactional(rollbackFor = Exception.class)
    public boolean modifyTemplate(CollectionTemplateRequest modifyRequest) {
        modifyTemplateCheck(modifyRequest);

        CollectionTemplateModel colTempModelToUpdate = new CollectionTemplateModel();
        BeanUtils.copyProperties(modifyRequest, colTempModelToUpdate);
        colTempModelToUpdate.setCreator(null);

        String templateId = modifyRequest.getId();
        List<CollectionTemplateProbeModel> allColTempProbeList = new ArrayList<>();
        List<ProbeParamModel> probeParamList = new ArrayList<>();
        processColTempRequestForUpdateTemplate(modifyRequest, templateId, allColTempProbeList, probeParamList);

        boolean updateSuccess = templateMapper.update(colTempModelToUpdate);

        boolean rollback = false;
        if (!updateSuccess) {
            log.error("update template:{} failed!", templateId);
            rollback = true;
        }

        if (rollback) {
            throw new MpRuntimeException("update template failed!");
        }

        if (CollectionUtil.isNotEmpty(allColTempProbeList)) {
            
            
            templateProbeMapper.deleteByTemplateIds(ArrayUtils.toArray(templateId));
            int cnt = templateProbeMapper.bulkCreate(allColTempProbeList).getSuccessCount();
            if (cnt <= 0) {
                log.error("create template-probe count less than 1:{}", cnt);
                rollback = true;
            }
        }

        if (CollectionUtil.isNotEmpty(probeParamList)) {
            
            probeParamMapper.deleteByTemplateIds(ArrayUtils.toArray(templateId));
            int cnt = probeParamMapper.bulkCreate(probeParamList).getSuccessCount();
            if (cnt <= 0) {
                log.error("create probe-param count less than 1:{}", cnt);
                rollback = true;
            }
        }

        if (rollback) {
            log.error("finally, update template failed! rollback:{}", rollback);
            throw new MpRuntimeException("finally, update template failed!");
        }

        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(String[] ids) {
        int cnt = templateMapper.bulkDelete(ids).getSuccessCount();

        templateProbeMapper.deleteByTemplateIds(ids);
        probeParamMapper.deleteByTemplateIds(ids);

        return cnt;
    }


    public CollectionTemplateModel getById(String id) {
        return templateMapper.get(id);
    }

    public CollectionTemplateModel getByName(String name) {
        return templateMapper.getByName(name);
    }


    
    private void addTemplateCheck(CollectionTemplateRequest addRequest) {
        check(addRequest);
        CollectionTemplateModel oldModel = getByName(addRequest.getName());
        if (Objects.nonNull(oldModel)) {
            log.error("template name:{} already existed!", addRequest.getName());
            throw new MpRuntimeException(SituationalAwareErrorCode.COLLECTION_TEMPLATE_NAME_EXISTED);
        }
    }


    
    private void modifyTemplateCheck(CollectionTemplateRequest modifyRequest) {
        check(modifyRequest);
        String templateId = modifyRequest.getId();
        CollectionTemplateModel templateModel = templateMapper.get(templateId);
        if (Objects.isNull(templateModel)) {
            log.error("collection template not existed! templateId:{}", templateId);
            throw new MpRuntimeException(SituationalAwareErrorCode.COLLECTION_TEMPLATE_NOT_EXISTED);
        }

        CollectionTemplateModel oldModel = getByName(modifyRequest.getName());
        
        if (Objects.nonNull(oldModel) && !Objects.equals(oldModel.getId(), templateId)) {
            log.error("template name:{} already existed!", modifyRequest.getName());
            throw new MpRuntimeException(SituationalAwareErrorCode.COLLECTION_TEMPLATE_NAME_EXISTED);
        }
    }

    
    private void processColTempRequestForAddTemplate(CollectionTemplateRequest colTempReq,
                                       String templateId,
                                       List<CollectionTemplateProbeModel> allColTempProbeList,
                                       List<ProbeParamModel> probeParamList) {

        List<ColTempCustomProbeRequest> customProbeReqs = commonProcessColTempReq(colTempReq, templateId, allColTempProbeList, probeParamList);

        
        
    }


    
    private void processColTempRequestForUpdateTemplate(CollectionTemplateRequest colTempReq,
                                       String templateId,
                                       List<CollectionTemplateProbeModel> allColTempProbeList,
                                       List<ProbeParamModel> probeParamList) {

        List<ColTempCustomProbeRequest> customProbeReqs = commonProcessColTempReq(colTempReq, templateId, allColTempProbeList, probeParamList);

        
        
    }

    private List<ColTempCustomProbeRequest> commonProcessColTempReq(CollectionTemplateRequest colTempReq, String templateId, List<CollectionTemplateProbeModel> allColTempProbeList, List<ProbeParamModel> probeParamList) {
        
        processDefaultColTempProbes(templateId, allColTempProbeList);

        
        List<ColTempCommonProbeRequest> eventProbeReqs = colTempReq.getEventProbes();
        processColTempCommonProbeReqs(templateId, eventProbeReqs, allColTempProbeList, ProbeModel.CATATORY_EVENT);

        
        List<ColTempCommonProbeRequest> logProbeReqs = colTempReq.getLogProbes();
        processColTempCommonProbeReqs(templateId, logProbeReqs, allColTempProbeList, ProbeModel.CATATORY_LOG);

        
        List<ColTempCustomProbeRequest> customProbeReqs = colTempReq.getCustomProbes();
        processColTempCustomProbeReqs(templateId, customProbeReqs, allColTempProbeList, probeParamList);
        return customProbeReqs;
    }


    
    private void processColTempCommonProbeReqs(String templateId, List<ColTempCommonProbeRequest> colTempCommonProbeReqs,
                                               List<CollectionTemplateProbeModel> allColTempProbeList, int catagory) {

        if (CollectionUtil.isEmpty(colTempCommonProbeReqs)) {
            return;
        }
        for (ColTempCommonProbeRequest colTempProbeReq : colTempCommonProbeReqs) {
            String probeId = colTempProbeReq.getId();
            ProbeModel probe = probeService.getById(probeId);
            CollectionTemplateProbeModel colTempProbeModel = CollectionTemplateProbeModel.builder()
                    .probeId(probeId)
                    .templateId(templateId)
                    
                    .disabled(colTempProbeReq.getDisabled())
                    .type(ProbeModel.TYPE_COMMON)
                    .probeCategory(catagory)
                    .name(probe.getName())
                    .description(probe.getDescription())
                    .build();

            if (Objects.equals(0, colTempProbeReq.getDisabled())) {
                if (CollectionUtil.isNotEmpty(colTempProbeReq.getPaths())) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("paths", colTempProbeReq.getPaths());
                    colTempProbeModel.setConfig(jsonObject.toJSONString());
                }
            }

            allColTempProbeList.add(colTempProbeModel);
        }
    }


    
    private void processColTempCustomProbeReqs(String templateId, List<ColTempCustomProbeRequest> colTempCustomProbeReqs,
                                               List<CollectionTemplateProbeModel> allColTempProbeList,
                                               List<ProbeParamModel> allProbeParamList) {
        if (CollectionUtil.isEmpty(colTempCustomProbeReqs)) {
            return;
        }
        for (ColTempCustomProbeRequest customProbeRequest : colTempCustomProbeReqs) {
            CollectionTemplateProbeModel colTempProbeModel = new CollectionTemplateProbeModel();
            BeanUtils.copyProperties(customProbeRequest, colTempProbeModel);
            colTempProbeModel.setId(null);
            colTempProbeModel.setProbeId(customProbeRequest.getId());
            colTempProbeModel.setTemplateId(templateId);
            colTempProbeModel.setType(ProbeModel.TYPE_CUSTOM);

            
            
            
            
            
            
            
            allColTempProbeList.add(colTempProbeModel);
        }

        
        
        

    }



    
    
    private void processCustomProbeScripts(String operate,
                                           List<ColTempCustomProbeRequest> customProbes,
                                           List<CollectionTemplateProbeModel> allColTempProbeList) {
        if (CollectionUtil.isEmpty(customProbes)) {
            return;
        }
        
        List<String> probeIds = new ArrayList<>();
        if (Objects.equals("add", operate)) {
            
            probeIds = customProbes.stream().map(ColTempCustomProbeRequest::getId).collect(Collectors.toList());
        } else if (Objects.equals("update", operate)) {
            log.info("process custom probe scripts... operate:{}", operate);
            log.info("customProbes:{}", customProbes);
            probeIds = customProbes.stream().filter(p -> Objects.equals(1, p.getReload())).map(ColTempCustomProbeRequest::getId).collect(Collectors.toList());
        }
        List<ProbeDto> probeDtos = probeService.listByIds(probeIds);

        List<String> finalProbeIds = probeIds;
        allColTempProbeList.forEach(tp -> {
            if (finalProbeIds.contains(tp.getProbeId())) {
                probeDtos.forEach(p -> {
                    if (Objects.equals(p.getId(), tp.getProbeId())) {
                        
                            log.info("reload script! operate:{}, colTempProbe Script reload, probeId:{}", operate, tp.getProbeId());
                        
                        tp.setScriptUrl(p.getScriptUrl());
                    }
                });
            }
        });

    }



    
    private void processDefaultColTempProbes(String templateId, List<CollectionTemplateProbeModel> allColTempProbeList) {
        for (String probeId : BASIC_INFO_PROBE_IDS) {
            ProbeModel basicProbe = probeService.getById(probeId);
            CollectionTemplateProbeModel colTempProbeModel = CollectionTemplateProbeModel.builder()
                    .probeId(probeId).disabled(0)
                    .templateId(templateId)
                    .type(ProbeModel.TYPE_COMMON)
                    .probeCategory(ProbeModel.CATATORY_BASIC_INFO)
                    .name(basicProbe.getName())
                    .description(basicProbe.getDescription())
                    .build();

            allColTempProbeList.add(colTempProbeModel);
        }
    }


    private ProbeSearchCondition generateBasicInfoProbesCondition() {
        return ProbeSearchCondition.builder()
                .types(ArrayUtils.toArray(ProbeModel.TYPE_COMMON))
                .probeCategories(ArrayUtils.toArray(ProbeModel.CATATORY_BASIC_INFO))
                .build();
    }

    private ProbeSearchCondition generateEventProbesCondition() {
        return ProbeSearchCondition.builder()
                .types(ArrayUtils.toArray(ProbeModel.TYPE_COMMON))
                .probeCategories(ArrayUtils.toArray(ProbeModel.CATATORY_EVENT))
                .build();
    }

    private ProbeSearchCondition generateLogProbesCondition() {
        return ProbeSearchCondition.builder()
                .types(ArrayUtils.toArray(ProbeModel.TYPE_COMMON))
                .probeCategories(ArrayUtils.toArray(ProbeModel.CATATORY_LOG))
                .build();
    }

    private ProbeSearchCondition generateCustomProbesCondition() {
        return ProbeSearchCondition.builder()
                .types(ArrayUtils.toArray(ProbeModel.TYPE_CUSTOM))
                .build();
    }

    
    private void processParamsForColTempCustomProbeDto(CollectionTemplateProbeDto colTempProbeDto) {
        if (StringUtils.isNotBlank(colTempProbeDto.getId())) {
            List<ProbeParamModel> probeParamModels = probeParamMapper.selectByProbeIdAndTemplateId(colTempProbeDto.getId(), colTempProbeDto.getTemplateId());
            String paramStr = probeParamService.paramModels2ParamStr(probeParamModels);
            colTempProbeDto.setParameter(paramStr);
        }
    }

    private List<String> processColTempProbePaths(String configJsonStr) {
        if (StringUtils.isNotBlank(configJsonStr)) {
            JSONArray jsonArray = JSONObject.parseObject(configJsonStr).getJSONArray("paths");
            if (Objects.nonNull(jsonArray)) {
                List<String> paths = jsonArray.stream().map(Object::toString).collect(Collectors.toList());
                return paths;
            }
        }
        return Collections.emptyList();
    }


    private void check(CollectionTemplateRequest templateRequest) {
        if (StringUtils.isNotBlank(templateRequest.getName())) {
            int len = templateRequest.getName().length();
            if (len < 1 || len > 40) {
                throw new MpRuntimeException(SituationalAwareErrorCode.PROBE_NAME_LENGTH_INVALID);
            }
        }

        if (StringUtils.isNotBlank(templateRequest.getDescription())) {
            int len = templateRequest.getDescription().length();
            if (len > 500) {
                throw new MpRuntimeException(SituationalAwareErrorCode.PROBE_DESC_LENGTH_INVALID);
            }
        }
    }


    
    private void processProbeDtoInternational(ColTempCommonProbeDto dto) {
        String name = MessageCommonUtil.convertMessage(SituationBase18nCode.SITUATION_AWARE_I18N_NAME_PREFIX + dto.getId());
        String description = MessageCommonUtil.convertMessage(SituationBase18nCode.SITUATION_AWARE_I18N_DESCRIPTION_PREFIX + dto.getId());
        dto.setName(name);
        dto.setDescription(description);
    }
}
