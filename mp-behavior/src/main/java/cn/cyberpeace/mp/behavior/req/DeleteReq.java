package cn.cyberpeace.mp.behavior.req;

import lombok.Data;

import java.util.List;

@Data
public class DeleteReq {

	private List<String> ids;
}
