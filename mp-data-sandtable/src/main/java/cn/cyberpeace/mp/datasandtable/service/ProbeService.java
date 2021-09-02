package cn.cyberpeace.mp.datasandtable.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.TextUtils;
import cn.cyberpeace.mp.datasandtable.model.ProbeModel;
import cn.cyberpeace.mp.datasandtable.model.ProbeParamModel;
import cn.cyberpeace.mp.datasandtable.model.ProbeParamsRequest;
import cn.cyberpeace.mp.datasandtable.model.ProbeParamsResult;
import cn.cyberpeace.mp.datasandtable.model.ProbeRequest;
import cn.cyberpeace.mp.datasandtable.model.ProbeResult;
import cn.cyberpeace.mp.datasandtable.model.ProbeSearchRequest;
import cn.cyberpeace.mp.datasandtable.repository.ProbeModelMapper;
import cn.cyberpeace.mp.datasandtable.repository.ProbeParamModelMapper;
import cn.cyberpeace.mp.datasandtable.repository.WazuhProbeManagerRepository;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
  *@Desciption 探针管理服务
  *@Author zhangjin
  *@Date 8/31/21 11:51 上午
**/
@Service
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
        probeModelMapper.insert(probeModel);
        insertProbeParams(probeRequest, probeModel.getId());
        probeRequest.setProbeId(probeModel.getId());
        wazuhProbeManagerRepository.addScript(probeRequest);
    }

    /**
     * 根据probeId删除探针
     *
     * @param probeId probeId
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProbeById(String probeId) {
        probeParamModelMapper.deleteParamByProbeId(probeId);
        wazuhProbeManagerRepository.deleteProbeByProbeId(probeId);
        return probeModelMapper.deleteByProbeId(probeId) > 0;
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public ListObjServiceResult<ProbeResult> queryProbeListWithPageable(ProbeSearchRequest probeSearchRequest) {
        int count = probeModelMapper.countQuery(probeSearchRequest);
        List<ProbeModel> probeModelList = probeModelMapper.queryListWithPage(probeSearchRequest, (probeSearchRequest.getPageNum() - 1) * probeSearchRequest.getPageSize());
        List<ProbeResult> probeResults = probeModelList.stream().map((model -> {
            var probeResult = new ProbeResult();
            BeanUtils.copyProperties(model, probeResult);
            probeResult.setScriptContent(new String(model.getScriptContent(), StandardCharsets.UTF_8));
            return probeResult;
        })).collect(Collectors.toList());

        return new ListObjServiceResult(probeResults, count);
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
        return probeResult;
    }

    @Transactional(rollbackFor = Exception.class)
    public int modifyProbe(ProbeRequest probeRequest) {
        //修改参数
        probeParamModelMapper.deleteParamByProbeId(probeRequest.getProbeId());
        insertProbeParams(probeRequest, probeRequest.getProbeId());
        //修改探针参数
        var probeModel = new ProbeModel();
        BeanUtils.copyProperties(probeRequest, probeModel);
        probeModel.setUpdateTime(new Date());
        probeModel.setId(probeRequest.getProbeId());
        probeModel.setScriptContent(buildScriptContent(probeRequest).getBytes(StandardCharsets.UTF_8));
        probeModel.setOsType(probeRequest.getOsType().getTypeVal());
        probeModel.setScriptType(probeRequest.getScriptType().getTypeVal());
        var rows = probeModelMapper.updateByProbeId(probeModel);
        //wazuh 远程操作
        wazuhProbeManagerRepository.deleteProbeByProbeId(probeModel.getId());
        wazuhProbeManagerRepository.addScript(probeRequest);

        return rows;
    }

    private void insertProbeParams(ProbeRequest probeRequest, String probeId) {
        List<ProbeParamModel> probeParamModels = probeRequest.getProbeParamsRequestList().stream()
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
     * @param probeRequest probeRequest
     * @return 脚本内容
     */
    private String buildScriptContent(ProbeRequest probeRequest) {
        var scriptContent =probeRequest.getScriptContent();
        for (ProbeParamsRequest param : probeRequest.getProbeParamsRequestList()) {
            scriptContent = scriptContent.replace("$" + param.getParamName() + "$", param.getDefaultVal());
        }
        return scriptContent;
    }
}
