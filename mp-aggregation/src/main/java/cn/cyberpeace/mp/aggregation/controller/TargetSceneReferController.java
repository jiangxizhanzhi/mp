package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.constant.ControllerConstant;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneStat;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(value = "靶标场景引用", tags = "靶标场景引用")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/target_scene_refer")
public class TargetSceneReferController {

    private CsceneSpecialService csceneSpecialService;

    @Autowired
    public TargetSceneReferController(CsceneSpecialService csceneSpecialService) {
        this.csceneSpecialService = csceneSpecialService;
    }

    @ApiOperation("分页查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "name", value = "名称", dataTypeClass = String.class),
        @ApiImplicitParam(name = "per_page", value = "每页大小", dataTypeClass = Integer.class),
        @ApiImplicitParam(name = "page", value = "页码", dataTypeClass = Integer.class),
    })
    @GetMapping("/page")
    public ServiceResult<DeviceReferCsceneInstanceStat> page(@RequestParam("name") String name, @RequestParam(value = "per_page", defaultValue = "10") Integer perPage,
                                                             @RequestParam(value = "page", defaultValue = "1") Integer page) {
        String[] imageNames = new String[]{ name };
        ServiceResult<Map<String, List<DeviceReferCsceneStat>>> mapServiceResult = csceneSpecialService.statDeviceReferCscenes(imageNames);
        Map<String, List<DeviceReferCsceneStat>> source = mapServiceResult.getSource();
        if (MapUtils.isEmpty(source)) {
            return new ListObjServiceResult(new ListObjResult()).asResult();
        }
        List<DeviceReferCsceneStat> list = source.get(name);
        if (CollectionUtils.isEmpty(list)) {
            return new ListObjServiceResult(new ListObjResult()).asResult();
        }
        Integer to = (page * perPage) > list.size() ? list.size() : page * perPage;
        List<DeviceReferCsceneStat> result = list.subList((page - 1) * perPage, to);
        
        List<DeviceReferCsceneInstanceStat> finalResult = result.stream().map(csceneStat -> {
            DeviceReferCsceneInstanceStat instanceStat = new DeviceReferCsceneInstanceStat();
            instanceStat.setCreateUser(csceneStat.getCreateUser());
            instanceStat.setCreateUsername(csceneStat.getCreateUsername());
            instanceStat.setCsceneId(csceneStat.getCsceneId());
            instanceStat.setName(csceneStat.getName());
            instanceStat.setReferenceImage(csceneStat.getReferenceImage());
            csceneSpecialService.loadEvent(instanceStat, csceneStat.getCsceneId());
            return instanceStat;
        }).collect(Collectors.toList());
        return new ListObjServiceResult(new ListObjResult(finalResult, list.size())).asResult();
    }
}
