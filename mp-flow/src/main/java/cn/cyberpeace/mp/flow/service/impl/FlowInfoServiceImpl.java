package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.config.FastDfsConfig;
import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.*;
import cn.cyberpeace.mp.base.util.TextUtils;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.cyberpeace.mp.flow.enums.InteractiveTypeEnum;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskMapper;
import cn.cyberpeace.mp.flow.request.*;
import cn.cyberpeace.mp.flow.request.queryparam.FlowInfoQueryParams;
import cn.cyberpeace.mp.flow.response.AttackFlowResponse;
import cn.cyberpeace.mp.flow.response.BackgroundFlowResponse;
import cn.cyberpeace.mp.flow.response.FlowInfoResponse;
import cn.cyberpeace.mp.flow.service.FlowInfoService;
import cn.cyberpeace.mp.flow.service.field.FlowInfoFields;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    public ServiceResult<Boolean> deleteFlowInfo(String id) {
        // 判断流量任务是否关联该流量信息
        List<FlowTaskModel> flowTaskList = flowTaskMapper.listByFlowIds(Collections.singletonList(id));
        if (CollectionUtils.isNotEmpty(flowTaskList)) {
            return ServiceResult.fail("Can not delete flow task related flow info");
        }

        return delete(id);
    }

    /**
     * 批量删除流量信息
     *
     * @param request
     * @return
     */
    public ServiceResult<Integer> batchDeleteFlowInfo(BatchDeleteFlowInfoRequest request) {
        // 判断流量任务是否关联该流量信息
        List<FlowTaskModel> flowTaskList = flowTaskMapper.listByFlowIds(request.getIdList());
        if (CollectionUtils.isNotEmpty(flowTaskList)) {
            log.info("flow info related flow task can not delete: {}", flowTaskList);
            return ServiceResult.fail("Can not delete flow task related flow info");
        }

        String[] delIds = request.getIdList().toArray(new String[0]);
        return bulkDelete(delIds);
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
        if (CollectionUtils.isNotEmpty(listObjServiceResult.getSource().getObjs())) {
            responseList = listObjServiceResult.getSource().getObjs().stream()
                    .map(FlowInfoResponse::new)
                    .collect(Collectors.toList());
        }

        return new ListObjServiceResult<>(new ListObjResult<>(responseList, listObjServiceResult.getSource().getTotal()));
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
                return ServiceResult.fail("Not background flow!");
            }
            response = new BackgroundFlowResponse(flowInfoModel, prefixUrl);
        } else {
            return ServiceResult.fail("No flow exist!");
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
                return ServiceResult.fail("Not attack flow!");
            }
            response = new AttackFlowResponse(flowInfoModel, prefixUrl);

            // 下载攻击脚本
            byte[] fileByte = fastDFSClient.download(flowInfoModel.getScriptFile(), FdfsGroupEnum.DEFAULT.toString());
            String code = new String(fileByte);
            response.setCode(code);
        } else {
            return ServiceResult.fail("No flow exist!");
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
            return ServiceResult.fail("Title must be unique");
        }

        if (!addRequest.getFileName().endsWith(".pcap")) {
            return ServiceResult.fail("Not pcap file");
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
    public ServiceResult<AttackFlowResponse> addAttackFlow(AddAttackFlowRequest addRequest) {
        if (!checkNameUnique(addRequest.getTitle(), org.apache.commons.lang3.StringUtils.EMPTY)) {
            return ServiceResult.fail("Title must be unique");
        }

        // 判断是否有文件名，没有则根据脚本语言默认生成一个
        String fileName;
        if (StringUtils.isBlank(addRequest.getFileName())) {
            fileName = TextUtils.uuid() + (addRequest.getSuffix() == 1 ? ".sh" : ".py");
            addRequest.setFileName(fileName);
        } else {
            fileName = addRequest.getFileName();
        }
        InputStream is = new ByteArrayInputStream(addRequest.getCode().getBytes());
        String filePath = fastDFSClient.upload(is, fileName, null, FdfsGroupEnum.DEFAULT.toString());
        addRequest.setFilePath(filePath);

        // 请求对象转换
        FlowInfoModel flowInfoModel = this.getFlowInfoModel(addRequest);
        // 保存流量信息
        ObjServiceResult<FlowInfoModel> serviceResult = create(flowInfoModel);

        FastDfsConfig.Group group = fastDfsConfig.getGroupByName(FdfsGroupEnum.DEFAULT);
        String prefixUrl = group.getInnerFileServerAddr() + File.separator;
        // 转换返回对象
        AttackFlowResponse response = new AttackFlowResponse(serviceResult.getSource().getObj(), prefixUrl);
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
            return ServiceResult.fail("Title must be unique");
        }

        if (StringUtils.isNotBlank(modifyRequest.getFileName()) && !modifyRequest.getFileName().endsWith(".pcap")) {
            return ServiceResult.fail("Not pcap file");
        }

        ObjServiceResult<FlowInfoModel> oldServiceResult = get(modifyRequest.getId());
        if (Objects.isNull(oldServiceResult.getSource())) {
            // 判断流量是否存在
            return ServiceResult.fail("Flow info not exist");
        } else {
            // 判断是否背景流量
            if (InteractiveTypeEnum.BACKGROUND.toValue() != oldServiceResult.getSource().getObj().getInteractiveType()) {
                return ServiceResult.fail("Not background flow");
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
    public ServiceResult<AttackFlowResponse> modifyAttackFlow(ModifyAttackFlowRequest modifyRequest) {
        if (!checkNameUnique(modifyRequest.getTitle(), modifyRequest.getId())) {
            return ServiceResult.fail("Title must be unique");
        }

        ObjServiceResult<FlowInfoModel> oldServiceResult = get(modifyRequest.getId());
        if (Objects.isNull(oldServiceResult.getSource())) {
            // 判断流量是否存在
            return ServiceResult.fail("Flow info not exist");
        } else {
            // 判断是否攻击流量
            if (InteractiveTypeEnum.ATTACK.toValue() != oldServiceResult.getSource().getObj().getInteractiveType()) {
                return ServiceResult.fail("Not attack flow");
            }
        }

        if (StringUtils.isNotBlank(modifyRequest.getCode())) {
            if (Objects.isNull(modifyRequest.getSuffix())) {
                // 更新脚本时，suffix不能为空
                return ServiceResult.fail("Suffix can not be empty");
            }

            // 判断是否有文件名，没有则根据脚本语言默认生成一个
            String fileName;
            if (StringUtils.isBlank(modifyRequest.getFileName())) {
                fileName = TextUtils.uuid() + (modifyRequest.getSuffix() == 1 ? ".sh" : ".py");
                modifyRequest.setFileName(fileName);
            } else {
                fileName = modifyRequest.getFileName();
            }
            InputStream is = new ByteArrayInputStream(modifyRequest.getCode().getBytes());
            String filePath = fastDFSClient.upload(is, fileName, null, FdfsGroupEnum.DEFAULT.toString());
            modifyRequest.setFilePath(filePath);
        }

        // 请求对象转换
        FlowInfoModel flowInfoModel = this.getFlowInfoModel(modifyRequest);
        // 更新流量信息
        ObjServiceResult<FlowInfoModel> serviceResult = update(flowInfoModel);

        FastDfsConfig.Group group = fastDfsConfig.getGroupByName(FdfsGroupEnum.DEFAULT);
        String prefixUrl = group.getInnerFileServerAddr() + File.separator;
        // 转换返回对象
        AttackFlowResponse response = new AttackFlowResponse(serviceResult.getSource().getObj(), prefixUrl);
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

}
