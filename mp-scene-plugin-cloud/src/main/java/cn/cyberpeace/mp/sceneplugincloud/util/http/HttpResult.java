package cn.cyberpeace.mp.sceneplugincloud.util.http;

import cn.cyberpeace.mp.base.common.JsonCommon;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpResult {

    private HttpRequestBase request;
    private HttpResponse response;
    private String result;
    private Exception exception;

    public static boolean isSuccessStatusCode(int statusCode) {
        return statusCode >= HttpStatus.SC_OK && statusCode < HttpStatus.SC_MULTIPLE_CHOICES;
    }

    public static String parseResult(HttpResponse response, Charset charset) throws IOException {
        return EntityUtils.toString(response.getEntity(), charset);
    }

    public static Map<String, Object> parseResultMap(String result) {
        if (StringUtils.isNotBlank(result)) {
            return JsonCommon.parse(result, new TypeReference<Map<String, Object>>() {});
        } else {
            return null;
        }
    }

    public static <T> T parseResultBean(String result, TypeReference<T> valueTypeRef) {
        if (StringUtils.isNotBlank(result)) {
            return JsonCommon.parse(result, valueTypeRef);
        } else {
            return null;
        }
    }

    public void loadResult() throws IOException {
        result = parseResult(response, StandardCharsets.UTF_8);
    }

    public void loadResult(Charset charset) throws IOException {
        if (response == null) {
            return;
        }

        result = parseResult(response, charset);
    }

    public boolean isSuccess() {
        if (response == null) {
            return false;
        }

        return isSuccessStatusCode(response.getStatusLine().getStatusCode());
    }

    public Map<String, Object> getResultMap() {
        return parseResultMap(result);
    }

    public <T> T getResultBean(TypeReference<T> valueTypeRef) {
        return parseResultBean(result, valueTypeRef);
    }
}
