package cn.cyberpeace.mp.devopscluster.controller;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import cn.cyberpeace.mp.devopscluster.cloud.platform.service.CloudStorageService;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 云存储相关接口
 * 
 * @Description:
 * @author zhangjiaxin
 * @date 2022年11月10日下午3:30:55
 */

@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cloud_storage")
@Api(tags = "云存储管理")
public class CloudStorageController {

    @Autowired
    private CloudStorageService cloudStorageService;

    /**
     * 云盘容量使用量
     */
    @ApiOperation("云盘容量使用量")
    @GetMapping("/capacity")
    public JSONObject capacity(@RequestParam(required = true, value = "con_id") String conId) {
        return cloudStorageService.capacity(conId);
    }

    /**
     * 变更分类
     */
    @ApiOperation("变更分类")
    @PutMapping("/file")
    public JSONObject changeFile(@RequestBody JSONObject request) {
        return cloudStorageService.changeFile(request);
    }

    /**
     * 删除文件或者目录
     */
    @ApiOperation("删除文件或者目录")
    @DeleteMapping("/file")
    public JSONObject deleteFile(@RequestBody JSONObject request) {
        return cloudStorageService.deleteFile(request);
    }

    /**
     * 上传文件
     */
    @ApiOperation("上传文件")
    @PostMapping("/file")
    public JSONObject uploadFile(@RequestParam(required = true, value = "con_id") String conId,
            @RequestParam(required = false, value = "dest_path") String destPath,
            @RequestParam(required = true, value = "file") MultipartFile file) {
        return cloudStorageService.uploadFile(conId, destPath, file);
    }

    /**
     * 下载文件
     */
    @ApiOperation("下载文件")
    @GetMapping("/file")
    public JSONObject downloadFile(@RequestParam(required = true, value = "con_id") String conId,
            @RequestParam(required = false, value = "download_file") String downloadFile,
            HttpServletResponse response) {
        return cloudStorageService.downloadFile(conId, downloadFile, response);
    }

    /**
     * 获取云盘目录list
     * @throws Exception 
     */
    @ApiOperation("目录 or 搜索")
    @GetMapping("/list")
    public JSONObject list(@RequestParam(required = true, value = "con_id") String conId,
            @RequestParam(required = false, value = "path") String path,
            @RequestParam(required = false, value = "search_word") String searchWord) throws Exception {
        return StringUtils.isBlank(searchWord) ? cloudStorageService.list(conId, path)
                : cloudStorageService.search(conId, searchWord);
    }

}
