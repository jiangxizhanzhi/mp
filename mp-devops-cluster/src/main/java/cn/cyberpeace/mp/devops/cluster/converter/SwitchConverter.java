package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.InterfaceDetail;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Switches;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.SwitchService;
import cn.cyberpeace.mp.devops.cluster.converter.model.SwitchConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.SwitchesCloudPlatform;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 运维管控-系统运维-网络实例管理-交换机
 * @author wanggang
 */
@Service
public class SwitchConverter {

    private SwitchService switchService;

    public SwitchConverter(SwitchService switchService) {
        this.switchService = switchService;
    }

    /**
     * 树形结构-交换机列表
     * @return
     */
    public List<SwitchConvertModel> treeList() {
        List<Switches> switchesList = this.list();
        List<SwitchConvertModel> switchConvertModels = new ArrayList<>();
        for (Switches switches : switchesList){
            SwitchConvertModel convertModel = new SwitchConvertModel();
            convertModel.setLabel(switches.getName());
            convertModel.setValue(switches.getName());
            if (CollectionUtils.isNotEmpty(switches.getInterfaceDetails())){
                List<SwitchConvertModel.Children> childrens = new ArrayList<>();
                for (InterfaceDetail interfaceDetail : switches.getInterfaceDetails()){
                    SwitchConvertModel.Children children = new SwitchConvertModel.Children();
                    children.setLabel(interfaceDetail.getInterfaceName());
                    children.setValue(interfaceDetail.getInterfaceName());
                    childrens.add(children);
                    convertModel.setChildren(childrens);
                }
            }
            switchConvertModels.add(convertModel);
        }
        return switchConvertModels;
    }

    /**
     * 查询交换机列表
     * @return
     */
    public List<Switches> list() {
        SwitchesCloudPlatform model = switchService.list();
        if (Objects.isNull(model)){
            return Lists.newArrayList();
        }
        return model.getSwitchs();
    }
}
