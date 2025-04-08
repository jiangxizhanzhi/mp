package cn.cyberpeace.mp.base.constant;

/**
 * Web 常量
 *
 * @author SN
 * @version 1.0.0
 * @date 2024/11/07
 * @since 1.0.0
 */
public interface WebConstant {

	/**
	 * 内容类型
	 *
	 * @see String
	 */
	String CONTENT_TYPE = "application/json;charset=UTF-8";

	/**
	 * http 200 （中文）
	 *
	 * @see Integer
	 */
	Integer HTTP_200 = 200;

	/**
	 * Authorization 标头
	 *
	 * @see String
	 */
	String AUTHORIZATION_HEADER = "Authorization";

	/**
	 * 跟踪标头
	 *
	 * @see String
	 */
	String TRACE_HEADER = "X-Request-Id";

	/**
	 * 语言
	 *
	 * @see String
	 */
	String LANGUAGE = "Accept-Language";

	/**
	 * 语言 zh
	 *
	 * @see String
	 */
	String LANGUAGE_ZH = "zh-CN";

	/**
	 * 语言 EN
	 *
	 * @see String
	 */
	String LANGUAGE_EN = "en-US";

	/**
	 * mp鉴权参数
	 *
	 * @see String
	 */
	String CLIENT_ID = "clientId";

	/**
	 * mp鉴权参数
	 *
	 * @see String
	 */
	String CLIENT_SECRET = "clientSecret";

	/**
	 * 编码
	 *
	 * @see String
	 */
	String ENCODING = "UTF-8";

	/**
	 * 通过通过业务id
	 *
	 * @see String
	 */
	String PASS_THROUGH_BUSINESS_ID = "business_id";

	/**
	 * 直通扩展
	 *
	 * @see String
	 */
	String PASS_THROUGH_EXTENSION = "extension";

	/**
	 * 直通运算符
	 *
	 * @see String
	 */
	String PASS_THROUGH_OPERATOR = "operator";

	/**
	 * 场景id长度
	 *
	 * @see Integer
	 */
	Integer SCENE_ID_LENGTH = 32;

	/**
	 * 外部接口使用
	 *
	 * @see String
	 */
	String OJ_HEADER_KEY = "OPT-OJ-KEY";

	/**
	 * 外部接口使用
	 *
	 * @see String
	 */
	String OJ_HEADER_KEY_VALUE = "Cmycjs1bWUHsKb8r";
}
