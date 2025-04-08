package cn.cyberpeace.mp.base.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * httpclient 模型
 *
 * @author SN
 * @version 1.0.0
 * @since 1.0.0
 * @date 2024/11/07
 */
@Data
public class HttpclientModel {

	/**
	 * 日志
	 *
	 * @see Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger("mp");

	/**
	 * 日志格式化程序
	 *
	 * @see String
	 */
	private static final String LOG_FORMATTER = "httpclientutil |{}|{}|{}|{}";

	/**
	 * 发送给对端系统的跟踪路由
	 *
	 * @see String
	 */
	private String traceId;

	/**
	 * 调用开始时间
	 */
	private long startTime;

	/**
	 * 调用耗时
	 */
	private long callTime;

	/**
	 * 响应日志最大长度
	 *
	 * @see Integer
	 */
	private Integer responseLogMaxLength = 2000;

	/**
	 * 网址
	 *
	 * @see String
	 */
	private String url;

	/**
	 * 方法
	 *
	 * @see String
	 */
	private String method;

//	/**
//	 * httpclient 模型
//	 *
//	 * @param traceId 跟踪id
//	 * @param url     网址
//	 * @throws
//	 * @see String
//	 * @see String
//	 */
//	public HttpclientModel(String traceId, String url) {
//		if (StringUtils.isBlank(traceId)) {
//			this.traceId = IDGeneratorUtil.getID();
//			TraceIdContext.setTraceId(this.traceId);
//		} else {
//			this.traceId = traceId;
//		}
//
//		this.startTime = System.currentTimeMillis();
//		this.url = url;
//	}

	/**
	 * 执行远程异常
	 *
	 * @param e e
	 * @throws
	 * @see Exception
	 */
	public void doRemoteException(Exception e) {
		callTime = System.currentTimeMillis() - startTime;
		LOG.error(LOG_FORMATTER, new Object[] { callTime, method, url, null }, e);
	}

	/**
	 * 新增日志
	 *
	 * @param httpResponse HTTP 响应
	 * @throws
	 * @see String
	 */
	public void addLog(String httpResponse) {
		callTime = System.currentTimeMillis() - startTime;
		// debug的时候才输出接口返回， 正常情况下输出调用时间，结果可以忽略
		if (LOG.isDebugEnabled()) {
			LOG.debug(LOG_FORMATTER, new Object[] { callTime, method, url, httpResponse });
		}
		if (LOG.isInfoEnabled()) {
			LOG.info(LOG_FORMATTER, new Object[] { callTime, method, url, "success" });
		}
	}
}
