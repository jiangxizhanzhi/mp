package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.RealEquipmentConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.common.ExtEntity;
import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipment;
import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipmentConvertModel;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.RealEquipmentByIdResponse;
import cn.cyberpeace.mp.devops.cluster.response.RealEquipmentPageResponse;
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
@Api(tags = "实装管理")
public class RealEquipmentController {

    private RealEquipmentConverter realEquipmentConverter;

    @Autowired
    public RealEquipmentController(RealEquipmentConverter realEquipmentConverter) {
        this.realEquipmentConverter = realEquipmentConverter;
    }

    /**
     * 分页查询
     */
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public ListObjServiceResult<RealEquipmentPageResponse> page(RealEquipmentPageRequest request){
        RealEquipmentConvertModel model = realEquipmentConverter.page(request);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getModelList())){
            return new ListObjServiceResult<>(Lists.newArrayList(), NumberUtils.INTEGER_ZERO);
        }
        List<RealEquipmentPageResponse> list = model.getModelList().stream().map(v -> {
            ExtEntity extEntity = v.getExtEntity();
            RealEquipmentPageResponse response = RealEquipmentPageResponse.builder().id(v.getUuid()).name(v.getName())
                    .category(v.getMainCategory()).subCategory(v.getSubCategory())
                    .description(v.getDescription()).build();
            if (Objects.nonNull(extEntity)){
                response.setModel(extEntity.getModel());
                response.setParams(extEntity.getParams());
                response.setEntityType(extEntity.getAccessType());
                response.setStatus(extEntity.getEntityStatus());
                response.setEntityDetail(extEntity.getAccessProtocols());
            }
            return response;
        }).collect(Collectors.toList());

        return new ListObjServiceResult<>(list,model.getTotal());
    }

    @ApiOperation("根据ID查询")
    @GetMapping("/{id}")
    public ServiceResult<RealEquipmentByIdResponse> getById(@PathVariable String id){
        RealEquipment realEquipment = realEquipmentConverter.findById(id);
        RealEquipmentByIdResponse response = RealEquipmentByIdResponse.builder().uuid(realEquipment.getUuid())
                .name(realEquipment.getName()).logo(realEquipment.getLogo()).role(realEquipment.getRole())
                .mainCategory(realEquipment.getMainCategory()).subCategory(realEquipment.getSubCategory())
                .systemInfo(realEquipment.getSystemInfo()).extEntity(realEquipment.getExtEntity())
                .description(realEquipment.getDescription()).build();
        return new ServiceResult(response);
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/add")
    public ServiceResult<Boolean> add(@RequestBody RealEquipmentRequest request){
        RealEquipment realEquipment = realEquipmentConverter.add(request);
        return new ServiceResult<>(realEquipment.isSuccess());
    }

    /**
     * 更新
     */
    @ApiOperation("更新")
    @PutMapping("/update")
    public ServiceResult<Boolean> update(@RequestBody RealEquipmentRequest request){
        if (StringUtils.isBlank(request.getUuid())){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        RealEquipment realEquipment = realEquipmentConverter.update(request);
        return new ServiceResult<>(realEquipment.isSuccess());
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @DeleteMapping("/delete")
    public ServiceResult<Boolean> delete(@RequestBody RealEquipmentDeleteRequest request){
        Boolean result = realEquipmentConverter.delete(request);
        return new ServiceResult<>(result);
    }


}
