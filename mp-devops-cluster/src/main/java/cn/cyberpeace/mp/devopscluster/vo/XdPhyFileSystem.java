package cn.cyberpeace.mp.devopscluster.vo;

import lombok.Data;

@Data
public class XdPhyFileSystem {

    //磁盘名称
    private String name;
    //已使用磁盘大小
    private ValueUnitVO usedFileSystem;
    //磁盘总大小
    private ValueUnitVO totalFileSystem;

}
