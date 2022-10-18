package cn.cyberpeace.mp.collection.bo.host;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NetifaceBO {
    private Integer receiveBytes;

    private Integer receiveLost;

    private String receiveErrors;

    private String receivePackets;

    private Integer sendBytes;

    private Integer sendLost;

    private String sendErrors;

    private String sendPackets;

    private LocalDateTime scanTime;

    private String type;

    private Integer mtu;

    private String state;

    private String mac;

    private String name;

    private String ip;
}
