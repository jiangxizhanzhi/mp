package cn.cyberpeace.mp.proxy.lf;

import cn.hutool.core.io.FileUtil;
import lombok.Data;

@Data
public class LfProxyConfig {
    public static final String URL_SEPARATOR = "/";
    public static final String FILE_LOCATION_PREFIX = "file:";
    public static final String CLASSPATH_LOCATION_PREFIX = "classpath:";

    private String name;
    private String url;
    private String location;
    private Boolean urlPatternEnabled;

    public String getUrl() {
        if (Boolean.TRUE.equals(urlPatternEnabled)) {
            return url;
        }

        var formattedUrl = url;
        if (!formattedUrl.endsWith(URL_SEPARATOR)) {
            formattedUrl = formattedUrl + URL_SEPARATOR;
        }
        formattedUrl = formattedUrl + "**";
        return formattedUrl;
    }

    public String getLocation() {
        var formattedLocation = location;
        if (!(formattedLocation.startsWith(FILE_LOCATION_PREFIX) || formattedLocation.startsWith(CLASSPATH_LOCATION_PREFIX))) {
            formattedLocation = FILE_LOCATION_PREFIX + formattedLocation;
        }
        if (formattedLocation.startsWith(FILE_LOCATION_PREFIX) && !formattedLocation.endsWith(FileUtil.FILE_SEPARATOR)) {
            formattedLocation = formattedLocation + FileUtil.FILE_SEPARATOR;
        }
        if (formattedLocation.startsWith(CLASSPATH_LOCATION_PREFIX) && !formattedLocation.endsWith(URL_SEPARATOR)) {
            formattedLocation = formattedLocation + URL_SEPARATOR;
        }
        return formattedLocation;
    }
}
