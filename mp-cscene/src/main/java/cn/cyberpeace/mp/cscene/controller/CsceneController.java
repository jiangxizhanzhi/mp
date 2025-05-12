package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.BaseModelListControllerBase;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.mptool.ExceptionUtil;
import cn.cyberpeace.mp.cscene.bo.*;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.controller.request.*;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneRoleModel;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneRoleModel;
import cn.cyberpeace.mp.cscene.repository.CsceneRoleMapper;
import cn.cyberpeace.mp.cscene.request.AreaChangeData;
import cn.cyberpeace.mp.cscene.request.PositionChangeTopoRequest;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.cscene.service.param.CsceneQueryParams;
import cn.cyberpeace.mp.cscene.util.CsceneDataModifier;
import cn.cyberpeace.mp.cscene.util.CsceneLogUtils;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.scene.core.bo.*;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import cn.cyberpeace.mp.scene.model.SceneConfigAreaModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneAreaModel;
import cn.cyberpeace.mp.scene.repository.SceneConfigAreaMapper;
import cn.cyberpeace.mp.scene.service.SceneConfigService;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Api(value = "场景模板", tags = "CSCENE-场景模板")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cscenes")
@Data
@AuthTarget({TargetEnum.CT_AD_LIST, TargetEnum.CT_CFS_LIST, TargetEnum.CT_OPERATION_LIST, TargetEnum.CR_INDIVIDUAL_TRAINING, TargetEnum.CR_TEAM_TRAINING, TargetEnum.RED_BLUE, TargetEnum.RANGE_TEST_SELF })
public class CsceneController implements BaseModelListControllerBase {


    private CsceneService modelService;

    private CsceneSpecialService csceneSpecialService;

    private BaseAppConfig baseAppConfig;

    private CsceneActionRequest formatActionRequest(CsceneActionRequest request, String paramId) {
        if (request == null) {
            request = new CsceneActionRequest();
        }
        if (!StringUtils.isEmpty(paramId)) {
            request.setId(paramId);
        }
        return request;
    }

    private CsceneCreateInstanceRequest formatCreateInstanceRequest(CsceneCreateInstanceRequest request, String paramId) {
        if (request == null) {
            request = new CsceneCreateInstanceRequest();
        }
        request = (CsceneCreateInstanceRequest) formatActionRequest(request, paramId);
        return request;
    }

    private void correctRequestCsceneParamsForSync(CsceneBO csceneBO) {
        if (csceneBO == null) {
            return;
        }
        CsceneDataModifier.removeEidWhenCreate(csceneBO);
        CsceneDataModifier.removeFilePathPrefix(csceneBO);
        CsceneDataModifier.assembleInitCommands(csceneBO);
        CsceneDataModifier.handlerInputEnableRecording(csceneBO);
        CsceneDataModifier.handlerEnableRate(csceneBO);
        CsceneDataModifier.checkFlowNum(csceneBO);
        //check cidr not same when enable collection
        CsceneDataModifier.checkSameCidrForCpcollection(csceneBO);
        CsceneDataModifier.checkVlanId(csceneBO);
        CsceneDataModifier.check(csceneBO);
        CsceneDataModifier.checkIngressAndEgress(csceneBO);
        CsceneDataModifier.correctQuickCreateSupport(csceneBO);
        CsceneDataModifier.checkFirewallRules(csceneBO);
        CsceneDataModifier.handleAccessModePath(csceneBO);
        CsceneDataModifier.checkCloudElement(csceneBO);
        CsceneDataModifier.handlerPosition(csceneBO.getSceneConfig());
    }

    private void correctRequestCsceneParamsForAppInstance(CsceneBO csceneBO) {
        if (csceneBO == null) {
            return;
        }
        CsceneDataModifier.removeFilePathPrefix(csceneBO);
        CsceneDataModifier.assembleInitCommands(csceneBO);
        CsceneDataModifier.handlerInputEnableRecording(csceneBO);
        CsceneDataModifier.removeUnUsePlugin(csceneBO);
    }

    @Autowired
    public CsceneController(CsceneService csceneService, CsceneSpecialService csceneSpecialService, BaseAppConfig baseAppConfig) {
        setModelService(csceneService);
        setCsceneSpecialService(csceneSpecialService);
        this.baseAppConfig = baseAppConfig;
    }

