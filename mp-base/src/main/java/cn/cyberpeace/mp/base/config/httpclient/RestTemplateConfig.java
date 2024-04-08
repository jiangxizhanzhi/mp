package cn.cyberpeace.mp.base.config.httpclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class RestTemplateConfig {

    private static final Long CONNECT_TIME_OUT = 5L;
    private static final Long READ_TIME_OUT = 10L;
    private static final Long WRITE_TIME_OUT = 10L;

    private static final Long CALL_TIME_OUT = 15L;

    @Bean("snakeRestTemplate")
    public RestTemplate sankeRestTemplate() {
        // 创建一个RestTemplate实例
        RestTemplate restTemplate = new RestTemplate();

        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS).writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                .callTimeout(CALL_TIME_OUT, TimeUnit.SECONDS).build();
        restTemplate.setRequestFactory(new OkHttp3ClientHttpRequestFactory(okHttpClient));

        restTemplate.setMessageConverters(getMessageConverters());

        return restTemplate;
    }

    @Bean("camelRestTemplate")
    public RestTemplate camelRestTemplate() {
        // 创建一个RestTemplate实例
        RestTemplate restTemplate = new RestTemplate();

        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS).writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                .callTimeout(CALL_TIME_OUT, TimeUnit.SECONDS).build();
        restTemplate.setRequestFactory(new OkHttp3ClientHttpRequestFactory(okHttpClient));
        return restTemplate;
    }

    private List<HttpMessageConverter<?>> getMessageConverters() {
        // 创建一个Jackson消息转换器
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        // 配置ObjectMapper以将属性命名转换为驼峰命名
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        converter.setObjectMapper(objectMapper);

        return List.of(converter);
    }
}
