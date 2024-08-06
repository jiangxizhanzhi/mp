package cn.cyberpeace.mp.agent.netconf.model;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class NetconfHeartBody implements SnakeHttpData {

	@ApiModelProperty("服务id")
	private String serviceId;

	@ApiModelProperty("子服务id")
	private List<String> children = Lists.newArrayList();

	@ApiModelProperty("代理服务ip地址")
	private String ipAddress;

	@ApiModelProperty("代理服务端口")
	private Integer port;

	@ApiModelProperty("是否为总代理")
	private Boolean fatherAgent;

	@ApiModelProperty("子代理信息")
	private List<Agent> agentList = Lists.newArrayList();

	@Data
	@ApiModel
	public static class Agent {

		@ApiModelProperty("代理id")
		private String agentId;

		@ApiModelProperty("代理服务ip地址")
		private String ipAddress;

		@ApiModelProperty("端口")
		private Integer port;
	}

}
