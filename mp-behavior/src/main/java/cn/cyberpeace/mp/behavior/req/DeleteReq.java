package cn.cyberpeace.mp.behavior.req;

import lombok.Data;

import java.util.List;

/**
 * @ClassName DeleteReq
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class DeleteReq {

	private List<String> ids;
}
