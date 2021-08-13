package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.constant.BaseConstants;
import cn.cyberpeace.mp.base.constant.BaseSingleton;
import cn.cyberpeace.mp.base.service.FileService;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.base.util.fdfsclient.FileResponseData;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.request.AddPcapRequest;
import cn.cyberpeace.mp.flow.request.AddScriptsRequest;
import cn.cyberpeace.mp.flow.request.BatchDeleteFlowInfoRequest;
import cn.cyberpeace.mp.flow.response.FlowInfoUploadResponse;
import cn.cyberpeace.mp.flow.service.FlowInfoService;
import cn.cyberpeace.mp.flow.service.field.FlowInfoFields;
import cn.cyberpeace.mp.flow.util.HttpDeleteUtil;
import cn.cyberpeace.mp.flow.util.ResponseUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 流量service
 * @Autho qiaokang
 * @Date：2021/6/10 10:31
 */
@Service
@Slf4j
public class FlowInfoServiceImpl extends BaseModelServiceImpl<FlowInfoModel, FlowInfoMapper, FlowInfoFields,
        QueryParams>
        implements FlowInfoService {

    @Value("${mp.flow.actuator-url}")
    private String actuatorUrl;

    @Autowired
    @Qualifier("fastDfsFileServiceImpl")
    private FileService fileService;

    public FlowInfoServiceImpl(FlowInfoMapper stuMapper, FlowInfoFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    /**
     * 上传流量文件
     *
     * @param resourceType
     * @param file
     * @param request
     * @return
     */
    public ServiceResult<FlowInfoUploadResponse> uploadFlowInfo(int resourceType, MultipartFile file,
                                                                HttpServletRequest request) {
        if (Objects.isNull(file)) {
            return ServiceResult.fail("File can not be empty!");
        }

        FlowInfoUploadResponse response = new FlowInfoUploadResponse();
        // 上传本地文件服务器
        ServiceResult<FileResponseData> result = fileService.uploadFile(file, request);
        this.buildUploadResponse(response, result.getData());
        boolean isSuccess = true;
        // 错误信息
        String errMsg = "Upload file error!";

        try {
            // 判断是pcap包还是脚本
            if (ResourceTypeEnum.PACP.toValue() == resourceType) {
                // 先向流量发生平台导入pcap包
                String pcapRsp = HttpUtils.postFile(actuatorUrl + "/pcap/import", file, null);
                JSONObject jsonObject = JSONObject.parseObject(pcapRsp);
                log.info("upload pcap file response:{}", jsonObject.toJSONString());

                if (ResponseUtils.checkResponse(jsonObject)) {
                    // 解析返回的名称
                    String fileName = jsonObject.getJSONObject(BaseConstants.STR_DATA).getString("file_name");
                    response.setThirdName(fileName);

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
                        response.setThirdId(uuid);
                    } else {
                        log.error("add pcap file error");
                        if (Objects.nonNull(addPcapObj.get("msg"))) {
                            errMsg = addPcapObj.getString("msg");
                        }
                        isSuccess = false;
                    }
                } else {
                    log.error("upload pcap file error");
                    if (Objects.nonNull(jsonObject.get("msg"))) {
                        errMsg = jsonObject.getString("msg");
                    }
                    isSuccess = false;
                }
            } else {
                AddScriptsRequest scriptsRequest = new AddScriptsRequest();
                scriptsRequest.setName(file.getOriginalFilename());
                // 先向流量发生平台新增资源文件
                String scriptRsp = HttpUtils.sendPost(actuatorUrl + "/scripts",
                        JsonUtils.toUnderlineJSONString(scriptsRequest));
                JSONObject scriptObj = JSONObject.parseObject(scriptRsp);
                log.info("add script file response:{}", scriptObj.toJSONString());

                if (ResponseUtils.checkResponse(scriptObj)) {
                    // 解析返回的uuid
                    String uuid = scriptObj.getJSONObject(BaseConstants.STR_DATA).getString("script_uuid");
                    response.setThirdId(uuid);

                    // 再向流量发生平台上传资源文件
                    JSONObject jsonObj = new JSONObject();
                    // 文件类型，1：攻击脚本 2：恶意文件
                    jsonObj.put("type", 1);
                    // 文件的uuid
                    jsonObj.put("uuid", uuid);
                    String uploadRsp = HttpUtils.postFile(actuatorUrl + "/scripts/upload", file, jsonObj);
                    JSONObject pcapObject = JSONObject.parseObject(uploadRsp);
                    log.info("upload script file response:{}", pcapObject.toJSONString());

                    if (ResponseUtils.checkResponse(pcapObject)) {
                        // 获取文件名称
                        String fileName = scriptObj.getJSONObject(BaseConstants.STR_DATA).getString("file_name");
                        response.setThirdName(fileName);
                    } else {
                        log.error("upload script file error");
                        if (Objects.nonNull(pcapObject.get("msg"))) {
                            errMsg = pcapObject.getString("msg");
                        }
                        isSuccess = false;
                    }
                } else {
                    log.error("add script file error");
                    if (Objects.nonNull(scriptObj.get("msg"))) {
                        errMsg = scriptObj.getString("msg");
                    }
                    isSuccess = false;
                }
            }
        } catch (Exception e) {
            log.error("upload file to flow actuator platform error!", e);
            isSuccess = false;
        }

        if (isSuccess) {
            return ServiceResult.success(response);
        } else {
            return ServiceResult.fail(errMsg);
        }
    }


    /**
     * 更新流量信息
     *
     * @param flowModel
     * @return
     */
    public ServiceResult<FlowInfoModel> updateFlowInfo(FlowInfoModel flowModel) {
        ObjServiceResult<FlowInfoModel> objServiceResult = get(flowModel.getId());
        FlowInfoModel oldFlowModel = objServiceResult.getSource().asResult();
        // 判断是否更新了流量文件
        if ((StringUtils.isNotBlank(flowModel.getThirdId()) && !flowModel.getThirdId().equals(oldFlowModel.getThirdId())) ||
                (StringUtils.isNotBlank(flowModel.getThirdName())) && !flowModel.getThirdName().equals(oldFlowModel.getThirdName())) {
            // 更新了流量文件，需要删除流量发生平台原文件
            if (this.deleteFlowFile(oldFlowModel.getResourceType(), Collections.singletonList(oldFlowModel.getThirdId()))) {
                update(flowModel);
                return ServiceResult.success(flowModel);
            } else {
                return ServiceResult.fail("update flow info error");
            }
        } else {
            // 没更新流量文件，直接更新本地信息
            update(flowModel);
            return ServiceResult.success(flowModel);
        }
    }

    /**
     * 删除流量信息
     *
     * @param id
     * @return
     */
    public ServiceResult<Boolean> deleteFlowInfo(String id) {
        FlowInfoModel oldModel = get(id).getSource().asResult();
        if (Objects.nonNull(oldModel)) {
            // 删除流量文件，同时需要删除流量发生平台原文件
            if (this.deleteFlowFile(oldModel.getResourceType(), Collections.singletonList(oldModel.getThirdId()))) {
                return delete(id);
            } else {
                return ServiceResult.fail("delete flow info error");
            }
        } else {
            return ServiceResult.fail("delete flow info not exit");
        }
    }

    /**
     * 批量删除流量信息
     *
     * @param request
     * @return
     */
    public ServiceResult<Integer> batchDeleteFlowInfo(BatchDeleteFlowInfoRequest request) {
        QueryParams params = new QueryParams();
        String[] delIds = request.getIdList().toArray(new String[0]);
        params.setIds(delIds);
        params.setPage(BaseConstants.DEFAULT_PAGE);
        params.setPageSize(BaseSingleton.baseAppConfig.getMaxPageSize());
        ListObjServiceResult<FlowInfoModel> serviceResultList = list(params);
        List<FlowInfoModel> infoModelList = serviceResultList.getSource().getList();

        if (CollectionUtils.isNotEmpty(infoModelList)) {
            // 过滤出所有pcap数据
            List<FlowInfoModel> pcapList = infoModelList.stream().filter(model -> (StringUtils.isNotBlank(model.getThirdId()) &&
                            ResourceTypeEnum.PACP.toValue() == model.getResourceType())).collect(Collectors.toList());
            List<String> pcapThirdIds = pcapList.stream().map(FlowInfoModel::getThirdId).collect(Collectors.toList());

            // 过滤出所有script数据
            List<FlowInfoModel> scriptList = infoModelList.stream().filter(model -> (StringUtils.isNotBlank(model.getThirdId()) &&
                            ResourceTypeEnum.SCRIPT.toValue() == model.getResourceType())).collect(Collectors.toList());
            List<String> scriptThirdIds = scriptList.stream().map(FlowInfoModel::getThirdId).collect(Collectors.toList());

            // 分别批量删除流量执行平台的pcap数据和script数据
            if (this.deleteFlowFile(ResourceTypeEnum.PACP.toValue(), pcapThirdIds) &&
                    this.deleteFlowFile(ResourceTypeEnum.SCRIPT.toValue(), scriptThirdIds)) {
                return bulkDelete(delIds);
            } else {
                return ServiceResult.fail("Delete flow info error");
            }
        } else {
            log.error("flow info to delete can not be empty");
            return ServiceResult.fail("No flow info to delete");
        }

    }

    /**
     * 批量删除流量发生平台文件
     *
     * @param resourceType
     * @param thirdIds
     * @return
     */
    private boolean deleteFlowFile(int resourceType, List<String> thirdIds) {
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

        if (CollectionUtils.isEmpty(thirdIds)) {
            log.info("no thirdIds to delete");
            return true;
        }

        JSONObject object = new JSONObject();
        JSONArray array = JSONArray.parseArray(JSON.toJSONString(thirdIds));
        object.put("uuids", array);

        try {
            String rsp = HttpDeleteUtil.sendDelete(actuatorUrl + delUrl, object.toJSONString());
            JSONObject rspObject = JSONObject.parseObject(rsp);
            log.info("delete flow info response: {}", rspObject.toJSONString());

            if (ResponseUtils.checkResponse(rspObject)) {
                return true;
            } else {
                log.error("delete flow info response error");
            }
        } catch (Exception e) {
            log.error("delete file from flow actuator platform error");
        }

        return false;
    }

    /**
     * 构建文件上传返回对象
     *
     * @param response
     * @param responseData
     */
    private void buildUploadResponse(FlowInfoUploadResponse response, FileResponseData responseData) {
        response.setFilePath(responseData.getFilePath());
        response.setFileType(responseData.getFileType());
        response.setFileUrl(responseData.getHttpUrl());
        response.setFileName(responseData.getFileName());
    }

}
