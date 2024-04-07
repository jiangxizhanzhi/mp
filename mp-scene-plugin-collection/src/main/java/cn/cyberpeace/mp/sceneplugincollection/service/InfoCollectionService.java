package cn.cyberpeace.mp.sceneplugincollection.service;


import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.sceneplugincollection.util.FilePortUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description 采集信息导入导出实现层 (Json,Xml,Xlsx)
 * @Author ling_yu
 * @Date: 2024/4/1$ 11:13$
 */
@Slf4j
@Service
public class InfoCollectionService {

    /**
     * 文件导入 (Json,Xml,Xlsx)
     *
     * @param file
     * @return
     */
    public ServiceResult<String> importFile(MultipartFile file) {
        // 获取文件名
        String filename = file.getOriginalFilename();
        // 获取文件后缀名
        String suffixName = FilePortUtil.getSuffixName(filename);

        // 判断文件类型 json, txt
        if ("json".equals(suffixName) || "txt".equals(suffixName)){
            return FilePortUtil.jsonImport(file);
        }
        // 判断文件类型 xml
        if ("xml".equals(suffixName)){
            return FilePortUtil.xmlImport(file);
        }
        // 判断文件类型 xlsx, xls
        if ("xlsx".equals(suffixName) || "xls".equals(suffixName)){
            return FilePortUtil.xlsxImport(file);
        }

        return ResultCommon.success();
    }

    /**
     * 文件导出 (Json,Xml,Xlsx)
     * @param response
     */
    public void exportFile(HttpServletResponse response, Object obj, String fileName) {



        //TODO 功能逻辑待补充
    }


}
