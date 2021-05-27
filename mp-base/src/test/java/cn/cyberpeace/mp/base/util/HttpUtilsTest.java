package cn.cyberpeace.mp.base.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@RunWith(SpringRunner.class)
public class HttpUtilsTest {

    @Test
    public void testGetRequest() throws IOException {
        String url = "http://10.100.7.50:5079/v1/scenes";
        String res = HttpUtils.sendGet(url);
        Assert.isTrue(JsonUtils.isValidJSON(res), "get request is not valid");
    }

    @Test
    public void testPostRequest() throws IOException{
        String url = "http://10.100.7.50:5079/v1/scenes";
        String content = "{\n" +
            "  \"scene_id\": \"4687c63b-d79f-4644-8027-b268f89aa8a9\",\n" +
            "  \"macs\": [\n" +
            "    \"fa:16:3e:00:00:01\",\n" +
            "    \"fa:16:3e:00:00:02\"\n" +
            "  ]\n" +
            "}";
        String res = HttpUtils.sendPost(url, content);
        Assert.isTrue(JsonUtils.isValidJSON(res), "post request is valid");
    }

    @Test
    public void testDelete() throws IOException{
        String url = "http://10.100.7.50:5079/v1/scenes/sfsd";
        String res = HttpUtils.sendDelete(url);
        Assert.isTrue(JsonUtils.isValidJSON(res), "delete request is valid");
    }

    @Test
    public void dateTest() {
        var index = "default_sessions2-210521";
        int end = index.indexOf("_sessions2");
        System.out.println(index.substring(0, end));
    }

    /**
     * 测试获取文件流
     * @throws IOException
     */
    @Test
    public void testSendGetDownLoadWithDigestAuth() throws IOException{
        String url = "http://10.100.7.50:8005/sessions.pcap?ids=210526-yzQZpKtkxjBCep8gY6Avw44q";

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials("api", "api");
        credsProvider.setCredentials(AuthScope.ANY, usernamePasswordCredentials);
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).build();
        var httpGet = new HttpGet(url);

        httpGet.setHeader("Accept-Encoding", "gzip, deflate");
        httpGet.setHeader("Accept", "*/*");

        var httpResponse = client.execute(httpGet);

        InputStream inputStream = httpResponse.getEntity().getContent();

        String result = new BufferedReader(new InputStreamReader(inputStream))
            .lines().collect(Collectors.joining(System.lineSeparator()));
        System.out.println("result: " + result);
    }
}
