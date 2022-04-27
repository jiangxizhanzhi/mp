package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.situationaware.model.CollectionTemplateModel;
import cn.cyberpeace.mp.situationaware.model.CollectionTemplateProbeModel;
import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import cn.cyberpeace.mp.situationaware.model.ProbeParamModel;
import cn.cyberpeace.mp.situationaware.model.dto.probe.CollectionTemplateDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.CollectionTemplatePageItemDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.CollectionTemplatePageQueryRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.CollectionTemplateProbeRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.CollectionTemplateRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.CollectionTemplateSearchCondition;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeDto;
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
import java.util.Objects;
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
    private ProbeService probeService;

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
        PageInfo pageInfo = new PageInfo(templateDtos);

        return pageInfo;
    }


    /**
     * 根据ids查询TemplateDto
     * @param asList
     * @return
     */
    public List<CollectionTemplateDto> listByIds(String[] ids) {
        CollectionTemplateSearchCondition condition = new CollectionTemplateSearchCondition();
        condition.setIds(ids);
        List<CollectionTemplatePageItemDto> templateDtos = templateMapper.listTemplateItemDtos(condition);

        for (CollectionTemplatePageItemDto itemDto : templateDtos) {
            CollectionTemplateDto templateDto = new CollectionTemplateDto();
            BeanUtils.copyProperties(itemDto, templateDto);

            List<ProbeDto> basicProbeDtos = probeService.listByCondition(generateBasicInfoProbesCondition());
            List<ProbeDto> eventProbeDtos = probeService.listByCondition(generateEventProbesCondition());
            List<ProbeDto> logProbeDtos = probeService.listByCondition(generateLogProbesCondition());

            templateDto.setBasicInfoProbes(basicProbeDtos);

        }

    }


    /**
     * 为采集模板查询可以导入的自定义探针
     * @param templateId
     * @param catetory
     * @param name
     * @return
     */
    public List<ProbeModel> listCustomProbesForTemplate(String templateId, Integer catetory, String name) {
        List<ProbeModel> probes = templateProbeMapper.listCustomProbesForTemplate(templateId, catetory, name);
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

        templateMapper.create(colTempModelToCreate);
        if (CollectionUtil.isNotEmpty(allColTempProbeList)) {
            templateProbeMapper.bulkCreate(allColTempProbeList);
        }

        if (CollectionUtil.isNotEmpty(allProbeParamList)) {
            probeParamMapper.bulkCreate(allProbeParamList);
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

        templateMapper.update(colTempModelToUpdate);
        if (CollectionUtil.isNotEmpty(allColTempProbeList)) {
            // templateId相关关联实体全部删除
            templateProbeMapper.deleteByTemplateId(templateId);
            templateProbeMapper.bulkCreate(allColTempProbeList);
        }

        if (CollectionUtil.isNotEmpty(allProbeParamList)) {
            // templateId相关参数全部删除
            probeParamMapper.deleteByTemplateId(templateId);
            probeParamMapper.bulkCreate(allProbeParamList);
        }

        return true;
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
            colTempProbeModel.setIntervalSec(colTempProbeReq.getIntervalSec());
            colTempProbeModel.setDisabled(colTempProbeReq.getDisabled());
            if (Objects.nonNull(colTempProbeReq.getConfig())) {
                colTempProbeModel.setConfig(JSONObject.toJSONString(colTempProbeReq.getConfig()));
            }
            allColTempProbeList.add(colTempProbeModel);

            // 不需要处理参数，如通用探针
            if (CollectionUtil.isEmpty(allProbeParamList)) {
                return;
            }

            if (CollectionUtil.isEmpty(colTempProbeReq.getParamList())) {
                return;
            }

            List<ProbeParamModel> params = colTempProbeReq.getParamList();
            List<ProbeParamModel> paramList =
                    params.stream().map(paramReq -> {
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

}
