package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneNodeInfoBO;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.ContainerConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.ContainerBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.ContainerPageDTO;
import cn.cyberpeace.mp.devopscluster.request.ContainerPageRequest;
import cn.cyberpeace.mp.devopscluster.request.ReviseContainerOrVmRequest;
import cn.cyberpeace.mp.devopscluster.vo.ContainerPageVO;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 容器管理类
 * @author wanggang
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/container")
@Api(tags = "计算实例管理-容器")
@RequiredArgsConstructor
@AuthTarget(TargetEnum.NO_AUTH)
public class ContainerController {

    private final ContainerConverter containerConverter;

    private final CsceneSpecialService csceneSpecialService;

    private final UserService userService;

    /**
     * 重启
     * @param request
     * @return
     */
    @ApiOperation("重启容器")
    @PostMapping("/restart")
    public ServiceResult<Boolean> restart(@RequestBody ReviseContainerOrVmRequest request) {
        if (StringUtils.isEmpty(request.getId())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        Container container = new Container();
        return new ServiceResult<>(container.restart(request.getId())).asResult();
    }

    /**
     * 暂停
     * @param request
     * @return
     */
    @ApiOperation("暂停容器")
    @PostMapping("/pause")
    public ServiceResult<Boolean> pause(@RequestBody ReviseContainerOrVmRequest request) {
        if (StringUtils.isEmpty(request.getId())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        Container container = new Container();
        return new ServiceResult<>(container.pause(request.getId())).asResult();
    }

    /**
     * 恢复
     * @param request
     * @return
     */
    @ApiOperation("恢复容器")
    @PostMapping("/unpause")
    public ServiceResult<Boolean> unpause(@RequestBody ReviseContainerOrVmRequest request) {
        if (StringUtils.isEmpty(request.getId())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        Container container = new Container();
        return new ServiceResult<>(container.unPause(request.getId())).asResult();
    }

    /**
     * 开启
     * @param request
     * @return
     */
    @ApiOperation("开启容器")
    @PostMapping("/start")
    public ServiceResult<Boolean> start(@RequestBody ReviseContainerOrVmRequest request) {
        if (StringUtils.isEmpty(request.getId())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        Container container = new Container();
        return new ServiceResult<>(container.start(request.getId())).asResult();
    }

    /**
     * 关闭
     * @param request
     * @return
     */
    @ApiOperation("关闭容器")
    @PostMapping("/stop")
    public ServiceResult<Boolean> stop(@RequestBody ReviseContainerOrVmRequest request) {
        if (StringUtils.isEmpty(request.getId())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        Container container = new Container();
        return new ServiceResult<>(container.stop(request.getId())).asResult();
    }

    /**
     * 分页查询容器列表
     * @param request
     * @return
     */
    @AuthTarget(TargetEnum.CLUSTER_MANAGEMENT)
    @ApiOperation("分页查询容器列表")
    @GetMapping("/page")
    public PageResult<ContainerPageVO> page(ContainerPageRequest request){
        BasePageBO<ContainerBO> model = containerConverter.page(new ContainerPageDTO(request));
        List<ContainerPageVO> list = convertBOToPageVO(model);
        return new PageResult<>(list, model.getTotal());
    }

    /**
     * 转换BO to VO
     * @param model
     * @return
     */
    private List<ContainerPageVO> convertBOToPageVO(BasePageBO<ContainerBO> model){
        final List<ContainerBO> modelList = model.getList();
        List<String> nameList = new ArrayList<>();
        for (ContainerBO containerBO : modelList) {
            String name = containerBO.getName();
            nameList.add(name);
        }
        ServiceResult<Map<String, CsceneNodeInfoBO>> serviceResult = csceneSpecialService.getSceneInfoByNodeResourceName(nameList);
        Map<String, CsceneNodeInfoBO> source = serviceResult.getSource();

        List<UserBo> userBoList = new ArrayList<>();
        if (Objects.nonNull(source)) {
            List<CsceneNodeInfoBO> values = new ArrayList<>(source.values());
            if (CollectionUtils.isNotEmpty(values)) {
                List<String> userIdList = new ArrayList<>();
                for (CsceneNodeInfoBO value : values) {
                    String userId = value.getUserId();
                    userIdList.add(userId);
                }
                if (CollectionUtils.isNotEmpty(userIdList)) {
                    List<UserBo> list = userService.listByUserIds(userIdList.toArray(new String[]{}));
                    if (CollectionUtils.isNotEmpty(list)) {
                        userBoList = list;
                    }
                }
            }
        }

        List<UserBo> tmpUserBoList = userBoList;
        List<ContainerPageVO> list = new ArrayList<>();
        for (ContainerBO containerModel : modelList) {
            ContainerPageVO containerPageVO = getContainerPageVO(source, tmpUserBoList, containerModel);
            list.add(containerPageVO);
        }
        return list;
    }

    private static ContainerPageVO getContainerPageVO(Map<String, CsceneNodeInfoBO> source, List<UserBo> tmpUserBoList, ContainerBO containerModel) {
        CsceneNodeInfoBO csceneNodeInfoBO = new CsceneNodeInfoBO();
        if (Objects.nonNull(source.get(containerModel.getName()))) {
            csceneNodeInfoBO = source.get(containerModel.getName());
        }
        String userName = "";
        String userId = csceneNodeInfoBO.getUserId();
        if (StringUtils.isNotBlank(userId)) {
            Optional<UserBo> optional =
                    Optional.empty();
            for (UserBo userDto : tmpUserBoList) {
                if (userDto.getId().equals(userId)) {
                    optional = Optional.of(userDto);
                    break;
                }
            }
            if (optional.isPresent()) {
                userName = optional.get().getUsername();
            }
        }

        return ContainerPageVO.builder().name(containerModel.getName())
                .sceneName(StringUtils.isBlank(csceneNodeInfoBO.getCsceneName()) ? "" : csceneNodeInfoBO.getCsceneName())
                .id(containerModel.getId())
                .image(containerModel.getImage()).node(containerModel.getNode())
                .status(containerModel.getStatus()).addressList(containerModel.getAddressList())
                .createdAt(containerModel.getCreatedAt())
                .userName(userName)
                .referenceType(csceneNodeInfoBO.getReferenceType())
                .event(csceneNodeInfoBO.getEvent())
                .eventStatus(csceneNodeInfoBO.getEventStatus())
                .sceneStatus(csceneNodeInfoBO.getSceneStatus())
                .sceneStatusDesc(csceneNodeInfoBO.getSceneStatusDesc())
                .runningTime(containerModel.getRunningTime())
                .processMessage(csceneNodeInfoBO.getProcessMessage())
                .vlanIdList(containerModel.getVlanIdList()).build();
    }
}
