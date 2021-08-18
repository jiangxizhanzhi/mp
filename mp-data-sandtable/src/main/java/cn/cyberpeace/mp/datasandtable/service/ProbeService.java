package cn.cyberpeace.mp.datasandtable.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.TextUtils;
import cn.cyberpeace.mp.datasandtable.model.ProbeModel;
import cn.cyberpeace.mp.datasandtable.model.ProbeParamModel;
import cn.cyberpeace.mp.datasandtable.model.ProbeRequest;
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
    @Transactional(rollbackFor = MpRuntimeException.class)
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
    @Transactional(rollbackFor = MpRuntimeException.class)
    public boolean deleteProbeById(String probeId) {
        probeParamModelMapper.deleteParamByProbeId(probeId);
        return probeModelMapper.deleteByProbeId(probeId) >0;
    }

    private void insertProbeParams(ProbeRequest probeRequest) {
        List<ProbeParamModel> probeParamModels = probeRequest.getProbeParamRequestList().stream()
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
