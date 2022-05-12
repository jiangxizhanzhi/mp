package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.config.FastDfsConfig;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.*;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.enums.InteractiveTypeEnum;
import cn.cyberpeace.mp.flow.enums.ParamTypeEnum;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.model.FlowCollectionRelModel;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 流量service
 * @Author qiaokang
 * @Date：2021/6/10 10:31
 */
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
    private FastDfsConfig fastDfsConfig;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private FlowCollectionRelMapper flowCollectionRelMapper;

    @Autowired
    private AsyncPlatFlowTaskService asyncPlatFlowTaskService;

    @Autowired
    private FlowCollectionServiceImpl flowCollectionService;

    @Autowired
    private FlowScriptParamServiceImpl flowScriptParamService;

    @Autowired
    private FlowCollectionRelServiceImpl flowCollectionRelService;

    /**
     * 预制流量文件目录
     */
    private static final String PRE_PATH = "/root/.mp/resources/flows";

    /**
     * 补充list查询条件
     * @param humanCondition
     * @param queryParams
     */
    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, FlowInfoQueryParams queryParams) {
        if (Objects.nonNull(queryParams.getCategory())) {
            humanCondition.filterEqual("category", queryParams.getCategory());
        }

        if (Objects.nonNull(queryParams.getInteractiveType())) {
            humanCondition.filterEqual("interactiveType", queryParams.getInteractiveType());
        }
    }

    /**
     * 删除流量信息
     *
     * @param id
     * @return
     */
    @Transactional
    public ServiceResult<Boolean> deleteFlowInfo(String id) {
        // 判断流量任务是否关联该流量信息
        List<FlowTaskModel> flowTaskList = flowTaskMapper.listByFlowIds(Collections.singletonList(id));
        if (CollectionUtils.isNotEmpty(flowTaskList)) {
            return ServiceResult.fail(FlowErrorCode.TASK_DELETE_FAILED);
        }

        // 判断流量是否关联流量集合信息
        List<FlowCollectionRelModel> flowCollectionRelModels =
                flowCollectionRelMapper.selectByFlowIds(Collections.singletonList(id));
        if (CollectionUtils.isNotEmpty(flowCollectionRelModels)) {
            // 遍历删除流量集合关联信息
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
            // 异步删除流量文件
            asyncPlatFlowTaskService.deleteFlowFile(Collections.singletonList(flowInfoModel));

            // 攻击流量，根据流量id删除对应的脚本参数信息
            if (InteractiveTypeEnum.ATTACK.toValue() == flowInfoModel.getInteractiveType()) {
                flowScriptParamService.deleteByFlowId(id);
            }

            // 删除流量信息
            return delete(id);
        } else {
            log.error("flow file to delete not exist");
        }

        return ServiceResult.success(false);
    }

    /**
     * 批量删除流量信息
     *
     * @param ids
     * @return
     */
    @Transactional
    public ServiceResult<Integer> batchDeleteFlowInfo(String[] ids) {
        List<String> idList = Arrays.asList(ids);
        // 判断流量任务是否关联该流量信息
        List<FlowTaskModel> flowTaskList = flowTaskMapper.listByFlowIds(Arrays.asList(ids));
        if (CollectionUtils.isNotEmpty(flowTaskList)) {
            log.info("flow info related flow task can not delete: {}", flowTaskList);
            return ServiceResult.fail(FlowErrorCode.TASK_DELETE_FAILED);
        }

        // 判断流量是否关联流量集合信息
        List<FlowCollectionRelModel> flowCollectionRelModels =
                flowCollectionRelMapper.selectByFlowIds(idList);
        if (CollectionUtils.isNotEmpty(flowCollectionRelModels)) {
            // 按流量集合id分组关联信息
            Map<String, List<String>> relMap =
                    flowCollectionRelModels.stream().collect(Collectors.groupingBy(FlowCollectionRelModel::getCollectionId,
                            Collectors.mapping(FlowCollectionRelModel::getFlowId, Collectors.toList())));
            // 遍历删除流量集合关联信息
            relMap.forEach((collectionId, flowIds) -> {
                DelFlowCollectionRelRequest delRelRequest = new DelFlowCollectionRelRequest();
                delRelRequest.setCollectionId(collectionId);
                delRelRequest.setFlowIds(flowIds);
                flowCollectionService.delCollectionRel(delRelRequest);
            });
        }

        List<FlowInfoModel> flowInfoModels = this.selectByFlowIds(idList);
        if (CollectionUtils.isNotEmpty(flowInfoModels)) {
            // 异步删除流量文件
            asyncPlatFlowTaskService.deleteFlowFile(flowInfoModels);

            // 过滤攻击流量
            List<FlowInfoModel> attackFlows =
                    flowInfoModels.stream().filter(flow -> InteractiveTypeEnum.ATTACK.toValue() == flow.getInteractiveType()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(attackFlows)) {
                List<String> attackFlowIds = attackFlows.stream().map(FlowInfoModel::getId).collect(Collectors.toList());
                // 根据流量id批量删除对应的脚本参数信息
                flowScriptParamService.deleteByFlowIds(attackFlowIds);
            }

            // 批量删除流量信息
            return bulkDelete(ids);
        } else {
            log.error("flow files to delete not exist");
        }

        return ServiceResult.success(0);
    }

    /**
     * 分页查询流量信息
     * @param queryParams
     * @return
     */
    public ListObjServiceResult<FlowInfoResponse> pageFlowInfo(FlowInfoQueryParams queryParams) {
        // 查询流量信息
        ListObjServiceResult<FlowInfoModel> listObjServiceResult = list(queryParams);

        // 结果转换
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
                                        // ip、port的参数说明进行国际化处理
                                        if ("ip".equalsIgnoreCase(scriptParam.getParamKey())) {
                                            scriptParam.setParamDesc(MessageCommon.get("flow.error_code.target_ip"));
                                        } else if ("port".equalsIgnoreCase(scriptParam.getParamKey())) {
                                            scriptParam.setParamDesc(MessageCommon.get("flow.error_code.target_port"));
                                        }

                                        return scriptParam;
                                    }).filter(param -> model.getId().equals(param.getFlowId())).collect(Collectors.toList());
                        }
                        FlowInfoResponse response = new FlowInfoResponse(model, currentFlowParamModels);
                        return response;
                    })
                    .collect(Collectors.toList());
        }

        return new ListObjServiceResult<>(new ListObjResult<>(responseList, listObjServiceResult.getSource().getTotal())).asResult();
    }

    /**
     * 根据集合id批量查询
     * @param request
     * @return
     */
    public ServiceResult<List<FlowInfoResponse>> listByCollectionIds(ListByCollectionIdsRequest request) {
        // 查询流量集合关联流量信息
        List<FlowCollectionRelModel> flowCollectionRelModels =
                flowCollectionRelService.selectByCollectionIds(request.getCollectionIds());
        if (CollectionUtils.isEmpty(flowCollectionRelModels)) {
            return ServiceResult.success(new ArrayList<>());
        }

        List<String> flowIds =
                flowCollectionRelModels.stream().map(FlowCollectionRelModel::getFlowId).collect(Collectors.toList());
        FlowInfoQueryParams queryParams = new FlowInfoQueryParams();
        queryParams.setIds(flowIds.toArray(new String[0]));
        ListObjServiceResult<FlowInfoResponse> listObjServiceResult = pageFlowInfo(queryParams);

        return ServiceResult.success(listObjServiceResult.getSource().getList());
    }

    /**
     * 背景流量详情
     * @param id
     * @return
     */
    public ServiceResult<BackgroundFlowResponse> queryBackgroundDetail(String id) {
        BackgroundFlowResponse response;
        FastDfsConfig.Group group = fastDfsConfig.getGroupByName(FdfsGroupEnum.DEFAULT);
        String prefixUrl = group.getInnerFileServerAddr() + File.separator;

        ObjResult<FlowInfoModel> objResult = get(id).getSource();
        if (Objects.nonNull(objResult)) {
            FlowInfoModel flowInfoModel = objResult.getObj();
            if (InteractiveTypeEnum.BACKGROUND.toValue() != flowInfoModel.getInteractiveType()) {
                return ServiceResult.fail(FlowErrorCode.NOT_BACKGROUND_FLOW);
            }
            response = new BackgroundFlowResponse(flowInfoModel, prefixUrl);
        } else {
            return ServiceResult.fail(FlowErrorCode.FLOW_NOT_EXIST);
        }

        return ServiceResult.success(response);
    }

    /**
     * 攻击脚本详情
     * @param id
     * @return
     */
    public ServiceResult<AttackFlowResponse> queryAttackDetail(String id) {
        AttackFlowResponse response;
        FastDfsConfig.Group group = fastDfsConfig.getGroupByName(FdfsGroupEnum.DEFAULT);
        String prefixUrl = group.getInnerFileServerAddr() + File.separator;

        ObjResult<FlowInfoModel> objResult = get(id).getSource();
        if (Objects.nonNull(objResult)) {
            FlowInfoModel flowInfoModel = objResult.getObj();
            if (InteractiveTypeEnum.ATTACK.toValue() != flowInfoModel.getInteractiveType()) {
                return ServiceResult.fail(FlowErrorCode.NOT_ATTACK_FLOW);
            }

            // 根据流量id查询脚本参数
            List<FlowScriptParamModel> flowScriptParamModels =
                    flowScriptParamService.selectByFlowId(flowInfoModel.getId());
            flowScriptParamModels.stream().map(scriptParam -> {
                // ip、port的参数说明进行国际化处理
                if ("ip".equalsIgnoreCase(scriptParam.getParamKey())) {
                    scriptParam.setParamDesc(MessageCommon.get("flow.error_code.target_ip"));
                } else if ("port".equalsIgnoreCase(scriptParam.getParamKey())) {
                    scriptParam.setParamDesc(MessageCommon.get("flow.error_code.target_port"));
                }

                return scriptParam;
            }).collect(Collectors.toList());
            response = new AttackFlowResponse(flowInfoModel, prefixUrl, flowScriptParamModels);

            // 下载攻击脚本
            byte[] fileByte = fastDFSClient.download(flowInfoModel.getScriptFile(), FdfsGroupEnum.DEFAULT.toString());
            String code = new String(fileByte, StandardCharsets.UTF_8);
            response.setCode(code);
        } else {
            return ServiceResult.fail(FlowErrorCode.FLOW_NOT_EXIST);
        }

        return ServiceResult.success(response);
    }

    /**
     * 新增背景流量
     * @param addRequest
     * @return
     */
    public ServiceResult<BackgroundFlowResponse> addBackgroundFlow(AddBackgroundFlowRequest addRequest) {
        if (!checkNameUnique(addRequest.getTitle(), org.apache.commons.lang3.StringUtils.EMPTY)) {
            return ServiceResult.fail(FlowErrorCode.TITLE_UNIQUE);
        }

        if (!addRequest.getFileName().endsWith(".pcap")) {
            return ServiceResult.fail(FlowErrorCode.NOT_PCAP_FILE);
        }

        // 上传文件获取路径
        String filePath = fastDFSClient.upload(addRequest.getPcapFile(), null, FdfsGroupEnum.DEFAULT.toString());
        addRequest.setPcapFilePath(filePath);

        // 请求对象转换
        FlowInfoModel flowInfoModel = this.getFlowInfoModel(addRequest);
        // 保存流量信息
        ObjServiceResult<FlowInfoModel> serviceResult = create(flowInfoModel);

        FastDfsConfig.Group group = fastDfsConfig.getGroupByName(FdfsGroupEnum.DEFAULT);
        String prefixUrl = group.getInnerFileServerAddr() + File.separator;
        // 转换返回对象
        BackgroundFlowResponse response = new BackgroundFlowResponse(serviceResult.getSource().getObj(), prefixUrl);
        return ServiceResult.success(response);
    }

    /**
     * 新增攻击流量
     * @param addRequest
     * @return
     */
    @Transactional
    public ServiceResult<AttackFlowResponse> addAttackFlow(AddAttackFlowRequest addRequest) {
        if (!checkNameUnique(addRequest.getTitle(), org.apache.commons.lang3.StringUtils.EMPTY)) {
            return ServiceResult.fail(FlowErrorCode.TITLE_UNIQUE);
        }

        // 解析攻击流量脚本参数
        if (StringUtils.isNotBlank(addRequest.getParameter())) {
            List<FlowScriptParamModel> scriptParamModels = new ArrayList<>();
            JSONObject paramObj = JSONObject.parseObject(addRequest.getParameter());
            paramObj.keySet().forEach(paramKey -> {
                FlowScriptParamModel paramModel = new FlowScriptParamModel();
                paramModel.setParamKey(paramKey);

                JSONArray paramArray = paramObj.getJSONArray(paramKey);
                // 参数类型
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

                // 参数值
                paramModel.setParamValue(paramArray.getString(1));
                // 参数描述
                paramModel.setParamDesc(paramArray.getString(2));

                // 参数校验
                if (paramKey.length() > 40 || paramModel.getParamValue().length() > 50 ||
                        paramModel.getParamDesc().length() > 2048) {
                    log.error("param length is invalid");
                    throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
                }

                scriptParamModels.add(paramModel);
            });

            addRequest.setScriptParamModels(scriptParamModels);
        }

        // 判断是否有文件名，没有则根据脚本语言默认生成一个
        String fileName;
        if (StringUtils.isBlank(addRequest.getFileName())) {
            fileName = TextUtils.uuid() + (addRequest.getSuffix() == 1 ? ".sh" : ".py");
            addRequest.setFileName(fileName);
        } else {
            fileName = addRequest.getFileName();
        }

        InputStream is = null;
        try {
            is = new ByteArrayInputStream(addRequest.getCode().getBytes());
            String filePath = fastDFSClient.upload(is, fileName, null, FdfsGroupEnum.DEFAULT.toString());
            addRequest.setFilePath(filePath);
        } catch (MpRuntimeException e) {
            log.error("add attack flow error", e);
            return ServiceResult.fail(FlowErrorCode.ADD_ATTACK_FLOW_ERROR);
        } finally {
            IOUtils.closeQuietly(is);
        }

        // 请求对象转换
        FlowInfoModel flowInfoModel = this.getFlowInfoModel(addRequest);
        // 保存流量信息
        ObjServiceResult<FlowInfoModel> serviceResult = create(flowInfoModel);
        FlowInfoModel resultModel = serviceResult.getSource().getObj();

        // 批量保存流量脚本参数信息
        List<FlowScriptParamModel> scriptParamModels = addRequest.getScriptParamModels();
        if (CollectionUtils.isNotEmpty(scriptParamModels)) {
            scriptParamModels.forEach(model -> model.setFlowId(resultModel.getId()));
            flowScriptParamService.bulkCreate(scriptParamModels);
        }

        FastDfsConfig.Group group = fastDfsConfig.getGroupByName(FdfsGroupEnum.DEFAULT);
        String prefixUrl = group.getInnerFileServerAddr() + File.separator;
        // 转换返回对象
        AttackFlowResponse response = new AttackFlowResponse(resultModel, prefixUrl, scriptParamModels);
        response.setCode(addRequest.getCode());
        return ServiceResult.success(response);
    }

    /**
     * 更新背景流量
     * @param modifyRequest
     * @return
     */
    public ServiceResult<BackgroundFlowResponse> modifyBackgroundFlow(ModifyBackgroundFlowRequest modifyRequest) {
        if (!checkNameUnique(modifyRequest.getTitle(), modifyRequest.getId())) {
            return ServiceResult.fail(FlowErrorCode.TITLE_UNIQUE);
        }

        if (StringUtils.isNotBlank(modifyRequest.getFileName()) && !modifyRequest.getFileName().endsWith(".pcap")) {
            return ServiceResult.fail(FlowErrorCode.NOT_PCAP_FILE);
        }

        ObjServiceResult<FlowInfoModel> oldServiceResult = get(modifyRequest.getId());
        if (Objects.isNull(oldServiceResult.getSource())) {
            // 判断流量是否存在
            return ServiceResult.fail(FlowErrorCode.FLOW_NOT_EXIST);
        } else {
            // 判断是否背景流量
            if (InteractiveTypeEnum.BACKGROUND.toValue() != oldServiceResult.getSource().getObj().getInteractiveType()) {
                return ServiceResult.fail(FlowErrorCode.NOT_BACKGROUND_FLOW);
            }
        }

        if (Objects.nonNull(modifyRequest.getPcapFile())) {
            // 上传文件获取路径
            String filePath = fastDFSClient.upload(modifyRequest.getPcapFile(), null, FdfsGroupEnum.DEFAULT.toString());
            modifyRequest.setPcapFilePath(filePath);
        }

        // 请求对象转换
        FlowInfoModel flowInfoModel = this.getFlowInfoModel(modifyRequest);
        // 更新流量信息
        ObjServiceResult<FlowInfoModel> serviceResult = update(flowInfoModel);

        FastDfsConfig.Group group = fastDfsConfig.getGroupByName(FdfsGroupEnum.DEFAULT);
        String prefixUrl = group.getInnerFileServerAddr() + File.separator;
        // 转换返回对象
        BackgroundFlowResponse response = new BackgroundFlowResponse(serviceResult.getSource().getObj(), prefixUrl);
        return ServiceResult.success(response);
    }

    /**
     * 更新攻击流量
     * @param modifyRequest
     * @return
     */
    @Transactional
    public ServiceResult<AttackFlowResponse> modifyAttackFlow(ModifyAttackFlowRequest modifyRequest) {
        if (!checkNameUnique(modifyRequest.getTitle(), modifyRequest.getId())) {
            return ServiceResult.fail(FlowErrorCode.TITLE_UNIQUE);
        }

        ObjServiceResult<FlowInfoModel> oldServiceResult = get(modifyRequest.getId());
        if (Objects.isNull(oldServiceResult.getSource())) {
            // 判断流量是否存在
            return ServiceResult.fail(FlowErrorCode.FLOW_NOT_EXIST);
        } else {
            // 判断是否攻击流量
            if (InteractiveTypeEnum.ATTACK.toValue() != oldServiceResult.getSource().getObj().getInteractiveType()) {
                return ServiceResult.fail(FlowErrorCode.NOT_ATTACK_FLOW);
            }
        }

        // 解析攻击流量脚本参数
        if (StringUtils.isNotBlank(modifyRequest.getParameter())) {
            List<FlowScriptParamModel> scriptParamModels = new ArrayList<>();
            JSONObject paramObj = JSONObject.parseObject(modifyRequest.getParameter());
            paramObj.keySet().forEach(paramKey -> {
                FlowScriptParamModel paramModel = new FlowScriptParamModel();
                paramModel.setParamKey(paramKey);

                JSONArray paramArray = paramObj.getJSONArray(paramKey);
                // 参数类型
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

                // 参数值
                paramModel.setParamValue(paramArray.getString(1));
                // 参数描述
                paramModel.setParamDesc(paramArray.getString(2));
                scriptParamModels.add(paramModel);
            });

            modifyRequest.setScriptParamModels(scriptParamModels);
        }

        if (StringUtils.isNotBlank(modifyRequest.getCode())) {
            // 判断是否有文件名，没有则根据脚本语言默认生成一个
            String fileName;
            if (StringUtils.isBlank(modifyRequest.getFileName())) {
                // 文件后缀
                String suffix = "";
                if (Objects.nonNull(modifyRequest.getSuffix())) {
                    suffix = modifyRequest.getSuffix() == 1 ? ".sh" : ".py";
                } else {
                    // 没变更suffix时，根据文件名后缀判断之前类型
                    suffix = oldServiceResult.getSource().getObj().getScriptFile().endsWith(".sh") ? ".sh" : ".py";
                }
                fileName = TextUtils.uuid() + suffix;
                modifyRequest.setFileName(fileName);
            } else {
                fileName = modifyRequest.getFileName();
            }

            InputStream is = null;
            try {
                is = new ByteArrayInputStream(modifyRequest.getCode().getBytes(StandardCharsets.UTF_8));
                String filePath = fastDFSClient.upload(is, fileName, null, FdfsGroupEnum.DEFAULT.toString());
                modifyRequest.setFilePath(filePath);
            } catch (MpRuntimeException e) {
                log.error("modify attack flow error", e);
                return ServiceResult.fail(FlowErrorCode.MODIFY_ATTACK_FLOW_ERROR);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }

        // 请求对象转换
        FlowInfoModel flowInfoModel = this.getFlowInfoModel(modifyRequest);
        // 更新流量信息
        ObjServiceResult<FlowInfoModel> serviceResult = update(flowInfoModel);

        // 更新脚本参数时，先删除旧参数，再重新保存新参数
        List<FlowScriptParamModel> scriptParamModels = modifyRequest.getScriptParamModels();
        if (CollectionUtils.isNotEmpty(scriptParamModels)) {
            flowScriptParamService.deleteByFlowId(modifyRequest.getId());
            scriptParamModels.forEach(model -> model.setFlowId(modifyRequest.getId()));
            flowScriptParamService.bulkCreate(scriptParamModels);
        }

        FastDfsConfig.Group group = fastDfsConfig.getGroupByName(FdfsGroupEnum.DEFAULT);
        String prefixUrl = group.getInnerFileServerAddr() + File.separator;
        // 转换返回对象
        AttackFlowResponse response = new AttackFlowResponse(serviceResult.getSource().getObj(), prefixUrl, scriptParamModels);
        response.setCode(modifyRequest.getCode());
        return ServiceResult.success(response);
    }

    /**
     * 背景流量请求转换
     * @param addRequest
     * @return
     */
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

    /**
     * 更新背景流量请求转换
     * @param modifyRequest
     * @return
     */
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

    /**
     * 攻击流量请求转换
     * @param addRequest
     * @return
     */
    private FlowInfoModel getFlowInfoModel(AddAttackFlowRequest addRequest) {
        FlowInfoModel flowInfoModel = new FlowInfoModel();
        flowInfoModel.setTitle(addRequest.getTitle());
        flowInfoModel.setFlowDesc(addRequest.getIntroduction());
        flowInfoModel.setCategory(addRequest.getCategory());
        flowInfoModel.setFileName(addRequest.getFileName());
        flowInfoModel.setResourceType(ResourceTypeEnum.SCRIPT.toValue());
        flowInfoModel.setInteractiveType(InteractiveTypeEnum.ATTACK.toValue());
        flowInfoModel.setScriptFile(addRequest.getFilePath());
        flowInfoModel.setRunCmd(addRequest.getRunCmd());

        return flowInfoModel;
    }

    /**
     * 更新攻击流量请求转换
     * @param modifyRequest
     * @return
     */
    private FlowInfoModel getFlowInfoModel(ModifyAttackFlowRequest modifyRequest) {
        FlowInfoModel flowInfoModel = new FlowInfoModel();
        flowInfoModel.setId(modifyRequest.getId());
        flowInfoModel.setTitle(modifyRequest.getTitle());
        flowInfoModel.setFlowDesc(modifyRequest.getIntroduction());
        flowInfoModel.setCategory(modifyRequest.getCategory());
        flowInfoModel.setFileName(modifyRequest.getFileName());
        flowInfoModel.setScriptFile(modifyRequest.getFilePath());
        flowInfoModel.setRunCmd(modifyRequest.getRunCmd());

        return flowInfoModel;
    }

    /**
     * 校验标题是否唯一
     * @param title
     * @param id
     * @return
     */
    private boolean checkNameUnique(String title, String id) {
        FlowInfoModel flowInfoModel = getModelMapper().selectByTitle(title);
        return Objects.isNull(flowInfoModel) || flowInfoModel.getId().equals(id);
    }

    /**
     * 根据流量id批量查询
     * @param flowIds
     * @return
     */
    public List<FlowInfoModel> selectByFlowIds(List<String> flowIds) {
        return getModelMapper().selectByFlowIds(flowIds);
    }

    /**
     * 根据标题查询
     * @param title
     * @return
     */
    public FlowInfoModel selectByTitle(String title) {
        return getModelMapper().selectByTitle(title);
    }

    /**
     *
     * 批量导入预制流量文件
     * @return
     */
    public ServiceResult<Boolean> batchImportFlowInfo() {
        String pcapPath = PRE_PATH + "/pcap";
        log.info("导入pcap文件开始,pcapPath:{}", pcapPath);
        File pcapFile = new File(pcapPath);
        if (!this.doImport(pcapFile)) {
            return new ServiceResult<>(false);
        }
        log.info("导入pcap文件结束");

        String scriptPath = PRE_PATH + "/scripts";
        log.info("导入script文件开始,scriptPath:{}", scriptPath);
        File scriptFile = new File(scriptPath);
        if (!this.doImport(scriptFile)) {
            return new ServiceResult<>(false);
        }
        log.info("导入script文件结束");

        return new ServiceResult<>(true);
    }

    /**
     * 导入流量文件
     * @param file
     * @return
     */
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
                        flowInfoModel.setTitle(fileName);
                        flowInfoModel.setFileName(flowFile.getName());
                        String fdfsPath = fastDFSClient.uploadFileWithFilepath(flowFile.getAbsolutePath(), FdfsGroupEnum.DEFAULT.toString());
                        if (flowFile.getName().endsWith(".pcap")) {
                            // 背景流量
                            flowInfoModel.setResourceType(ResourceTypeEnum.PACP.toValue());
                            flowInfoModel.setInteractiveType(InteractiveTypeEnum.BACKGROUND.toValue());
                            flowInfoModel.setPcapFile(fdfsPath);
                        } else {
                            // 攻击流量
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
                        // 攻击流量，要添加默认脚本参数信息
                        List<FlowScriptParamModel> scriptParamModelList = new ArrayList<>();
                        flowInfoModelList.forEach(flowInfo -> {
                            FlowInfoModel flowInfoModel = this.selectByTitle(flowInfo.getTitle());
                            FlowScriptParamModel scriptParamIp = new FlowScriptParamModel();
                            scriptParamIp.setFlowId(flowInfoModel.getId());
                            scriptParamIp.setParamKey("ip");
                            scriptParamIp.setParamType(1);
                            scriptParamIp.setParamDesc("流量发生目标地址");
                            scriptParamModelList.add(scriptParamIp);

                            FlowScriptParamModel scriptParamPort = new FlowScriptParamModel();
                            scriptParamPort.setFlowId(flowInfoModel.getId());
                            scriptParamPort.setParamKey("port");
                            scriptParamPort.setParamValue("80");
                            scriptParamPort.setParamType(2);
                            scriptParamPort.setParamDesc("流量发生目标端口");
                            scriptParamModelList.add(scriptParamPort);
                        });

                        flowScriptParamService.bulkCreate(scriptParamModelList);
                    }
                }
            }
        } else {
            log.error("文件目录不存在!");
            return false;
        }

        return true;
    }


}
