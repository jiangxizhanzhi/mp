package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudElementAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Connection extends CloudClient {

    public VncFileContentParseResult getVncFileContent(String vmId) {
        String fileContent = "";
        OutputStream os = null;
        try {
            String urlStr = CloudSingleton.cloudAppConfig.getCpcloudServer() + CloudUrlConstant.VIRTUAL_MACHINE_ACCESS_URL + "?vm_id=" + vmId + "&console_type=" + CloudElementAccessMode.Protocol.VNC;
            log.info("get vnc file content , url is {}", urlStr);
            URL url = new URL(urlStr);
            // 建立连接
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            // 设置字符编码
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            // 打开到此 URL引用的资源的通信链接
            httpURLConnection.connect();
            // 从httpURLConnection中获取返回的内容
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            fileContent = content.toString();
            log.info("get vnc file content , result is {}", fileContent);
        } catch (Exception e) {
            log.error("download connection file error", e);
            fileContent = "";
        } finally {
            IOUtils.closeQuietly(os);
        }

        String str = fileContent.split("#")[0] + "#";
        log.info("get vnc content file start with `{}`", str);
        return new VncFileContentParseResult(str);
    }

    public ConnectionCreateResult createConnection(ConnectionCreateData connectionCreateData) {
        String finalUrl = "/cloud/v1" + CloudUrlConstant.GUACAMOLE_CONNECTION_CREATE;
        var a = getHttpClient().post(finalUrl, connectionCreateData, null, ConnectionCreateResult.class);
        return a;
    }

    @Getter
    @Setter
    public static class ConnectionCreateData implements SnakeHttpData {
        private String username;
        private ConnectionCreateParamsData connectionParams;
    }

    @Getter
    @Setter
    public static class ConnectionCreateResult {
        private String url;
        private String connectionId;
        private String connectionName;
    }

    @Getter
    @Setter
    public static class VncFileContentParseResult {
        private String type;
        private String host;
        private String port;
        private String password;

        public VncFileContentParseResult(String fileContent) {
            String regex = "\\[.*\\](type=(?<type>[^ ]*)host=(?<host>[^ ]*)port=(?<port>[^ ]*)password=(?<password>.*)#)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(fileContent);

            if (matcher.find()) {
                type = matcher.group("type");
                host = matcher.group("host");
                port = matcher.group("port");
                password = matcher.group("password");
            }
        }
    }

    @Getter
    @Setter
    public static class ConnectionCreateParamsData {
        private String protocol;
        private String hostname;
        private String username;
        private String password;
        private String port;
        private boolean needPrivateKey;
        private String privateKeyPath;
        private String command;
    }
}
