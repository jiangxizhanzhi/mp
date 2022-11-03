package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.flow.config.FlowConfig;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.request.AddVmRequest;
import cn.cyberpeace.mp.flow.v2.bo.CountBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBO;
import cn.cyberpeace.mp.flow.v2.model.FlowGeneratorModel;
import cn.cyberpeace.mp.flow.v2.repository.FlowGeneratorMapper;
import cn.cyberpeace.mp.flow.v2.service.fields.FlowGeneratorFields;
import cn.cyberpeace.mp.flow.v2.service.param.FlowGeneratorQueryParams;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FlowGeneratorService extends BaseModelServiceImpl<FlowGeneratorModel, FlowGeneratorMapper,
        FlowGeneratorFields, FlowGeneratorQueryParams> {

    @Autowired
    private FlowConfig flowConfig;

    @Autowired
    public FlowGeneratorService(FlowGeneratorMapper modelMapper, FlowGeneratorFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, FlowGeneratorQueryParams queryParams) {
        var types = queryParams.getTypes();
        if (types != null && !types.isEmpty()) {
            humanCondition.filterIn(FlowGeneratorModel::getType, types.toArray());
        }
    }

    public ObjServiceResult<FlowGeneratorBO> createFlowGenerator(FlowGeneratorBO bo, String[] fields) {
        // 创建对应资源
        log.info("================ Add flow generate start ==================");
        String trexName = bo.getName();

        // 获取外部networkId，用于管理端口
        String externalNetId = "";
        try {
            String getNetworkRsp = HttpUtils.sendPost(flowConfig.getCpUrl() + "/v1/scenarios/virtual_internet",
                    new JSONObject().toJSONString());
            JSONObject getNetworkObj = JSONObject.parseObject(getNetworkRsp);
            log.info("get network response:{}", getNetworkObj.toJSONString());
            if (BaseConstants.SUCCESS_ZERO.equals(getNetworkObj.getString(BaseConstants.STR_CODE))) {
                JSONObject networkObj = getNetworkObj.getJSONObject(BaseConstants.STR_DATA);
                externalNetId = networkObj.getString("external_net_id");
            }
        } catch (IOException e) {
            log.error("get external network error", e);
            throw new MpRuntimeException(FlowErrorCode.GET_OUTER_NETWORK_ERROR);
        }

        AddVmRequest addVmRequest = new AddVmRequest();
        addVmRequest.setName(trexName);
        addVmRequest.setImage(flowConfig.getTrexImage());
        AddVmRequest.Networks networks = new AddVmRequest.Networks();
        networks.setNetworkId(externalNetId);
        AddVmRequest.AccessModes accessModes = new AddVmRequest.AccessModes();
        accessModes.setPort(80);
        accessModes.setEnablePortMapping(false);
        networks.setAccessModes(Collections.singletonList(accessModes));
        addVmRequest.setNetworks(Collections.singletonList(networks));

        try {
            // 创建trex虚拟机
            String addVmRsp = HttpUtils.sendPost(flowConfig.getCpUrl() + "/v1/scenarios/vm",
                    JsonUtils.snakeStringify(addVmRequest));
            JSONObject addVmObj = JSONObject.parseObject(addVmRsp);
            log.info("add trex generate response:{}", addVmObj.toJSONString());

            String fixedIp = null;
            if (BaseConstants.SUCCESS_ZERO.equals(addVmObj.getString(BaseConstants.STR_CODE))) {
                JSONObject vmObj = addVmObj.getJSONObject(BaseConstants.STR_DATA);
                String instanceId = vmObj.getString("id");
                JSONObject netWorkObj = vmObj.getJSONArray("networks").getJSONObject(0);
                fixedIp = netWorkObj.getString("fixed_ip");
                String ip = fixedIp + BaseConstants.COLON + "80";

                bo.setName(trexName);
                bo.setIp(fixedIp);
                bo.setServer(ip);
                bo.setResourceId(instanceId);
            } else {
                log.error("Add flow generate error");
                throw new MpRuntimeException(addVmObj.getString(BaseConstants.STR_DETAIL));
            }
        } catch (MpRuntimeException e) {
            log.error("Add flow instance error", e);
            throw e;
        } catch (Exception e) {
            log.error("Add vm instance error", e);
            throw new MpRuntimeException(FlowErrorCode.ADD_FLOW_EXECUTOR_ERROR);
        }
        log.info("================ Add flow generate end ==================");
        return createBO(bo, fields, FlowGeneratorBO.class);
    }

    public ObjServiceResult<FlowGeneratorBO> updateFlowGenerator(FlowGeneratorBO bo, String[] fields) {
        // TODO: 更新对应资源
        return updateBO(bo, fields, FlowGeneratorBO.class);
    }

    public ServiceResult<Integer> bulkDeleteFlowGenerator(String[] ids) {
        // TODO: 删除对应资源
        return bulkDeleteBO(ids, FlowGeneratorBO.class);
    }

    public ServiceResult<List<FlowGeneratorBO.Nic>> addFlowGeneratorNics(String id, List<FlowGeneratorBO.Nic> nics) {
        var bo = getBO(id, FlowGeneratorBO.class).sourceObj();

        String resourceId = bo.getResourceId();
        nics.forEach(nic -> {
            String networkId = nic.getCpCloudNetworkId();
            try {
                JSONObject addPortReq = new JSONObject();
                addPortReq.put("network_id", networkId);

                String addPortRsp = HttpUtils.sendPost(flowConfig.getCpUrl() + "/v1/instances/" +
                        resourceId + "/interfaces", addPortReq.toJSONString());
                JSONObject addPortObj = JSONObject.parseObject(addPortRsp);
                log.info("add networkInfo response:{}", addPortObj.toJSONString());
                if (BaseConstants.SUCCESS_ZERO.equals(addPortObj.getString(BaseConstants.STR_CODE))) {
                    JSONObject clientDataObj = addPortObj.getJSONObject(BaseConstants.STR_DATA);
                    String mac = clientDataObj.getString("mac_address");
                    String ip = clientDataObj.getString("fixed_ip");
                    String portId = clientDataObj.getString("port_id");

                    nic.setId(portId);
                    nic.setResourceId(resourceId);
                    nic.setCpCloudNetworkId(networkId);
                    nic.setMac(mac);
                    nic.setIp(ip);
                } else {
                    log.error("【{}】add networkInfo error for networkId: {}", resourceId, networkId);
                    throw new MpRuntimeException(FlowErrorCode.ADD_PORT_ERROR);
                }
            } catch (Exception e) {
                log.error("【{}】add networkInfo error for networkId: {}", resourceId, networkId, e);
                throw new MpRuntimeException(FlowErrorCode.ADD_PORT_ERROR);
            }
        });

        // 添加对应网卡，注意同步问题
        bo.setNics(nics);
        updateBO(bo, FlowGeneratorBO.class);
        return new ServiceResult<>(nics);
    }

    public ServiceResult<Integer> removeFlowGeneratorNics(String id, List<String> nicIds) {
        var bo = getBO(id, FlowGeneratorBO.class).sourceObj();
        // 删除对应网卡
        nicIds.forEach(portId -> {
            try {
                String delPortRsp =
                        HttpUtils.sendDelete(flowConfig.getCpUrl() + "/v1/instances/" + bo.getResourceId() +
                                "/interfaces/" + portId);
                JSONObject delServerPortObj = JSONObject.parseObject(delPortRsp);
                log.info("del server port response:{}", delServerPortObj.toJSONString());
                if (!BaseConstants.SUCCESS_ZERO.equals(delServerPortObj.getString(BaseConstants.STR_CODE))) {
                    log.error("del server port error: {}", portId);
                }
            } catch (Exception e) {
                log.error("del port error", e);
            }
        });

        // 同步更新流量执行器中网口信息
        List<FlowGeneratorBO.Nic> nics = bo.getNics();
        List<FlowGeneratorBO.Nic> leftNics =
                nics.stream().filter(nic -> !nicIds.contains(nic)).collect(Collectors.toList());
        bo.setNics(leftNics);
        updateBO(bo, FlowGeneratorBO.class);
        return new ServiceResult<>(nicIds.size());
    }

    public ServiceResult<List<CountBO>> statisticsFlowGenerator() {
        HumanCondition humanCondition = new HumanCondition();
        List<FlowGeneratorModel> models = getModelMapper().list(humanCondition);
        Map<String, CountBO> type2CountBO = new HashMap<>();
        models.forEach(flowGeneratorModel -> {
            String type = flowGeneratorModel.getType();
            if (Objects.isNull(type2CountBO.get(type))) {
                type2CountBO.put(type, new CountBO());
            }
            var countBO = type2CountBO.get(type);
            countBO.setName(type);
            countBO.setCount(countBO.getCount() == null ? 1 : countBO.getCount() + 1);
        });
        return new ServiceResult<>(new ArrayList<>(type2CountBO.values()));
    }
}
