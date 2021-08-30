package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.TypeConvert;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeAllConvertModel;
import cn.cyberpeace.mp.devops.cluster.response.TypeAllResponse;
import cn.cyberpeace.mp.devops.cluster.response.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 类型管理类
 */
@Validated
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/type")
@Api(tags = "靶标管理-类型")
public class TypeController {

    private TypeConvert typeConvert;

    @Autowired
    public TypeController(TypeConvert typeConvert) {
        this.typeConvert = typeConvert;
    }

    /**
     * 查询所有分类
     * @return
     */
    @ApiOperation("查询所有分类")
    @GetMapping("/categories")
    public Result<TypeAllResponse> list(){
        TypeAllConvertModel l1AndL2AndL3Type = typeConvert.findL1AndL2AndL3Type();
        if (Objects.isNull(l1AndL2AndL3Type)){
            return new Result<>();
        }
        TypeAllResponse response = new TypeAllResponse(l1AndL2AndL3Type.getFirstTypeList(),
                l1AndL2AndL3Type.getSecondTypeList(),l1AndL2AndL3Type.getThirdTypeList());
        return new Result<>(response);
    }

    /**
     * 查询所有平台
     * @return
     */
    @ApiOperation("查询所有平台数据")
    @GetMapping("/installer-platforms")
    public Result<TypeAllResponse> installerPlatforms(){
        TypeAllConvertModel model = typeConvert.findByType("installer_platforms");
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllResponse response = new TypeAllResponse();
        response.setIdStringModelList(model.getInstallerPlatformAndImageTypeList());
        return new Result<>(response);

    }

    /**
     * 查询所有角色
     * @return
     */
    @ApiOperation("查询所有角色")
    @GetMapping("/roles")
    public Result<TypeAllResponse>  roles(){
        TypeAllConvertModel model = typeConvert.findAllRoleType();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllResponse response = new TypeAllResponse();
        response.setIdIntegerModelList(model.getRoleList());
        return new Result<>(response);
    }

    /**
     * 查询所有虚拟类型
     * @return
     */
    @ApiOperation("查询所有虚拟类型")
    @GetMapping("/image-types")
    public Result<TypeAllResponse> imageType(){
        TypeAllConvertModel model = typeConvert.findByType("image_type");
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllResponse response = new TypeAllResponse();
        response.setIdStringModelList(model.getInstallerPlatformAndImageTypeList());
        return new Result<>(response);
    }

}
