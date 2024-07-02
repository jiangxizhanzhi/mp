package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.SdnSwitch;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudDeleteResponse;
import cn.cyberpeace.mp.devopscluster.model.CloudResponse;
import cn.cyberpeace.mp.devopscluster.model.SwitchesCloudPlatform;
import cn.cyberpeace.mp.devopscluster.request.ModifySdnSwitchCommand;
import cn.cyberpeace.mp.devopscluster.request.RegisterSdnSwitchCommand;
import cn.cyberpeace.mp.devopscluster.request.SearchSdnSwitchRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import static cn.cyberpeace.mp.base.constant.BaseErrorCode.CUSTOM_ERROR;

/**
 * @ClassName SwitchService
 * @Description 运维管控-系统运维-网络实例管理-交换机
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCpService
 */
@Service
@Slf4j
public class SwitchService extends BaseCpService {
    private static final String REGISTER_PHYSIC_SWITCH = "/cloud/v1/physical/physical_switch";
    private static final String LIST_PHYSIC_SWITCH = "/cloud/v1/physical/physical_switch";

    public static final String MODIFY_PHYSIC_SWITCH = "/cloud/v1/physical/physical_switch/{name}";

    public static final String DELETE_PHYSIC_SWITCH = "/cloud/v1/physical/physical_switch/{name}";

    public static final String LIST_PHYSIC_SWITCH_DETAIL = "/cloud/v1/switch/switchs";

    public SwitchService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 查询交换机信息
     * @return
     */
    public SwitchesCloudPlatform list() {
        return super.sendGet(LIST_PHYSIC_SWITCH_DETAIL , SwitchesCloudPlatform.class);
    }

    /**
     * delete
     *
     * @param name
     */
    public void delete(String name) {
        String requestUrl = UriComponentsBuilder.fromUriString(DELETE_PHYSIC_SWITCH).buildAndExpand(name).toUriString();
        CloudDeleteResponse cloudDeleteResponse = super.sendDelete(requestUrl, "", CloudDeleteResponse.class);
        if (!cloudDeleteResponse.successFlag()) {
            throw new MpRuntimeException(CUSTOM_ERROR.format("删除交换机异常"), cloudDeleteResponse.getMessage());
        }

    }


    /**
     * modify
     *
     * @param modifySdnSwitchCommand
     * @param name
     */
    public void modify(ModifySdnSwitchCommand modifySdnSwitchCommand, String name) {
        modifySdnSwitchCommand.base64EncodePw();
        String requestUrl = UriComponentsBuilder.fromUriString(MODIFY_PHYSIC_SWITCH).buildAndExpand(name).toUriString();
        super.sendPatch(requestUrl, JsonCommon.snakeStringify(modifySdnSwitchCommand));
    }

    /**
     * add
     *
     * @param registerSdnSwitchCommand
     * @return
     */
    public SdnSwitch add(RegisterSdnSwitchCommand registerSdnSwitchCommand) {
        registerSdnSwitchCommand.base64EncodePw();
        return super.sendPost(REGISTER_PHYSIC_SWITCH, JsonCommon.snakeStringify(registerSdnSwitchCommand), new TypeReference<SdnSwitch>() {
        });
    }

    /**
     * pageSdnSwitch
     *
     * @param searchSdnSwitchRequest
     * @return
     */
    public PageInfo<SdnSwitch> pageSdnSwitch(SearchSdnSwitchRequest searchSdnSwitchRequest) {
        String requestParams = ParamUtils.wrapperGetParamsByObject(true, searchSdnSwitchRequest);
        CloudResponse<SdnSwitch> cloudResponse = super.sendGet(LIST_PHYSIC_SWITCH + requestParams, new TypeReference<CloudResponse<SdnSwitch>>() {
        });
        PageInfo<SdnSwitch> page = new PageInfo<>(cloudResponse.getData());
        page.setPageNum(searchSdnSwitchRequest.getPerPage());
        page.setPageSize(searchSdnSwitchRequest.getPage());
        page.setTotal(cloudResponse.getTotal());
        page.setList(cloudResponse.getData());
        return page;
    }

}
