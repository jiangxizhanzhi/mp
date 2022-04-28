package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.situationaware.model.CollectionTemplateModel;
import cn.cyberpeace.mp.situationaware.model.CollectionTemplateProbeModel;
import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import cn.cyberpeace.mp.situationaware.model.ProbeParamModel;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplateDto;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplatePageItemDto;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplatePageQueryRequest;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplateProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplateProbeRequest;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplateRequest;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplateSearchCondition;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeParamRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeSearchCondition;
import cn.cyberpeace.mp.situationaware.repository.CollectionTemplateMapper;
import cn.cyberpeace.mp.situationaware.repository.CollectionTemplateProbeMapper;
import cn.cyberpeace.mp.situationaware.repository.ProbeParamMapper;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/24 17:22
 */
@Service
@Slf4j
public class CollectionTemplateService {

    @Autowired
    private CollectionTemplateMapper templateMapper;

    @Autowired
    private CollectionTemplateProbeMapper templateProbeMapper;


    @Autowired
    private ProbeParamMapper probeParamMapper;

    /**
     * 分页查询探针列表
     *
     * @return
     */
    public PageInfo<CollectionTemplatePageItemDto> page(CollectionTemplatePageQueryRequest pageQueryRequest) {
        log.info("pageQueryRequest:{}", pageQueryRequest);
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


    /**
     * 根据ids查询TemplateDto
     * @return
     */
    public List<CollectionTemplateDto> listByIds(String[] ids) {
        log.info("ids:{}", ids);
        CollectionTemplateSearchCondition condition = new CollectionTemplateSearchCondition();
        condition.setIds(ids);
        List<CollectionTemplatePageItemDto> templatePageItemDtos = templateMapper.listTemplateItemDtos(condition);

        List<CollectionTemplateDto> templateDtoList =
            templatePageItemDtos.stream().map(itemDto -> {
                CollectionTemplateDto templateDto = new CollectionTemplateDto();
                BeanUtils.copyProperties(itemDto, templateDto);

                List<CollectionTemplateProbeDto> basicProbeDtos = templateProbeMapper.listCollectionTemplateProbeDtos(itemDto.getId(), generateBasicInfoProbesCondition());
                List<CollectionTemplateProbeDto> eventProbeDtos = templateProbeMapper.listCollectionTemplateProbeDtos(itemDto.getId(), generateEventProbesCondition());
                List<CollectionTemplateProbeDto> logProbeDtos = templateProbeMapper.listCollectionTemplateProbeDtos(itemDto.getId(), generateLogProbesCondition());
                List<CollectionTemplateProbeDto> customProbeDtos = templateProbeMapper.listCollectionTemplateProbeDtos(itemDto.getId(), generateCustomProbesCondition());

                templateDto.setBasicInfoProbes(basicProbeDtos);
                templateDto.setEventProbes(eventProbeDtos);
                templateDto.setLogProbes(logProbeDtos);
                templateDto.setCustomProbes(customProbeDtos);

                return templateDto;
            }).collect(Collectors.toList());

        return templateDtoList;
    }


    /**
     * 为采集模板查询可以导入的自定义探针
     *
     * @param templateId
     * @param catetory
     * @param name
     * @return
     */
    public List<ProbeModel> listCustomProbesNotInTemplate(String templateId, Integer catetory, String name) {
        log.info("templateId:{}, category:{}, name:{}", templateId, catetory, name);
        List<ProbeModel> probes = templateProbeMapper.listCustomProbesNotInTemplate(templateId, catetory, name);
        return probes;
    }




    /**
     * 新增采集模板
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addTemplate(CollectionTemplateRequest addRequest) {
        log.info("addRequest:{}", addRequest);
        CollectionTemplateModel colTempModelToCreate = new CollectionTemplateModel();
        BeanUtils.copyProperties(addRequest, colTempModelToCreate);

        String templateId = TextUtils.uuid();
        colTempModelToCreate.setId(templateId);
        colTempModelToCreate.setCreator(UserUtils.getLoginUser() != null ? UserUtils.getLoginUser().getUsername() : "");

        List<CollectionTemplateProbeModel> allColTempProbeList = new ArrayList<>();
        List<ProbeParamModel> allProbeParamList = new ArrayList<>();
        // 基础信息采集
        List<CollectionTemplateProbeRequest> basicProbes = addRequest.getBasicInfoProbes();
        processColTempProbeDto(templateId, basicProbes, allColTempProbeList, null);

        // 事件采集
        List<CollectionTemplateProbeRequest> eventProbes = addRequest.getEventProbes();
        processColTempProbeDto(templateId, eventProbes, allColTempProbeList, null);

        // 日志采集
        List<CollectionTemplateProbeRequest> logProbes = addRequest.getLogProbes();
        processColTempProbeDto(templateId, logProbes, allColTempProbeList, null);

        // 自定义采集
        List<CollectionTemplateProbeRequest> customProbes = addRequest.getCustomProbes();
        processColTempProbeDto(templateId, customProbes, allColTempProbeList, allProbeParamList);

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
            int cnt = templateProbeMapper.bulkCreate(allColTempProbeList);
            if (cnt <= 0) {
                log.error("create template-probe count less than 1:{}", cnt);
                rollback = true;
            }
        }

        if (CollectionUtil.isNotEmpty(allProbeParamList)) {
            int cnt = probeParamMapper.bulkCreate(allProbeParamList);
            if (cnt <= 0) {
                log.error("create probe-param count less than 1:{}", cnt);
                rollback = true;
            }
        }

        if (rollback) {
            log.error("finally, add template failed! rollback:{}", rollback);
            throw new MpRuntimeException("finally, add template failed!");
        }

        return true;
    }


    /**
     * 更新采集模板
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean modifyTemplate(CollectionTemplateRequest modifyRequest) {
        log.info("modifyRequest:{}", modifyRequest);
        CollectionTemplateModel colTempModelToUpdate = new CollectionTemplateModel();
        BeanUtils.copyProperties(modifyRequest, colTempModelToUpdate);

        String templateId = modifyRequest.getId();

        List<CollectionTemplateProbeModel> allColTempProbeList = new ArrayList<>();
        List<ProbeParamModel> allProbeParamList = new ArrayList<>();
        // 基础信息采集
        List<CollectionTemplateProbeRequest> basicProbes = modifyRequest.getBasicInfoProbes();
        processColTempProbeDto(templateId, basicProbes, allColTempProbeList, null);

        // 事件采集
        List<CollectionTemplateProbeRequest> eventProbes = modifyRequest.getEventProbes();
        processColTempProbeDto(templateId, eventProbes, allColTempProbeList, null);

        // 日志采集
        List<CollectionTemplateProbeRequest> logProbes = modifyRequest.getLogProbes();
        processColTempProbeDto(templateId, logProbes, allColTempProbeList, null);

        // 自定义采集
        List<CollectionTemplateProbeRequest> customProbes = modifyRequest.getCustomProbes();
        processColTempProbeDto(templateId, customProbes, allColTempProbeList, allProbeParamList);

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
            // templateId相关关联实体全部删除
            templateProbeMapper.deleteByTemplateIds(ArrayUtils.toArray(templateId));
            int cnt = templateProbeMapper.bulkCreate(allColTempProbeList);
            if (cnt <= 0) {
                log.error("create template-probe count less than 1:{}", cnt);
                rollback = true;
            }
        }

        if (CollectionUtil.isNotEmpty(allProbeParamList)) {
            // templateId相关参数全部删除
            probeParamMapper.deleteByTemplateIds(ArrayUtils.toArray(templateId));
            int cnt = probeParamMapper.bulkCreate(allProbeParamList);
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
        log.info("ids:{}", ids);
        int cnt = templateMapper.bulkDelete(ids);

        templateProbeMapper.deleteByTemplateIds(ids);
        probeParamMapper.deleteByTemplateIds(ids);

        return cnt;
    }

    /**
     * 处理采集模板中的通用探针，自定义探针，探针配置，探针参数等
     *
     * @param templateId
     * @param colTelProbeDtos
     * @param allColTempProbeList
     * @param allProbeParamList   需要处理的参数列表，null表示无参数，如通用探针
     */
    private void processColTempProbeDto(String templateId, List<CollectionTemplateProbeRequest> colTelProbeDtos, List<CollectionTemplateProbeModel> allColTempProbeList, List<ProbeParamModel> allProbeParamList) {
        for (CollectionTemplateProbeRequest colTempProbeReq : colTelProbeDtos) {
            CollectionTemplateProbeModel colTempProbeModel = new CollectionTemplateProbeModel();
            colTempProbeModel.setProbeId(colTempProbeReq.getProbeId());
            colTempProbeModel.setTemplateId(templateId);
            colTempProbeModel.setIntervalSec(colTempProbeReq.getTemplateProbeIntervalSec());
            colTempProbeModel.setDisabled(colTempProbeReq.getDisabled());
            if (CollectionUtil.isNotEmpty(colTempProbeReq.getPaths())) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("paths", colTempProbeReq.getPaths());
                colTempProbeModel.setConfig(jsonObject.toJSONString());
            }
            allColTempProbeList.add(colTempProbeModel);

            // 不需要处理参数，如通用探针
            if (CollectionUtil.isEmpty(allProbeParamList)) {
                return;
            }

            if (CollectionUtil.isEmpty(colTempProbeReq.getParamList())) {
                return;
            }

            List<ProbeParamRequest> paramReqs = colTempProbeReq.getParamList();
            List<ProbeParamModel> paramList =
                    paramReqs.stream().map(paramReq -> {
                        ProbeParamModel paramModel = ProbeParamModel.builder()
                                .probeId(colTempProbeReq.getProbeId())
                                .templateId(templateId)
                                .name(paramReq.getName())
                                .defaultValue(paramReq.getDefaultValue())
                                .description(paramReq.getDescription())
                                .type(paramReq.getType())
                                .build();
                        return paramModel;
                    }).collect(Collectors.toList());

            allProbeParamList.addAll(paramList);
        }
    }


    public CollectionTemplateModel getById(String id) {
        return null;
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

}
