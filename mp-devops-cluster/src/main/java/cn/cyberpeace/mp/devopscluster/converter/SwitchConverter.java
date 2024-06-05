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
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 运维管控-系统运维-网络实例管理-交换机
 * @author wanggang
 */
@Service
@RequiredArgsConstructor
public class SwitchConverter {

    private final SwitchService switchService;


    public SdnSwitch registerSdnSwitch(RegisterSdnSwitchCommand registerSdnSwitchCommand) {
        return switchService.add(registerSdnSwitchCommand);
    }



    public PageInfo<SdnSwitch> pageSdnSwitch(SearchSdnSwitchRequest searchSdnSwitchRequest) {
        return switchService.pageSdnSwitch(searchSdnSwitchRequest);
    }

    public void modifySdnSwitch(ModifySdnSwitchCommand modifySdnSwitchCommand, String name) {
        switchService.modify(modifySdnSwitchCommand, name);
    }

    public void deleteSdnSwitch(String name) {
        switchService.delete(name);
    }





    /**
     * 树形结构-交换机列表
     * @return
     */
    public List<SwitchConvertModel> treeList() {
        SwitchConvertModel switchConvertModel = new SwitchConvertModel();
        switchConvertModel.setLabel("test_switch");
        switchConvertModel.setValue("test_switch");

        SwitchConvertModel.Children children = new SwitchConvertModel.Children();
        children.setLabel("interface_1");
        children.setValue("interface_2");
        switchConvertModel.setChildren(Collections.singletonList(children));
        return Collections.singletonList(switchConvertModel);
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
