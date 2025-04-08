package cn.cyberpeace.mp.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基础响应
 *
 * @author SN
 * @version 1.0.0
 * @date 2024/11/07
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseRsp<T> {

	/**
	 * 数据
	 *
	 * @see T
	 */
	public T data;

	/**
	 * 代号
	 *
	 * @see String
	 */
	public String code = "OJ-000000";

	/**
	 * 消息
	 *
	 * @see String
	 */
	public String message;

	/**
	 * 保留字段
	 *
	 * @see String
	 */
	public String detail;

	/**
	 * 时间戳
	 */
	public long timestamp = System.currentTimeMillis();

	public BaseRsp(T data) {
		this.data = data;
	}
}
