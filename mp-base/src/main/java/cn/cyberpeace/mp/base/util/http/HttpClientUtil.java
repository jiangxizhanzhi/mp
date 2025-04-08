package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.constant.WebConstant;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.BaseRsp;
import cn.cyberpeace.mp.base.model.HttpclientContext;
import cn.cyberpeace.mp.base.model.HttpclientModel;
import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * HTTP 客户端实用程序
 *
 * @author SN
 * @version 1.0.0
 * @date 2024/11/07
 * @since 1.0.0
 */
public class HttpClientUtil {

	/**
	 * 日志
	 *
	 * @see Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger("mp");

	/**
	 * HTTP 客户端实用程序
	 *
	 * @throws
	 */
	private HttpClientUtil() {}

	/**
	 * 默认标头
	 *
	 * @see List<Header>
	 */
	private static List<Header> defaultHeaders;

	/**
	 * 成功后缀
	 *
	 * @see String
	 */
	private static final String SUCCESS_POSTFIX = "-000000";

	/**
	 * 所有参数都放在url中都get方法
	 *
	 * @param url
	 * @param resultType
	 * @return
	 * @throws
	 * @see String
	 * @see Class<T>
	 * @see T
	 */
	public static <T> T rawGet(String url, Class<T> resultType) {
		// 1、设置请求线程数据
//		setContext(url);

		// 2、生成get请求
		HttpGet httpGet = new HttpGet(url);
//		httpGet.setHeader(WebConstant.TRACE_HEADER, TraceIdContext.getTraceId());
//		httpGet.setHeader(WebConstant.LANGUAGE, CommonContext.getLanguage());
//		String authorization = CommonContext.getAuthorization();
//		if (null != authorization) { // 调用竞赛接口需要
//			httpGet.setHeader(WebConstant.AUTHORIZATION_HEADER, "JWT " + authorization);
//		}
		// 3、执行实际调用、返回序列化后的结果
		return doHttp(httpGet, resultType);
	}

//	/**
//	 * 设置请求上下文
//	 *
//	 * @param url 网址
//	 * @throws
//	 * @see String
//	 */
//	private static void setContext(String url) {
////		HttpclientModel httpclientModel = new HttpclientModel(TraceIdContext.getTraceId(), url);
//		HttpclientModel httpclientModel = new HttpclientModel("TraceIdContext.getTraceId()", url);
//		HttpclientContext.setContext(httpclientModel);
//	}

	private static <T> T doHttp(HttpUriRequest httpRequest, Class<T> resultType) {
		HttpResponse response = null;
		String httpResponse = null;
		HttpclientContext.getContext().setMethod(httpRequest.getMethod());
		try {
			// 1、调用远程服务
			response = getClient().execute(httpRequest);

			// 2、解析远程服务的返回值
			httpResponse = EntityUtils.toString(response.getEntity(), WebConstant.ENCODING);

			// 3、记录调用日志
			HttpclientContext.getContext().addLog(httpResponse);
		} catch (Exception e) {
			try {
				HttpclientContext.getContext().doRemoteException(e);
			} catch (Exception e1) {
				LOG.error("remote error logging Exception", e1);
			} finally {
				HttpclientContext.removeContext();
				throw new MpRuntimeException("远程服务异常");
			}
		}

		if (StringUtils.isBlank(httpResponse)) {
			HttpclientContext.getContext().addLog("httpResponse is null");
			HttpclientContext.removeContext();
			throw new MpRuntimeException("远程服务异常");
		}

		// 4、解析结果并返回
		return parseHttpResponse(httpResponse, resultType);
	}

	private static HttpClient getClient() {

		return HttpClients.custom().setDefaultHeaders(defaultHeader()).build();
	}

	/**
	 * 默认加载的http请求头
	 *
	 * @return
	 * @throws
	 * @see List<Header>
	 */
	private static List<Header> defaultHeader() {
		if (null != defaultHeaders) {
			return defaultHeaders;
		}

		// 以下代码可能会并发，不会带来大问题
//		String mpAuthenticationParam = SystemPropUtil.getProp("MP_SECRET");
		String mpAuthenticationParam = "MP_SECRET";

		LOG.info("read prop mpAuthenticationParam=[{}]", mpAuthenticationParam);

		String[] ss = StringUtils.split(mpAuthenticationParam, ",");
		BasicHeader clientId = new BasicHeader(WebConstant.CLIENT_ID, ss[0]);

		BasicHeader clientSecret = new BasicHeader(WebConstant.CLIENT_SECRET, ss[1]);

		List<Header> defaultHeader = Arrays.asList(clientId, clientSecret);

		HttpClientUtil.defaultHeaders = defaultHeader;

		return defaultHeader;
	}

	private static <T> T parseHttpResponse(String httpResponse, Class<T> resultType) {

		try {
			BaseRsp baseRsp = JSON.parseObject(httpResponse, BaseRsp.class);

			if (!isSuccess(baseRsp)) {
				// 只要中台的错误码不是000000，都认为是有问题的
				LOG.error("url=[{}], result=[{}] error, code=[{}]",
						HttpclientContext.getContext().getUrl(), httpResponse, baseRsp.getCode());
				throw new MpRuntimeException(baseRsp.getMessage());
			}
			if (null == baseRsp.getData()) {
				return null;
			}
			return JSONObject.parseObject(baseRsp.getData().toString(), resultType);
		} catch (MpRuntimeException e) {
			throw e;
		} catch (Exception e) {
			// 遇到不能解析的返回结果，直接报错
			LOG.error("parse httpResponse error, url=[{}], result=[{}], resultType=[{}]",
					HttpclientContext.getContext().getUrl(), httpResponse, resultType);
			throw new MpRuntimeException("远程服务异常");
		} finally {
			HttpclientContext.removeContext();
		}
	}

	private static boolean isSuccess(BaseRsp baseRsp) {
		return StringUtils.endsWith(baseRsp.getCode(), SUCCESS_POSTFIX);
	}
}
