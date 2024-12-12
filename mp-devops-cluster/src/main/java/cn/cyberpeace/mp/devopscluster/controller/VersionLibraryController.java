package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.model.SystemAlarmModel;
import cn.cyberpeace.mp.devopscluster.model.VersionLibraryModel;
import cn.cyberpeace.mp.devopscluster.request.alarm.VersionLibraryParams;
import cn.cyberpeace.mp.devopscluster.service.VersionLibraryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Objects;

import static cn.cyberpeace.mp.base.constant.BaseErrorCode.SUCCESS;

/**
 * @Description 软件/固件版本库controller
 * @Author qiaokang
 * @Date 2022-07-18 17:35:04
 */
@Slf4j
@Api(tags = "软件/固件版本库接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/version_library")
public class VersionLibraryController {

    private final static String DIRECTORY_PATH = "/home/ftp/";
    @Autowired
    private VersionLibraryService versionLibraryService;

    /**
     * 分页查询软件/固件版本库
     *
     * @param queryParams
     * @return
     */
    @ApiOperation("分页查询软件/固件版本库")
    @GetMapping("/page")
    public ListObjServiceResult<VersionLibraryModel> page(VersionLibraryParams queryParams) {
        if (Objects.isNull(queryParams.getOrders()) && Objects.isNull(queryParams.getSort())) {
            // 默认按时间降序
            String updateTime = BeanUtils.getBeanGetterFieldName(SystemAlarmModel::getUpdateTime);
            updateTime = TextUtils.humpToLine(updateTime);
            queryParams.setOrders(new String[]{"-" + updateTime});
        }
        ListObjServiceResult<VersionLibraryModel> listResult = versionLibraryService.page(queryParams).asResult();
        return listResult;
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @ApiImplicitParam(name = "request", value = "查询信息请求体", dataTypeClass = VersionLibraryModel.class)
    @PostMapping("/add")
    public ServiceResult<VersionLibraryModel> add(@RequestBody VersionLibraryModel versionLibraryModel) {
        if (fileExists(versionLibraryModel)) {
            return ResultCommon.fail("文件 " + versionLibraryModel.getName() + " 不存在于目录,请先上传软件 ");
        }
        ObjServiceResult<VersionLibraryModel> versionLibraryModelObjServiceResult = versionLibraryService.create(versionLibraryModel);
        String code = versionLibraryModelObjServiceResult.getServiceCode().getCode();
        if (SUCCESS.getCode().equals(code)) {
            return ResultCommon.success(versionLibraryModelObjServiceResult.getSource().getObj());
        } else {
            return ResultCommon.fail(versionLibraryModelObjServiceResult.getServiceCode().getMessage());
        }
    }

    private static boolean fileExists(VersionLibraryModel versionLibraryModel) {
        if(null == versionLibraryModel.getType() || 0== versionLibraryModel.getType()){
            File file = new File(DIRECTORY_PATH, versionLibraryModel.getSoftwareName());
            if (!file.exists()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 更新
     */
    @ApiOperation("更新")
    @ApiImplicitParam(name = "request", value = "更新信息请求体", dataTypeClass = VersionLibraryModel.class)
    @PostMapping("/update")
    public ServiceResult update(@RequestBody VersionLibraryModel versionLibraryModel) {
        if (fileExists(versionLibraryModel)) {
            return ResultCommon.fail("文件 " + versionLibraryModel.getName() + " 不存在于目录,请先上传软件 ");
        }
        ObjServiceResult<VersionLibraryModel> update = versionLibraryService.update(versionLibraryModel);
        String code = update.getServiceCode().getCode();
        if (SUCCESS.getCode().equals(code)) {
            return ResultCommon.success(update.getSource().getObj());
        } else {
            return ResultCommon.fail(update.getServiceCode().getMessage());
        }
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @ApiImplicitParam(name = "request", value = "删除信息请求体", dataTypeClass = String[].class)
    @DeleteMapping("")
    public ServiceResult delete(@RequestBody String[] ids) {
        versionLibraryService.bulkDelete(ids);
        return ResultCommon.success();
    }

    /**
     * 根据UUID查询
     */
    @ApiOperation("根据UUID查询")
    @ApiImplicitParam(name = "uuid", value = "软件/固件版本库ID", dataTypeClass = String.class)
    @GetMapping("/search/{uuid}")
    public ServiceResult<VersionLibraryModel> findByUuid(@PathVariable("uuid") String uuid) {
        ObjServiceResult<VersionLibraryModel> versionLibraryModelObjServiceResult = versionLibraryService.get(uuid);
        String code = versionLibraryModelObjServiceResult.getServiceCode().getCode();
        if (SUCCESS.getCode().equals(code)) {
            return ResultCommon.success(versionLibraryModelObjServiceResult.getSource().getObj());
        } else {
            return ResultCommon.fail(versionLibraryModelObjServiceResult.getServiceCode().getMessage());
        }
    }
}
