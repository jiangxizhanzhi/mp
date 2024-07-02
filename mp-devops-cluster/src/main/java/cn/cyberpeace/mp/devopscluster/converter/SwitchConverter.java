package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.SdnSwitch;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Switches;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.SwitchService;
import cn.cyberpeace.mp.devopscluster.converter.model.SwitchConvertModel;
import cn.cyberpeace.mp.devopscluster.model.SwitchesCloudPlatform;
import cn.cyberpeace.mp.devopscluster.request.ModifySdnSwitchCommand;
import cn.cyberpeace.mp.devopscluster.request.RegisterSdnSwitchCommand;
import cn.cyberpeace.mp.devopscluster.request.SearchSdnSwitchRequest;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName SwitchConverter
 * @Description 运维管控-系统运维-网络实例管理-交换机
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class SwitchConverter {

    private final SwitchService switchService;


    /**
     * registerSdnSwitch
     *
     * @param registerSdnSwitchCommand
     * @return
     */
    public SdnSwitch registerSdnSwitch(RegisterSdnSwitchCommand registerSdnSwitchCommand) {
        return switchService.add(registerSdnSwitchCommand);
    }


    /**
     * pageSdnSwitch
     *
     * @param searchSdnSwitchRequest
     * @return
     */
    public PageInfo<SdnSwitch> pageSdnSwitch(SearchSdnSwitchRequest searchSdnSwitchRequest) {
        return switchService.pageSdnSwitch(searchSdnSwitchRequest);
    }

    /**
     * modifySdnSwitch
     *
     * @param modifySdnSwitchCommand
     * @param name
     */
    public void modifySdnSwitch(ModifySdnSwitchCommand modifySdnSwitchCommand, String name) {
        switchService.modify(modifySdnSwitchCommand, name);
    }

    /**
     * deleteSdnSwitch
     *
     * @param name
     */
    public void deleteSdnSwitch(String name) {
        switchService.delete(name);
    }





    /**
     * 树形结构-交换机列表
     * @return
     */
    public List<SwitchConvertModel> treeList() {
        SearchSdnSwitchRequest searchSdnSwitchRequest = new SearchSdnSwitchRequest();
        searchSdnSwitchRequest.setPerPage(Integer.MAX_VALUE);
        List<SdnSwitch> switches = switchService.pageSdnSwitch(new SearchSdnSwitchRequest()).getList();

        if (CollectionUtils.isEmpty(switches)) {
            return Collections.emptyList();
        } else {
            return switches.stream().map(sdnSwitch -> {
                SwitchConvertModel switchConvertModel = new SwitchConvertModel();
                switchConvertModel.setLabel(sdnSwitch.getName());
                switchConvertModel.setValue(sdnSwitch.getPhysicalSwitchName());

                List<SwitchConvertModel.Children> children = new ArrayList<>();
                for (int i = 0; i < sdnSwitch.getPortNum(); i++) {
                    SwitchConvertModel.Children child = new SwitchConvertModel.Children();
                    child.setLabel(getFormatSdnSwitchPortName(i+1));
                    child.setValue(String.valueOf(i+1));
                    children.add(child);
                }

                switchConvertModel.setChildren(children);
                return switchConvertModel;
            }).collect(Collectors.toList());
        }
    }

    /**
     * getFormatSdnSwitchPortName
     *
     * @param portIndex
     * @return
     */
    public String getFormatSdnSwitchPortName(Integer portIndex) {
        return "opf_g" + String.format("%0" + 6 + "d", portIndex);
    }

    /**
     * 查询交换机列表
     * @return
     */
    public List<Switches> list() {
        SwitchesCloudPlatform model = switchService.list();
        if (Objects.isNull(model) || !model.isSuccess()){
            return Lists.newArrayList();
        }
        return model.getSwitchs();
    }

    /**
     * 获取交换机总数
     * @return
     */
    public Integer getTotalSwitch() {
        return this.list().size();
    }
}
