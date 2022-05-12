package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.dto.log.LogFileDownloadUrlRequest;
import cn.cyberpeace.mp.cscene.dto.log.LogFileVO;
import cn.cyberpeace.mp.cscene.service.LogFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/6 15:27
 */
@Api(value = "节点日志文件Api", tags = "节点日志文件Api")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/logfile")
public class LogFileController {


    @Autowired
    private LogFileService logFileService;

    /**
     * elementIds不能为空，如果为多个值，默认只取第一个
     *
     * @param pageRequest
     * @return
     */
    @ApiOperation(value = "分页查询日志文件")
    @GetMapping("/page")
    public ListObjServiceResult<LogFileVO> pageLogFiles(BaseSceneElementPageRequest pageRequest) {
        log.info("{}", pageRequest);
        ListObjServiceResult<LogFileVO> result = logFileService.pageByElementId(pageRequest);
        return result;
    }


    /**
     * elementIds不能为空，如果为多个值，默认只取第一个
     *
     * @param pageRequest
     * @return
     */
    @ApiOperation(value = "查询日志原始文件下载地址")
    @GetMapping("/download_url")
    public ListObjServiceResult<LogFileVO> getDownloadUrl(LogFileDownloadUrlRequest request) {
        log.info("{}", request);
        logFileService.getDownloadUrls(request);
        return null;
    }

}
