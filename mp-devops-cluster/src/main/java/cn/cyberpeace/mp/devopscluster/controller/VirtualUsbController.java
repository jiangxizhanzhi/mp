package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.VirtualUsbStorage;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.VirtualUsbService;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.request.VirtualUsbDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.queryparam.VirtualUsbQueryParams;
import cn.cyberpeace.mp.devopscluster.vo.TargetUploadVO;
import cn.cyberpeace.mp.devopscluster.vo.VirtualUsbVO;
import cn.cyberpeace.mp.devopscluster.vo.common.Result;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/virtual_usb")
@Api(tags = "靶标管理-USB仿真")
public class VirtualUsbController {

    @Autowired
    private VirtualUsbService usbServicesbService;

    
    @ApiOperation("分页查询虚拟USB信息")
    @GetMapping("/page")
    public ListObjServiceResult<VirtualUsbVO> page(VirtualUsbQueryParams queryParams) {
        queryParams.ensure();
        ListObjServiceResult<VirtualUsbVO> result = usbServicesbService.virtualUsbPage(queryParams);
        return result;
    }

    
    @ApiOperation("根据UUID查询虚拟USB信息")
    @ApiImplicitParam(name = "uuid", value = "虚拟USB的uuid", dataTypeClass = String.class)
    @GetMapping("/find_by_uuid")
    public Result<VirtualUsbVO> findByUuid(@RequestParam String uuid){
        VirtualUsbVO virtualUsbVO = usbServicesbService.findByUuid(uuid);
        return new Result<>(virtualUsbVO);
    }

    
    @ApiOperation("新增虚拟USB")
    @PostMapping("/add")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.CREATE, title = "新增虚拟USB", target = OpTargetEnums.VIRTUAL_USB)
    public ServiceResult add(@RequestBody VirtualUsbVO virtualUsbVO){
        usbServicesbService.add(virtualUsbVO);
        return ResultCommon.success();
    }

    
    @ApiOperation("编辑虚拟USB")
    @PostMapping("/update")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.UPDATE, title = "编辑虚拟USB", target = OpTargetEnums.VIRTUAL_USB)
    public ServiceResult update(@RequestBody VirtualUsbVO virtualUsbVO){
        usbServicesbService.update(virtualUsbVO);
        return ResultCommon.success();
    }

    
    @ApiOperation("删除虚拟USB")
    @ApiImplicitParam(name = "request", value = "删除虚拟USB请求体", dataTypeClass = VirtualUsbDeleteRequest.class)
    @DeleteMapping("/delete")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.DELETE, title = "删除虚拟USB", target = OpTargetEnums.VIRTUAL_USB)
    public ServiceResult delete(@RequestBody VirtualUsbDeleteRequest request){
        usbServicesbService.delete(request);
        return ResultCommon.success();
    }

    
    @ApiOperation("根据UUID查询虚拟USB文件列表")
    @ApiImplicitParam(name = "uuid", value = "虚拟USB的uuid", dataTypeClass = String.class)
    @GetMapping("/file_list_by_uuid")
    public Result<VirtualUsbStorage> fileListByUuid(@RequestParam String uuid){
        VirtualUsbStorage virtualUsbStorage = usbServicesbService.fileListByUuid(uuid);
        return new Result<>(virtualUsbStorage);
    }

    
    @ApiOperation("上传文件到虚拟USB")
    @ApiImplicitParam(name = "uuid", value = "虚拟USB的uuid", dataTypeClass = String.class)
    @PostMapping(value = "/upload")
    @ResponseBody
    public ServiceResult<?> upload(MultipartFile file, @RequestParam String uuid) {
        BaseCloudPlatformResult result = usbServicesbService.upload(file, uuid);
        return new Result(new TargetUploadVO(result.getTmpdirUuid(),result.getFileNames()));
    }

    
    @ApiOperation("虚拟USB删除文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "虚拟USB的uuid", dataTypeClass = String.class),
            @ApiImplicitParam(name = "fileName", value = "文件名", dataTypeClass = String.class)
    })
    @DeleteMapping("/delete_file")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.DELETE, title = "虚拟USB删除文件", target = OpTargetEnums.VIRTUAL_USB)
    public ServiceResult deleteFile(@RequestParam String uuid, @RequestParam String fileName){
        usbServicesbService.deleteFile(uuid, fileName);
        return ResultCommon.success();
    }

    
    @ApiOperation("虚拟USB格式化")
    @ApiImplicitParam(name = "uuid", value = "虚拟USB的uuid", dataTypeClass = String.class)
    @DeleteMapping("/delete_all_files")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.DELETE, title = "虚拟USB格式化", target = OpTargetEnums.VIRTUAL_USB)
    public ServiceResult deleteAllFiles(@RequestParam String uuid){
        usbServicesbService.deleteAllFiles(uuid);
        return ResultCommon.success();
    }

    
    @ApiOperation("虚拟USB下载文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "虚拟USB的uuid", dataTypeClass = String.class),
            @ApiImplicitParam(name = "fileName", value = "文件名", dataTypeClass = String.class)
    })
    @GetMapping("/download_file")
    public Result<String> downloadFile(@RequestParam String uuid, @RequestParam String fileName){
        String url = usbServicesbService.downloadFile(uuid, fileName);
        return new Result<>(url);
    }
}
