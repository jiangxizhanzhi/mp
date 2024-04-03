package cn.cyberpeace.mp.sceneplugincollection.util;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scene.util.CommandUtils;
import cn.cyberpeace.mp.scene.util.InstallerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.charset.StandardCharsets;


/**
 * @Description (Json, xml, xlsx) 导入导出操作工具类
 * @Author ling_yu
 * @Date: 2024/4/1 11:38
 */
@Slf4j
public class FilePortUtil {

    /**
     * 导入json文件
     *
     * @param multipartFile
     */
    public static ServiceResult jsonImport(MultipartFile multipartFile) {

        // 获取文件名
        String filename = multipartFile.getOriginalFilename();
        // 获取文件后缀名
        String suffixName  = getSuffixName(filename);

        //TODO 功能逻辑待补充
        return ResultCommon.success();
    }

    /**
     * 导出json文件
     */
    public static void jsonExport(HttpServletResponse response, Object obj, String fileName) {
        //TODO 功能逻辑待补充
    }

    /**
     * 导入xml文件
     * @param multipartFile
     */
    public static ServiceResult xmlImport(MultipartFile multipartFile) {


        //TODO 功能逻辑待补充
        return ResultCommon.success();
    }

    /**
     * 导出xml文件
     */
    public static void xmlExport(HttpServletResponse response, Object obj, String fileName) {
        //TODO 功能逻辑待补充
    }

    /**
     * 导入xls文件
     * @param multipartFile
     */
    public static ServiceResult xlsxImport(MultipartFile multipartFile) {
        //TODO 功能逻辑待补充
        return ResultCommon.success();
    }

    /**
     * 导出xls文件
     */
    public static void xlsxExport(HttpServletResponse response, Object obj, String fileName) {
        //TODO 功能逻辑待补充
    }

    /**
     * 获取文件后缀
     * @param filename
     * @return
     */
    public static String getSuffixName(String filename) {
        String suffixName = "";
        if (StringUtils.isBlank(filename)) {
            return suffixName;
        }
        int dotIndex = filename.lastIndexOf(AuthConstant.SEPARATOR_CHAR_DOT);
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            suffixName = filename.substring(dotIndex + 1);
        }
        return suffixName;
    }

}
