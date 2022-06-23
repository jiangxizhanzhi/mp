package cn.cyberpeace.mp.flow.constant;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 流量类型
 * @Author qiaokang
 * @Date：2022/6/23 11:52
 */
@Component
public class FlowCategory {

    public static Map<String, Integer> flowCategoryMap = new HashMap<>();

    public static Map<String, Integer> paramTypeMap = new HashMap<>();

    public FlowCategory() {
        init();
    }

    /**
     * 流量类型枚举类：0:"测试测量",1:"应用升级",2:"网络管理",3:"P2P",4:"金融",5:"邮件",6:"安全工作",7:"分布式计算",8:"手机",9:"移动设备",10:"即时通讯",11:"SCADA",12:"机动",13:"远程控制",14:"音视频",15:"企业应用",16:"社会网络搜索",17:"文件传输",18:"数据库",19:"身份认证",20:"ERP",21:"存储",22:"游戏",23:"安全数据传输"
     */
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
