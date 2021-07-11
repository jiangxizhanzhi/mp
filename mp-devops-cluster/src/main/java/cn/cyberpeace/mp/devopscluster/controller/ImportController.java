package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.ImportConverter;
import cn.cyberpeace.mp.devopscluster.vo.ImportDiskSpaceVO;
import cn.cyberpeace.mp.devopscluster.vo.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Validated
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/import")
@Api(tags = "靶标导入管理")
@RequiredArgsConstructor
public class ImportController {

    private final ImportConverter importConverter;

    
    @ApiOperation("获取磁盘剩余空间")
    @GetMapping("/disk_space")
    public Result<ImportDiskSpaceVO> getDiskSpace(){
        ImportDiskSpaceVO diskSpaceResponse = importConverter.getDiskSpace();
        return new Result(diskSpaceResponse);
    }

}
