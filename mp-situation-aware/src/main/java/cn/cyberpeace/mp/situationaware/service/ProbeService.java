package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.TextUtils;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import cn.cyberpeace.mp.situationaware.model.ProbeParamModel;
import cn.cyberpeace.mp.situationaware.model.ProbeParamsRequest;
import cn.cyberpeace.mp.situationaware.model.ProbeParamsResult;
import cn.cyberpeace.mp.situationaware.model.ProbeRequest;
import cn.cyberpeace.mp.situationaware.model.ProbeResult;
import cn.cyberpeace.mp.situationaware.model.ProbeSearchRequest;
import cn.cyberpeace.mp.situationaware.model.enums.OsType;
import cn.cyberpeace.mp.situationaware.model.enums.ProbeStatus;
import cn.cyberpeace.mp.situationaware.model.enums.ScriptType;
import cn.cyberpeace.mp.situationaware.repository.ProbeModelMapper;
import cn.cyberpeace.mp.situationaware.repository.ProbeParamModelMapper;
import cn.cyberpeace.mp.situationaware.repository.WazuhProbeManagerRepository;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
  *@Desciption
  *@Author zhangjin
  *@Date 9/10/21 11:12 上午
**/
@Service
@Slf4j
public class ProbeService {

    @Autowired
    private ProbeModelMapper probeModelMapper;

    @Autowired
    private ProbeParamModelMapper probeParamModelMapper;

    @Autowired
    private WazuhProbeManagerRepository wazuhProbeManagerRepository;

    /**
     * 新增探针
     *
     * @param probeRequest
     */
    @Transactional(rollbackFor = Exception.class)
    public void addProbe(ProbeRequest probeRequest) {
        var probeModel = new ProbeModel();
        BeanUtils.copyProperties(probeRequest, probeModel);
        probeModel.setCreateTime(new Date());
        probeModel.setUpdateTime(new Date());
        probeModel.setId(TextUtils.uuid());
        probeModel.setScriptContent(buildScriptContent(probeRequest).getBytes(StandardCharsets.UTF_8));
        probeModel.setOsType(probeRequest.getOsType().getTypeVal());
        probeModel.setScriptType(probeRequest.getScriptType().getTypeVal());
        probeModel.setCommandInterval(probeRequest.getCommandInterval() + "m");
        var probeExist = probeModelMapper.countQueryByProbeName(probeRequest.getProbeName()) > 0;
        if (Boolean.TRUE.equals(probeExist)) {
            throw new MpRuntimeException(SituationalAwareErrorCode.PROBE_NAME_EXIST);
        }
        probeModelMapper.insert(probeModel);
        insertProbeParams(probeRequest, probeModel.getId());
        probeRequest.setProbeId(probeModel.getId());
        wazuhProbeManagerRepository.addScript(probeRequest);
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
        probeParamModelMapper.deleteParamByProbeIds(probeIds);

        probeIds.forEach(probeId -> {
            wazuhProbeManagerRepository.deleteProbeByProbeId(probeId);
        });
        return probeModelMapper.deleteByProbeIds(probeIds) > 0;
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
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
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
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
    }

    @Transactional(rollbackFor = Exception.class)
    public int modifyProbe(ProbeRequest probeRequest) {
        ProbeRequest mergedRequest = buildMergedRequest(probeRequest);
        try {
            cn.cyberpeace.mp.base.util.BeanUtils.merge(mergedRequest, probeRequest);
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
        //是否存在保证name去重
        var probeExist = probeModelMapper.countQueryModifyByProbeName(mergedRequest.getProbeName(), probeModel.getId()) > 0;
        if (Boolean.TRUE.equals(probeExist)) {
            throw new MpRuntimeException(SituationalAwareErrorCode.PROBE_NAME_EXIST);
        }

        var rows = probeModelMapper.updateByProbeId(probeModel);
        //wazuh 远程操作
        wazuhProbeManagerRepository.deleteProbeByProbeId(probeModel.getId());
        wazuhProbeManagerRepository.addScript(mergedRequest);

        return rows;
    }

    /**
     * 修改探针参数
     * @param probeRequest probeRequest
     */
    private void modifyProbeParams(ProbeRequest probeRequest) {
        if (CollectionUtils.isEmpty(probeRequest.getParamsResultList())) {
            return;
        }
        probeParamModelMapper.deleteParamByProbeId(probeRequest.getProbeId());
        insertProbeParams(probeRequest, probeRequest.getProbeId());
    }


    /**
     * 构造patch merge 请求参数
     * @param probeRequest probeRequest
     * @return ProbeRequest
     */
    private ProbeRequest buildMergedRequest(ProbeRequest probeRequest) {
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
        return mergedRequest;
    }

    @Transactional(rollbackFor = Exception.class)
    public int modifyProbeStatus(String probeId, ProbeStatus status) {
        return probeModelMapper.updateProbeStatus(probeId, status);
    }

    private void insertProbeParams(ProbeRequest probeRequest, String probeId) {
        if (CollectionUtils.isEmpty(probeRequest.getParamsResultList())) {
            return;
        }
        List<ProbeParamModel> probeParamModels = probeRequest.getParamsResultList().stream()
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
    }

    /**
     * 替换参数
     *
     * @param probeRequest probeRequest
     * @return 脚本内容
     */
    private String buildScriptContent(ProbeRequest probeRequest) {
        if (CollectionUtils.isEmpty(probeRequest.getParamsResultList())) {
            return probeRequest.getScriptContent();
        }
        var scriptContent = probeRequest.getScriptContent();
        for (ProbeParamsRequest param : probeRequest.getParamsResultList()) {
            scriptContent = scriptContent.replace("$" + param.getParamName() + "$", param.getDefaultVal());
        }
        return scriptContent;
    }
}
