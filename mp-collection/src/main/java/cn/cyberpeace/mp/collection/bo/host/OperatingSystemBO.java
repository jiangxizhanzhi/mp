package cn.cyberpeace.mp.collection.bo.host;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperatingSystemBO {
    private String major;

    private String minor;

    private String name;

    private String platform;

    private String versionNumber;

    private LocalDateTime time;

    private String version;

    private String hostname;

    private String release;

    private String architecture;

    private String systemName;
}
