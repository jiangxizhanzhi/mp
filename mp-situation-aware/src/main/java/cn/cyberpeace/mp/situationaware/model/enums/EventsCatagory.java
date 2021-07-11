package cn.cyberpeace.mp.situationaware.model.enums;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;

@SwaggerDisplayEnum
public enum EventsCatagory {

    
    WINDOWS("windows"),
    LINUX("linux"),
    CISCO("cisco"),
    HP("hp"),
    FREEBSD("freebsd"),
    JUNOS("junos"),

    
    MAIL_SERVER("mail_server"),
    WEB_SERVER("web_server"),
    DATABASE("database"),
    NET_SERVER("net_server"),
    APP("app"),
    CLOUD("cloud"),
    LOAD_BALANCER("load_balancer"),
    APP_FRAMEWORK("app_framework"),

    
    AV("av"),
    FW("fw"),
    IDS("ids/ips"),
    VPN("vpn"),
    OTHER("other");

    private String code;

    EventsCatagory(String code) {
        this.code = code;
    }
}
