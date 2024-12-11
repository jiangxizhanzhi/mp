package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.request.DownloadFileRequest;
import cn.cyberpeace.mp.devopscluster.task.GenerateEntityLogfileTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 场景内通过frame执行操作
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/by_frame")
@Api(tags = "场景内通过frame执行操作")
@RequiredArgsConstructor
public class GenerateEntityLogfileController {

    @Autowired
    private GenerateEntityLogfileTask generateEntityLogfileTask;

    /**
     * 业务服务监控
     * @return
     */
    @ApiOperation("下载原始日志")
    @GetMapping("/download_file")
    public ServiceResult downloadFile(DownloadFileRequest downloadFileRequest, HttpServletRequest request, HttpServletResponse response){
        generateEntityLogfileTask.downloadFile(downloadFileRequest,request,response);
        return ResultCommon.success();
    }
}
