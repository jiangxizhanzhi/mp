package cn.cyberpeace.mp.base.model;

/**
 * httpclient调用时需要的上下文
 *
 * @author SN
 * @version 1.0.0
 * @date 2024/11/07
 * @since 1.0.0
 */
public class HttpclientContext {

	/**
	 * 上下文
	 *
	 * @see ThreadLocal<HttpclientModel>
	 */
	private static final ThreadLocal<HttpclientModel> context = new ThreadLocal<HttpclientModel>();

	/**
	 * 获取上下文
	 *
	 * @return {@code HttpclientModel }
	 * @throws
	 * @see HttpclientModel
	 */
	public static HttpclientModel getContext() {
		return context.get();
	}

	/**
	 * 设置上下文
	 *
	 * @param model 型
	 * @throws
	 * @see HttpclientModel
	 */
	public static void setContext(HttpclientModel model) {
		context.set(model);
	}

	/**
	 * 删除上下文
	 *
	 * @throws
	 */
	public static void removeContext() {
		context.remove();
	}
}