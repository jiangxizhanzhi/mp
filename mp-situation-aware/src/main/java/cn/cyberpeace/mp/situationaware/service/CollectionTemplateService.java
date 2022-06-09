package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.model.CollectionTemplateModel;
import cn.cyberpeace.mp.situationaware.model.CollectionTemplateProbeModel;
import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import cn.cyberpeace.mp.situationaware.model.ProbeParamModel;
import cn.cyberpeace.mp.situationaware.model.dto.collection.ColTempCommonProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.collection.ColTempCommonProbeRequest;
import cn.cyberpeace.mp.situationaware.model.dto.collection.ColTempCustomProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.collection.ColTempCustomProbeRequest;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplateDto;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplatePageItemDto;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplatePageQueryRequest;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplateProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplateRequest;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplateSearchCondition;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeParamRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeSearchCondition;
import cn.cyberpeace.mp.situationaware.repository.CollectionTemplateMapper;
import cn.cyberpeace.mp.situationaware.repository.CollectionTemplateProbeMapper;
import cn.cyberpeace.mp.situationaware.repository.ProbeMapper;
import cn.cyberpeace.mp.situationaware.repository.ProbeParamMapper;
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

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/24 17:22
 */
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

    @Autowired
    private FastDFSClient fastDFSClient;

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
     *
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

                    customProbeDtos.forEach(p -> {
                        processParamsForColTempCustomProbeDto(p);
                    });

                    // 通用探针
                    List<ColTempCommonProbeDto> basicProbeResps =
                            basicProbeDtos.stream().map(dto -> {
                                        ColTempCommonProbeDto commonDto = new ColTempCommonProbeDto();
                                        BeanUtils.copyProperties(dto, commonDto);
                                        commonDto.setId(dto.getProbeId());
                                        return commonDto;
                                    }
                            ).collect(Collectors.toList());

                    // 事件采集
                    List<ColTempCommonProbeDto> eventProbeResps =
                            eventProbeDtos.stream().map(dto -> {
                                        ColTempCommonProbeDto commonDto = new ColTempCommonProbeDto();
                                        BeanUtils.copyProperties(dto, commonDto);
                                        commonDto.setId(dto.getProbeId());
                                        commonDto.setPaths(processColTempProbePaths(dto.getConfig()));
                                        return commonDto;
                                    }
                            ).collect(Collectors.toList());

                    // 日志采集
                    List<ColTempCommonProbeDto> logProbeResps =
                            logProbeDtos.stream().map(dto -> {
                                        ColTempCommonProbeDto commonDto = new ColTempCommonProbeDto();
                                        BeanUtils.copyProperties(dto, commonDto);
                                        commonDto.setId(dto.getProbeId());
                                        commonDto.setPaths(processColTempProbePaths(dto.getConfig()));
                                        return commonDto;
                                    }
                            ).collect(Collectors.toList());

                    // 自定义探针
                    List<ColTempCustomProbeDto> customProbeResps =
                            customProbeDtos.stream().map(dto -> {
                                        ColTempCustomProbeDto customDto = new ColTempCustomProbeDto();
                                        BeanUtils.copyProperties(dto, customDto);
                                        customDto.setId(dto.getProbeId());
                                        return customDto;
                                    }
                            ).collect(Collectors.toList());


                    templateDto.setBasicInfoProbes(basicProbeResps);
                    templateDto.setEventProbes(eventProbeResps);
                    templateDto.setLogProbes(logProbeResps);
                    templateDto.setCustomProbes(customProbeResps);

                    return templateDto;
                }).collect(Collectors.toList());

        return templateDtoList;
    }


    /**
     * 查询模板下探针内容（加载脚本内容）
     *
     * @param templateId
     * @param probeId
     * @return
     */
    public CollectionTemplateProbeDto getTemplateProbeDtoWithScriptContent(String templateId, String probeId) {
        log.info("templateId:{}, probeId:{}", templateId, probeId);
        ProbeSearchCondition probeSearchCondition = generateCustomProbesCondition();
        probeSearchCondition.setIds(ArrayUtils.toArray(probeId));
        List<CollectionTemplateProbeDto> customProbeDtos = templateProbeMapper.listCollectionTemplateProbeDtos(templateId, probeSearchCondition);
        if (CollectionUtil.isNotEmpty(customProbeDtos)) {
            CollectionTemplateProbeDto colTempProbeDto = customProbeDtos.get(0);
            if (StringUtils.isNotBlank(colTempProbeDto.getScriptUrl())) {
                log.info("Render Script Content...");
                // 加载脚本内容
                byte[] scriptByte = fastDFSClient.download(colTempProbeDto.getScriptUrl(), FdfsGroupEnum.DEFAULT.toString());
                String scriptContent = new String(scriptByte, StandardCharsets.UTF_8);
                colTempProbeDto.setScriptContent(scriptContent);
            }
            // 查询参数
            processParamsForColTempCustomProbeDto(colTempProbeDto);
            return colTempProbeDto;
        }

        return null;
    }


    /**
     * 为采集模板查询可以导入的自定义探针
     * 如果是编辑已有的采集模板，则必须传入templateId，这样才能查出之前保存的模板相关的参数
     *
     * @param catetory
     * @param name
     * @return
     */
    public List<ProbeDto> listCustomProbesForTemplate(String templateId, Integer catetory, String name) {
        log.info("templateId:{}, category:{}, name:{}", templateId, catetory, name);
        ProbeSearchCondition condition = new ProbeSearchCondition();
        condition.setTypes(ArrayUtils.toArray(ProbeModel.TYPE_CUSTOM));
        condition.setProbeCategories(ArrayUtils.toArray(catetory));
        condition.setName(name);
        List<ProbeDto> probeDtos = probeMapper.listProbeDtos(condition);

        // 因为导入的都是新的自定义探针，所以不需要templateId做关联
        probeDtos.forEach(p -> {
                    String paramStr = probeParamService.getProbeParamAsJsonStr(p.getId(), null);
                    p.setParameter(paramStr);
                }
        );
        return probeDtos;
    }


    /**
     * 新增采集模板
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String addTemplate(CollectionTemplateRequest addRequest) {
        log.info("addRequest:{}", addRequest);
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
            int cnt = templateProbeMapper.bulkCreate(allColTempProbeList);
            if (cnt <= 0) {
                log.error("create template-probe count less than 1:{}", cnt);
                rollback = true;
            }
        }

        if (CollectionUtil.isNotEmpty(probeParamList)) {
            int cnt = probeParamMapper.bulkCreate(probeParamList);
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


    /**
     * 更新采集模板
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean modifyTemplate(CollectionTemplateRequest modifyRequest) {
        log.info("modifyRequest:{}", modifyRequest);
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
            // templateId相关关联实体全部删除
            // todo 保留 通用探针>基础信息 类探针关联数据
            templateProbeMapper.deleteByTemplateIds(ArrayUtils.toArray(templateId));
            int cnt = templateProbeMapper.bulkCreate(allColTempProbeList);
            if (cnt <= 0) {
                log.error("create template-probe count less than 1:{}", cnt);
                rollback = true;
            }
        }

        if (CollectionUtil.isNotEmpty(probeParamList)) {
            // templateId相关参数全部删除
            probeParamMapper.deleteByTemplateIds(ArrayUtils.toArray(templateId));
            int cnt = probeParamMapper.bulkCreate(probeParamList);
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


    public CollectionTemplateModel getById(String id) {
        return templateMapper.get(id);
    }

    public CollectionTemplateModel getByName(String name) {
        return templateMapper.getByName(name);
    }


    /**
     * 创建模板检查
     *
     * @param addRequest
     */
    private void addTemplateCheck(CollectionTemplateRequest addRequest) {
        check(addRequest);
        CollectionTemplateModel oldModel = getByName(addRequest.getName());
        if (Objects.nonNull(oldModel)) {
            log.error("template name:{} already existed!", addRequest.getName());
            throw new MpRuntimeException(SituationalAwareErrorCode.COLLECTION_TEMPLATE_EXISTED);
        }
    }


    /**
     * 修改模板检查
     *
     * @param modifyRequest
     */
    private void modifyTemplateCheck(CollectionTemplateRequest modifyRequest) {
        check(modifyRequest);
        String templateId = modifyRequest.getId();
        CollectionTemplateModel templateModel = templateMapper.get(templateId);
        if (Objects.isNull(templateModel)) {
            log.error("collection template not existed! templateId:{}", templateId);
            throw new MpRuntimeException(SituationalAwareErrorCode.COLLECTION_TEMPLATE_NOT_EXISTED);
        }

        CollectionTemplateModel oldModel = getByName(modifyRequest.getName());
        // 名称已被占用，且不是被修改的这个模板
        if (Objects.nonNull(oldModel) && !Objects.equals(oldModel.getId(), templateId)) {
            log.error("template name:{} already existed!", modifyRequest.getName());
            throw new MpRuntimeException(SituationalAwareErrorCode.COLLECTION_TEMPLATE_EXISTED);
        }
    }

    /**
     * 处理采集模板请求实体
     *
     * @param colTempReq
     * @param templateId
     * @param allColTempProbeList
     * @param probeParamList
     */
    private void processColTempRequestForAddTemplate(CollectionTemplateRequest colTempReq,
                                       String templateId,
                                       List<CollectionTemplateProbeModel> allColTempProbeList,
                                       List<ProbeParamModel> probeParamList) {

        List<ColTempCustomProbeRequest> customProbeReqs = commonProcessColTempReq(colTempReq, templateId, allColTempProbeList, probeParamList);

        // 2. 处理脚本
        processCustomProbeScripts("add", customProbeReqs, allColTempProbeList);
    }


    /**
     * 处理采集模板请求实体
     *
     * @param colTempReq
     * @param templateId
     * @param allColTempProbeList
     * @param probeParamList
     */
    private void processColTempRequestForUpdateTemplate(CollectionTemplateRequest colTempReq,
                                       String templateId,
                                       List<CollectionTemplateProbeModel> allColTempProbeList,
                                       List<ProbeParamModel> probeParamList) {

        List<ColTempCustomProbeRequest> customProbeReqs = commonProcessColTempReq(colTempReq, templateId, allColTempProbeList, probeParamList);

        // 2. 处理脚本
        processCustomProbeScripts("update", customProbeReqs, allColTempProbeList);
    }

    private List<ColTempCustomProbeRequest> commonProcessColTempReq(CollectionTemplateRequest colTempReq, String templateId, List<CollectionTemplateProbeModel> allColTempProbeList, List<ProbeParamModel> probeParamList) {
        // todo 直接关联默认的基础信息
        processDefaultColTempProbes(templateId, allColTempProbeList);

        // 事件采集
        List<ColTempCommonProbeRequest> eventProbeReqs = colTempReq.getEventProbes();
        processColTempCommonProbeReqs(templateId, eventProbeReqs, allColTempProbeList);

        // 日志采集
        List<ColTempCommonProbeRequest> logProbeReqs = colTempReq.getLogProbes();
        processColTempCommonProbeReqs(templateId, logProbeReqs, allColTempProbeList);

        // 自定义采集
        List<ColTempCustomProbeRequest> customProbeReqs = colTempReq.getCustomProbes();
        processColTempCustomProbeReqs(templateId, customProbeReqs, allColTempProbeList, probeParamList);
        return customProbeReqs;
    }


    /**
     * 处理采集模板请求体中的通用探针请求
     *
     * @param templateId
     * @param colTempCommonProbeReqs
     * @param allColTempProbeList
     */
    private void processColTempCommonProbeReqs(String templateId, List<ColTempCommonProbeRequest> colTempCommonProbeReqs,
                                               List<CollectionTemplateProbeModel> allColTempProbeList) {

        log.info("templateId:{},\ncolTempCommonProbeReqs:{},\nallColTempProbeList:{}", templateId, colTempCommonProbeReqs, allColTempProbeList);
        if (CollectionUtil.isEmpty(colTempCommonProbeReqs)) {
            return;
        }
        for (ColTempCommonProbeRequest colTempProbeReq : colTempCommonProbeReqs) {
            CollectionTemplateProbeModel colTempProbeModel = CollectionTemplateProbeModel.builder()
                    .probeId(colTempProbeReq.getId())
                    .templateId(templateId)
                    //.intervalSec(colTempProbeReq.getTemplateProbeIntervalSec())
                    .disabled(colTempProbeReq.getDisabled())
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


    /**
     * 处理采集模板请求体中的自定义探针请求
     *
     * @param templateId
     * @param colTempCustomProbeReqs
     * @param allColTempProbeList
     * @param allProbeParamList      需要处理的参数列表，null表示无参数，如通用探针
     */
    private void processColTempCustomProbeReqs(String templateId, List<ColTempCustomProbeRequest> colTempCustomProbeReqs,
                                               List<CollectionTemplateProbeModel> allColTempProbeList,
                                               List<ProbeParamModel> allProbeParamList) {

        log.info("templateId:{},\ncolTempCustomProbeReqs:{},\n allColTempProbeList:{},\nallProbeParamList:{}", templateId, colTempCustomProbeReqs, allColTempProbeList, allProbeParamList);
        if (CollectionUtil.isEmpty(colTempCustomProbeReqs)) {
            return;
        }
        for (ColTempCustomProbeRequest colTempCustomProbeReq : colTempCustomProbeReqs) {
            CollectionTemplateProbeModel colTempProbeModel = CollectionTemplateProbeModel.builder()
                    .probeId(colTempCustomProbeReq.getId())
                    .templateId(templateId)
                    //.intervalSec(colTempProbeReq.getTemplateProbeIntervalSec())
                    .disabled(0)
                    .build();

            allColTempProbeList.add(colTempProbeModel);
        }

        // 以下处理逻辑仅针对自定义探针有效
        // 1. 处理参数
        for (ColTempCustomProbeRequest colTempProbeReq : colTempCustomProbeReqs) {
            List<ProbeParamRequest> paramReqs = probeParamService.paramStr2paramsRequest(colTempProbeReq.getParameter());
            if (CollectionUtil.isNotEmpty(paramReqs)) {
                List<ProbeParamModel> paramList =
                        paramReqs.stream().map(paramReq -> {
                            ProbeParamModel paramModel = ProbeParamModel.builder()
                                    .probeId(colTempProbeReq.getId())
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

    }



    /**
     *
     *
     * @param customProbes        自定义探针
     * @param allColTempProbeList 采集模板-探针关联实体列表
     */
    /**
     * if operate == add  新增采集模板的时候，模板需要另存一份脚本内容
     * if operate == update  编辑采集模板的时候，根据前端是否执行了"导入"动作(reload=1)，来判断是否需要从探针那边抓取一份最新的脚本来保存
     * @param operate
     * @param customProbes
     * @param allColTempProbeList
     */
    private void processCustomProbeScripts(String operate,
                                           List<ColTempCustomProbeRequest> customProbes,
                                           List<CollectionTemplateProbeModel> allColTempProbeList) {
        if (CollectionUtil.isEmpty(customProbes)) {
            return;
        }
        // 处理脚本
        List<String> probeIds = new ArrayList<>();
        if (Objects.equals("add", operate)) {
            // 从探针里面查询scriptUrl，采集模板需要单独保存一份。 防止保存采集模板之后，修改了探针脚本对采集模板产生影响
            probeIds = customProbes.stream().map(ColTempCustomProbeRequest::getId).collect(Collectors.toList());
        } else if (Objects.equals("update", operate)) {
            probeIds = customProbes.stream().filter(p -> Objects.equals(1, p.getReload())).map(ColTempCustomProbeRequest::getId).collect(Collectors.toList());
        }
        List<ProbeDto> probeDtos = probeService.listByIds(probeIds);

        List<String> finalProbeIds = probeIds;
        allColTempProbeList.forEach(tp -> {
            if (finalProbeIds.contains(tp.getProbeId())) {
                probeDtos.forEach(p -> {
                    if (Objects.equals(p.getId(), tp.getProbeId())) {
                        //if (Objects.equals("update", operate)) {
                            log.info("reload script! operate:{}, colTempProbe Script reload, probeId:{}", operate, tp.getProbeId());
                        //}
                        tp.setScriptUrl(p.getScriptUrl());
                    }
                });
            }
        });

    }



    /**
     * 处理通用探针--基础信息采集探针
     *
     * @param templateId
     * @param allColTempProbeList
     */
    private void processDefaultColTempProbes(String templateId, List<CollectionTemplateProbeModel> allColTempProbeList) {
        for (String probeId : BASIC_INFO_PROBE_IDS) {
            CollectionTemplateProbeModel colTempProbeModel = CollectionTemplateProbeModel.builder()
                    .probeId(probeId).disabled(0).templateId(templateId).build();
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

    /**
     * 处理自定义探针参数
     *
     * @param colTempProbeDto
     */
    private void processParamsForColTempCustomProbeDto(CollectionTemplateProbeDto colTempProbeDto) {
        if (StringUtils.isNotBlank(colTempProbeDto.getProbeId())) {
            List<ProbeParamModel> probeParamModels = probeParamMapper.selectByProbeIdAndTemplateId(colTempProbeDto.getProbeId(), colTempProbeDto.getTemplateId());
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

}
