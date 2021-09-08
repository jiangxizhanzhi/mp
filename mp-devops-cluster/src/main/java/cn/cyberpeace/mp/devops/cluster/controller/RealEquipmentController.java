package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.RealEquipmentConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipment;
import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipmentConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.SnmpParams;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtEntity;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.response.*;
import cn.cyberpeace.mp.devops.cluster.response.base.PageResult;
import cn.cyberpeace.mp.devops.cluster.response.base.Result;
import cn.cyberpeace.mp.devops.cluster.service.RealEquipmentService;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/real-equipment")
@Api(tags = "靶标管理-实装管理")
public class RealEquipmentController {

    private RealEquipmentConverter realEquipmentConverter;

    private RealEquipmentService realEquipmentService;

    @Autowired
    public RealEquipmentController(RealEquipmentConverter realEquipmentConverter,RealEquipmentService realEquipmentService) {
        this.realEquipmentConverter = realEquipmentConverter;
        this.realEquipmentService = realEquipmentService;
    }

    /**
     * 分页查询
     */
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public PageResult<RealEquipmentPageResponse> page(RealEquipmentPageRequest request){
        RealEquipmentConvertModel model = realEquipmentConverter.page(request);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getModelList())){
            return new PageResult<>(Lists.newArrayList(), NumberUtils.INTEGER_ZERO);
        }
        List<RealEquipmentPageResponse> list = model.getModelList().stream().map(v -> {
            ExtEntity extEntity = v.getExtEntity();
            RealEquipmentPageResponse response = RealEquipmentPageResponse.builder().id(v.getUuid()).name(v.getName())
                    .category(v.getMainCategory()).subCategory(v.getSubCategory())
                    .description(v.getDescription()).extSystemInfo(v.getExtSystemInfo()).useStatus(v.getUseStatus()).referenceStatus(v.getReferenceStatus())
                    .inPosition(v.getInPosition()).build();
            if (Objects.nonNull(extEntity)){
                response.setModel(extEntity.getStyle());
                response.setParams(extEntity.getParams());
                response.setEntityType(extEntity.getAccessType());
                response.setEntityDetail(extEntity.getAccessProtocols());
            }
            return response;
        }).collect(Collectors.toList());

        return new PageResult<>(list,model.getTotal());
    }

    @ApiOperation("根据ID查询")
    @GetMapping("/{id}")
    public Result<RealEquipmentByIdResponse> getById(@PathVariable String id){
        RealEquipment realEquipment = realEquipmentConverter.findById(id);
        RealEquipmentByIdResponse response = RealEquipmentByIdResponse.builder().uuid(realEquipment.getUuid())
                .name(realEquipment.getName()).logo(realEquipment.getLogo()).role(realEquipment.getRole())
                .mainCategory(realEquipment.getMainCategory()).subCategory(realEquipment.getSubCategory())
                .extSystemInfo(realEquipment.getExtSystemInfo()).extEntity(realEquipment.getExtEntity())
                .description(realEquipment.getDescription()).build();
        return new Result(response);
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/add")
    public ServiceResult add(@RequestBody RealEquipmentRequest request){
        RealEquipment realEquipment = realEquipmentConverter.add(request);
        return realEquipment.isSuccess() ? ServiceResult.successful() :ServiceResult.fail("");
    }

    /**
     * 更新
     */
    @ApiOperation("更新")
    @PutMapping("/update")
    public ServiceResult update(@RequestBody RealEquipmentRequest request){
        if (StringUtils.isBlank(request.getUuid())){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        RealEquipment realEquipment = realEquipmentConverter.update(request);
        return realEquipment.isSuccess() ? ServiceResult.successful() :ServiceResult.fail("");
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @DeleteMapping("/delete")
    public Result<Boolean> delete(@RequestBody RealEquipmentDeleteRequest request){
        Boolean result = realEquipmentConverter.delete(request);
        return new Result<>(result);
    }

    /**
     * 登录
     */
    @ApiOperation("登录")
    @PostMapping("/sign-in")
    public Result<Boolean> signIn(@RequestBody RealEquipmentSignInRequest request){
//        Boolean result = realEquipmentConverter.delete(request);
        return new Result<>();
    }

    /**
     * 释放
     */
    @ApiOperation("释放")
    @PostMapping("/freed")
    public Result<Boolean> freed(@RequestBody RealEquipmentFreedRequest request){
        return new Result<>();
    }

    @ApiOperation("查询详情")
    @GetMapping("/get-devops-details")
    public Result<RealEquipmentDevopsDetailResponse> findDevopsDetails(@RequestParam("uuid") String id){
        RealEquipment realEquipment = realEquipmentConverter.findById(id);
        RealEquipmentDevopsDetailResponse response = RealEquipmentDevopsDetailResponse.builder()
                .name(realEquipment.getName()).logo(realEquipment.getLogo())
                .extSystemInfo(realEquipment.getExtSystemInfo()).extEntity(realEquipment.getExtEntity()).build();

        if (Objects.nonNull(realEquipment) && Objects.nonNull(realEquipment.getExtEntity())){
            ExtEntity extEntity = realEquipment.getExtEntity();
            response.setAddress(extEntity.getAccessIpAddress());
            response.setModel(extEntity.getStyle());
            response.setParams(extEntity.getParams());
            if (Objects.nonNull(extEntity.getSnmpConfigs())) {
                SnmpParams snmpParams = new SnmpParams();
                if (extEntity.getSnmpVersion().equals("V1")) {
                    snmpParams.setVersion(1);
                } else if (extEntity.getSnmpVersion().equals("V2C")) {
                    snmpParams.setVersion(2);
                } else if (extEntity.getSnmpVersion().equals("V3")) {
                    snmpParams.setVersion(3);
                }
                snmpParams.setIp(extEntity.getSnmpConfigs().getIp());
                snmpParams.setAuth(extEntity.getSnmpConfigs());
                realEquipmentService.snmpForCpu(snmpParams);
                realEquipmentService.snmpForRam(snmpParams);
                realEquipmentService.snmpForDisk(snmpParams);
            }
        }

        return new Result<>(response);
    }


    @ApiOperation("接口资源")
    @GetMapping("/network-card")
    public PageResult<RealEquipmentNetworkCardPageResponse> pageNetworkCard(RealEquipmentNetworkCardPageRequest request){
        return new PageResult<>(Lists.newArrayList(),100);
    }

    @ApiOperation("场景引用")
    @GetMapping("/scenes")
    public PageResult<RealEquipmentScenesPageResponse> pageScenes(RealEquipmentScenesPageRequest request){
        return new PageResult<>(Lists.newArrayList(),100);
    }
}
