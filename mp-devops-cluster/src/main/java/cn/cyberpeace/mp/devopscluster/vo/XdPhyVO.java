package cn.cyberpeace.mp.devopscluster.vo;

import lombok.Data;

import java.util.List;

@Data
public class XdPhyVO {
    //节点名称
    private String nodeName;
    //系统名称
    private String sysName;
    //实例ip
    private String instance;
    //cpu百分比
    private String cpuPercent;
    //已使用内存
    private ValueUnitVO usedMemory;
    //总内存
    private ValueUnitVO totalMemory;
    //磁盘信息
    private List<XdPhyFileSystem> fileSystems;
}
