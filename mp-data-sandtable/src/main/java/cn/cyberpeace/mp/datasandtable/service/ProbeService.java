package cn.cyberpeace.mp.datasandtable.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.TextUtils;
import cn.cyberpeace.mp.datasandtable.model.ProbeModel;
import cn.cyberpeace.mp.datasandtable.model.ProbeParamModel;
import cn.cyberpeace.mp.datasandtable.model.ProbeParamsResult;
import cn.cyberpeace.mp.datasandtable.model.ProbeRequest;
import cn.cyberpeace.mp.datasandtable.model.ProbeResult;
import cn.cyberpeace.mp.datasandtable.model.ProbeSearchRequest;
import cn.cyberpeace.mp.datasandtable.repository.ProbeModelMapper;
import cn.cyberpeace.mp.datasandtable.repository.ProbeParamModelMapper;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProbeService {

    @Autowired
    private ProbeModelMapper probeModelMapper;

    @Autowired
    private ProbeParamModelMapper probeParamModelMapper;

    /**
     * 新增
     * @param probeRequest
     */
    @Transactional(rollbackFor = Exception.class)
    public void addProbe(ProbeRequest probeRequest) {
        ProbeModel probeModel = new ProbeModel();
        BeanUtils.copyProperties(probeRequest, probeModel);
        probeModel.setCreateTime(new Date());
        probeModel.setUpdateTime(new Date());
        probeModel.setId(TextUtils.uuid());
        probeModelMapper.insert(probeModel);
        insertProbeParams(probeRequest);
    }

    /**
     * 根据probeId删除探针
     * @param probeId probeId
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProbeById(String probeId) {
        probeParamModelMapper.deleteParamByProbeId(probeId);
        return probeModelMapper.deleteByProbeId(probeId) >0;
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public ListObjServiceResult<ProbeResult> queryProbeListWithPageable(ProbeSearchRequest probeSearchRequest) {
        int count = probeModelMapper.countQuery(probeSearchRequest);
        List<ProbeModel> probeModelList = probeModelMapper.queryListWithPage(probeSearchRequest);
        List<ProbeResult> probeResults = probeModelList.stream().map((model -> {
            ProbeResult probeResult = new ProbeResult();
            BeanUtils.copyProperties(model, probeResult);
            return probeResult;
        })).collect(Collectors.toList());

        return new ListObjServiceResult(probeResults, count);
    }


    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public ProbeResult queryProbeDetail(String probeId) {
        ProbeResult probeResult = new ProbeResult();
        ProbeModel probeModel = probeModelMapper.queryByProbeId(probeId);
        BeanUtils.copyProperties(probeModel, probeResult);
        List<ProbeParamsResult> probeParamsResults = probeParamModelMapper.queryProbeParamList(probeId).stream()
            .map(paramModel->{
                ProbeParamsResult paramsResult = new ProbeParamsResult();
                BeanUtils.copyProperties(paramModel, paramsResult);
                return paramsResult;
            }).collect(Collectors.toList());
        probeResult.setParamsResultList(probeParamsResults);
        return probeResult;
    }


    private void insertProbeParams(ProbeRequest probeRequest) {
        List<ProbeParamModel> probeParamModels = probeRequest.getProbeParamsRequestList().stream()
            .map((probeParamRequest -> {
                ProbeParamModel probeParamModel = new ProbeParamModel();
                BeanUtils.copyProperties(probeParamRequest, probeParamModel);
                probeParamModel.setProbeId(probeParamModel.getId());
                probeParamModel.setCreateTime(new Date());
                probeParamModel.setUpdateTime(new Date());
                return probeParamModel;
            })).collect(Collectors.toList());
        probeParamModelMapper.batchInsert(probeParamModels);
    }
}
