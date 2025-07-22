package cn.cyberpeace.mp.auth.bo.redis;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DeleteUserMessage {

	private String ids;

	public List<String> parseIds() {
		return List.of(ids.split(","));
	}
}
