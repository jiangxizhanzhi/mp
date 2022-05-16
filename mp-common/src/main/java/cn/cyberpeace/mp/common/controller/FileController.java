package cn.cyberpeace.mp.common.controller;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.common.service.FileService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.fdfsclient.FileResponseData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 文件操作控制层
 * @Author qiaokang
 * @Date：2021/6/21 15:01
 */
@RestController
@Slf4j
@Api("文件操作管理接口")
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

    /**
     * 以附件形式下载文件
     *
     * @param filePath 文件地址
     * @param response
     */
    @ApiOperation("文件下载通用接口")
    @RequestMapping(value = "/file/download", method = {RequestMethod.POST, RequestMethod.GET})
    public void downloadFile(String filePath, HttpServletResponse response) throws MpRuntimeException {
        fileService.downloadFile(filePath, response);
    }


    /**
     * 根据指定的路径删除服务器文件
     *
     * @param filePath
     */
    @ApiOperation("文件删除通用接口")
    @DeleteMapping("/file/delete")
    public ServiceResult deleteFile(String filePath) {
        return fileService.deleteFile(filePath);
    }

}
