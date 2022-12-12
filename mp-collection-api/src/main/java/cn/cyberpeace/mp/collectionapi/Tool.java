package cn.cyberpeace.mp.collectionapi;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Tool {
    public static void main(String[] args) {
        // 选手的ip
        File source = new File("");
        // 设备方的ip
        File target = new File("");

        List<String> sourceIps = FileUtil.readLines(source, "utf8");
        List<String> targetIps = FileUtil.readLines(target, "utf8");
        for (String sourceIp : sourceIps) {
            for (String targetIp : targetIps) {

            }
        }
    }

//    public static Map<String,Object> result(){
//
//    }
}
