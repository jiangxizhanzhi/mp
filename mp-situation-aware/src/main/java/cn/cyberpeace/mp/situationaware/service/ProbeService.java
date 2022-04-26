package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.config.FastDfsConfig;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import cn.cyberpeace.mp.situationaware.model.ProbeParamModel;
import cn.cyberpeace.mp.situationaware.model.ProbeScriptModel;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbePageQueryRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeParamsRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeSearchCondition;
import cn.cyberpeace.mp.situationaware.repository.ProbeMapper;
import cn.cyberpeace.mp.situationaware.repository.ProbeParamMapper;
import cn.cyberpeace.mp.situationaware.repository.ProbeScriptMapper;
import cn.cyberpeace.mp.situationaware.utils.FileUtils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
  *@Desciption
  *@Author zhangjin
  *@Date 9/10/21 11:12 上午
**/
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
    private FastDfsConfig fastDfsConfig;

    @Autowired
    private FastDFSClient fastDFSClient;


    @Autowired
    private DataCollectionService dataCollectionService;

    /**
     * 新增探针
     *
     * @param probeRequest
     */
    @Transactional(rollbackFor = Exception.class)
    public void addProbe(ProbeRequest probeRequest) {
        ProbeModel probeModel = new ProbeModel();
        BeanUtils.copyProperties(probeRequest, probeModel);
        //新增探针，默认为自定义探针
        probeModel.setType(ProbeModel.TYPE_CUSTOM);

        String osTypes = probeRequest.getOsTypes().stream().filter(Objects::nonNull).map(t -> String.valueOf(t)).collect(Collectors.joining(","));
        probeModel.setOsType(osTypes);


        String probeId = TextUtils.uuid();
        probeModel.setId(probeId);
        probeMapper.create(probeModel);

        if (StringUtils.isNotBlank(probeRequest.getScriptContent())) {
            ProbeScriptModel scriptModel = ProbeScriptModel.builder()
                    .probeId(probeId)
                    .scriptType(probeRequest.getScriptType())
                    .scriptContent(probeRequest.getScriptContent())
                    .scriptUrl(probeRequest.getScriptUrl()).build();
            probeScriptMapper.create(scriptModel);
        }


        List<ProbeParamsRequest> params = probeRequest.getParams();
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

            probeParamMapper.bulkCreate(paramList);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void modifyProbe(ProbeRequest probeRequest) {
        String probeId = probeRequest.getId();

        ProbeModel probeModel = new ProbeModel();
        BeanUtils.copyProperties(probeRequest, probeModel);
        //新增探针，默认为自定义探针
        probeModel.setType(ProbeModel.TYPE_CUSTOM);

        String osTypes = probeRequest.getOsTypes().stream().filter(Objects::nonNull).map(t -> String.valueOf(t)).collect(Collectors.joining(","));
        probeModel.setOsType(osTypes);

        probeModel.setId(probeId);
        probeMapper.update(probeModel);

        if (StringUtils.isNotBlank(probeRequest.getScriptContent())) {
            ProbeScriptModel scriptModel = ProbeScriptModel.builder()
                    .probeId(probeId)
                    .scriptType(probeRequest.getScriptType())
                    .scriptContent(probeRequest.getScriptContent())
                    .scriptUrl(probeRequest.getScriptUrl()).build();
            probeScriptMapper.deleteByProbeId(probeId);
            probeScriptMapper.create(scriptModel);
        }

        List<ProbeParamsRequest> params = probeRequest.getParams();
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
            probeParamMapper.bulkCreate(paramList);
        } else {
            probeParamMapper.deleteByProbeIdAndTemplateId(probeId, null);
        }

    }


    public ProbeDto getById(String id) {
        log.info("id:{}", id);
        return probeMapper.getById(id);
    }

    public ProbeDto delete(String id) {
        log.info("id:{}", id);
        return probeMapper.getById(id);
    }

    /**
     * 分页查询探针列表
     *
     * @return
     */
    public PageInfo<ProbeDto> page(ProbePageQueryRequest pageQueryRequest) {
        log.info("pageQueryRequest:{}", pageQueryRequest);
        pageQueryRequest.ensure();

        ProbeSearchCondition condition = new ProbeSearchCondition();
        BeanUtils.copyProperties(pageQueryRequest, condition);

        PageHelper.startPage(pageQueryRequest.getPage(), pageQueryRequest.getPageSize());
        List<ProbeDto> probeDtos = probeMapper.listProbes(condition);
        PageInfo pageInfo = new PageInfo(probeDtos);

        return pageInfo;
    }



    /**
     * 根据ids查询列表
     * @param ids
     * @return
     */
    public List<ProbeDto> listByIds(List<String> ids) {
        ProbeSearchCondition condition = new ProbeSearchCondition();
        condition.setIds(ids.toArray(new String[0]));
        condition.setNeedScriptContent(true);
        List<ProbeDto> probeDtos = probeMapper.listProbes(condition);
        return probeDtos;
    }

    /**
     * 根据ids逻辑删除
     * @param ids
     * @return
     */
    public int deleteByIds(List<String> ids) {
        List<ProbeModel> probesToUpdate =
            ids.stream().map(id -> {
                ProbeModel probeModel = new ProbeModel();
                probeModel.setId(id);
                probeModel.setIsDeleted(1);
                return probeModel;
            }).collect(Collectors.toList());

        int cnt = probeMapper.bulkUpdate(probesToUpdate);
        return cnt;
    }


    /**
     * 清除探针数据，包括表，es索引，doc等
     * @param agentNames
     * @param sceneInstanceStartTime
     */
    public void cleanProbeData(List<String> agentNames, LocalDateTime sceneInstanceStartTime) {
        log.info("agentNames:{}, deleteTime:{}", agentNames, sceneInstanceStartTime);
        if (Objects.isNull(sceneInstanceStartTime)) {
            log.error("scene instance startTime should not be null!");
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setAgentNames(agentNames);
        wazuhEventRequest.setStartTime(sceneInstanceStartTime);
        wazuhEventRequest.setEndTime(LocalDateTime.now());

        // 1. 清除es 文档数据
        Long deletedCount = dataCollectionService.deleteDocByAgentNames(wazuhEventRequest);
        log.info("deletedCount:{}", deletedCount);

        // 2. 清除es索引?

        // 3. wazuh api清除agent数据

        // 4. 表数据？
    }



    /**
     * 将脚本内容上传FDFS，返回上传文件url
     * @param scriptContent
     * @return
     */
    public String uploadScriptContentToFDFS(String scriptContent) {
        // 处理探针脚本
        // todo 语法校验
        FastDfsConfig.Group group = fastDfsConfig.getGroupByName(FdfsGroupEnum.DEFAULT);
        String filepath = fastDFSClient.upload(scriptContent, "script", null, FdfsGroupEnum.DEFAULT.toString());
        String httpUrl = group.getFileServerAddr() + File.separator + filepath;
        return httpUrl;
    }


    /**
     * 导入脚本文件，返回脚本内容
     * @return 脚本内容
     */
    public String readScriptFileContent(MultipartFile scriptFile) {
        if (Objects.isNull(scriptFile)) {
            return "";
        }
        File file = null;
        String content = "";
        try {
            file = FileUtils.multipartToFile(scriptFile, scriptFile.getOriginalFilename());
            content = FileUtil.readUtf8String(file);
        } catch (IOException e) {
            log.error("error:{}", ExceptionUtil.stacktraceToString(e));
            throw new MpRuntimeException("parse script file error!");
        }
        file.deleteOnExit();
        return content;
    }


    /*@Transactional(rollbackFor = Exception.class, readOnly = true)
    public ListObjServiceResult<ProbeResult> queryProbeListWithPageable(ProbeSearchRequest probeSearchRequest) {
        int count = probeModelMapper.countQuery(probeSearchRequest);
        List<ProbeModel> probeModelList = probeModelMapper.queryListWithPage(probeSearchRequest, (probeSearchRequest.getPage() - 1) * probeSearchRequest.getPerPage());
        List<ProbeResult> probeResults = probeModelList.stream().map((model -> {
            var probeResult = new ProbeResult();
            BeanUtils.copyProperties(model, probeResult);
            probeResult.setScriptContent(new String(model.getScriptContent(), StandardCharsets.UTF_8));
            probeResult.setProbeStatus(model.getProbeStatus().ordinal());
            List<ProbeParamsResult> probeParamsResults = probeParamModelMapper.queryProbeParamList(model.getId()).stream()
                .map(paramModel -> {
                    var paramsResult = new ProbeParamsResult();
                    BeanUtils.copyProperties(paramModel, paramsResult);
                    return paramsResult;
                }).collect(Collectors.toList());
            probeResult.setParamsResultList(probeParamsResults);
            return probeResult;
        })).collect(Collectors.toList());

        return new ListObjServiceResult<ProbeResult>(new ListObjResult<>(probeResults, count));
    }*/

    /*@Transactional(rollbackFor = Exception.class, readOnly = true)
    public ProbeResult queryProbeDetail(String probeId) {
        var probeResult = new ProbeResult();
        var probeModel = probeModelMapper.queryByProbeId(probeId);
        if (Objects.isNull(probeModel)) {
            return null;
        }
        BeanUtils.copyProperties(probeModel, probeResult);
        List<ProbeParamsResult> probeParamsResults = probeParamModelMapper.queryProbeParamList(probeId).stream()
            .map(paramModel -> {
                ProbeParamsResult paramsResult = new ProbeParamsResult();
                BeanUtils.copyProperties(paramModel, paramsResult);
                return paramsResult;
            }).collect(Collectors.toList());
        probeResult.setParamsResultList(probeParamsResults);
        probeResult.setScriptContent(new String(probeModel.getScriptContent(), StandardCharsets.UTF_8));
        probeResult.setProbeStatus(probeModel.getProbeStatus().ordinal());
        probeResult.setCommandInterval(Integer.parseInt(probeModel.getCommandInterval().replace("m", "")));
        return probeResult;
    }*/



    /**
     * 修改探针参数
     * @param probeRequest probeRequest
     */
    /*private void modifyProbeParams(ProbeRequest probeRequest) {
        probeParamModelMapper.deleteParamByProbeId(probeRequest.getProbeId());
        if (CollectionUtils.isEmpty(probeRequest.getParams())) {
            return;
        }
        insertProbeParams(probeRequest, probeRequest.getProbeId());
    }*/


    /**
     * 构造patch merge 请求参数
     * @param probeRequest probeRequest
     * @return ProbeRequest
     */
    /*private ProbeRequest buildMergedRequest(ProbeRequest probeRequest) {
        var queryModel = probeModelMapper.queryByProbeId(probeRequest.getProbeId());
        var mergedRequest = new ProbeRequest();

        mergedRequest.setProbeId(queryModel.getId());
        mergedRequest.setProbeName(queryModel.getProbeName());
        mergedRequest.setOsType(OsType.values()[queryModel.getOsType()]);
        mergedRequest.setDes(queryModel.getDes());
        mergedRequest.setCommandInterval(Integer.parseInt(queryModel.getCommandInterval().replace("m", "")));
        mergedRequest.setProbeCategory(queryModel.getProbeCategory());
        mergedRequest.setScriptType(ScriptType.values()[queryModel.getScriptType()]);
        mergedRequest.setScriptContent(new String(queryModel.getScriptContent(), StandardCharsets.UTF_8));
        mergedRequest.setOutputFormat(queryModel.getOutputFormat());
        return mergedRequest;
    }

    @Transactional(rollbackFor = Exception.class)
    public int modifyProbeStatus(String probeId, ProbeStatus status) {
        return probeModelMapper.updateProbeStatus(probeId, status);
    }

    private void insertProbeParams(ProbeRequest probeRequest, String probeId) {
        if (CollectionUtils.isEmpty(probeRequest.getParams())) {
            return;
        }
        List<ProbeParamModel> probeParamModels = probeRequest.getParams().stream()
            .map((probeParamRequest -> {
                var probeParamModel = new ProbeParamModel();
                BeanUtils.copyProperties(probeParamRequest, probeParamModel);
                probeParamModel.setProbeId(probeId);
                probeParamModel.setCreateTime(new Date());
                probeParamModel.setUpdateTime(new Date());
                probeParamModel.setId(TextUtils.uuid());
                return probeParamModel;
            })).collect(Collectors.toList());
        probeParamModelMapper.batchInsert(probeParamModels);
    }*/

    /**
     * 替换参数
     *
     * @param probeRequest probeRequest
     * @return 脚本内容
     */
   /* private String buildScriptContent(ProbeRequest probeRequest) {
        if (CollectionUtils.isEmpty(probeRequest.getParams())) {
            return probeRequest.getScriptContent();
        }
        var scriptContent = probeRequest.getScriptContent();
        for (ProbeParamsRequest param : probeRequest.getParams()) {
            scriptContent = scriptContent.replace("$" + param.getParamName() + "$", param.getDefaultVal());
        }
        return scriptContent;
    }*/

    /**
     * 根据probeId删除探针
     *
     * @param probeIds probeIds
     * @return 是否删除成功
     */
    /*
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProbeById(List<String> probeIds) {
        if (CollectionUtils.isEmpty(probeIds) || probeIds.stream().anyMatch(ObjectUtils::isEmpty)) {
            throw new MpRuntimeException(SituationalAwareErrorCode.PROBE_MANAGER_DELETE_PARAM_ERROR);
        }
        String[] probeIdArray = probeIds.toArray(new String[0]);
        probeParamMapper.bulkDelete(probeIdArray);

        return false;

//        return probeModelMapper.deleteByProbeIds(probeIds) > 0;

    }
    */

     /*  @Transactional(rollbackFor = Exception.class)
        public int modifyProbe(ProbeRequest probeRequest) {
        ProbeRequest mergedRequest = buildMergedRequest(probeRequest);
        try {
            cn.cyberpeace.mp.base.util.basic.BeanUtils.copyPropertiesIgnoreNull(probeRequest, mergedRequest);
        } catch (Exception e) {
            log.error("patch probe error probeId : {}", mergedRequest.getProbeId());
            throw new MpRuntimeException(SituationalAwareErrorCode.PROBE_MODIFY_FAIL);
        }

        //修改探针参数
        modifyProbeParams(probeRequest);
        //修改探针参数
        var probeModel = new ProbeModel();
        BeanUtils.copyProperties(mergedRequest, probeModel);
        probeModel.setUpdateTime(new Date());
        probeModel.setId(mergedRequest.getProbeId());
        probeModel.setScriptContent(buildScriptContent(mergedRequest).getBytes(StandardCharsets.UTF_8));
        probeModel.setOsType(mergedRequest.getOsType().getTypeVal());
        probeModel.setScriptType(mergedRequest.getScriptType().getTypeVal());
        probeModel.setCommandInterval(mergedRequest.getCommandInterval() + "m");
        probeModel.setOutputFormat(mergedRequest.getOutputFormat());
        //是否存在保证name去重
        var probeExist = probeModelMapper.countQueryModifyByProbeName(mergedRequest.getProbeName(), probeModel.getId()) > 0;
        if (Boolean.TRUE.equals(probeExist)) {
            throw new MpRuntimeException(SituationalAwareErrorCode.PROBE_NAME_EXIST);
        }

        return probeModelMapper.updateByProbeId(probeModel);
    }*/

}
