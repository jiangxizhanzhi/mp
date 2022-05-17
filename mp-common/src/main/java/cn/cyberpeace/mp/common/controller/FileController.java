package cn.cyberpeace.mp.common.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.fdfsclient.FileResponseData;
import cn.cyberpeace.mp.common.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 文件操作控制层
 * @Author qiaokang
 * @Date：2021/6/21 15:01
 */
@RestController
@Slf4j
@Api(value = "文件操作管理接口", tags = "[废弃]COMMON-文件操作管理接口")
@RequestMapping("/base")
public class FileController {

    @Qualifier("fastDfsFileServiceImpl")
    @Autowired
    private FileService fileService;

    /**
     * 上传文件通用，只上传文件到服务器
     *
     * @param file
     * @param request
     * @return 返回文件路径等信息
     */
    @ApiOperation("文件上传通用接口")
    @PostMapping(value = "/file/upload")
    @ResponseBody
    public ServiceResult<FileResponseData> uploadFile(MultipartFile file, HttpServletRequest request) {
        return fileService.uploadFile(file, request);
    }

}
