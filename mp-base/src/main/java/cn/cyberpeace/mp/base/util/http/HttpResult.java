package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.util.json.JsonUtils;
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

/**
 * @ClassName HttpResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpResult {

    private HttpRequestBase request;
    private HttpResponse response;
    private String result;
    private Exception exception;

    /**
     * isSuccessStatusCode
     *
     * @param statusCode
     * @return boolean
     */
    public static boolean isSuccessStatusCode(int statusCode) {
        return statusCode >= HttpStatus.SC_OK && statusCode < HttpStatus.SC_MULTIPLE_CHOICES;
    }

    /**
     * parseResult
     *
     * @param response
     * @param charset
     * @return
     */
    public static String parseResult(HttpResponse response, Charset charset) throws IOException {
        return EntityUtils.toString(response.getEntity(), charset);
    }

    /**
     * parseResultMap
     *
     * @param result
     * @return
     */
    public static Map<String, Object> parseResultMap(String result) {
        if (StringUtils.isNotBlank(result)) {
            return JsonUtils.parse(result, new TypeReference<Map<String, Object>>() {});
        } else {
            return null;
        }
    }

    /**
     * parseResultBean
     *
     * @param result
     * @param valueTypeRef
     * @return
     */
    public static <T> T parseResultBean(String result, TypeReference<T> valueTypeRef) {
        if (StringUtils.isNotBlank(result)) {
            return JsonUtils.parse(result, valueTypeRef);
        } else {
            return null;
        }
    }

    /**
     * snakeParseResultBean
     *
     * @param result
     * @param valueTypeRef
     * @return
     */
    public static <T> T snakeParseResultBean(String result, TypeReference<T> valueTypeRef) {
        if (StringUtils.isNotBlank(result)) {
            return JsonUtils.snakeParse(result, valueTypeRef);
        } else {
            return null;
        }
    }

    /**
     * loadResult
     */
    public void loadResult() throws IOException {
        result = parseResult(response, StandardCharsets.UTF_8);
    }

    /**
     * loadResult
     *
     * @param charset
     */
    public void loadResult(Charset charset) throws IOException {
        if (response == null) {
            return;
        }

        result = parseResult(response, charset);
    }

    /**
     * isSuccess
     *
     * @return boolean
     */
    public boolean isSuccess() {
        if (response == null) {
            return false;
        }

        return isSuccessStatusCode(response.getStatusLine().getStatusCode());
    }

    /**
     * getResultMap
     *
     * @return
     */
    public Map<String, Object> getResultMap() {
        return parseResultMap(result);
    }

    /**
     * getResultBean
     *
     * @param valueTypeRef
     * @return
     */
    public <T> T getResultBean(TypeReference<T> valueTypeRef) {
        return parseResultBean(result, valueTypeRef);
    }

    /**
     * snakeGetResultBean
     *
     * @param valueTypeRef
     * @return
     */
    public <T> T snakeGetResultBean(TypeReference<T> valueTypeRef) {
        return snakeParseResultBean(result, valueTypeRef);
    }
}
