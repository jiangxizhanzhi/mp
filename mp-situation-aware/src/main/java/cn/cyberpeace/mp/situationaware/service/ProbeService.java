package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.dto.ProbePageQueryRequest;
import cn.cyberpeace.mp.situationaware.model.dto.ProbeRequest;
import cn.cyberpeace.mp.situationaware.model.dto.ProbeResult;
import cn.cyberpeace.mp.situationaware.repository.ProbeMapper;
import cn.cyberpeace.mp.situationaware.repository.ProbeParamMapper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private DataCollectionService dataCollectionService;

    /**
     * 新增探针
     *
     * @param probeRequest
     */
    @Transactional(rollbackFor = Exception.class)
    public void addProbe(ProbeRequest probeRequest) {
        var probeModel = new ProbeModel();
        BeanUtils.copyProperties(probeRequest, probeModel);
        //新增探针，默认为自定义探针
        probeModel.setType(ProbeModel.TYPE_CUSTOM);
        probeModel.setDescription(probeRequest.getDescription());
        probeModel.setOsType(probeRequest.getOsType());
//        probeModel.setScriptType(probeRequest.getScriptType().getTypeVal());
//        probeModel.setCommandInterval(probeRequest.getCommandInterval() + "m");
        probeModel.setOutputFormat(probeRequest.getOutputFormat());
//        var probeExist = probeModelMapper.countQueryByProbeName(probeRequest.getProbeName()) > 0;
//        if (Boolean.TRUE.equals(probeExist)) {
//            throw new MpRuntimeException(SituationalAwareErrorCode.PROBE_NAME_EXIST);
//        }
        // todo params
        probeMapper.insert(probeModel);
//        insertProbeParams(probeRequest, probeModel.getId());
//        probeRequest.setProbeId(probeModel.getId());
    }

    /**
     * 根据probeId删除探针
     *
     * @param probeIds probeIds
     * @return 是否删除成功
     */
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


    /**
     * 导入脚本文件，返回脚本内容
     * @return 脚本内容
     */
    public String importScriptFileAndGetContent(MultipartFile scriptFile) {

        return "";
    }


    /**
     * 分页查询探针列表
     *
     * @return
     */
    public PageInfo<ProbeModel> pageProbes(ProbePageQueryRequest pageQueryRequest) {
        pageQueryRequest.ensure();

        // todo 通用探针

        return null;
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

    public List<ProbeResult> queryProbeByIds(List<String> ids) {
        List<ProbeModel> probeModels = probeMapper.listByIds(ids);
        if (Objects.isNull(probeModels)) {
            return null;
        }
        Map<String, ProbeModel> probeId2Probe = new HashMap<>();
        probeModels.forEach(probeModel -> {
            probeId2Probe.put(probeModel.getId(), probeModel);
        });
        List<ProbeResult> results = probeModels.stream().map(m -> {
            ProbeResult result = new ProbeResult();
            BeanUtils.copyProperties(m, result);
            return result;
        }).collect(Collectors.toList());
//        results.forEach(result -> {
//            result.setScriptContent(new String(probeId2Probe.get(result.getId()).getScriptContent(), StandardCharsets.UTF_8));
//        });
        return results;
    }

    @Transactional(rollbackFor = Exception.class)
   /* public int modifyProbe(ProbeRequest probeRequest) {
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
}
