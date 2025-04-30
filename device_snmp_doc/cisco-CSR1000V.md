# CISCO-CSR1000V SNMP设置

## 设置端口dhcp获取ip

```
enable //特权模式密码123456
configure terminal
interface g1  //给管理口开启dhcp
ip address dhcp
not shutdown
exit
write memory
show ip interface brief //查看是否获取到ip

```

## 开启SNMP服务

```
configure terminal
snmp-server community private PW
snmp-server host <管理网络ip> version 2c private
write memory
```

## 查询参数

- cpu

  ```json
   .1.3.6.1.4.1.9.9.109.1.1.1.1.7.7 Value (Gauge): 6  //cpu使用率0-100
  ```

- 内存

  ```
   .1.3.6.1.4.1.9.9.48.1.1.1.6.1; Value (Gauge): 102316   //空闲
    .1.3.6.1.4.1.9.9.48.1.1.1.5.1; Value (Gauge): 252708176 //已使用
  
  ```

  

- flash

  ```
   .1.3.6.1.4.1.9.9.10.1.1.4.1.1.13.1.1     ciscoFlashPartitionSizeExtended  flash大小
   .1.3.6.1.4.1.9.9.10.1.1.4.1.1.14.1.1  ciscoFlashPartitionFreeSpaceExtended flash空闲大小
  ```

- system

  ```
   .1.3.6.1.2.1.1.1 Name/OID: sysDescr.0; Value (OctetString): Cisco IOS Software [Amsterdam], Virtual XE Software (X86_64_LINUX_IOSD-UNIVERSALK9-M), Version 17.3.1a, RELEASE SOFTWARE (fc3)
  Technical Support: http://www.cisco.com/techsupport
  Copyright (c) 1986-2020 by Cisco Systems, Inc.
  Compiled Wed 12-Aug-20 00:
  ```

  



