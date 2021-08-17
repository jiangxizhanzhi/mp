package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.constant.BaseConstants;
import cn.cyberpeace.mp.base.constant.BaseSingleton;
import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.cyberpeace.mp.flow.enums.FlowPlatformEnum;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowTaskMapper;
import cn.cyberpeace.mp.flow.request.AddPcapRequest;
import cn.cyberpeace.mp.flow.request.AddScriptsRequest;
import cn.cyberpeace.mp.flow.request.BatchDeleteFlowTaskRequest;
import cn.cyberpeace.mp.flow.service.FlowInfoService;
import cn.cyberpeace.mp.flow.service.FlowTaskService;
import cn.cyberpeace.mp.flow.service.field.FlowTaskFields;
import cn.cyberpeace.mp.flow.util.HttpDeleteUtil;
import cn.cyberpeace.mp.flow.util.ResponseUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 流量任务service
 * @Autho qiaokang
 * @Date：2021/6/10 10:31
 */
@Service
@Slf4j
public class FlowTaskServiceImpl extends BaseModelServiceImpl<FlowTaskModel, FlowTaskMapper, FlowTaskFields,
        QueryParams> implements FlowTaskService {

    @Value("${mp.flow.actuator-url}")
    private String actuatorUrl;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private FlowInfoService flowInfoService;

    public FlowTaskServiceImpl(FlowTaskMapper stuMapper, FlowTaskFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    /**
     * 新增流量任务
     *
     * @param flowTaskModel
     * @return
     */
    public ServiceResult<FlowTaskModel> addFlowTask(FlowTaskModel flowTaskModel) {
        ObjResult<FlowInfoModel> flowInfoObjResult = flowInfoService.get(flowTaskModel.getFlowId()).getSource();
        if (Objects.nonNull(flowInfoObjResult)) {
            FlowInfoModel flowInfoModel = flowInfoObjResult.asResult();

            if (FlowPlatformEnum.TREX.toValue() == flowTaskModel.getFlowPlatform()) {
                if (this.uploadFlowInfo(flowInfoModel, flowTaskModel)) {
                    create(flowTaskModel);
                    return ServiceResult.success(flowTaskModel);
                } else {
                    return ServiceResult.fail("Add flow task error");
                }
            } else {
                // 暂无其他流量发生器，直接保存
                create(flowTaskModel);
                return ServiceResult.success(flowTaskModel);
            }
        } else {
            log.error("flow file be chosen not existed");
            return ServiceResult.fail("Flow file be chosen not existed");
        }
    }

    /**
     * 上传流量文件
     *
     * @param flowInfoModel
     * @param flowTaskModel
     * @return
     */
    private boolean uploadFlowInfo(FlowInfoModel flowInfoModel, FlowTaskModel flowTaskModel) {
        if (Objects.isNull(flowInfoModel) || Objects.isNull(flowTaskModel)) {
            return false;
        }

        boolean isSuccess = true;
        try {
            // 判断是pcap包还是脚本
            if (ResourceTypeEnum.PACP.toValue() == flowInfoModel.getResourceType()) {
                // 获取pcap流量包
                byte[] fileByte = fastDFSClient.download(flowInfoModel.getPcapFile(), FdfsGroupEnum.DEFAULT.name());
                // 先向流量发生平台导入pcap包
                JSONObject object = new JSONObject();
                object.put("fileName",
                        flowInfoModel.getTitle() + "." + flowInfoModel.getPcapFile().split(BaseConstants.SPOT)[1]);
                String pcapRsp = HttpUtils.postFile(actuatorUrl + "/pcap/import", fileByte, object);
                JSONObject jsonObject = JSONObject.parseObject(pcapRsp);
                log.info("upload pcap file response:{}", jsonObject.toJSONString());

                if (ResponseUtils.checkResponse(jsonObject)) {
                    // 解析返回的名称
                    String fileName = jsonObject.getJSONObject(BaseConstants.STR_DATA).getString("file_name");
                    flowTaskModel.setThirdFlowName(fileName);

                    // 再向流量发生平台创建pcap管理
                    AddPcapRequest addPcapRequest = new AddPcapRequest();
                    addPcapRequest.setFileName(fileName);
                    String addPcapRsp = HttpUtils.sendPost(actuatorUrl + "/pcap",
                            JsonUtils.toUnderlineJSONString(addPcapRequest));

                    JSONObject addPcapObj = JSONObject.parseObject(addPcapRsp);
                    log.info("add pcap file response:{}", addPcapObj.toJSONString());
                    if (ResponseUtils.checkResponse(addPcapObj)) {
                        // 获取返回的uuid
                        String uuid = addPcapObj.getJSONObject(BaseConstants.STR_DATA).getString("uuid");
                        flowTaskModel.setThirdFlowId(uuid);
                    } else {
                        log.error("add pcap file error");
                        isSuccess = false;
                    }
                } else {
                    log.error("upload pcap file error");
                    isSuccess = false;
                }
            } else {
                // 获取script流量脚本
                byte[] fileByte = fastDFSClient.download(flowInfoModel.getScriptFile(), FdfsGroupEnum.DEFAULT.name());

                AddScriptsRequest scriptsRequest = new AddScriptsRequest();
                // 流量执行平台名称不能重复，名称后加上时间戳
                String flowName =
                        flowInfoModel.getTitle() + System.currentTimeMillis()  + "." +  flowInfoModel.getScriptFile().split(BaseConstants.SPOT)[1];
                scriptsRequest.setName(flowName);
                // TODO type改为1, isDefault设为true
                scriptsRequest.setType(2);
                // 先向流量发生平台新增资源文件
                String scriptRsp = HttpUtils.sendPost(actuatorUrl + "/scripts",
                        JSONObject.parseObject(JsonUtils.toUnderlineJSONString(scriptsRequest)));
                JSONObject scriptObj = JSONObject.parseObject(scriptRsp);
                log.info("add script file response:{}", scriptObj.toJSONString());

                if (ResponseUtils.checkResponse(scriptObj)) {
                    // 解析返回的uuid
                    String uuid = scriptObj.getString(BaseConstants.STR_DATA);
                    flowTaskModel.setThirdFlowId(uuid);

                    // 再向流量发生平台上传资源文件
                    JSONObject jsonObj = new JSONObject();
                    // 文件的uuid
                    jsonObj.put("file_uuid", uuid);
                    jsonObj.put("fileName", flowName);
                    String uploadRsp = HttpUtils.postFile(actuatorUrl + "/upload", fileByte, jsonObj);

                    JSONObject pcapObject = JSONObject.parseObject(uploadRsp);
                    log.info("upload script file response:{}", pcapObject.toJSONString());

                    if (ResponseUtils.checkResponse(pcapObject)) {
                        // 获取文件名称
                        String fileName = pcapObject.getJSONObject(BaseConstants.STR_DATA).getString("file_name");
                        flowTaskModel.setThirdFlowName(fileName);
                    } else {
                        log.error("upload script file error");
                        isSuccess = false;
                    }
                } else {
                    log.error("add script file error");
                    isSuccess = false;
                }
            }
        } catch (Exception e) {
            log.error("upload file to flow actuator platform error!", e);
            isSuccess = false;
        }

        return isSuccess;
    }

    /**
     * 更新流量任务
     *
     * @param flowTaskModel
     * @return
     */
    public ServiceResult<FlowTaskModel> updateFlowTask(FlowTaskModel flowTaskModel) {
        ObjResult<FlowTaskModel> flowTaskResult = this.get(flowTaskModel.getId()).getSource();
        if (Objects.isNull(flowTaskResult)) {
            log.error("flow task not exist");
            return ServiceResult.fail("Flow task not exist");
        }
        FlowTaskModel oldTaskModel = flowTaskResult.asResult();

        ObjResult<FlowInfoModel> oldFlowInfoResult = flowInfoService.get(oldTaskModel.getFlowId()).getSource();
        if (Objects.nonNull(oldFlowInfoResult)) {
            FlowInfoModel oldFlowInfoModel = oldFlowInfoResult.asResult();
            // 判断是否更新了流量文件
            if (!oldTaskModel.getFlowId().equals(flowTaskModel.getFlowId())) {
                // 更新了流量文件，需要删除流量发生平台原流量文件
                if (!this.deleteFlowFile(oldFlowInfoModel.getResourceType(), Collections.singletonList(oldTaskModel.getThirdFlowId()))) {
                    log.warn("delete old flow file error");
                }

                // 再向流量发生平台上传新流量文件
                ObjResult<FlowInfoModel> newFlowInfoResult = flowInfoService.get(flowTaskModel.getFlowId()).getSource();
                if (Objects.nonNull(newFlowInfoResult)) {
                    FlowInfoModel newFlowInfoModel = newFlowInfoResult.asResult();
                    if (!this.uploadFlowInfo(newFlowInfoModel, flowTaskModel)) {
                        return ServiceResult.fail("Update flow task error");
                    }
                } else {
                    log.error("flow info not exist:{}", flowTaskModel.getFlowId());
                    return ServiceResult.fail("Update flow task error");
                }
            } else {
                log.info("no need to update flow file");
            }
        } else {
            log.warn("old flow file not exist");
            // 直接向流量发生平台上传新流量文件
            ObjResult<FlowInfoModel> newFlowInfoResult = flowInfoService.get(flowTaskModel.getFlowId()).getSource();
            if (Objects.nonNull(newFlowInfoResult)) {
                FlowInfoModel newFlowInfoModel = newFlowInfoResult.asResult();
                if (!this.uploadFlowInfo(newFlowInfoModel, flowTaskModel)) {
                    return ServiceResult.fail("Update flow task error");
                }
            } else {
                log.error("flow info not exist:{}", flowTaskModel.getFlowId());
                return ServiceResult.fail("Update flow task error");
            }
        }

        // 更新本地信息
        update(flowTaskModel);
        return ServiceResult.success(flowTaskModel);
    }

    /**
     * 删除流量任务
     *
     * @param id 流量任务id
     * @return
     */
    public ServiceResult<Boolean> deleteFlowTask(String id) {
        ObjResult<FlowTaskModel> flowTaskResult = get(id).getSource();
        if (Objects.nonNull(flowTaskResult)) {
            FlowTaskModel flowTaskModel = flowTaskResult.asResult();

            ObjResult<FlowInfoModel> flowInfoResult = flowInfoService.get(flowTaskModel.getFlowId()).getSource();
            if (Objects.nonNull(flowInfoResult)) {
                FlowInfoModel oldModel = flowInfoResult.asResult();
                // 删除流量任务，同时需要删除该任务关联的流量发生平台流量文件
                if (this.deleteFlowFile(oldModel.getResourceType(), Collections.singletonList(flowTaskModel.getThirdFlowId()))) {
                    log.info("delete flow file success");
                } else {
                    log.error("delete flow file error");
                }
            } else {
                log.warn("delete flow file not exit");
            }

            return delete(id);
        } else {
            log.error("flow task not exist: {}", id);
            return ServiceResult.fail("Flow task not exist");
        }

    }

    /**
     * 批量删除流量任务
     *
     * @param request 批量删除请求
     * @return
     */
    public ServiceResult<Integer> batchDeleteFlowTask(BatchDeleteFlowTaskRequest request) {
        QueryParams taskParams = new QueryParams();
        String[] taskDelIds = request.getIdList().toArray(new String[0]);
        taskParams.setIds(taskDelIds);
        taskParams.setPage(BaseConstants.DEFAULT_PAGE);
        taskParams.setPageSize(BaseSingleton.baseAppConfig.getMaxPageSize());
        ListObjResult<FlowTaskModel> flowTaskResult = list(taskParams).getSource();
        List<FlowTaskModel> flowTaskList = flowTaskResult.getList();

        if (CollectionUtils.isNotEmpty(flowTaskList)) {
            QueryParams flowInfoParams = new QueryParams();
            String[] flowInfoDelIds = flowTaskList.stream().map(FlowTaskModel::getFlowId).toArray(String[]::new);
            flowInfoParams.setIds(flowInfoDelIds);
            flowInfoParams.setPage(BaseConstants.DEFAULT_PAGE);
            flowInfoParams.setPageSize(BaseSingleton.baseAppConfig.getMaxPageSize());
            ListObjServiceResult<FlowInfoModel> flowInfoResult = flowInfoService.list(flowInfoParams);
            List<FlowInfoModel> infoModelList = flowInfoResult.getSource().getList();

            if (CollectionUtils.isNotEmpty(infoModelList)) {
                // 过滤出所有pcap数据
                List<String> pcapIdList =
                        infoModelList.stream().filter(model -> (ResourceTypeEnum.PACP.toValue() == model.getResourceType())).map(FlowInfoModel::getId).collect(Collectors.toList());
                List<String> pcapThirdIds =
                        flowTaskList.stream().filter(task -> pcapIdList.contains(task.getFlowId())).map(FlowTaskModel::getThirdFlowId).collect(Collectors.toList());

                // 过滤出所有script数据
                List<String> scriptIdList =
                        infoModelList.stream().filter(model -> (ResourceTypeEnum.SCRIPT.toValue() == model.getResourceType())).map(FlowInfoModel::getId).collect(Collectors.toList());
                List<String> scriptThirdIds =
                        flowTaskList.stream().filter(task -> scriptIdList.contains(task.getFlowId())).map(FlowTaskModel::getThirdFlowId).collect(Collectors.toList());

                // 分别批量删除流量执行平台的pcap数据和script数据
                if (this.deleteFlowFile(ResourceTypeEnum.PACP.toValue(), pcapThirdIds) &&
                        this.deleteFlowFile(ResourceTypeEnum.SCRIPT.toValue(), scriptThirdIds)) {
                    log.info("batch delete flow file success");
                } else {
                    log.error("batch delete flow file error");
                }
            } else {
                log.warn("no flow file to delete");
            }
        } else {
            log.error("no flow task to delete");
            return ServiceResult.fail("No flow task to delete");
        }

        return bulkDelete(taskDelIds);
    }

    /**
     * 批量删除流量发生平台文件
     *
     * @param resourceType
     * @param thirdFlowIds
     * @return
     */
    private boolean deleteFlowFile(int resourceType, List<String> thirdFlowIds) {
        String delUrl = "";
        if (ResourceTypeEnum.PACP.toValue() == resourceType) {
            log.info("start to delete pcap");
            // 删除pcap包地址
            delUrl = "/pcap";
        } else {
            log.info("start to delete scripts");
            // 删除脚本文件地址
            delUrl = "/scripts";
        }

        if (CollectionUtils.isEmpty(thirdFlowIds)) {
            log.info("no thirdFlowIds to delete");
            return true;
        }

        JSONObject object = new JSONObject();
        JSONArray array = JSONArray.parseArray(JSON.toJSONString(thirdFlowIds));
        object.put("uuids", array);

        try {
            String rsp = HttpDeleteUtil.sendDelete(actuatorUrl + delUrl, object.toJSONString());
            JSONObject rspObject = JSONObject.parseObject(rsp);
            log.info("delete flow file response: {}", rspObject.toJSONString());

            if (ResponseUtils.checkResponse(rspObject)) {
                return true;
            } else {
                log.error("delete flow file response error");
            }
        } catch (Exception e) {
            log.error("delete file from flow actuator platform error");
        }

        return false;
    }

}
