package cn.cyberpeace.mp.collection.bo.host;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PortBO {
    private String localIp;

    private Integer localPort;

    private String remoteIp;

    private Integer remotePort;

    private LocalDateTime scanTime;

    private Integer inode;

    private Integer pid;

    private Integer rxQueue;

    private String txQueue;

    private String protocol;

    private String state;

    private String process;
}
