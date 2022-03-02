package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Flavor;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.FlavorService;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.model.FlavorConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.FlavorByIdCloudPlatformModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Flavor
 * @author wanggang
 */
@Service
@Slf4j
public class FlavorConverter{

    private FlavorService flavorService;

    @Autowired
    public FlavorConverter(FlavorService flavorService) {
        this.flavorService = flavorService;
    }

    /**
     * 根据ID查询Flavor信息
     * @param id
     * @return
     */
    public FlavorConvertModel getById(String id) {
        FlavorByIdCloudPlatformModel model = flavorService.getById(id);
        if (Objects.isNull(model) || !model.getCode().equals(ControllerConstant.CODE) || Objects.isNull(model.getData())){
            return null;
        }
        Flavor flavor = model.getData();
        FlavorConvertModel convertModel = new FlavorConvertModel();
        convertModel.setCpu(flavor.getCpu());
        convertModel.setDescription(flavor.getDescription());
        convertModel.setDisk(flavor.getDisk());
        convertModel.setId(flavor.getId());
        convertModel.setName(flavor.getName());
        convertModel.setUuid(flavor.getUuid());
        convertModel.setRam(flavor.getRam());

        return convertModel;
    }
}
