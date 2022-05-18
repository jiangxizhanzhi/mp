package cn.cyberpeace.mp.common.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件上传支持后缀常量
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


}
