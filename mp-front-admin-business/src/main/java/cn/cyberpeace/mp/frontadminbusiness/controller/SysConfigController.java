package cn.cyberpeace.mp.frontadminbusiness.controller;

import cn.cyberpeace.mp.base.controller.stereotype.UnlimitPagination;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.constant.CommonErrorCode;
import cn.cyberpeace.mp.common.constant.FileSuffixConstant;
import cn.cyberpeace.mp.common.deprecated.FileResponseData;
import cn.cyberpeace.mp.common.service.FileService;
import cn.cyberpeace.mp.common.util.SuffixCheckUtil;
import cn.cyberpeace.mp.devopslog.annotation.OperateLog;
import cn.cyberpeace.mp.devopslog.enums.OpModuleEnums;
import cn.cyberpeace.mp.devopslog.enums.OpTargetEnums;
import cn.cyberpeace.mp.devopslog.enums.OpTypeEnums;
import cn.cyberpeace.mp.frontadmin.constant.AdminErrorCode;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;
import cn.cyberpeace.mp.frontadmin.service.sys.SysConfigService;
import cn.cyberpeace.mp.frontadminbusiness.service.SysConfigCheckService;
import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 系统配置管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "配置管理接口", tags = "AUTH-配置管理接口")
@Slf4j
@RestController
@RequestMapping("/sys/config")
@UnlimitPagination("list")
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private SysConfigCheckService checkService;

    /**
     * 文件上传客户端
     */
    @Qualifier("fastDfsFileServiceImpl")
    @Autowired
    private FileService fileService;


    @ApiOperation(value = "查询所有系统设置", notes = "")
    @GetMapping("/all")
    public ServiceResult<List<SysConfigModel>> listAll() {
        List<SysConfigModel> configs = sysConfigService.listAll();
        return ServiceResult.success(configs);
    }

    @ApiOperation(value = "根据keyname更新单条sysconfig", notes = "")
    @ApiImplicitParam(name = "updateConfig", value = "更新请求体")
    @PostMapping("/update/bykeyname")
    @OperateLog(module = OpModuleEnums.SYSTEM, type = OpTypeEnums.UPDATE, title = "更新系统配置", target = OpTargetEnums.SYS_CONFIG)
    public ServiceResult updateByKeyName(@RequestBody SysConfigModel updateConfig) {
        log.info("updateConfig:{}", updateConfig);
        checkService.updateCheck(Arrays.asList(updateConfig));

        boolean success = sysConfigService.updateByKeyName(updateConfig);
        if (success) {
            sysConfigService.publishConfigUpdateEvent(CollectionUtil.toList(updateConfig));
            return ServiceResult.successful();
        } else {
            log.error("update config failed! {}", updateConfig);
            throw new MpRuntimeException(AdminErrorCode.SYS_CONFIG_MODIFY_ERROR);
        }
    }

    @ApiOperation(value = "根据keyname批量更新sysconfig", notes = "")
    @ApiImplicitParam(name = "configs", value = "配置列表", dataTypeClass = List.class)
    @PostMapping("/updateall")
    @OperateLog(module = OpModuleEnums.SYSTEM, type = OpTypeEnums.UPDATE, title = "更新系统配置", target = OpTargetEnums.SYS_CONFIG)
    public ServiceResult updateAll(@RequestBody @NotEmpty List<SysConfigModel> configs) {
        // 更新规则，如果key存在，就修改value值，如果key不存在，就忽略不改
        checkService.updateCheck(configs);

        boolean success = sysConfigService.batchUpdateByKeyname(configs);
        if (success) {
            sysConfigService.publishConfigUpdateEvent(configs);
            return ServiceResult.successful();
        } else {
            log.error("update config failed!");
            throw new MpRuntimeException(AdminErrorCode.SYS_CONFIG_MODIFY_ERROR);
        }
    }

    /**
     * 系统logo导入
     *
     * @param file
     * @return
     */
    @ApiOperation("系统logo导入")
    @ApiImplicitParam(name = "file", value = "logo图片")
    @PostMapping(value = "/logo/upload")
    @ResponseBody
    public ServiceResult<FileResponseData> logoUpload(@RequestParam(value = "file") MultipartFile file) {
        if (!SuffixCheckUtil.checkByFile(file, FileSuffixConstant.IMPORT_SYSTEM_LOGO_LIST)) {
            return ServiceResult.fail(CommonErrorCode.UPLOAD_FILE_TYPE_ERROR);
        }

        return fileService.uploadFile(file);
    }

    /**
     * VPN软件包导入
     *
     * @param file
     * @return
     */
    @ApiOperation("VPN软件包导入")
    @ApiImplicitParam(name = "file", value = "vpn软件包")
    @PostMapping(value = "/vpn/upload")
    @ResponseBody
    public ServiceResult<FileResponseData> importVPN(@RequestParam(value = "file") MultipartFile file) {
        if (!SuffixCheckUtil.checkByFile(file, FileSuffixConstant.IMPORT_VPN_LIST)) {
            return ServiceResult.fail(CommonErrorCode.UPLOAD_FILE_TYPE_ERROR);
        }

        return fileService.uploadFile(file);
    }

}
