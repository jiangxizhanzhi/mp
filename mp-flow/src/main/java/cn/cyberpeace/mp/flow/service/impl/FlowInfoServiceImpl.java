package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.*;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.flow.bo.ProtocolBO;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.constant.FlowCategory;
import cn.cyberpeace.mp.flow.constant.FlowConstant;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.enums.InteractiveTypeEnum;
import cn.cyberpeace.mp.flow.enums.ParamTypeEnum;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.model.*;
import cn.cyberpeace.mp.flow.repository.FlowCollectionMapper;
import cn.cyberpeace.mp.flow.repository.FlowCollectionRelMapper;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskMapper;
import cn.cyberpeace.mp.flow.request.*;
import cn.cyberpeace.mp.flow.request.queryparam.FlowInfoQueryParams;
import cn.cyberpeace.mp.flow.response.AttackFlowResponse;
import cn.cyberpeace.mp.flow.response.BackgroundFlowResponse;
import cn.cyberpeace.mp.flow.response.FlowInfoResponse;
import cn.cyberpeace.mp.flow.service.FlowInfoService;
import cn.cyberpeace.mp.flow.service.field.FlowInfoFields;
import cn.cyberpeace.mp.flow.util.FlowCacheUtil;
import cn.cyberpeace.mp.flow.util.FlowTaskPool;
import cn.cyberpeace.mp.situationaware.utils.MessageCommonUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class FlowInfoServiceImpl extends BaseModelServiceImpl<FlowInfoModel, FlowInfoMapper, FlowInfoFields, FlowInfoQueryParams> implements FlowInfoService {

    public FlowInfoServiceImpl(FlowInfoMapper stuMapper, FlowInfoFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    @Autowired
    private FlowTaskMapper flowTaskMapper;

    @Autowired
    private FlowCollectionRelMapper flowCollectionRelMapper;

    @Autowired
    private FlowCollectionMapper flowCollectionMapper;

    @Autowired
    private AsyncPlatFlowTaskService asyncPlatFlowTaskService;

    @Autowired
    private FlowCollectionServiceImpl flowCollectionService;

    @Autowired
    private FlowScriptParamServiceImpl flowScriptParamService;

    @Autowired
    private FlowCollectionRelServiceImpl flowCollectionRelService;

    @Autowired
    private FlowProtocolServiceImpl flowProtocolService;

    @Autowired
    private FlowPcapServiceImpl flowPcapService;

    @Autowired
    public BaseAppConfig baseAppConfig;

    
    private static final String PRE_PATH = "/root/.mp/resources/flows";

    
    @Override
    public List<FlowInfoModel> findByIds(List<String> idList) {
        log.info("FlowInfoServiceImpl.findByIds begin. idList:{}", idList);
        HumanCondition condition = new HumanCondition();
        condition.filterIn(FlowInfoModel::getId, idList.toArray());
        List<FlowInfoModel> flowInfoModelList = getModelMapper().list(condition);
        log.info("FlowInfoServiceImpl.findByIds end. flowInfoModelList:{}", flowInfoModelList);
        return flowInfoModelList;
    }

    
    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, FlowInfoQueryParams queryParams) {
        if (Objects.nonNull(queryParams.getCategory())) {
            humanCondition.filterEqual("category", queryParams.getCategory());
        }

        if (Objects.nonNull(queryParams.getInteractiveType())) {
            humanCondition.filterEqual("interactiveType", queryParams.getInteractiveType());
        }
    }

    
    @Transactional
    public ServiceResult<Boolean> deleteFlowInfo(String id, boolean checkTask) {
        
        if (checkTask) {
            List<FlowTaskModel> flowTaskList = flowTaskMapper.listByFlowIds(Collections.singletonList(id));
            if (CollectionUtils.isNotEmpty(flowTaskList)) {
                return ResultCommon.fail(FlowErrorCode.TASK_DELETE_FAILED);
            }
        }

        
        List<FlowCollectionRelModel> flowCollectionRelModels =
                flowCollectionRelMapper.selectByFlowIds(Collections.singletonList(id));
        if (CollectionUtils.isNotEmpty(flowCollectionRelModels)) {
            
            flowCollectionRelModels.forEach(rel -> {
                DelFlowCollectionRelRequest delRelRequest = new DelFlowCollectionRelRequest();
                delRelRequest.setCollectionId(rel.getCollectionId());
                delRelRequest.setFlowIds(Collections.singletonList(id));
                flowCollectionService.delCollectionRel(delRelRequest);
            });
        }

        ObjServiceResult<FlowInfoModel> serviceResult = get(id);
        ObjResult<FlowInfoModel> flowInfoModelObjResult = serviceResult.getSource();
        if (Objects.nonNull(flowInfoModelObjResult)) {
            FlowInfoModel flowInfoModel = flowInfoModelObjResult.getObj();
            
            asyncPlatFlowTaskService.deleteFlowFile(Collections.singletonList(flowInfoModel));

            
            if (InteractiveTypeEnum.ATTACK.toValue() == flowInfoModel.getInteractiveType()) {
                flowScriptParamService.deleteByFlowId(id);
            }

            
            return delete(id);
        } else {
            log.error("flow file to delete not exist");
        }

        return ResultCommon.success(false);
    }

    
    @Transactional
    public ServiceResult<Integer> batchDeleteFlowInfo(String[] ids) {
        List<String> idList = Arrays.asList(ids);
        
        List<FlowTaskModel> flowTaskList = flowTaskMapper.listByFlowIds(Arrays.asList(ids));
        if (CollectionUtils.isNotEmpty(flowTaskList)) {
            log.info("flow info related flow task can not delete: {}", flowTaskList);
            return ResultCommon.fail(FlowErrorCode.TASK_DELETE_FAILED);
        }

        
        List<FlowCollectionRelModel> flowCollectionRelModels =
                flowCollectionRelMapper.selectByFlowIds(idList);
        if (CollectionUtils.isNotEmpty(flowCollectionRelModels)) {
            
            Map<String, List<String>> relMap =
                    flowCollectionRelModels.stream().collect(Collectors.groupingBy(FlowCollectionRelModel::getCollectionId,
                            Collectors.mapping(FlowCollectionRelModel::getFlowId, Collectors.toList())));
            
            relMap.forEach((collectionId, flowIds) -> {
                DelFlowCollectionRelRequest delRelRequest = new DelFlowCollectionRelRequest();
                delRelRequest.setCollectionId(collectionId);
                delRelRequest.setFlowIds(flowIds);
                flowCollectionService.delCollectionRel(delRelRequest);
            });
        }

        List<FlowInfoModel> flowInfoModels = this.selectByFlowIds(idList);
        if (CollectionUtils.isNotEmpty(flowInfoModels)) {
            
            asyncPlatFlowTaskService.deleteFlowFile(flowInfoModels);

            
            List<FlowInfoModel> attackFlows =
                    flowInfoModels.stream().filter(flow -> InteractiveTypeEnum.ATTACK.toValue() == flow.getInteractiveType()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(attackFlows)) {
                List<String> attackFlowIds = attackFlows.stream().map(FlowInfoModel::getId).collect(Collectors.toList());
                
                flowScriptParamService.deleteByFlowIds(attackFlowIds);
            }

            
            return bulkDelete(ids);
        } else {
            log.error("flow files to delete not exist");
        }

        return ResultCommon.success(0);
    }

    
    public ListObjServiceResult<FlowInfoResponse> pageFlowInfo(FlowInfoQueryParams queryParams) {
        
        ListObjServiceResult<FlowInfoModel> listObjServiceResult = list(queryParams);

        
        List<FlowInfoResponse> responseList = new ArrayList<>();
        List<FlowInfoModel> flowInfoModels = listObjServiceResult.getSource().getObjs();
        if (CollectionUtils.isNotEmpty(flowInfoModels)) {
            List<String> flowIds = flowInfoModels.stream().map(FlowInfoModel::getId).collect(Collectors.toList());
            List<FlowScriptParamModel> flowScriptParamModels = flowScriptParamService.selectByFlowIds(flowIds);
            responseList = listObjServiceResult.getSource().getObjs().stream()
                    .map(model -> {
                        List<FlowScriptParamModel> currentFlowParamModels = new ArrayList<>();
                        if (CollectionUtils.isNotEmpty(flowScriptParamModels)) {
                            currentFlowParamModels =
                                    flowScriptParamModels.stream().map(scriptParam -> {
                                        
                                        if ("ip".equalsIgnoreCase(scriptParam.getParamKey())) {
                                            scriptParam.setParamDesc(MessageCommon.get("flow.error_code.target_ip"));
                                        } else if ("port".equalsIgnoreCase(scriptParam.getParamKey())) {
                                            scriptParam.setParamDesc(MessageCommon.get("flow.error_code.target_port"));
                                        }

                                        return scriptParam;
                                    }).filter(param -> model.getId().equals(param.getFlowId())).collect(Collectors.toList());
                        }
                        this.internationalize(model);
                        FlowInfoResponse response = new FlowInfoResponse(model, currentFlowParamModels);
                        return response;
                    })
                    .collect(Collectors.toList());
        }

        return new ListObjServiceResult<>(new ListObjResult<>(responseList, listObjServiceResult.getSource().getTotal())).asResult();
    }

    
    private void internationalize(FlowInfoModel flowInfoModel) {
        String title = flowInfoModel.getTitle();
        boolean needInternational = true;
        if ("ftp".equalsIgnoreCase(title) || "dns".equalsIgnoreCase(title)
                || "vnc".equalsIgnoreCase(title) || "arp".equalsIgnoreCase(title)) {
            needInternational = false;
        } else {
            title = MessageCommonUtil.convertMessage(flowInfoModel.getTitle());
            flowInfoModel.setFlowDesc(MessageCommonUtil.convertMessage(flowInfoModel.getFlowDesc()));
        }

        flowInfoModel.setTitle(title);
        String fileName = flowInfoModel.getFileName();
        if (fileName.endsWith(".pcap")) {
            fileName = fileName.substring(0, fileName.length() - 5);
            if (needInternational) {
                flowInfoModel.setFileName(MessageCommonUtil.convertMessage(fileName).concat(".pcap"));
            }
        } else if (fileName.endsWith(".py")) {
            fileName = fileName.substring(0, fileName.length() - 3);
            flowInfoModel.setFileName(MessageCommonUtil.convertMessage(fileName).concat(".py"));
        }
    }

    
    public ServiceResult<List<FlowInfoResponse>> listByCollectionIds(ListByCollectionIdsRequest request) {
        
        List<FlowCollectionRelModel> flowCollectionRelModels =
                flowCollectionRelService.selectByCollectionIds(request.getCollectionIds());
        if (CollectionUtils.isEmpty(flowCollectionRelModels)) {
            return ResultCommon.success(new ArrayList<>());
        }

        List<String> flowIds =
                flowCollectionRelModels.stream().map(FlowCollectionRelModel::getFlowId).collect(Collectors.toList());
        FlowInfoQueryParams queryParams = new FlowInfoQueryParams();
        queryParams.setIds(flowIds.toArray(new String[0]));
        ListObjServiceResult<FlowInfoResponse> listObjServiceResult = pageFlowInfo(queryParams);

        return ResultCommon.success(listObjServiceResult.getSource().getList());
    }

    
    public ServiceResult<BackgroundFlowResponse> queryBackgroundDetail(String id) {
        BackgroundFlowResponse response;
        String prefixUrl = baseAppConfig.getFdfsOuterHttpServer();

        ObjResult<FlowInfoModel> objResult = get(id).getSource();
        if (Objects.nonNull(objResult)) {
            FlowInfoModel flowInfoModel = objResult.getObj();
            this.internationalize(flowInfoModel);
            if (InteractiveTypeEnum.BACKGROUND.toValue() != flowInfoModel.getInteractiveType()) {
                return ResultCommon.fail(FlowErrorCode.NOT_BACKGROUND_FLOW);
            }
            response = new BackgroundFlowResponse(flowInfoModel, prefixUrl);
        } else {
            return ResultCommon.fail(FlowErrorCode.FLOW_NOT_EXIST);
        }

        response.setProtocolList(this.getProtocolBOList(id));
        return ResultCommon.success(response);
    }

    
    private List<ProtocolBO> getProtocolBOList(String flowId) {
        
        List<ProtocolBO> protocolBOList = new ArrayList<>();
        List<FlowProtocolModel> flowProtocolModels = flowProtocolService.selectByFlowId(flowId);
        if (CollectionUtils.isNotEmpty(flowProtocolModels)) {
            for (FlowProtocolModel flowProtocolModel : flowProtocolModels) {
                ProtocolBO protocolBO = new ProtocolBO();
                BeanUtils.copyProperties(flowProtocolModel, protocolBO);
                String desc = MessageCommonUtil.convertMessageWithNull(flowProtocolModel.getProtocolName().toLowerCase());
                if (org.apache.commons.lang3.StringUtils.isBlank(desc)) {
                    
                    desc = MessageCommonUtil.convertMessageWithNull(FlowConstant.FLOW_UNKNOWN_PROTOCOL);
                }
                protocolBO.setDesc(desc);
                protocolBOList.add(protocolBO);
            }
        }

        return this.sortProtocol(protocolBOList);
    }

    
    public ServiceResult<AttackFlowResponse> queryAttackDetail(String id) {
        AttackFlowResponse response;
        String prefixUrl = baseAppConfig.getFdfsOuterHttpServer();

        ObjResult<FlowInfoModel> objResult = get(id).getSource();
        if (Objects.nonNull(objResult)) {
            FlowInfoModel flowInfoModel = objResult.getObj();
            this.internationalize(flowInfoModel);
            if (InteractiveTypeEnum.ATTACK.toValue() != flowInfoModel.getInteractiveType()) {
                return ResultCommon.fail(FlowErrorCode.NOT_ATTACK_FLOW);
            }

            
            List<FlowScriptParamModel> flowScriptParamModels =
                    flowScriptParamService.selectByFlowId(flowInfoModel.getId());
            if (CollectionUtils.isNotEmpty(flowScriptParamModels)) {
                flowScriptParamModels.stream().map(scriptParam -> {
                    
                    if ("ip".equalsIgnoreCase(scriptParam.getParamKey())) {
                        scriptParam.setParamDesc(MessageCommon.get("flow.error_code.target_ip"));
                    } else if ("port".equalsIgnoreCase(scriptParam.getParamKey())) {
                        scriptParam.setParamDesc(MessageCommon.get("flow.error_code.target_port"));
                    }

                    return scriptParam;
                }).collect(Collectors.toList());
            }

            response = new AttackFlowResponse(flowInfoModel, prefixUrl, flowScriptParamModels);

            
            if (StringUtils.isNotBlank(flowInfoModel.getScriptFile())) {
                String code = FsCommon.downloadContent(flowInfoModel.getScriptFile());
                response.setCode(code);
            }
        } else {
            return ResultCommon.fail(FlowErrorCode.FLOW_NOT_EXIST);
        }

        response.setProtocolList(this.getProtocolBOList(id));
        return ResultCommon.success(response);
    }

    
    public ServiceResult<BackgroundFlowResponse> addBackgroundFlow(AddBackgroundFlowRequest addRequest) {
        if (!checkNameUnique(addRequest.getTitle(), org.apache.commons.lang3.StringUtils.EMPTY)) {
            return ResultCommon.fail(FlowErrorCode.TITLE_UNIQUE);
        }

        if (StringUtils.isBlank(addRequest.getPcapFilePath())) {
            if (Objects.isNull(addRequest.getPcapFile())) {
                throw new MpRuntimeException(FlowErrorCode.PARAMS_SHOULD_NOT_EMPTY.format("pcapFile"));
            }

            String suffix = FileUtil.getSuffix(addRequest.getPcapFile().getOriginalFilename());
            if (!BaseConstants.STR_PCAP.equals(suffix)) {
                return ResultCommon.fail(FlowErrorCode.NOT_PCAP_FILE);
            }

            
            String filePath = null;
            try {
                filePath = FsCommon.upload(addRequest.getPcapFile().getInputStream(), suffix);
                log.info("{} upload result path: {} with suffix: {}", addRequest.getPcapFile().getOriginalFilename(), filePath, suffix);
            } catch (IOException e) {
                log.error("upload pcap file error, {}", ExceptionUtils.getStackTrace(e));
                return ResultCommon.fail(FlowErrorCode.FLOW_FILE_UPLOAD_ERROR);
            }
            addRequest.setPcapFilePath(filePath);
        }


        
        FlowInfoModel flowInfoModel = this.getFlowInfoModel(addRequest);
        
        ObjServiceResult<FlowInfoModel> serviceResult = create(flowInfoModel);

        
        this.batchSaveProtcols(addRequest.getProtocols(), serviceResult.getSource().getObj().getId());

        String prefixUrl = baseAppConfig.getFdfsOuterHttpServer();
        
        BackgroundFlowResponse response = new BackgroundFlowResponse(serviceResult.getSource().getObj(), prefixUrl);
        return ResultCommon.success(response);
    }

    
    private void batchSaveProtcols(String strProtocols, String flowId) {
        log.info("batch save protocols for flowId:{} with protocols: {}", flowId, strProtocols);
        if (StringUtils.isNotBlank(strProtocols)) {
            FlowInfoModel flowInfoModel = getModelMapper().get(flowId);
            if (Objects.isNull(flowInfoModel)) {
                log.error("flow info is empty");
                return;
            }
            String filePath = flowInfoModel.getPcapFile();
            List<FlowProtocolModel> flowProtocolModels = new ArrayList<>();
            JSONArray protocolsArray = JSONArray.parseArray(strProtocols);
            for (Object obj : protocolsArray) {
                JSONObject jsonObject = (JSONObject) obj;
                FlowProtocolModel flowProtocolModel = new FlowProtocolModel();
                flowProtocolModel.setProtocolName(jsonObject.getString("protocol_name"));
                flowProtocolModel.setRate(jsonObject.getBigDecimal("rate"));
                flowProtocolModel.setMarked(jsonObject.getInteger("marked"));
                flowProtocolModel.setFlowId(flowId);
                flowProtocolModels.add(flowProtocolModel);
            }

            if (CollectionUtils.isNotEmpty(flowProtocolModels)) {
                
                List<FlowProtocolModel> protocolModels = flowProtocolService.selectByFlowId(flowId);
                if (CollectionUtils.isNotEmpty(protocolModels)) {
                    
                    flowProtocolService.deleteByFlowId(flowId);
                }

                
                List<String> filterProtocols = new ArrayList<>();
                
                for (FlowProtocolModel flowProtocolModel : flowProtocolModels) {
                    if (1 == flowProtocolModel.getMarked()) {
                        filterProtocols.add(flowProtocolModel.getProtocolName());
                    }
                }
                flowProtocolService.bulkCreate(flowProtocolModels);

                
                TaskCommon.executeTask(() -> this.pcapFilter(flowId, filePath, filterProtocols));
            }
        }
    }

    
    @Transactional
    public ServiceResult<AttackFlowResponse> addAttackFlow(AddAttackFlowRequest addRequest) {
        if (!checkNameUnique(addRequest.getTitle(), org.apache.commons.lang3.StringUtils.EMPTY)) {
            return ResultCommon.fail(FlowErrorCode.TITLE_UNIQUE);
        }

        AttackFlowResponse response = new AttackFlowResponse();
        if (ResourceTypeEnum.SCRIPT.toValue() == addRequest.getResourceType()) {
            if (StringUtils.isBlank(addRequest.getCode())) {
                return ResultCommon.fail("code can not be empty");
            }
            if (Objects.isNull(addRequest.getSuffix())) {
                return ResultCommon.fail("suffix can not be empty");
            }

            
            if (StringUtils.isNotBlank(addRequest.getParameter())) {
                List<FlowScriptParamModel> scriptParamModels = new ArrayList<>();
                JSONObject paramObj = JSONObject.parseObject(addRequest.getParameter());
                paramObj.keySet().forEach(paramKey -> {
                    FlowScriptParamModel paramModel = new FlowScriptParamModel();
                    paramModel.setParamKey(paramKey);

                    JSONArray paramArray = paramObj.getJSONArray(paramKey);
                    
                    Integer type;
                    if (ParamTypeEnum.STRING.toString().equals(paramArray.getString(0))) {
                        type = ParamTypeEnum.STRING.toValue();
                    } else if (ParamTypeEnum.IP.toString().equals(paramArray.getString(0))) {
                        type = ParamTypeEnum.IP.toValue();
                    } else if (ParamTypeEnum.NUMBER.toString().equals(paramArray.getString(0))) {
                        type = ParamTypeEnum.NUMBER.toValue();
                    } else {
                        type = ParamTypeEnum.BOOL.toValue();
                    }
                    paramModel.setParamType(type);

                    
                    paramModel.setParamValue(paramArray.getString(1));
                    
                    paramModel.setParamDesc(paramArray.getString(2));

                    
                    if (paramKey.length() > 40 || paramModel.getParamValue().length() > 50 ||
                            paramModel.getParamDesc().length() > 2048) {
                        log.error("param length is invalid");
                        throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
                    }

                    scriptParamModels.add(paramModel);
                });

                addRequest.setScriptParamModels(scriptParamModels);
            }

            
            String fileName;
            String suffix = addRequest.getSuffix() == 1 ? "sh" : "py";
            if (StringUtils.isBlank(addRequest.getFileName())) {
                fileName = TextUtils.uuid() + "." + suffix;
                addRequest.setFileName(fileName);
            }

            InputStream is = null;
            try {
                is = new ByteArrayInputStream(addRequest.getCode().getBytes());
                String filePath = FsCommon.upload(is, suffix);
                addRequest.setFilePath(filePath);
            } catch (MpRuntimeException e) {
                log.error("add attack flow error, {}", ExceptionUtils.getStackTrace(e));
                return ResultCommon.fail(FlowErrorCode.ADD_ATTACK_FLOW_ERROR);
            } finally {
                IOUtils.closeQuietly(is);
            }

            
            FlowInfoModel flowInfoModel = this.getFlowInfoModel(addRequest);
            
            ObjServiceResult<FlowInfoModel> serviceResult = create(flowInfoModel);
            FlowInfoModel resultModel = serviceResult.getSource().getObj();

            
            List<FlowScriptParamModel> scriptParamModels = addRequest.getScriptParamModels();
            if (CollectionUtils.isNotEmpty(scriptParamModels)) {
                scriptParamModels.forEach(model -> model.setFlowId(resultModel.getId()));
                flowScriptParamService.bulkCreate(scriptParamModels);
            }

            String prefixUrl = baseAppConfig.getFdfsOuterHttpServer();
            
            response = new AttackFlowResponse(resultModel, prefixUrl, scriptParamModels);
            response.setCode(addRequest.getCode());
        } else {
            if (StringUtils.isEmpty(addRequest.getFileName())) {
                return ResultCommon.fail("fileName can not be empty");
            }

            if (StringUtils.isBlank(addRequest.getPcapFilePath())) {
                if (Objects.isNull(addRequest.getPcapFile())) {
                    return ResultCommon.fail("pcapFile can not be empty");
                }

                String suffix = FileUtil.getSuffix(addRequest.getPcapFile().getOriginalFilename());
                if (!BaseConstants.STR_PCAP.equals(suffix)) {
                    return ResultCommon.fail(FlowErrorCode.NOT_PCAP_FILE);
                }

                
                String filePath;
                try {
                    filePath = FsCommon.upload(addRequest.getPcapFile().getInputStream(), suffix);
                } catch (IOException e) {
                    log.error("attack upload pcap file error, {}", ExceptionUtils.getStackTrace(e));
                    return ResultCommon.fail(FlowErrorCode.FLOW_FILE_UPLOAD_ERROR);
                }
                addRequest.setPcapFilePath(filePath);
            }

            
            FlowInfoModel flowInfoModel = this.getFlowInfoModel(addRequest);
            
            ObjServiceResult<FlowInfoModel> serviceResult = create(flowInfoModel);
            FlowInfoModel resultModel = serviceResult.getSource().getObj();

            
            this.batchSaveProtcols(addRequest.getProtocols(), resultModel.getId());

            String prefixUrl = baseAppConfig.getFdfsOuterHttpServer();
            
            response = new AttackFlowResponse(resultModel, prefixUrl, null);
        }

        return ResultCommon.success(response);
    }

    
    public ServiceResult<BackgroundFlowResponse> modifyBackgroundFlow(ModifyBackgroundFlowRequest modifyRequest) {
        if (!checkNameUnique(modifyRequest.getTitle(), modifyRequest.getId())) {
            return ResultCommon.fail(FlowErrorCode.TITLE_UNIQUE);
        }

        if (StringUtils.isNotBlank(modifyRequest.getFileName()) && !modifyRequest.getFileName().endsWith(".pcap")) {
            return ResultCommon.fail(FlowErrorCode.NOT_PCAP_FILE);
        }

        ObjServiceResult<FlowInfoModel> oldServiceResult = get(modifyRequest.getId());
        if (Objects.isNull(oldServiceResult.getSource())) {
            
            return ResultCommon.fail(FlowErrorCode.FLOW_NOT_EXIST);
        } else {
            
            if (InteractiveTypeEnum.BACKGROUND.toValue() != oldServiceResult.getSource().getObj().getInteractiveType()) {
                return ResultCommon.fail(FlowErrorCode.NOT_BACKGROUND_FLOW);
            }
        }

        if (StringUtils.isBlank(modifyRequest.getPcapFilePath()) && Objects.nonNull(modifyRequest.getPcapFile())) {
            
            String suffix = FileUtil.getSuffix(modifyRequest.getPcapFile().getOriginalFilename());
            if (!BaseConstants.STR_PCAP.equals(suffix)) {
                return ResultCommon.fail(FlowErrorCode.NOT_PCAP_FILE);
            }
            
            String filePath = null;
            try {
                filePath = FsCommon.upload(modifyRequest.getPcapFile().getInputStream(), suffix);
                log.info("{} upload result path: {} with suffix: {}", modifyRequest.getPcapFile().getOriginalFilename(), filePath, suffix);
            } catch (IOException e) {
                log.error("upload pcap file for update error, {}", ExceptionUtils.getStackTrace(e));
                return ResultCommon.fail(FlowErrorCode.FLOW_FILE_UPLOAD_ERROR);
            }
            modifyRequest.setPcapFilePath(filePath);
        }

        
        FlowInfoModel flowInfoModel = this.getFlowInfoModel(modifyRequest);
        
        ObjServiceResult<FlowInfoModel> serviceResult = update(flowInfoModel);

        
        this.batchSaveProtcols(modifyRequest.getProtocols(), modifyRequest.getId());

        String prefixUrl = baseAppConfig.getFdfsOuterHttpServer();
        
        BackgroundFlowResponse response = new BackgroundFlowResponse(serviceResult.getSource().getObj(), prefixUrl);
        return ResultCommon.success(response);
    }

    
    @Transactional
    public ServiceResult<AttackFlowResponse> modifyAttackFlow(ModifyAttackFlowRequest modifyRequest) {
        if (!checkNameUnique(modifyRequest.getTitle(), modifyRequest.getId())) {
            return ResultCommon.fail(FlowErrorCode.TITLE_UNIQUE);
        }

        ObjServiceResult<FlowInfoModel> oldServiceResult = get(modifyRequest.getId());
        if (Objects.isNull(oldServiceResult.getSource())) {
            
            return ResultCommon.fail(FlowErrorCode.FLOW_NOT_EXIST);
        } else {
            
            if (InteractiveTypeEnum.ATTACK.toValue() != oldServiceResult.getSource().getObj().getInteractiveType()) {
                return ResultCommon.fail(FlowErrorCode.NOT_ATTACK_FLOW);
            }
        }

        AttackFlowResponse response;
        if (ResourceTypeEnum.SCRIPT.toValue() == modifyRequest.getResourceType()) {
            
            if (StringUtils.isNotBlank(modifyRequest.getParameter())) {
                List<FlowScriptParamModel> scriptParamModels = new ArrayList<>();
                JSONObject paramObj = JSONObject.parseObject(modifyRequest.getParameter());
                paramObj.keySet().forEach(paramKey -> {
                    FlowScriptParamModel paramModel = new FlowScriptParamModel();
                    paramModel.setParamKey(paramKey);

                    JSONArray paramArray = paramObj.getJSONArray(paramKey);
                    
                    Integer type;
                    if (ParamTypeEnum.STRING.toString().equals(paramArray.getString(0))) {
                        type = ParamTypeEnum.STRING.toValue();
                    } else if (ParamTypeEnum.IP.toString().equals(paramArray.getString(0))) {
                        type = ParamTypeEnum.IP.toValue();
                    } else if (ParamTypeEnum.NUMBER.toString().equals(paramArray.getString(0))) {
                        type = ParamTypeEnum.NUMBER.toValue();
                    } else {
                        type = ParamTypeEnum.BOOL.toValue();
                    }
                    paramModel.setParamType(type);

                    
                    paramModel.setParamValue(paramArray.getString(1));
                    
                    paramModel.setParamDesc(paramArray.getString(2));
                    scriptParamModels.add(paramModel);
                });

                modifyRequest.setScriptParamModels(scriptParamModels);
            }

            String suffix = null;
            if (Objects.nonNull(modifyRequest.getSuffix())) {
                suffix = modifyRequest.getSuffix() == 1 ? "sh" : "py";
                if (StringUtils.isBlank(modifyRequest.getCode())) {
                    
                    String code = FsCommon.downloadContent(oldServiceResult.getSource().getObj().getScriptFile());
                    InputStream is = null;
                    try {
                        is = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
                        String filePath = FsCommon.upload(is, suffix);
                        modifyRequest.setFilePath(filePath);
                    } catch (MpRuntimeException e) {
                        log.error("modify attack flow error, {}", ExceptionUtils.getStackTrace(e));
                        return ResultCommon.fail(FlowErrorCode.MODIFY_ATTACK_FLOW_ERROR);
                    } finally {
                        IOUtils.closeQuietly(is);
                    }
                }
            }

            if (StringUtils.isNotBlank(modifyRequest.getCode())) {
                
                String fileName;
                if (StringUtils.isBlank(suffix)) {
                    
                    suffix = oldServiceResult.getSource().getObj().getScriptFile().endsWith(".sh") ? "sh" : "py";
                }

                if (StringUtils.isBlank(modifyRequest.getFileName())) {
                    fileName = TextUtils.uuid() + "." + suffix;
                    modifyRequest.setFileName(fileName);
                }

                InputStream is = null;
                try {
                    is = new ByteArrayInputStream(modifyRequest.getCode().getBytes(StandardCharsets.UTF_8));
                    String filePath = FsCommon.upload(is, suffix);
                    modifyRequest.setFilePath(filePath);
                } catch (MpRuntimeException e) {
                    log.error("modify attack flow error, {}", ExceptionUtils.getStackTrace(e));
                    return ResultCommon.fail(FlowErrorCode.MODIFY_ATTACK_FLOW_ERROR);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }

            
            FlowInfoModel flowInfoModel = this.getFlowInfoModel(modifyRequest);
            
            ObjServiceResult<FlowInfoModel> serviceResult = update(flowInfoModel);

            
            List<FlowScriptParamModel> scriptParamModels = modifyRequest.getScriptParamModels();
            if (CollectionUtils.isNotEmpty(scriptParamModels)) {
                flowScriptParamService.deleteByFlowId(modifyRequest.getId());
                scriptParamModels.forEach(model -> model.setFlowId(modifyRequest.getId()));
                flowScriptParamService.bulkCreate(scriptParamModels);
            }

            String prefixUrl = baseAppConfig.getFdfsOuterHttpServer();
            
            response = new AttackFlowResponse(serviceResult.getSource().getObj(), prefixUrl, scriptParamModels);
            response.setCode(modifyRequest.getCode());
        } else {
            if (StringUtils.isBlank(modifyRequest.getPcapFilePath())) {
                if (ResourceTypeEnum.PACP.toValue() != oldServiceResult.getSource().getObj().getResourceType()) {
                    
                    if (Objects.isNull(modifyRequest.getPcapFile())) {
                        return ResultCommon.fail("pcapFile can not be empty");
                    }
                }
                if (Objects.nonNull(modifyRequest.getPcapFile())) {
                    
                    String suffix = FileUtil.getSuffix(modifyRequest.getPcapFile().getOriginalFilename());
                    if (!BaseConstants.STR_PCAP.equals(suffix)) {
                        return ResultCommon.fail(FlowErrorCode.NOT_PCAP_FILE);
                    }
                    
                    String filePath = null;
                    try {
                        filePath = FsCommon.upload(modifyRequest.getPcapFile().getInputStream(), suffix);
                    } catch (IOException e) {
                        log.error("attack upload pcap file for update error, {}", ExceptionUtils.getStackTrace(e));
                        return ResultCommon.fail(FlowErrorCode.FLOW_FILE_UPLOAD_ERROR);
                    }
                    modifyRequest.setPcapFilePath(filePath);
                }
            }

            
            FlowInfoModel flowInfoModel = this.getFlowInfoModel(modifyRequest);
            
            ObjServiceResult<FlowInfoModel> serviceResult = update(flowInfoModel);

            
            this.batchSaveProtcols(modifyRequest.getProtocols(), modifyRequest.getId());

            String prefixUrl = baseAppConfig.getFdfsOuterHttpServer();
            
            response = new AttackFlowResponse(serviceResult.getSource().getObj(), prefixUrl, null);
        }

        return ResultCommon.success(response);
    }

    
    private FlowInfoModel getFlowInfoModel(AddBackgroundFlowRequest addRequest) {
        FlowInfoModel flowInfoModel = new FlowInfoModel();
        flowInfoModel.setTitle(addRequest.getTitle());
        flowInfoModel.setFlowDesc(addRequest.getIntroduction());
        flowInfoModel.setCategory(addRequest.getCategory());
        flowInfoModel.setFileName(addRequest.getFileName());
        flowInfoModel.setResourceType(ResourceTypeEnum.PACP.toValue());
        flowInfoModel.setInteractiveType(InteractiveTypeEnum.BACKGROUND.toValue());
        flowInfoModel.setPcapFile(addRequest.getPcapFilePath());

        return flowInfoModel;
    }

    
    private FlowInfoModel getFlowInfoModel(ModifyBackgroundFlowRequest modifyRequest) {
        FlowInfoModel flowInfoModel = new FlowInfoModel();
        flowInfoModel.setId(modifyRequest.getId());
        flowInfoModel.setTitle(modifyRequest.getTitle());
        flowInfoModel.setFlowDesc(modifyRequest.getIntroduction());
        flowInfoModel.setCategory(modifyRequest.getCategory());
        flowInfoModel.setFileName(modifyRequest.getFileName());
        flowInfoModel.setPcapFile(modifyRequest.getPcapFilePath());

        return flowInfoModel;
    }

    
    private FlowInfoModel getFlowInfoModel(AddAttackFlowRequest addRequest) {
        FlowInfoModel flowInfoModel = new FlowInfoModel();
        flowInfoModel.setTitle(addRequest.getTitle());
        flowInfoModel.setFlowDesc(addRequest.getIntroduction());
        flowInfoModel.setCategory(addRequest.getCategory());
        flowInfoModel.setFileName(addRequest.getFileName());
        flowInfoModel.setResourceType(addRequest.getResourceType());
        flowInfoModel.setInteractiveType(InteractiveTypeEnum.ATTACK.toValue());
        flowInfoModel.setScriptFile(addRequest.getFilePath());
        flowInfoModel.setRunCmd(addRequest.getRunCmd());
        flowInfoModel.setPcapFile(addRequest.getPcapFilePath());

        return flowInfoModel;
    }

    
    private FlowInfoModel getFlowInfoModel(ModifyAttackFlowRequest modifyRequest) {
        FlowInfoModel flowInfoModel = new FlowInfoModel();
        flowInfoModel.setId(modifyRequest.getId());
        flowInfoModel.setTitle(modifyRequest.getTitle());
        flowInfoModel.setFlowDesc(modifyRequest.getIntroduction());
        flowInfoModel.setCategory(modifyRequest.getCategory());
        flowInfoModel.setFileName(modifyRequest.getFileName());
        flowInfoModel.setScriptFile(modifyRequest.getFilePath());
        flowInfoModel.setRunCmd(modifyRequest.getRunCmd());
        flowInfoModel.setResourceType(modifyRequest.getResourceType());
        flowInfoModel.setPcapFile(modifyRequest.getPcapFilePath());

        return flowInfoModel;
    }

    
    private boolean checkNameUnique(String title, String id) {
        FlowInfoModel flowInfoModel = getModelMapper().selectByTitle(title);
        return Objects.isNull(flowInfoModel) || flowInfoModel.getId().equals(id);
    }

    
    public List<FlowInfoModel> selectByFlowIds(List<String> flowIds) {
        List<FlowInfoModel> flowInfoModels = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(flowIds)) {
            flowInfoModels = getModelMapper().selectByFlowIds(flowIds);
        }
        return flowInfoModels;
    }

    
    public FlowInfoModel selectByTitle(String title) {
        return getModelMapper().selectByTitle(title);
    }

    
    public ServiceResult<Boolean> batchImportFlowInfo() {
        String pcapPath = PRE_PATH + "/pcap";
        File pcapFile = new File(pcapPath);
        if (!this.doImport(pcapFile)) {
            return new ServiceResult<>(false);
        }

        String scriptPath = PRE_PATH + "/scripts";
        File scriptFile = new File(scriptPath);
        if (!this.doImport(scriptFile)) {
            return new ServiceResult<>(false);
        }

        return new ServiceResult<>(true);
    }

    
    public void checkDefaultFlowScriptParams(){
        log.info("============= checkDefaultFlowScriptParams ================");
        
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(FlowInfoModel::getResourceType, ResourceTypeEnum.SCRIPT.toValue());
        List<FlowInfoModel> flowInfoModelList = getModelMapper().list(humanCondition);
        List<FlowScriptParamModel> scriptParamModelList  = flowScriptParamService.getModelMapper().list(new HumanCondition());

        
        Set<String> flowIds = scriptParamModelList.stream().map(FlowScriptParamModel::getFlowId).collect(Collectors.toSet());

        
        List<FlowInfoModel> unInitialedModels = flowInfoModelList.stream().filter(info -> !flowIds.contains(info.getId())).collect(Collectors.toList());
        if(unInitialedModels.isEmpty()){
            log.info("all default flow script params are valid, ignore");
            return;
        }

        
        addDefaultScriptParams(unInitialedModels);
    }

    
    private boolean doImport(File file) {
        LinkedList<File> list = new LinkedList<>();
        if (file.exists()) {
            if (null == file.listFiles()) {
                log.error("文件目录为空");
                return false;
            }

            list.addAll(Arrays.asList(file.listFiles()));
            if (CollectionUtils.isNotEmpty(list)) {
                String firstTitle = list.get(0).getName().split(BaseConstants.SPOT)[0];
                FlowInfoModel existModel = this.getModelMapper().selectByTitle(firstTitle);
                if (Objects.nonNull(existModel)) {
                    log.info("流量文件已导入:{}", existModel.getTitle());
                    return true;
                }
                List<FlowInfoModel> flowInfoModelList = new ArrayList<>();

                list.forEach(flowFile -> {
                    if (flowFile.getName().endsWith(".pcap") || flowFile.getName().endsWith(".sh") || flowFile.getName().endsWith(".py")) {
                        String fileName = flowFile.getName().split(BaseConstants.SPOT)[0];
                        FlowInfoModel flowInfoModel = new FlowInfoModel();
                        String flowId = DigestUtil.md5Hex(fileName);
                        flowInfoModel.setId(flowId);
                        flowInfoModel.setTitle(fileName);
                        flowInfoModel.setFileName(flowFile.getName());
                        String fdfsPath = FsCommon.upload(flowFile.getAbsolutePath());
                        if (flowFile.getName().endsWith(".pcap")) {
                            
                            flowInfoModel.setResourceType(ResourceTypeEnum.PACP.toValue());
                            flowInfoModel.setInteractiveType(InteractiveTypeEnum.BACKGROUND.toValue());
                            flowInfoModel.setPcapFile(fdfsPath);
                        } else {
                            
                            flowInfoModel.setResourceType(ResourceTypeEnum.SCRIPT.toValue());
                            flowInfoModel.setInteractiveType(InteractiveTypeEnum.ATTACK.toValue());
                            flowInfoModel.setScriptFile(fdfsPath);
                        }
                        flowInfoModel.setFlowDesc(fileName);
                        flowInfoModel.setCategory(2);
                        flowInfoModelList.add(flowInfoModel);
                    } else {
                        log.info("过滤非脚本文件:{}", flowFile.getName());
                    }
                });

                if (CollectionUtils.isNotEmpty(flowInfoModelList)) {
                    bulkCreate(flowInfoModelList);

                    if (!flowInfoModelList.get(0).getFileName().endsWith(".pcap")) {
                        
                        addDefaultScriptParams(flowInfoModelList);
                    }
                }
            }
        } else {
            log.error("文件目录不存在!");
            return false;
        }

        return true;
    }

    private void addDefaultScriptParams(List<FlowInfoModel> flowInfoModelList) {
        List<FlowScriptParamModel> scriptParamModelList = new ArrayList<>();
        flowInfoModelList.forEach(flowInfo -> {
            log.info("build default script params of flow {}/{}", flowInfo.getId(), flowInfo.getTitle());
            FlowInfoModel flowInfoModel = this.selectByTitle(flowInfo.getTitle());
            FlowScriptParamModel scriptParamIp = new FlowScriptParamModel();
            scriptParamIp.setFlowId(flowInfoModel.getId());
            scriptParamIp.setParamKey("ip");
            scriptParamIp.setParamType(1);
            scriptParamIp.setParamDesc(MessageCommon.get("flow.error_code.target_ip"));
            scriptParamModelList.add(scriptParamIp);

            FlowScriptParamModel scriptParamPort = new FlowScriptParamModel();
            scriptParamPort.setFlowId(flowInfoModel.getId());
            scriptParamPort.setParamKey("port");
            scriptParamPort.setParamValue("80");
            scriptParamPort.setParamType(2);
            scriptParamPort.setParamDesc(MessageCommon.get("flow.error_code.target_port"));
            scriptParamModelList.add(scriptParamPort);
        });

        flowScriptParamService.bulkCreate(scriptParamModelList);
    }

    
    public ServiceResult<Boolean> flowImport(ImportFlowInfoRequest request) {
        String filePath = request.getPath();
        File fileFolder = new File(filePath);
        if (fileFolder.exists()) {
            File[] flowFiles = fileFolder.listFiles();
            if (ArrayUtil.isEmpty(flowFiles)) {
                log.error("file folder is empty");
                return ResultCommon.fail("file folder is empty");
            }

            
            List<String> readmeList = new ArrayList<>();
            
            File pcapFile = null;
            for (File flowFile : flowFiles) {
                if (flowFile.isDirectory()) {
                    File[] pcapFiles = flowFile.listFiles();
                    if (ArrayUtil.isEmpty(pcapFiles)) {
                        log.error("flow info folder is empty");
                        return ResultCommon.fail("flow info folder is empty");
                    }
                    pcapFile = pcapFiles[0];
                } else {
                    if (flowFile.getName().contains("README.md")) {
                        
                        File readmeFile = flowFile;
                        FileReader fileReader = null;
                        BufferedReader bufferedReader = null;
                        try {
                            fileReader = new FileReader(readmeFile);
                            bufferedReader  = new BufferedReader(fileReader);
                            String s;
                            while ((s = bufferedReader.readLine()) != null) {
                                readmeList.add(s);
                            }
                            
                            readmeList = readmeList.stream().filter(text -> StringUtils.isNotBlank(text)).collect(Collectors.toList());
                        } catch (FileNotFoundException e) {
                            log.error("flow info import error for FileNotFoundException, {}", ExceptionUtils.getStackTrace(e));
                            return ResultCommon.fail("flow file import error");
                        } catch (IOException e) {
                            log.error("flow info import error for IOException, {}", ExceptionUtils.getStackTrace(e));
                            return ResultCommon.fail("flow file import error");
                        } finally {
                            IOUtils.closeQuietly(fileReader);
                            IOUtils.closeQuietly(bufferedReader);
                        }
                    }
                }
            }

            if (Objects.isNull(pcapFile)) {
                log.error("flow info file is empty");
                return ResultCommon.fail("flow info file is empty");
            }

            FlowInfoModel flowInfoModel = new FlowInfoModel();
            String fileName = readmeList.get(1);
            flowInfoModel.setTitle(fileName.split(BaseConstants.SPOT)[0]);

            
            FlowInfoModel existFlowInfoModel = getModelMapper().selectByTitle(fileName);
            if (Objects.nonNull(existFlowInfoModel)) {
                
                this.deleteFlowInfo(existFlowInfoModel.getId(), false);
            }

            flowInfoModel.setFileName(pcapFile.getName());
            String fdfsPath = FsCommon.upload(pcapFile.getAbsolutePath());
            flowInfoModel.setFlowDesc(readmeList.get(3));
            boolean isContainType = readmeList.get(10).contains("类型");
            int diffCount = 0;
            if (!isContainType) {
                diffCount = 2;
                
                flowInfoModel.setCategory(2);
            } else {
                String categoryName = readmeList.get(11);
                flowInfoModel.setCategory(FlowCategory.flowCategoryMap.get(categoryName));
            }

            
            boolean isPcap = pcapFile.getName().endsWith("pcap");
            if (isPcap) {
                flowInfoModel.setResourceType(ResourceTypeEnum.PACP.toValue());
                flowInfoModel.setPcapFile(fdfsPath);
            } else {
                flowInfoModel.setResourceType(ResourceTypeEnum.SCRIPT.toValue());
                flowInfoModel.setScriptFile(fdfsPath);
            }
            boolean isBackFlow = readmeList.get(13 - diffCount).contains("背景流量");
            if (isBackFlow) {
                
                flowInfoModel.setInteractiveType(InteractiveTypeEnum.BACKGROUND.toValue());
            } else {
                
                flowInfoModel.setInteractiveType(InteractiveTypeEnum.ATTACK.toValue());
            }
            this.create(flowInfoModel);

            if (!isPcap) {
                
                List<FlowScriptParamModel> scriptParamModelList = new ArrayList<>();
                
                int countParam = readmeList.size() - (15 - diffCount);
                for (int i = 0; i < countParam; i++) {
                    FlowScriptParamModel scriptParam = new FlowScriptParamModel();
                    
                    String strParam = readmeList.get((15 - diffCount) + i);
                    if (StringUtils.isNotBlank(strParam)) {
                        
                        strParam = strParam.substring(1);
                        String[] parmaArray = strParam.split("\\s\\|\\s");
                        if (parmaArray.length != 4) {
                            log.error("script param parse error");
                            return ResultCommon.fail("script param parse error");
                        }
                        scriptParam.setFlowId(flowInfoModel.getId());
                        scriptParam.setParamKey(parmaArray[0].trim());
                        scriptParam.setParamType(FlowCategory.paramTypeMap.get(parmaArray[1].trim()));
                        scriptParam.setParamDesc(parmaArray[2].trim());
                        if (!ParamTypeEnum.IP.toString().equalsIgnoreCase(parmaArray[0].trim())) {
                            scriptParam.setParamValue(parmaArray[3].trim());
                        }
                        scriptParamModelList.add(scriptParam);
                    }
                }

                if (CollectionUtils.isNotEmpty(scriptParamModelList)) {
                    flowScriptParamService.bulkCreate(scriptParamModelList);
                }
            } else {
                FlowTaskPool.getInstance().executeTask(() -> this.initPcapProtocols(flowInfoModel));
            }
        } else {
            log.error("file floder is not exist");
            return ResultCommon.fail("file folder is not exist");
        }

        return ResultCommon.success(true);
    }


    
    public ServiceResult<List<String>> checkNamesExist(CheckNamesRequest request) {
        List<String> existNameList = new ArrayList<>();
        List<FlowInfoModel> flowInfoModels = getModelMapper().selectByTitles(request.getFileNames());
        if (CollectionUtils.isNotEmpty(flowInfoModels)) {
            existNameList = flowInfoModels.stream().map(FlowInfoModel::getTitle).collect(Collectors.toList());
        }

        return ResultCommon.success(existNameList);
    }


    
    public List<ProtocolBO> pcapAnalyse(MultipartFile multipartFile) {
        List<ProtocolBO> protocolList = new ArrayList<>();
        String fileMd5 = null;
        try {
            fileMd5 = DigestUtils.md5Hex(multipartFile.getBytes());
            if (org.apache.commons.lang3.StringUtils.isNotBlank(fileMd5)) {
                String pcapMd5Result = FlowCacheUtil.pcapAnalyseCache.getIfPresent(fileMd5);
                if (org.apache.commons.lang3.StringUtils.isNotBlank(pcapMd5Result)) {
                    log.info("get file analyse result from cache: {}", pcapMd5Result);
                    Map proMap = JSON.parseObject(pcapMd5Result);
                    for (Object key : proMap.keySet()) {
                        ProtocolBO protocol = new ProtocolBO();
                        protocol.setRate(new BigDecimal(proMap.get(key).toString()));
                        protocol.setProtocolName(key.toString());
                        String desc = MessageCommonUtil.convertMessageWithNull(key.toString().toLowerCase());
                        if (org.apache.commons.lang3.StringUtils.isBlank(desc)) {
                            
                            desc = MessageCommonUtil.convertMessageWithNull(FlowConstant.FLOW_UNKNOWN_PROTOCOL);
                        }
                        protocol.setDesc(desc);
                        protocolList.add(protocol);
                    }

                    
                    this.sortProtocol(protocolList);
                    return protocolList;
                }
            }
        } catch (Exception e) {
            log.error("get pcap file md5 error", e);
        }

        String strProtocols;
        String scriptPath = PRE_PATH.concat("/protocol_analyse.py");
        String pcapPath = "";
        OutputStream out = null;
        BufferedReader input = null;
        File tmpFile = null;

        try {
            String fileName = System.currentTimeMillis() + BaseConstants.SHORT_DASH + multipartFile.getOriginalFilename();
            pcapPath = PRE_PATH.concat("/" + System.currentTimeMillis() + "_" + fileName);
            tmpFile = new File(pcapPath);
            out = new FileOutputStream(tmpFile);
            byte[] ss = multipartFile.getBytes();
            for (byte s : ss) {
                out.write(s);
            }

            String pythonCmd = String.format("python3 %s %s", scriptPath, pcapPath);
            log.info("============python analyse cmd: {}", pythonCmd);

            Process process = Runtime.getRuntime().exec(pythonCmd);
            process.waitFor();
            strProtocols = "";
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = input.readLine()) != null) {
                strProtocols = line;
            }
            log.info("============pcapRead result: {}", strProtocols);

            if (org.apache.commons.lang3.StringUtils.isNotBlank(strProtocols)) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(fileMd5)) {
                    FlowCacheUtil.pcapAnalyseCache.put(fileMd5, strProtocols);
                }

                Map proMap = JSON.parseObject(strProtocols);
                for (Object key : proMap.keySet()) {
                    ProtocolBO protocol = new ProtocolBO();
                    protocol.setProtocolName(key.toString());
                    String desc = MessageCommonUtil.convertMessageWithNull(key.toString().toLowerCase());
                    if (org.apache.commons.lang3.StringUtils.isBlank(desc)) {
                        
                        desc = MessageCommonUtil.convertMessageWithNull(FlowConstant.FLOW_UNKNOWN_PROTOCOL);
                    }
                    protocol.setDesc(desc);
                    protocol.setRate(new BigDecimal(proMap.get(key).toString()));
                    protocolList.add(protocol);
                }
            }
        } catch (Exception e) {
            log.error("exec error:", e);
        } finally {
            org.apache.poi.util.IOUtils.closeQuietly(out);
            org.apache.poi.util.IOUtils.closeQuietly(input);

            if (Objects.nonNull(tmpFile) && tmpFile.exists()) {
                try {
                    tmpFile.delete();
                } catch (Exception e) {
                    log.error(pcapPath + "delete error");
                }
            }
        }

        
        this.sortProtocol(protocolList);
        return protocolList;
    }

    
    public void initPcapAnalyse() {
        log.info("start init pcap analyse");
        
        flowPcapService.deleteAll();
        flowProtocolService.deleteAll();

        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(FlowInfoModel::getResourceType, ResourceTypeEnum.PACP.toValue());
        List<FlowInfoModel> flowInfoModelList = this.getModelMapper().list(humanCondition);
        if (CollectionUtils.isNotEmpty(flowInfoModelList)) {
            List<Runnable> tasks = new ArrayList<>();
            flowInfoModelList.forEach(flowInfoModel -> tasks.add(() -> initPcapProtocols(flowInfoModel)));
            TaskCommon.runTasks(tasks, true, true);
        }
        log.info("end init pcap analyse");
    }

    
    public void replacePcapPath() {
        log.info("start init pcap replace path");
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(FlowInfoModel::getResourceType, ResourceTypeEnum.PACP.toValue());
        List<FlowInfoModel> flowInfoModelList = this.getModelMapper().list(humanCondition);
        if (CollectionUtils.isNotEmpty(flowInfoModelList)) {
            
            List<FlowPcapModel> flowPcapModels = flowPcapService.listAll();
            if (CollectionUtils.isNotEmpty(flowPcapModels)) {
                flowPcapModels.forEach(flowPcapModel -> flowInfoModelList.forEach(flowInfoModel -> {
                    if (flowPcapModel.getFlowId().equals(flowInfoModel.getId())) {
                        flowPcapModel.setPcapFile(flowInfoModel.getPcapFile());
                    }
                }));
                flowPcapService.bulkUpdate(flowPcapModels);
            }
        }
        log.info("end init pcap replace path");
    }

    
    private void initPcapProtocols(FlowInfoModel flowInfoModel) {
        String title = flowInfoModel.getTitle();
        log.info("start init pcap protocols for {}", title);
        List<ProtocolBO> protocolList = new ArrayList<>();
        String strProtocols = "";
        String scriptPath = PRE_PATH.concat("/protocol_analyse.py");
        String pcapPath = flowInfoModel.getPcapFile();
        BufferedReader input = null;
        File srcFile = null;

        try {
            byte[] flowBytes = FsCommon.downloadRawContent(pcapPath);
            String fileName = FilenameUtils.getName(pcapPath);;
            String srcPcapPath = PRE_PATH.concat("/" + title + "_" + fileName);
            log.info("===========srcPcapPath: {}", srcPcapPath);
            srcFile = new File(srcPcapPath);
            try (FileOutputStream fs = new FileOutputStream(srcFile)) {
                fs.write(flowBytes);
            }

            String pythonCmd = String.format("python3 %s %s", scriptPath, srcPcapPath);
            log.info("============{} python analyse cmd: {}", title, pythonCmd);

            strProtocols = this.executePython(pythonCmd, title);
            log.info("============{} init pcap analyse pcapRead result: {}", title, strProtocols);

            String fileMd5 = DigestUtils.md5Hex(flowBytes);
            if (org.apache.commons.lang3.StringUtils.isNotBlank(strProtocols)) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(fileMd5)) {
                    FlowCacheUtil.pcapAnalyseCache.put(fileMd5, strProtocols);
                }

                Map proMap = JSON.parseObject(strProtocols);
                for (Object key : proMap.keySet()) {
                    ProtocolBO protocol = new ProtocolBO();
                    protocol.setProtocolName(key.toString());
                    String desc = MessageCommonUtil.convertMessageWithNull(key.toString().toLowerCase());
                    if (org.apache.commons.lang3.StringUtils.isBlank(desc)) {
                        
                        desc = MessageCommonUtil.convertMessageWithNull(FlowConstant.FLOW_UNKNOWN_PROTOCOL);
                    }
                    protocol.setDesc(desc);
                    protocol.setRate(new BigDecimal(proMap.get(key).toString()));
                    protocolList.add(protocol);
                }
            }
        } catch (Exception e) {
            log.error("init pcap protocols exec error for {}:", title, e);
            return;
        } finally {
            org.apache.poi.util.IOUtils.closeQuietly(input);

            if (Objects.nonNull(srcFile) && srcFile.exists()) {
                try {
                    srcFile.delete();
                } catch (Exception e) {
                    log.error("tmp pcap file delete error");
                }
            }
        }

        
        this.sortProtocol(protocolList);

        
        this.initSaveProtcols(protocolList, flowInfoModel.getId(), flowInfoModel.getPcapFile());
        log.info("end init pcap protocols for {}", title);
    }

    
    private String executePython(String command, String title) {
        String result = "";
        try {
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result = line;
                    log.info("{} execute python success: {}", title, line);
                }
            } else {
                
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String errorLine;
                StringBuilder errorBuilder = new StringBuilder();
                while ((errorLine = errorReader.readLine()) != null) {
                    errorBuilder.append(errorLine + "  ");
                }
                log.error("{} execute python error: {}", title, errorBuilder);
            }
        } catch (Exception e) {
            log.error("{} execute python error", title, e);
        }
        return result;
    }

    
    private void initSaveProtcols(List<ProtocolBO> protocolList, String flowId, String filePath) {
        if (CollectionUtils.isNotEmpty(protocolList)) {
            List<FlowProtocolModel> flowProtocolModels = new ArrayList<>();

            
            List<String> filterProtocols = new ArrayList<>();
            
            for (ProtocolBO flowProtocolBO : protocolList) {
                FlowProtocolModel flowProtocolModel = new FlowProtocolModel();
                BeanUtils.copyProperties(flowProtocolBO, flowProtocolModel);
                flowProtocolModel.setFlowId(flowId);
                flowProtocolModels.add(flowProtocolModel);

                if (1 == flowProtocolBO.getMarked()) {
                    filterProtocols.add(flowProtocolBO.getProtocolName());
                }
            }
            flowProtocolService.bulkCreate(flowProtocolModels);

            
            this.pcapFilter(flowId, filePath, filterProtocols);
        }
    }

    private List<ProtocolBO> sortProtocol(List<ProtocolBO> protocolList) {
        if (CollectionUtils.isNotEmpty(protocolList)) {
            Collections.sort(protocolList);

            
            if (protocolList.size() == 11) {
                int otherIndex = 0;
                for (int i = 0; i < protocolList.size(); i++) {
                    if ("other".equals(protocolList.get(i).getProtocolName())) {
                        otherIndex = i;
                        break;
                    }
                }

                ProtocolBO bo = protocolList.get(otherIndex);
                protocolList.remove(otherIndex);
                protocolList.add(bo);
            }
        }

        return protocolList;
    }

    public boolean checkCategoryUsed(List<String> categoryList) {
        log.info("FlowInfoServiceImpl.checkCategoryUsed begin. categoryList:{}", categoryList);
        if (CollectionUtils.isEmpty(categoryList)) {
            return false;
        }
        HumanCondition condition = new HumanCondition();
        condition.filterIn(FlowInfoModel::getCategory, categoryList.toArray(new String[categoryList.size()]));
        List<FlowInfoModel> flowInfoModelList = getModelMapper().list(condition);
        if (CollectionUtils.isNotEmpty(flowInfoModelList)) {
            return true;
        }
        HumanCondition conditionCollection = new HumanCondition();
        conditionCollection.filterIn(FlowCollectionModel::getCategory, categoryList.toArray(new String[categoryList.size()]));
        List<FlowCollectionModel> flowCollectionModelList = flowCollectionMapper.list(conditionCollection);
        if (CollectionUtils.isNotEmpty(flowCollectionModelList)) {
            return true;
        }
        log.info("FlowInfoServiceImpl.checkCategoryUsed end.");
        return false;
    }

    
    public void pcapFilter(String flowId, String pcapPath, List<String> protocolList) {
        if (CollectionUtils.isEmpty(protocolList)) {
            log.error("protocolList to filter is empty");
            return;
        }
        String outPcapPath = "";
        String scriptPath = PRE_PATH.concat("/protocol_filter.py");
        BufferedReader input = null;
        File srcFile = null;
        File dstFile = null;
        String strProtocols = org.apache.commons.lang3.StringUtils.join(protocolList, BaseConstants.COMMA);

        try {
            
            byte[] pcapByte = FsCommon.downloadRawContent(pcapPath);
            String fileName = FilenameUtils.getName(pcapPath);;
            String srcPcapPath = PRE_PATH.concat("/" + System.currentTimeMillis() + "_" + fileName);
            srcFile = new File(srcPcapPath);
            try (FileOutputStream fs = new FileOutputStream(srcFile)) {
                fs.write(pcapByte);
            }

            
            String pythonCmd = String.format("python3 %s %s %s", scriptPath, srcPcapPath, strProtocols);
            log.info("============python filter cmd: {}", pythonCmd);
            Process process = Runtime.getRuntime().exec(pythonCmd);
            process.waitFor();
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            String filterResult = "";
            while ((line = input.readLine()) != null) {
                filterResult = line;
            }

            
            log.info("============pcap filter result: {}", filterResult);

            if (StringUtils.isBlank(filterResult)) {
                StringBuilder errorBuilder = new StringBuilder();
                try (InputStream errorStream = process.getErrorStream();
                     InputStreamReader isr = new InputStreamReader(errorStream);
                     BufferedReader br = new BufferedReader(isr)) {
                    String errorResult;
                    while ((errorResult = br.readLine()) != null) {
                        errorBuilder.append(errorResult + " ");
                    }
                    log.error("pcap file filter failed: {}", errorBuilder);
                    return;
                }
            }

            
            JSONObject filterObj = JSONObject.parseObject(filterResult);
            Double duration = filterObj.getDoubleValue("duration");
            Integer pktNum = filterObj.getInteger("count");
            String dstPcapPath = filterObj.getString("dstPath");
            Long pcapSize = filterObj.getLongValue("fileSize");

            dstFile = new File(dstPcapPath);
            
            outPcapPath = FsCommon.upload(dstPcapPath);

            FlowPcapModel pcapModel = flowPcapService.getByFlowId(flowId);
            if (Objects.nonNull(pcapModel)) {
                pcapModel.setPcapFile(outPcapPath);
                pcapModel.setPcapSize(pcapSize);
                pcapModel.setPktNum(pktNum.longValue());
                pcapModel.setDuration(Math.round(Math.abs(duration) * 1000));
                pcapModel.setProtocols(String.join(BaseConstants.COMMA, protocolList));
                pcapModel.setUpdateTime(LocalDateTime.now());
                flowPcapService.update(pcapModel);
            } else {
                
                FlowPcapModel flowPcapModel = FlowPcapModel.builder()
                        .flowId(flowId)
                        .pcapFile(outPcapPath)
                        .pcapSize(pcapSize)
                        .pktNum(pktNum.longValue())
                        .duration(Math.round(duration * 1000))
                        .protocols(String.join(BaseConstants.COMMA, protocolList))
                        .build();
                flowPcapService.create(flowPcapModel);
            }

        } catch (Exception e) {
            log.error("pcap file filter error", e);
        } finally {
            if (Objects.nonNull(srcFile) && srcFile.exists()) {
                try {
                    srcFile.delete();
                } catch (Exception e) {
                    log.error("pcap src file delete error");
                }
            }

            if (Objects.nonNull(dstFile) && dstFile.exists()) {
                try {
                    dstFile.delete();
                } catch (Exception e) {
                    log.error("pcap dst file delete error");
                }
            }
        }
    }

}
