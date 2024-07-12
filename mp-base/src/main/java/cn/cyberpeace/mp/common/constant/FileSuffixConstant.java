 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FileSuffixConstant.java.
 // File size 1794.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.common.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName FileSuffixConstant
 * @Description 文件上传支持后缀常量
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface FileSuffixConstant {

    /**
     * MD上传文件支持后缀：zip、rar
     */
    List<String> MD_UPLOAD_FILE_LIST = new ArrayList<>(){{
        add("zip");
        add("rar");
    }};

    /**
     * MD上传图片支持后缀：tiff，pjp，jfif，bmp，gif，svg，png，xbm，dib，jxl，jpeg，svgz，jpg，webp，ico，tif，pjpeg，avif
     */
    List<String> MD_UPLOAD_IMAGE_LIST = new ArrayList<>(){{
        add("tiff");
        add("pjp");
        add("jfif");
        add("bmp");
        add("gif");
        add("svg");
        add("png");
        add("xbm");
        add("dib");
        add("jxl");
        add("jpeg");
        add("svgz");
        add("jpg");
        add("webp");
        add("ico");
        add("tif");
        add("pjpeg");
        add("avif");
    }};

    /**
     * 导入用户头像支持后缀：png，jfif，pjpeg，jpeg，pjp，jpg
     */
    List<String> UPLOAD_HEAD_IMAGE_LIST = new ArrayList<>(){{
        add("png");
        add("jfif");
        add("pjpeg");
        add("jpeg");
        add("pjp");
        add("jpg");
    }};

    /**
     * 导入系统logo支持后缀：jpg，png，jpeg
     */
    List<String> IMPORT_SYSTEM_LOGO_LIST = new ArrayList<>(){{
        add("jpg");
        add("png");
        add("jpeg");
    }};


    /**
     * 导入VPN软件包支持后缀：zip，rar
     */
    List<String> IMPORT_VPN_LIST = new ArrayList<>(){{
        add("zip");
        add("rar");
    }};

}
