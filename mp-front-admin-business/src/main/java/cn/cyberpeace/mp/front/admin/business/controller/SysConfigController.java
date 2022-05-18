package cn.cyberpeace.mp.front.admin.business.controller;

import cn.cyberpeace.mp.base.controller.stereotype.UnlimitPagination;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.fdfsclient.FileResponseData;
import cn.cyberpeace.mp.common.constant.CommonErrorCode;
import cn.cyberpeace.mp.common.constant.FileSuffixConstant;
import cn.cyberpeace.mp.common.service.FileService;
import cn.cyberpeace.mp.common.util.SuffixCheckUtil;
import cn.cyberpeace.mp.front.admin.business.service.SysConfigCheckService;
import cn.cyberpeace.mp.front.admin.constant.AdminErrorCode;
import cn.cyberpeace.mp.front.admin.model.SysConfigModel;
import cn.cyberpeace.mp.front.admin.service.sys.SysConfigService;
import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
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
    public ServiceResult<List<SysConfigModel>> getall() {
        List<SysConfigModel> configs = sysConfigService.listAll();
        return ServiceResult.success(configs);
    }

    @ApiOperation(value = "根据keyname更新单条sysconfig", notes = "")
    @PostMapping("/update/bykeyname")
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
    @PostMapping("/updateall")
    public ServiceResult updateAll(@RequestBody @NotEmpty List<SysConfigModel> configs) {
        log.info("configs:{}", configs);
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
    @PostMapping(value = "/vpn/upload")
    @ResponseBody
    public ServiceResult<FileResponseData> importVPN(@RequestParam(value = "file") MultipartFile file) {
        if (!SuffixCheckUtil.checkByFile(file, FileSuffixConstant.IMPORT_VPN_LIST)) {
            return ServiceResult.fail(CommonErrorCode.UPLOAD_FILE_TYPE_ERROR);
        }

        return fileService.uploadFile(file);
    }

}
