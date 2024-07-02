package cn.cyberpeace.mp.auth.util;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @ClassName FileTypeUtils
 * @Description 文件类型集合
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class FileTypeUtils {

    public static final Map<String, String> FILE_TYPE_MAP;

    static {
        FILE_TYPE_MAP = new ConcurrentSkipListMap<>((s1, s2) -> {
            int len1 = s1.length();
            int len2 = s2.length();
            if (len1 == len2) {
                return s1.compareTo(s2);
            } else {
                return len2 - len1;
            }
        });

        FILE_TYPE_MAP.put("ffd8ff", "jpg"); // JPEG (jpg)
        FILE_TYPE_MAP.put("89504e47", "png"); // PNG (png)
        FILE_TYPE_MAP.put("4749463837", "gif"); // GIF (gif)
        FILE_TYPE_MAP.put("4749463839", "gif"); // GIF (gif)
        FILE_TYPE_MAP.put("49492a00227105008037", "tif"); // TIFF (tif)
        FILE_TYPE_MAP.put("424d228c010000000000", "bmp"); // 16色位图(bmp)
        FILE_TYPE_MAP.put("424d8240090000000000", "bmp"); // 24色位图(bmp)
        FILE_TYPE_MAP.put("424d8e1b030000000000", "bmp"); // 256色位图(bmp)
        FILE_TYPE_MAP.put("41433130313500000000", "dwg"); // CAD (dwg)
        FILE_TYPE_MAP.put("7b5c727466315c616e73", "rtf"); // Rich Text Format (rtf)
        FILE_TYPE_MAP.put("38425053000100000000", "psd"); // Photoshop (psd)
        FILE_TYPE_MAP.put("46726f6d3a203d3f6762", "eml"); // Email [Outlook Express 6] (eml)
        FILE_TYPE_MAP.put("5374616e64617264204a", "mdb"); // MS Access (mdb)
        FILE_TYPE_MAP.put("252150532d41646f6265", "ps");
        FILE_TYPE_MAP.put("255044462d312e", "pdf"); // Adobe Acrobat (pdf)
        FILE_TYPE_MAP.put("2e524d46000000120001", "rmvb"); // rmvb/rm相同
        FILE_TYPE_MAP.put("464c5601050000000900", "flv"); // flv与f4v相同
        FILE_TYPE_MAP.put("0000001c66747970", "mp4");
        FILE_TYPE_MAP.put("00000020667479706", "mp4");
        FILE_TYPE_MAP.put("00000018667479706d70", "mp4");
        FILE_TYPE_MAP.put("49443303000000002176", "mp3");
        FILE_TYPE_MAP.put("000001ba210001000180", "mpg"); //
        FILE_TYPE_MAP.put("3026b2758e66cf11a6d9", "wmv"); // wmv与asf相同
        FILE_TYPE_MAP.put("52494646e27807005741", "wav"); // Wave (wav)
        FILE_TYPE_MAP.put("52494646d07d60074156", "avi");
        FILE_TYPE_MAP.put("4d546864000000060001", "mid"); // MIDI (mid)
        FILE_TYPE_MAP.put("526172211a0700cf9073", "rar"); // WinRAR
        FILE_TYPE_MAP.put("235468697320636f6e66", "ini");
        FILE_TYPE_MAP.put("504b03040a0000000000", "jar");
        FILE_TYPE_MAP.put("504b0304140008000800", "jar");
        // MS Excel 注意：word、msi 和 excel的文件头一样
        FILE_TYPE_MAP.put("d0cf11e0a1b11ae10", "xls");
        FILE_TYPE_MAP.put("504b0304", "zip");
        FILE_TYPE_MAP.put("4d5a9000030000000400", "exe"); // 可执行文件
        FILE_TYPE_MAP.put("3c25402070616765206c", "jsp"); // jsp文件
        FILE_TYPE_MAP.put("4d616e69666573742d56", "mf"); // MF文件
        FILE_TYPE_MAP.put("7061636b616765207765", "java"); // java文件
        FILE_TYPE_MAP.put("406563686f206f66660d", "bat"); // bat文件
        FILE_TYPE_MAP.put("1f8b0800000000000000", "gz"); // gz文件
        FILE_TYPE_MAP.put("cafebabe0000002e0041", "class"); // class文件
        FILE_TYPE_MAP.put("49545346030000006000", "chm"); // chm文件
        FILE_TYPE_MAP.put("04000000010000001300", "mxp"); // mxp文件
        FILE_TYPE_MAP.put("6431303a637265617465", "torrent");
        FILE_TYPE_MAP.put("6d6f6f76", "mov"); // Quicktime (mov)
        FILE_TYPE_MAP.put("ff575043", "wpd"); // WordPerfect (wpd)
        FILE_TYPE_MAP.put("cfad12fec5fd746f", "dbx"); // Outlook Express (dbx)
        FILE_TYPE_MAP.put("2142444e", "pst"); // Outlook (pst)
        FILE_TYPE_MAP.put("ac9ebd8f", "qdf"); // Quicken (qdf)
        FILE_TYPE_MAP.put("e3828596", "pwl"); // Windows Password (pwl)
        FILE_TYPE_MAP.put("2e7261fd", "ram"); // Real Audio (ram)
    }

}

