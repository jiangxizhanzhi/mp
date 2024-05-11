package cn.cyberpeace.mp.sceneplugincollection.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.sceneplugincollection.bo.ImportFileBO;
import cn.cyberpeace.mp.sceneplugincollection.service.InfoCollectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description 采集信息导入导出（json、xml、xlsx）
 * @Author ling_yu
 * @Date 2024/3/28 16:40
 */
@Api(value = "采集信息导入导出 (Json,Xml,Xlsx)", tags = "采集信息导入导出 (Json,Xml,Xlsx)")
@Slf4j
@RestController
@RequestMapping("/info_collection")
public class InfoCollectionController {

    @Autowired
    private InfoCollectionService infoCollectionService;

    /**
     * 文件导入 (Json,Xml,Xlsx)
     */
    @ApiOperation("文件导入 (Json,Xml,Xlsx)")
    @PostMapping("/import")
    public ServiceResult<Boolean> importFile(@RequestBody ImportFileBO bo) {
        // 校验文件
//        if (file.isEmpty() || StringUtils.isEmpty(file.getOriginalFilename())){
//            return ResultCommon.fail("文件不能为空");
//        }
//        infoCollectionService.importFile(file);
        return ResultCommon.success(true);
    }

    /**
     * 文件导出 (Json,Xml,Xlsx)
     * @param response
     */
    @ApiOperation("文件导出 (Json,Xml,Xlsx)")
    @GetMapping("/export/")
    public ServiceResult<Boolean> exportFile(HttpServletResponse response,
                                             @ApiParam(value = "导出格式(json、xml、xlsx)", required = true) String type,
                                             @ApiParam(value = "场景id", required = true) String sceneId,
                                             @ApiParam(value = "节点id") String nodeId) {
        infoCollectionService.exportFile(response, type);
        return ResultCommon.success(true);
    }

}
