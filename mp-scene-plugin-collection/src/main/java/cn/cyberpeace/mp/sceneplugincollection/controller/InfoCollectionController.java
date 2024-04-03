package cn.cyberpeace.mp.sceneplugincollection.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.sceneplugincollection.service.InfoCollectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
     *
     * @param file
     * @return
     */
    @ApiOperation("文件导入 (Json,Xml,Xlsx)")
    @PostMapping("/import")
    public ServiceResult<String> importFile(@RequestBody MultipartFile file) {
        // 校验文件
        if (file.isEmpty() || StringUtils.isEmpty(file.getOriginalFilename())){
            return ResultCommon.fail("文件不能为空");
        }
        return infoCollectionService.importFile(file);
    }

    /**
     * 文件导出 (Json,Xml,Xlsx)
     * @param response
     */
    @ApiOperation("文件导出 (Json,Xml,Xlsx)")
    @PostMapping("/export")
    public void exportFile(HttpServletResponse response, Object obj, String fileName) {
        infoCollectionService.exportFile(response, obj, fileName);
    }

}
