package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.devops.cluster.converter.SwitchConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.SwitchConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.InterfaceDetail;
import cn.cyberpeace.mp.devops.cluster.model.Switches;
import cn.cyberpeace.mp.devops.cluster.model.SwitchesCloudPlatform;
import cn.cyberpeace.mp.devops.cluster.service.SwitchService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SwitchConverterImpl implements SwitchConverter {

    private SwitchService switchService;

    @Autowired
    public SwitchConverterImpl(SwitchService switchService) {
        this.switchService = switchService;
    }

    @Override
    public List<SwitchConvertModel> list() {
        SwitchesCloudPlatform model = switchService.list();
        if (Objects.isNull(model)){
            return Lists.newArrayList();
        }
        List<SwitchConvertModel> switchConvertModels = new ArrayList<>();
        for (Switches switches : model.getSwitchs()){
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
}
