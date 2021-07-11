package cn.cyberpeace.mp.flow.constant;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class FlowCategory {

    public static Map<String, Integer> flowCategoryMap = new HashMap<>();

    public static Map<String, Integer> paramTypeMap = new HashMap<>();

    public FlowCategory() {
        init();
    }

    
    public void init() {
        flowCategoryMap.put("测试测量", 0);
        flowCategoryMap.put("应用升级", 1);
        flowCategoryMap.put("网络管理", 2);
        flowCategoryMap.put("P2P", 3);
        flowCategoryMap.put("金融", 4);
        flowCategoryMap.put("邮件", 5);
        flowCategoryMap.put("安全工作", 6);
        flowCategoryMap.put("分布式计算", 7);
        flowCategoryMap.put("手机", 8);
        flowCategoryMap.put("移动设备", 9);
        flowCategoryMap.put("即时通讯", 10);
        flowCategoryMap.put("SCADA", 11);
        flowCategoryMap.put("机动", 12);
        flowCategoryMap.put("远程控制", 13);
        flowCategoryMap.put("音视频", 14);
        flowCategoryMap.put("企业应用", 15);
        flowCategoryMap.put("社会网络搜索", 16);
        flowCategoryMap.put("文件传输", 17);
        flowCategoryMap.put("数据库", 18);
        flowCategoryMap.put("身份认证", 19);
        flowCategoryMap.put("ERP", 20);
        flowCategoryMap.put("存储", 21);
        flowCategoryMap.put("游戏", 22);
        flowCategoryMap.put("安全数据传输", 23);

        paramTypeMap.put("String", 0);
        paramTypeMap.put("IP", 1);
        paramTypeMap.put("Number", 2);
        paramTypeMap.put("Bool", 3);
    }

}
