package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.InterfaceDetail;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Switches;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.SwitchService;
import cn.cyberpeace.mp.devopscluster.converter.model.SwitchConvertModel;
import cn.cyberpeace.mp.devopscluster.model.SwitchesCloudPlatform;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class SwitchConverter {

    private final SwitchService switchService;

    
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

    
    public List<Switches> list() {
        SwitchesCloudPlatform model = switchService.list();
        if (Objects.isNull(model) || !model.isSuccess()){
            return Lists.newArrayList();
        }
        return model.getSwitchs();
    }

    
    public Integer getTotalSwitch() {
        return this.list().size();
    }
}