    @AuthTarget(TargetEnum.NO_AUTH)
    @ApiOperation("授权场景模板")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneAuthorizeRequest.class)
    @PostMapping("/authorize")
    public ServiceResult<Boolean> authorize(@RequestBody CsceneAuthorizeRequest request) {
        return modelService.csceneAuthorize(request.getTargetIds(), request.getCsceneIds(), request.getAuthType()).asResult();
    }

    @AuthTarget(TargetEnum.NO_AUTH)
    @ApiOperation("获取指定场景模板授权信息")
    @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class)
    @GetMapping("/get_authorize")
    public ServiceResult<CsceneAuthorizeBO> getAuthorize(@RequestParam String id) {
        return modelService.getCsceneAuthorizeInfo(id).asResult();
    }

    @AuthTarget(TargetEnum.CSCENE_TARGET_GROUP)
    @GetMapping
    public ListObjServiceResult<CsceneBO> list(CsceneQueryParams queryParams) {
        handleQueryParams("list", queryParams);
        ListObjServiceResult<CsceneBO> result = getModelService().listCsceneBO(queryParams, false, null).asResult();
        getModelService().setSharedUser(result.getData().getList(), queryParams);
        return result;
    }

    @AuthTarget(TargetEnum.CSCENE_TARGET_GROUP)
    @ApiOperation("获取指定id场景模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "area_id", value = "区域id", dataTypeClass = String.class)
    })
    @GetMapping(params = "id")
    public ServiceResult<CsceneBO> get(@RequestParam String id, @RequestParam(value = "area_id", required = false) String areaId, @RequestParam(required = false, name = "whole") Boolean whole) {
        if (Boolean.TRUE.equals(whole)) {
            areaId = null;
        } else if (areaId == null) {
            areaId = "";
        }
        var result = modelService.getById(id, areaId, false, null);
        var dataBO = result.getSource().getSceneConfig();
        String sceneId = dataBO.getTopology().getScene().getEid();
        result.getSource().getSceneConfig().getTopology()
                .setOutAreaElementNum(this.modelService.getOutAreaElementNum(sceneId, areaId, dataBO));
        return result.asResult();
    }

    @ApiOperation("根据区域获取指定id场景模板")
    @GetMapping("/get_by_area")
    public ServiceResult<CsceneBO> getByArea(@RequestParam String id, @RequestParam(required = false, name = "area_id") String areaId) {
        if (areaId == null) {
            areaId = StringUtils.EMPTY;
        }
        String sceneId = this.modelService.get(id).getSource().getObj().getSceneConfig();
        var csceneBO = getByAreaId(sceneId, areaId);
        var roleMapper = BeanCommon.getBean(CsceneRoleMapper.class);
        var list = roleMapper.list(new HumanCondition().filterEqual(BaseCsceneRoleModel::getCscene, id)
                                           .orderBy(BaseCsceneRoleModel::getPk));
        if (list != null) {
            csceneBO.setRoles(convertToRoleBOs(list, CsceneRoleBO.class));
        } else {
            csceneBO.setRoles(new ArrayList<>());
        }
        return new ServiceResult<>(csceneBO).asResult();
    }

    @ApiOperation("根据区域获取指定id场景模板，同时针对3d态势特制，额外将层节点放到elements处")
    @GetMapping("/get_by_area/3d")
    public ServiceResult<CsceneBO> getByAreaFor3D(@RequestParam String id, @RequestParam(required = false, name = "area_id") String areaId) {
        if (areaId == null) {
            areaId = StringUtils.EMPTY;
        }
        String sceneId = this.modelService.get(id).getSource().getObj().getSceneConfig();
        var csceneBO = getByAreaIdFor3D(sceneId, areaId);
        var roleMapper = BeanCommon.getBean(CsceneRoleMapper.class);
        var list = roleMapper.list(new HumanCondition().filterEqual(BaseCsceneRoleModel::getCscene, id)
                .orderBy(BaseCsceneRoleModel::getPk));
        if (list != null) {
            csceneBO.setRoles(convertToRoleBOs(list, CsceneRoleBO.class));
        } else {
            csceneBO.setRoles(new ArrayList<>());
        }
        return new ServiceResult<>(csceneBO).asResult();
    }

    @ApiOperation(value = "处理指定变化的原始拓扑信息")
    @PostMapping("/change_topo_info")
    public ServiceResult<CsceneBO> changeTopoInfo(@RequestBody @Validated PositionChangeTopoRequest request) {
        String sceneId = this.modelService.get(request.getTopoId()).getSource().getObj().getSceneConfig();
        log.info("sceneConfigId is {}",sceneId);
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(sceneId, null, null);
        if(Objects.isNull(sceneConfigHandler)){
            log.warn("sceneConfig data is not exist!");
            return ResultCommon.success(null);
        }
        List<AreaChangeData> areaDataList = request.getAreaDataList();
        //区域信息处理
        SceneConfigDataBO dataBO = areaHandle(sceneConfigHandler, areaDataList);
        //独立节点信息处理,暂不处理
        CsceneBO csceneBO = new CsceneBO();
        csceneBO.setSceneConfig(dataBO);
        CsceneDataModifier.correctReturnCsceneData(csceneBO);
        return new ServiceResult<>(csceneBO).asResult();
    }

    /**
     * @description: 处理区域等数据
     * @param sceneConfigHandler
     * @param areaDataList
     * @throws
     * @return SceneConfigDataBO
     */
    private SceneConfigDataBO areaHandle(SceneConfigHandler sceneConfigHandler, List<AreaChangeData> areaDataList) {
        SceneConfigDataBO sceneConfigDataBO = sceneConfigHandler.toDataFor3D();
        SceneConfigTopologyBO topology = sceneConfigDataBO.getTopology();
        if(CollectionUtils.isEmpty(areaDataList)){
            return sceneConfigDataBO;
        }
        Map<String, AreaChangeData> areaChangeDataMap =
            areaDataList.stream().collect(Collectors.toMap(AreaChangeData::getAreaId, Function.identity()));
        List<SceneConfigAreaBO> areas = topology.getAreas();
        //变更的数据
        List<CsceneConfigChangeBO> csceneChangeBOS = new ArrayList<>();
        areas.forEach(sceneConfigAreaBO -> {
            String id = sceneConfigAreaBO.getId();
            AreaChangeData areaChangeData = areaChangeDataMap.get(id);
            if(Objects.nonNull(areaChangeData)){
                //新位置
                String newPosition2 = areaChangeData.getPosition2();
                AreaPosition newPosition = JSON.parseObject(newPosition2, AreaPosition.class);
                String oldPosition2 = sceneConfigAreaBO.getPosition2();
                AreaPosition oldPosition = JSON.parseObject(oldPosition2, AreaPosition.class);
                //对应节点改变的位置坐标
                Integer changeX = newPosition.getOrigin().getX() - oldPosition.getOrigin().getX();
                Integer changeY = newPosition.getOrigin().getY() - oldPosition.getOrigin().getY();
                //处理需要改变的节点和区域
                CsceneConfigChangeBO csceneChangeBO =
                    handlerAllAreaAndElements(topology, sceneConfigAreaBO, changeX, changeY);
                csceneChangeBOS.add(csceneChangeBO);
            }
        });
        if(CollectionUtils.isEmpty(csceneChangeBOS)){
            return sceneConfigDataBO;
        }
        List<SceneConfigAreaBO> sceneConfigAreaBOS = new ArrayList<>();
        List<SceneConfigElementBO> sceneConfigElementBOS = new ArrayList<>();
        csceneChangeBOS.stream().forEach(csceneChangeBO -> {
            List<SceneConfigAreaBO> updateAreas = csceneChangeBO.getUpdateAreas();
            if(CollectionUtils.isNotEmpty(updateAreas)){
                sceneConfigAreaBOS.addAll(updateAreas);
            }
            List<SceneConfigElementBO> updateElements = csceneChangeBO.getUpdateElements();
            if(CollectionUtils.isNotEmpty(updateElements)){
                sceneConfigElementBOS.addAll(updateElements);
            }
        });
        //返回变更的数据
        if(CollectionUtils.isNotEmpty(sceneConfigAreaBOS)){
            topology.setAreas(sceneConfigAreaBOS);
        }
        if(CollectionUtils.isNotEmpty(sceneConfigElementBOS)){
            topology.setElements(sceneConfigElementBOS);
        }
        return sceneConfigDataBO;
    }

    /**
     * @description: 处理所有区域节点信息
     * @param topology
     * @param sceneConfigAreaBO
     * @param changeX
     * @param changeY
     * @throws
     * @return void
     */
    private CsceneConfigChangeBO handlerAllAreaAndElements(SceneConfigTopologyBO topology,SceneConfigAreaBO sceneConfigAreaBO,Integer changeX,Integer changeY){
        String position2 = sceneConfigAreaBO.getPosition2();
        AreaPosition areaPosition = JSON.parseObject(position2, AreaPosition.class);
        Position origin = areaPosition.getOrigin();
        Integer x = origin.getX();
        Integer y = origin.getY();
        Integer width = areaPosition.getWidth();
        Integer height = areaPosition.getHeight();
        //X坐标范围在 x~xEnd
        Integer xEnd = x + width;
        //Y坐标范围在 y~yEnd
        Integer yEnd = y + height;
        //取出范围内所有节点（因为目前节点只和单个层节点相关，只能通过位置获取区域及其内部索引子区域和节点）
        //可能存在偏差，要求topo区域准确
        List<SceneConfigAreaBO> areas = topology.getAreas();
        List<SceneConfigAreaBO> updateAreas = areas.stream().filter(sceneArea -> {
            String position = sceneArea.getPosition2();
            AreaPosition aPosition = JSON.parseObject(position, AreaPosition.class);
            Position positionOrigin = aPosition.getOrigin();
            return (positionOrigin.getX() >= x) && (positionOrigin.getX() <= xEnd) && (positionOrigin.getY() >= y) && (
                positionOrigin.getY() <= yEnd);
        }).map(configAreaBO -> {
            String position = configAreaBO.getPosition2();
            AreaPosition aPosition = JSON.parseObject(position, AreaPosition.class);
            Position positionOrigin = aPosition.getOrigin();
            positionOrigin.setX(positionOrigin.getX() + changeX);
            positionOrigin.setY(positionOrigin.getY() + changeY);
            //使用新的对象，不改变原来的数据
            SceneConfigAreaBO newConfigAreaBO = new SceneConfigAreaBO();
            BeanUtils.copyProperties(configAreaBO,newConfigAreaBO);
            newConfigAreaBO.setPosition2(JSON.toJSONString(aPosition));
            return newConfigAreaBO;
        }).collect(Collectors.toList());
        //最终响应的为改变位置的所有区域和节点信息
        CsceneConfigChangeBO.CsceneConfigChangeBOBuilder builder = CsceneConfigChangeBO.builder();
        if(CollectionUtils.isNotEmpty(updateAreas)){
            builder.updateAreas(updateAreas);
        }

        List<SceneConfigElementBO> elements = topology.getElements();
        List<SceneConfigElementBO> updateElements = elements.stream().filter(sceneElementBO -> {
            String elePosition = sceneElementBO.getPosition2();
            Position position = JSON.parseObject(elePosition, Position.class);
            return position.getX() >= x && position.getX() <= xEnd && position.getY() >= y && position.getY() <= yEnd;
        }).map(sceneConfigElementBO -> {
            String elePosition = sceneConfigElementBO.getPosition2();
            Position position = JSON.parseObject(elePosition, Position.class);
            position.setX(position.getX() + changeX);
            position.setY(position.getY() + changeY);
            //使用新的对象，不改变原来的数据
            SceneConfigElementBO newConfigElementBO = new SceneConfigElementBO();
            BeanUtils.copyProperties(sceneConfigElementBO,newConfigElementBO);
            newConfigElementBO.setPosition2(JSON.toJSONString(position));
            return newConfigElementBO;
        }).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(updateElements)){
            builder.updateElements(updateElements);
        }
        return builder.build();
    }

    @ApiOperation("是否可删除连线等的校验")
    @GetMapping("/delete_check")
    public ServiceResult<DeleteCheckResultBO> deleteCheck( @RequestParam String areaConnectionEid) {
        // 是否可删除节点和区域的连线连线
        if (StringUtils.isEmpty(areaConnectionEid)) {
            return new ServiceResult<>(new DeleteCheckResultBO(true)).asResult();
        }
        boolean result = getModelService().checkDeleteNodeConnection(areaConnectionEid);
        return new ServiceResult<>(new DeleteCheckResultBO(result)).asResult();
    }

    private List<CsceneRoleBO> convertToRoleBOs(List<CsceneRoleModel> roleObjs, Class<CsceneRoleBO> roleBOClass) {
        List<CsceneRoleBO> roleBOS = new ArrayList<>();
        if (roleObjs == null) {
            return roleBOS;
        }
        Map<String, CsceneRoleBO> roleBOMap = new HashMap<>();
        Map<String, String> roleParentMap = new HashMap<>();
        for (CsceneRoleModel roleObj : roleObjs) {
            CsceneRoleBO roleBO = new CsceneRoleBO();
            roleBO.fromModel(roleObj);
            roleBOMap.put(roleObj.getId(), roleBO);
            roleParentMap.put(roleObj.getId(), roleObj.getParent());
            if (StringUtils.isEmpty(roleObj.getParent())) {
                roleBOS.add(roleBO);
            }
        }
        for (Map.Entry<String, CsceneRoleBO> entry : roleBOMap.entrySet()) {
            String roleId = entry.getKey();
            CsceneRoleBO roleBO = entry.getValue();
            String parentId = roleParentMap.getOrDefault(roleId, null);
            CsceneRoleBO parentRoleBO = roleBOMap.getOrDefault(parentId, null);
            if (parentRoleBO == null) {
                continue;
            }

            if (parentRoleBO.getChildren() == null) {
                parentRoleBO.setChildren(new ArrayList<>());
            }
            parentRoleBO.getChildren().add(roleBO);
        }

        return roleBOS;
    }

    private CsceneBO getByAreaId(String sceneId, String areaId) {
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(sceneId, areaId, true, null);
        SceneConfigDataBO dataBO = sceneConfigHandler.toData();
        if (areaId != null) {
            int num = this.modelService.getOutAreaElementNum(sceneId, areaId, dataBO);
            dataBO.getTopology().setOutAreaElementNum(num);
        }
        CsceneBO csceneBO = new CsceneBO();
        csceneBO.setSceneConfig(dataBO);
        CsceneDataModifier.correctReturnCsceneData(csceneBO);
        return csceneBO;
    }

    private CsceneBO getByAreaIdFor3D(String sceneId, String areaId) {
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(sceneId, areaId, true, null);
        SceneConfigDataBO dataBO = sceneConfigHandler.toDataFor3D();
        CsceneBO csceneBO = new CsceneBO();
        csceneBO.setSceneConfig(dataBO);
        CsceneDataModifier.correctReturnCsceneData(csceneBO);
        return csceneBO;
    }

    @ApiOperation("更新区域显示参数")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = AreaUpdateRequest.class)
    @PostMapping("/update_area")
    public ServiceResult<Boolean> updateArea(@RequestBody AreaUpdateRequest request) {

        String id = this.modelService.get(request.getSceneId()).getSource().getObj().getSceneConfig();

        var sceneConfigAreaMapper = BeanCommon.getBean(SceneConfigAreaMapper.class);
        SceneConfigAreaModel sceneConfigAreaModel = sceneConfigAreaMapper.first(new HumanCondition().select(SceneConfigAreaModel::getId)
                                                                                       .filterEqual(SceneConfigAreaModel::getScene, id)
                                                                                       .filterEqual(SceneConfigAreaModel::getNodeId, request.getAreaId()));
        if (sceneConfigAreaModel == null) {
            log.error("scene {} ,  area {} not find , ", request.getSceneId(), request.getAreaId());
            return new ServiceResult<>(true).asResult();
        }
        sceneConfigAreaModel.setDisplayContext(request.getDisplayContext());
        return new ServiceResult<>(sceneConfigAreaMapper.update(sceneConfigAreaModel)).asResult();
    }

    @ApiOperation("更新场景拓扑(模板拖拽)")
    @PostMapping("/sync_with_template")
    public ServiceResult<CsceneBO> syncWithTemplate(@RequestBody CsceneSyncRequest request) {
        String id = this.modelService.get(request.getId()).getSource().getObj().getSceneConfig();
        String templateId = this.modelService.get(request.getTemplateId()).getSource().getObj().getSceneConfig();

        SceneConfigService sceneConfigService = BeanCommon.getBean(SceneConfigService.class);

        var originSceneConfig = sceneConfigService.get(id, false, null, null).getSource();
        var templateSceneConfig = sceneConfigService.get(templateId, false, null, null).getSource();

        int currentNum = originSceneConfig.getTopology().getElements().size();
        int templateNum = templateSceneConfig.getTopology().getElements().size();
        int limit = BeanCommon.getBean(CsceneAppConfig.class).getSceneEditNodeLimit();
        if (currentNum + templateNum > limit) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_EDIT_LIMIT.format(String.valueOf(limit), String.valueOf(currentNum), String.valueOf(templateNum)));
        }

        this.removeId(templateSceneConfig);
        this.updateOldId(templateSceneConfig);
        this.modifyAreaId(templateSceneConfig, request.getAreaId());
        templateSceneConfig.getTopology().setAreaId(request.getAreaId());
        CsceneDataModifier.handlerPosition(templateSceneConfig);
        this.mergeSceneConfig(originSceneConfig, templateSceneConfig);
        sceneConfigService.sync(originSceneConfig, false, null);
        var res = sceneConfigService.get(id, true, request.getAreaId(), null).getSource();
        var csceneBO = new CsceneBO();
        csceneBO.setSceneConfig(res);
        res.getTopology().setOutAreaElementNum(this.modelService.getOutAreaElementNum(id, request.getAreaId(), res));
        CsceneDataModifier.correctReturnCsceneData(csceneBO);
        return new ServiceResult<>(csceneBO).asResult();
    }

    private void modifyAreaId(SceneConfigDataBO sceneConfigDataBO, String areaId) {
        if (Objects.nonNull(sceneConfigDataBO) && Objects.nonNull(sceneConfigDataBO.getTopology())) {
            for (SceneConfigAreaBO sceneConfigAreaBO : sceneConfigDataBO.getTopology().getAreas()) {
                // 只需要改变模板中的第一层拓扑
                if (StringUtils.isEmpty(sceneConfigAreaBO.getArea())) {
                    sceneConfigAreaBO.setArea(areaId);
                }
            }
            for (SceneConfigElementBO sceneConfigElementBO : sceneConfigDataBO.getTopology().getElements()) {
                if (StringUtils.isEmpty(sceneConfigElementBO.getArea())) {
                    sceneConfigElementBO.setArea(areaId);
                }
            }
        }
    }

    private void removeId(SceneConfigDataBO sceneConfigDataBO) {
        if (Objects.isNull(sceneConfigDataBO)) {
            return;
        }
        if (sceneConfigDataBO.getTopology() != null) {
            var topology = sceneConfigDataBO.getTopology();
            topology.getScene().setEid(null);
            topology.getElements().forEach(sceneElementBO -> {
                sceneElementBO.setEid(null);
                if (CollectionUtils.isEmpty(sceneElementBO.getPorts())) {
                    return;
                }
                sceneElementBO.getPorts().forEach(p -> p.setEid(null));
            });
            topology.getAreas().forEach(sceneAreaBO -> sceneAreaBO.setEid(null));
            topology.getConnections().forEach(sceneElementPortConnectionBO -> sceneElementPortConnectionBO.setEid(null));
            topology.getNodeConnections().forEach(sceneNodeConnectionBO -> sceneNodeConnectionBO.setEid(null));
            topology.getPieces().forEach(scenePieceBO -> scenePieceBO.setEid(null));
        }
        if (sceneConfigDataBO.getPlugins() != null) {
            sceneConfigDataBO.getPlugins().values().forEach(scenePluginDataBO -> {
                if (Objects.nonNull(scenePluginDataBO.getScene())) {
                    scenePluginDataBO.getScene().remove("id");
                }
                if (Objects.nonNull(scenePluginDataBO.getElements()) && CollectionUtils.isNotEmpty(scenePluginDataBO.getElements().values())) {
                    scenePluginDataBO.getElements().values().forEach(map -> map.remove("id"));
                }
                if (Objects.nonNull(scenePluginDataBO.getElementPorts()) && CollectionUtils.isNotEmpty(scenePluginDataBO.getElementPorts().values())) {
                    scenePluginDataBO.getElementPorts().values().forEach(map -> map.remove("id"));
                }
            });
        }
    }

    private void updateOldId(SceneConfigDataBO sceneConfigDataBO) {
        if (Objects.isNull(sceneConfigDataBO)) {
            return;
        }
        // 构建新老id的映射

        if (Objects.nonNull(sceneConfigDataBO.getTopology())) {
            var topology = sceneConfigDataBO.getTopology();
            // 构建area的新老id映射
            Map<String, String> idMap = new HashMap<>();
            idMap.put("","");
            for (SceneConfigAreaBO sceneConfigAreaBO : topology.getAreas()) {
                if (StringUtils.isNotEmpty(sceneConfigAreaBO.getId())) {
                    String nodeId = TextUtils.uuid();
                    idMap.put(sceneConfigAreaBO.getId(), nodeId);
                }
            }

            // 构建element的新老id映射
            for (SceneConfigElementBO sceneConfigElementBO : topology.getElements()) {
                if (StringUtils.isNotEmpty(sceneConfigElementBO.getId())) {
                    String oldNodeId = sceneConfigElementBO.getId();
                    String nodeId = TextUtils.uuid();
                    idMap.put(oldNodeId, nodeId);
                    if (CollectionUtils.isNotEmpty(sceneConfigElementBO.getPorts())) {
                        for (var p : sceneConfigElementBO.getPorts()) {
                            idMap.put(p.getId(), p.getId().replace(oldNodeId, nodeId));
                        }
                    }
                }
            }

            // 替换area为新的id
            for (SceneConfigAreaBO a : topology.getAreas()) {
                a.setId(idMap.get(a.getId()));
                a.setArea(idMap.get(a.getArea()));
            }

            // 替换element为新的id
            for (SceneConfigElementBO e : topology.getElements()) {
                String oldNodeId = e.getId();
                String newNodeId = idMap.get(oldNodeId);
                String oldAreaId = e.getArea();
                String newAreaId = idMap.get(oldAreaId);
                e.setId(newNodeId);
                e.setArea(newAreaId);
                if (e.getOuterPosition2() != null && !e.getOuterPosition2().isEmpty()) {
                    Map<String, String> newOuterPosition2 = new HashMap<>();
                    for (Map.Entry<String, String> entry : e.getOuterPosition2().entrySet()) {
                        String area = entry.getKey();
                        String position2 = entry.getValue();
                        newOuterPosition2.put(idMap.get(area), position2);
                    }
                    e.setOuterPosition2(newOuterPosition2);
                }
                if (CollectionUtils.isNotEmpty(e.getPorts())) {
                    for (var p : e.getPorts()) {
                        p.setId(idMap.get(p.getId()));
                    }
                }
            }

            Map<String, String> connectionIdMap = new HashMap<>();
            for (SceneConfigElementPortConnectionBO c : topology.getConnections()) {
                String nodeId = TextUtils.uuid();
                connectionIdMap.put(c.getId(), nodeId);
                c.setId(nodeId);
                c.setSource(idMap.get(c.getSource()));
                c.setTarget(idMap.get(c.getTarget()));
            }

            Map<String,String> nodeConnectionIdMap = new HashMap<>();
            for (SceneConfigAreaNodeConnectionBO nc : topology.getNodeConnections()) {
                String nodeId = TextUtils.uuid();
                nodeConnectionIdMap.put(nc.getId(), nodeId);
                nc.setId(nodeId);

                String sourceNodeId = parseNodeId(nc.getSourceNodeId());
                String newSourceNodeId = idMap.get(sourceNodeId);
                String targetNodeId = parseNodeId(nc.getTargetNodeId());
                String newTargetNodeId = idMap.get(targetNodeId);

                nc.setSource(nc.getSource().replace(sourceNodeId, newSourceNodeId));
                nc.setTarget(nc.getTarget().replace(targetNodeId, newTargetNodeId));
            }

            if (sceneConfigDataBO.getPlugins() != null) {
                for (ScenePluginDataBO scenePluginDataBO : sceneConfigDataBO.getPlugins().values()) {
                    if (Objects.nonNull(scenePluginDataBO.getElements()) && !scenePluginDataBO.getElements()
                            .isEmpty()) {
                        var elementMap = scenePluginDataBO.getElements();
                        for (Map.Entry<String, String> entry : idMap.entrySet()) {
                            String oldId = entry.getKey();
                            String newId = entry.getValue();
                            var value = elementMap.remove(oldId);
                            if (value != null) {
                                elementMap.put(newId, value);
                            }
                        }
                    }
                    if (Objects.nonNull(scenePluginDataBO.getElementPorts()) && !scenePluginDataBO.getElementPorts()
                            .isEmpty()) {
                        var portMap = scenePluginDataBO.getElementPorts();
                        for (Map.Entry<String, String> entry : idMap.entrySet()) {
                            String oldId = entry.getKey();
                            String newId = entry.getValue();
                            var value = portMap.remove(oldId);
                            if (value != null) {
                                portMap.put(newId, value);
                            }
                        }
                    }
                }
            }
        }
    }

    @ApiOperation("查询场景拓扑区域树形结构")
    @ApiImplicitParam(name = "id", value = "场景拓扑id", dataTypeClass = String.class)
    @GetMapping("/tree")
    public ServiceResult<SceneAreaTreeBO> getSceneConfigAreaTree(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) {
            return new ServiceResult<>(new SceneAreaTreeBO()).asResult();
        }
        String sceneId = this.modelService.get(id).getSource().getObj().getSceneConfig();
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(sceneId, null, null);
        SceneAreaTreeBO sceneAreaTree = new SceneAreaTreeBO();
        SceneConfigDataBO result = sceneConfigHandler.toData();
        if (result != null && result.getTopology() != null && CollectionUtils.isNotEmpty(result.getTopology().getAreas())) {
            var areas = result.getTopology().getAreas();
            Map<String, List<SceneConfigAreaBO>> parentId2Children = new HashMap<>();
            areas.stream()
                    .filter(sceneConfigAreaBO -> StringUtils.isNotEmpty(sceneConfigAreaBO.getArea()))
                    .forEach(sceneConfigAreaBO -> {
                        if (CollectionUtils.isEmpty(parentId2Children.get(sceneConfigAreaBO.getArea()))) {
                            parentId2Children.put(sceneConfigAreaBO.getArea(), new ArrayList<>());
                        }
                        parentId2Children.get(sceneConfigAreaBO.getArea()).add(sceneConfigAreaBO);
                    });
            areas.stream()
                    .filter(sceneConfigAreaBO -> StringUtils.isEmpty(sceneConfigAreaBO.getArea()))
                    .forEach(sceneConfigAreaBO -> {
                        if (sceneConfigAreaBO.getType() == BaseSceneAreaModel.Type.LAYER) {
                            var area = getTreeArea(sceneConfigAreaBO, parentId2Children);
                            sceneAreaTree.getAreaList().add(area);
                        } else {
                            if (CollectionUtils.isNotEmpty(parentId2Children.get(sceneConfigAreaBO.getId()))) {
                                parentId2Children.get(sceneConfigAreaBO.getId()).stream()
                                        .filter(a -> a.getType() == BaseSceneAreaModel.Type.LAYER)
                                        .forEach(sceneConfigAreaBO1 -> {
                                            var area = getTreeArea(sceneConfigAreaBO1, parentId2Children);
                                            sceneAreaTree.getAreaList().add(area);
                                        });
                            }
                        }
                    });
        }
        return new ServiceResult<>(sceneAreaTree).asResult();
    }

    private SceneAreaTreeBO.Area getTreeArea(SceneConfigAreaBO sceneConfigAreaBO, Map<String, List<SceneConfigAreaBO>> parentId2Children) {
        SceneAreaTreeBO.Area area = new SceneAreaTreeBO.Area();
        area.setId(sceneConfigAreaBO.getId());
        area.setName(sceneConfigAreaBO.getName());
        area.setDescription(sceneConfigAreaBO.getDescription());
        area.setDisplayContext(sceneConfigAreaBO.getDisplayContext());
        processChildren(area.getId(), area, parentId2Children);
        return area;
    }

    private void processChildren(String parentId, SceneAreaTreeBO.Area area, Map<String, List<SceneConfigAreaBO>> parentId2Children) {
        if (CollectionUtils.isNotEmpty(parentId2Children.get(parentId))) {
            parentId2Children.get(parentId).forEach(sceneConfigAreaBO -> {
                if (sceneConfigAreaBO.getType() == BaseSceneAreaModel.Type.LAYER) {
                    SceneAreaTreeBO.Area child = new SceneAreaTreeBO.Area();
                    child.setLabel(parentId);
                    child.setId(sceneConfigAreaBO.getId());
                    child.setName(sceneConfigAreaBO.getName());
                    child.setDescription(sceneConfigAreaBO.getDescription());
                    child.setDisplayContext(sceneConfigAreaBO.getDisplayContext());
                    processChildren(child.getId(), child, parentId2Children);
                    area.getChildren().add(child);
                } else {
                    processChildren(sceneConfigAreaBO.getId(), area, parentId2Children);
                }
            });
        }
    }

    @ApiOperation("导出指定id场景模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
    })
    @GetMapping(value = "/export", params = "id")
    public ServiceResult<CsceneBO> export(@RequestParam String id) {
        return modelService.export(id, null).asResult();
    }

    @ApiOperation("导出指定id场景模板（分层）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
    })
    @GetMapping(value = "/export_file", params = "id")
    public void exportFile(@RequestParam String id, HttpServletResponse response) {
        var csceneBO = modelService.export(id, null).getSource();
        csceneBO.setRoles(new ArrayList<>());
        File file;
        var res = csceneBO.getSceneConfig();
        if (Objects.nonNull(res.getTopology()) && Objects.nonNull(res.getTopology().getScene()) &&
                StringUtils.isNotEmpty(res.getTopology().getScene().getName())) {
            file = new File(res.getTopology().getScene().getName());
        } else {
            file = new File(id);
        }

        InputStream fis = null;
        OutputStream outputStream = null;
        if (!file.exists()) {
            try {
                String filename = file.getName();
                file.createNewFile();
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(JsonCommon.snakeStringify(csceneBO));
                fileWriter.close();

                FileInputStream fileInputStream = new FileInputStream(file);
                fis = new BufferedInputStream(fileInputStream);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);

                response.reset();
                response.setCharacterEncoding("UTF-8");
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("scene.json", StandardCharsets.UTF_8));
                response.addHeader("Content-Length", "" + file.length());
                outputStream = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/octet-stream");
                outputStream.write(buffer);
                outputStream.flush();
            } catch (Exception e) {
                log.error("create file error", e);
            } finally {
                try {
                    outputStream.close();
                    fis.close();
                    file.delete();
                } catch (Exception e) {

                }
            }
        }
    }

    @AuthTarget(TargetEnum.CSCENE_TARGET_GROUP)
    @ApiOperation(value = "保存（创建/更新）场景", notes = "请忽略test_scenes和app_scenes字段")
    @ApiImplicitParam(name = "csceneBO", value = "请求体", dataTypeClass = CsceneBO.class)
    @PostMapping("/sync")
    public ServiceResult<CsceneBO> sync(@RequestBody CsceneBO csceneBO) {
        log.info("scene sync,params is {}", JsonCommon.stringify(csceneBO));
        boolean isUpdate = csceneBO != null && StringUtils.isNotEmpty(csceneBO.getId());
        correctRequestCsceneParamsForSync(csceneBO);
        ServiceResult<CsceneBO> result = modelService.sync(csceneBO, null, true).asResult();
        if (csceneBO.getType() != null && csceneBO.getType() == CsceneModel.Type.TEMPLATE) {
            if (isUpdate) {
                CsceneLogUtils.recordSceneConfigLog(csceneBO.getId(), OpTypeEnums.UPDATE, CsceneLogUtils.UPDATE_SCENE_CONFIG, csceneBO.getContext());
            } else {
                CsceneLogUtils.recordSceneConfigLog(result.getData(), OpTypeEnums.CREATE, CsceneLogUtils.CREATE_SCENE_CONFIG, csceneBO.getContext());
            }
        }
        return result;
    }

    @AuthTarget(TargetEnum.CSCENE_TARGET_GROUP)
    @ApiOperation(value = "创建场景模板")
    @ApiImplicitParam(name = "createTemplateBO", value = "请求体", dataTypeClass = CreateTemplateBO.class)
    @PostMapping("/create_template")
    public ServiceResult<CsceneBO> syncTemplate(@RequestBody CreateTemplateBO createTemplateBO) {
        var csceneBO = modelService.getById(createTemplateBO.getId(), null, false, null).getSource();
        csceneBO.setName(createTemplateBO.getName());
        csceneBO.setLogo(createTemplateBO.getLogo());
        csceneBO.getSceneConfig().getTopology().getScene().setName(createTemplateBO.getName());
        csceneBO.getSceneConfig().getTopology().getScene().setDescription(createTemplateBO.getDescription());
        csceneBO.setType(CsceneModel.Type.TEMPLATE);
        csceneBO.setId(null);
        csceneBO.getSceneConfig().getTopology().getScene().setEid(null);
        CsceneDataModifier.removeEidWhenCreate(csceneBO);
        modelService.sync(csceneBO, null, false);
        return new ServiceResult<>(csceneBO).asResult();
    }

    @AuthTarget(TargetEnum.CSCENE_TARGET_GROUP)
    @ApiOperation(value = "导入场景模板")
    @PostMapping("/import_template")
    public ServiceResult<CsceneBO> importTemplate(ImportTemplateBO importTemplateBO) {
        InputStream inputStream = null;
        try {
            inputStream = importTemplateBO.getFile().getInputStream();
            var bytes = inputStream.readAllBytes();
            CsceneBO csceneBO = JsonCommon.snakeParse(new String(bytes, StandardCharsets.UTF_8), CsceneBO.class);

            String id = this.modelService.get(importTemplateBO.getId()).getSource().getObj().getSceneConfig();
            SceneConfigService sceneConfigService = BeanCommon.getBean(SceneConfigService.class);
            var originSceneConfig = sceneConfigService.get(id, false, null, null).getSource();
            var templateSceneConfig = csceneBO.getSceneConfig();

            int currentNum = originSceneConfig.getTopology().getElements().size();
            int templateNum = templateSceneConfig.getTopology().getElements().size();
            int limit = BeanCommon.getBean(CsceneAppConfig.class).getSceneEditNodeLimit();
            if (currentNum + templateNum > limit) {
                throw new MpRuntimeException(CsceneErrorCode.SCENE_EDIT_LIMIT.format(String.valueOf(limit), String.valueOf(currentNum), String.valueOf(templateNum)));
            }

            this.removeId(templateSceneConfig);
            this.updateOldId(templateSceneConfig);
            this.modifyAreaId(templateSceneConfig, importTemplateBO.getAreaId());
            templateSceneConfig.getTopology().setAreaId(importTemplateBO.getAreaId());
            CsceneDataModifier.handlerPosition(templateSceneConfig);
            this.mergeSceneConfig(originSceneConfig, templateSceneConfig);
            sceneConfigService.sync(originSceneConfig, false, null);
            var res = sceneConfigService.get(id, true, importTemplateBO.getAreaId(), null).getSource();
            res.getTopology()
                    .setOutAreaElementNum(this.modelService.getOutAreaElementNum(id, importTemplateBO.getAreaId(), res));
            var resultBO = new CsceneBO();
            resultBO.setSceneConfig(res);
            CsceneDataModifier.correctReturnCsceneData(resultBO);
            return new ServiceResult<>(resultBO).asResult();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AuthTarget(TargetEnum.CSCENE_TARGET_GROUP)
    @ApiOperation(value = "syncWithOutCheck 保存（创建/更新）场景模板", notes = "请忽略test_scenes和app_scenes字段")
    @ApiImplicitParam(name = "csceneBO", value = "请求体", dataTypeClass = CsceneBO.class)
    @PostMapping("/sync_with_out_check")
    public ServiceResult<CsceneBO> syncWithOutCheck(@RequestBody CsceneBO csceneBO) {
        log.info("scene sync,params is {}", JsonCommon.stringify(csceneBO));
        boolean isUpdate = csceneBO != null && StringUtils.isNotEmpty(csceneBO.getId());
        correctRequestCsceneParamsForSync(csceneBO);
        var roles = csceneBO.getRoles();
        ServiceResult<CsceneBO> result = modelService.sync(csceneBO, null, true).asResult();
        if (csceneBO.getType() != null && csceneBO.getType() == CsceneModel.Type.TEMPLATE) {
            if (isUpdate) {
                CsceneLogUtils.recordSceneConfigLog(csceneBO.getId(), OpTypeEnums.UPDATE, CsceneLogUtils.UPDATE_SCENE_CONFIG, csceneBO.getContext());
            } else {
                CsceneLogUtils.recordSceneConfigLog(result.getData(), OpTypeEnums.CREATE, CsceneLogUtils.CREATE_SCENE_CONFIG, csceneBO.getContext());
            }
        }

        try {
            CsceneRoleMapper mapper = BeanCommon.getBean(CsceneRoleMapper.class);
            mapper.filterDelete(new HumanCondition().filterEqual(CsceneRoleModel::getCscene, csceneBO.getId()));
            if (CollectionUtils.isNotEmpty(roles)) {
                List<CsceneRoleModel> roleObjs = convertToRoleObjs(roles, null);
                roleObjs.forEach(r -> r.setCscene(csceneBO.getId()));
                mapper.bulkCreate(roleObjs).getSuccessCount();
            }
        } catch (Exception e) {
            log.error("failed to save roles , cscene id {} , exception is {}", csceneBO.getId(), ExceptionUtil.stacktraceToString(e));
        }

        var topo = result.getData().getSceneConfig().getTopology();
        return this.getByArea(csceneBO.getId(), topo.getAreaId());
    }

    private List<CsceneRoleModel> convertToRoleObjs(List<CsceneRoleBO> roleBOs, String parentId) {
        List<CsceneRoleModel> roleObjs = new ArrayList<>();
        if (roleBOs == null) {
            return roleObjs;
        }

        for (CsceneRoleBO roleBO : roleBOs) {
            var roleObj = roleBO.toModel();
            roleObj.setParent(parentId);
            roleObj.initDefault(BaseModel::getId);
            roleObjs.add(roleObj);

            var children = roleBO.getChildren();
            if (children != null) {
                roleObjs.addAll(convertToRoleObjs(children, roleObj.getId()));
            }
        }
        return roleObjs;
    }

    @ApiOperation(value = "业务服务端保存（创建/更新）场景模板", notes = "不会校验节点数量,请忽略test_scenes和app_scenes字段")
    @ApiImplicitParam(name = "csceneBO", value = "请求体", dataTypeClass = CsceneBO.class)
    @PostMapping("/server_api/sync")
    public ServiceResult<CsceneBO> serverApiSync(@RequestBody CsceneBO csceneBO) {
        log.info("scene sync,params is {}", JsonCommon.stringify(csceneBO));
        boolean isUpdate = csceneBO != null && StringUtils.isNotEmpty(csceneBO.getId());
        correctRequestCsceneParamsForSync(csceneBO);
        ServiceResult<CsceneBO> result = modelService.sync(csceneBO, null, false).asResult();
        if (csceneBO.getType() != null && csceneBO.getType() == CsceneModel.Type.TEMPLATE) {
            if (isUpdate) {
                CsceneLogUtils.recordSceneConfigLog(csceneBO.getId(), OpTypeEnums.UPDATE, CsceneLogUtils.UPDATE_SCENE_CONFIG, csceneBO.getContext());
            } else {
                CsceneLogUtils.recordSceneConfigLog(result.getData(), OpTypeEnums.CREATE, CsceneLogUtils.CREATE_SCENE_CONFIG, csceneBO.getContext());
            }
        }
        return result;
    }

    @ApiOperation("克隆指定id场景模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneActionRequest.class)
    })
    @PostMapping(value = "/clone")
    public ServiceResult<CsceneBO> clone(@RequestParam(required = false) String id,
                                         @RequestBody(required = false) CsceneActionRequest request) {
        request = formatActionRequest(request, id);
        ServiceResult<CsceneBO> result = modelService.clone(request.getId(), null).asResult();
        CsceneLogUtils.recordSceneConfigLog(request.getId(), OpTypeEnums.CREATE, CsceneLogUtils.CREATE_SCENE_CONFIG, request.getContext());
        return result;
    }

    @AuthTarget(TargetEnum.CSCENE_TARGET_GROUP)
    @ApiOperation("删除场景模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneActionRequest.class)
    })
    @DeleteMapping
    public ServiceResult<Boolean> delete(@RequestParam(required = false) String id,
                                         @RequestParam(required = false) String areaId,
                                         @RequestBody(required = false) CsceneActionRequest request) {
        log.info("delete cscene config , cscene id is {}", id);
        request = formatActionRequest(request, id);
        if (StringUtils.isNotEmpty(areaId)) {
            request.setAreaId(areaId);
        }
        ServiceResult<Boolean> result = modelService.deleteById(request.getId(), request.getAreaId(), true, false, null)
                .asResult();
        CsceneLogUtils.recordSceneConfigLog(request.getId(), OpTypeEnums.DELETE, CsceneLogUtils.DELETE_SCENE_CONFIG, request.getContext());
        return result;
    }

    @ApiOperation("批量删除场景模板")
    @ApiImplicitParam(name = "ids", value = "删除数据请求体", dataTypeClass = String[].class)
    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        log.info("delete cscene configs , cscene ids is {}", JsonCommon.stringify(ids));
        ServiceResult<Integer> result = modelService.bulkDeleteByIds(ids, true, false, null).asResult();
        CsceneLogUtils.recordSceneConfigLog(Arrays.asList(ids), OpTypeEnums.DELETE, CsceneLogUtils.BATCH_DELETE_SCENE_CONFIGS, null);
        return result;
    }

    @ApiOperation("创建场景模板测试实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "新增数据请求体", dataTypeClass = CsceneCreateInstanceRequest.class)
    })
    @PostMapping(value = "/test_instance")
    public ServiceResult<CsceneInstanceBO> createTestInstance(
            @RequestParam(required = false) String id, @RequestBody(required = false) CsceneCreateInstanceRequest request) {
        log.info("test_instance params,id:{},request:{}", id, JsonCommon.stringify(request));
        request = formatCreateInstanceRequest(request, id);
        correctRequestCsceneParamsForAppInstance(request.getCscene());
        ServiceResult<CsceneInstanceBO> result = modelService.createTestInstance(request.getId(), false, request.toCsceneInstanceCreateContextBO()).asResult();
        CsceneLogUtils.recordSceneInstanceLog(result == null ? null : result.getData(), OpTypeEnums.START, CsceneLogUtils.START_SCENE, request.getContext());
        return result;
    }

    @ApiOperation("创建场景模板应用实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "新增数据请求体", dataTypeClass = CsceneCreateInstanceRequest.class)
    })
    @PostMapping(value = "/app_instance")
    public ServiceResult<CsceneInstanceBO> createAppInstance(
            @RequestParam(required = false) String id, @RequestBody(required = false) CsceneCreateInstanceRequest request) {
        log.info("app_instance params,id:{},request:{}", id, JsonCommon.stringify(request));
        request = formatCreateInstanceRequest(request, id);
        correctRequestCsceneParamsForAppInstance(request.getCscene());
        ServiceResult<CsceneInstanceBO> result = modelService.createAppInstance(request.getId(), false, request.toCsceneInstanceCreateContextBO()).asResult();
        CsceneLogUtils.recordSceneInstanceLog(result == null ? null : result.getData(), OpTypeEnums.START, CsceneLogUtils.START_SCENE, request.getContext());
        return result;
    }

    @ApiOperation("获取场景模板统计数据")
    @ApiImplicitParam(name = "ids", value = "请求参数", dataTypeClass = String[].class)
    @GetMapping("/stat")
    public ServiceResult<List<CsceneStat>> statCscenes(@RequestParam String[] ids) {
        return csceneSpecialService.statCscenes(ids).asResult();
    }

    @ApiOperation("统计靶标对应引用该靶标的场景模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "images", value = "靶标名称", dataTypeClass = String[].class, allowMultiple = true),
    })
    @GetMapping("/stat_device_refer_cscenes")
    public ServiceResult<Map<String, List<DeviceReferCsceneStat>>> statDeviceReferCscenes(
            @RequestParam String[] images) {
        return csceneSpecialService.statDeviceReferCscenes(images).asResult();
    }

    private void mergeSceneConfig(SceneConfigDataBO originSceneConfig, SceneConfigDataBO extraSceneConfig) {
        if (Objects.nonNull(originSceneConfig) && Objects.nonNull(extraSceneConfig)) {
            if (Objects.nonNull(originSceneConfig.getTopology()) && Objects.nonNull(extraSceneConfig.getTopology())) {
                originSceneConfig.getTopology().getElements().addAll(extraSceneConfig.getTopology().getElements());
                originSceneConfig.getTopology().getAreas().addAll(extraSceneConfig.getTopology().getAreas());
                originSceneConfig.getTopology().getConnections().addAll(extraSceneConfig.getTopology().getConnections());
                originSceneConfig.getTopology().getNodeConnections().addAll(extraSceneConfig.getTopology().getNodeConnections());
            }
            if (Objects.nonNull(originSceneConfig.getPlugins()) && Objects.nonNull(extraSceneConfig.getPlugins())) {
                extraSceneConfig.getPlugins().forEach(((s, scenePluginDataBO) -> {
                    if (originSceneConfig.getPlugins().containsKey(s)) {
                        originSceneConfig.getPlugins().get(s).getElements().putAll(scenePluginDataBO.getElements());
                        originSceneConfig.getPlugins().get(s).getElementPorts().putAll(scenePluginDataBO.getElementPorts());
                    } else {
                        originSceneConfig.getPlugins().put(s, scenePluginDataBO);
                    }
                }));
            }
        }
    }

    private String parseNodeId(String input){
        String regex = "port_(.+)_layer_[0-9]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String extracted = matcher.group(1);
            return extracted;
        }
        return input;
    }
}
