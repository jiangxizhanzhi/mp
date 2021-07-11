package cn.cyberpeace.mp.common.constant;

import java.util.ArrayList;
import java.util.List;


public interface FileSuffixConstant {

    
    List<String> MD_UPLOAD_FILE_LIST = new ArrayList<>(){{
        add("zip");
        add("rar");
    }};

    
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

    
    List<String> UPLOAD_HEAD_IMAGE_LIST = new ArrayList<>(){{
        add("png");
        add("jfif");
        add("pjpeg");
        add("jpeg");
        add("pjp");
        add("jpg");
    }};

    
    List<String> IMPORT_SYSTEM_LOGO_LIST = new ArrayList<>(){{
        add("jpg");
        add("png");
        add("jpeg");
    }};


    
    List<String> IMPORT_VPN_LIST = new ArrayList<>(){{
        add("zip");
        add("rar");
    }};

}
