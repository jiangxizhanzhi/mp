package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @Description 流量类型枚举类
 * @Author ling_yu
 * @Date 2024/3/29 14:39
 */
public enum FlowTypeEnum {

    DATA_FLOW("数据流", 0),

    PICTURE_FLOW("图片流", 1),

    AUDIO_FLOW("音频流", 2),

    VIDEO_FLOW("视频流", 3),

    MALWARE_FLOW("恶意软件流", 4),

    WEB_BROWSE_FLOW("网页浏览", 5),

    SEND_AND_RECEIVE_EMAILS("收发邮件", 6),

    REAL_TIME_COMMUNICATION("实时通信", 7),

    STREAM_MEDIA("流媒体", 8),

    INDUSTRIAL_CONTROL_DATA_FLOW("工业控制数据流", 9),

    INTRUSION_INFILTRATION("入侵检测", 10),

    SERVICE_DISRUPTION("服务破坏", 11),

    REMOTE_CONTROL("远程控守", 12),

    VIDEO_WEBSITE_TRAFFIC("视频网站流量", 13),

    SOCIAL_NETWORK_TRAFFIC("社交网络流量", 14),

    SEARCH_ENGINE_TRAFFIC("搜索引擎流量", 15),

    MAIL_TRAFFIC("邮件流量", 16),

    GAME_TRAFFIC("游戏流量", 17),

    FILE_TRANSFER_TRAFFIC("文件传输流量", 18),

    NETWORK_MANAGE_TRAFFIC("网络管理流量", 19);

    private final String name;
    private final Integer code;

    FlowTypeEnum(String name, Integer code){
        this.name = name;
        this.code = code;
    }

    @Override
    public String toString() {
        return "FlowTypeEnum{" +
                "name='" + name + '\'' +
                ", code=" + code +
                '}';
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
