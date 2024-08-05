package cn.cyberpeace.mp.agent.netconf.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

@Slf4j
@Data
public class AgentSelf implements Serializable {

	private static AgentSelf agentSelf;

	static {
		agentSelf = new AgentSelf();
	}

	private String agentName;

	private String desc;

	private String agentType;

	private String id;

	private String nodeId;

	private Integer port;

	private String ip;

	private Integer parent = 1;

	private String url;

	private String frameRegisterUrl;

	private String frameHeartUrl;

	private String getSecretUrl;

	private String secret;

	private Boolean registry = false;

	private Properties properties = new Properties();

	private AgentSelf() {
		// 加载约定的配置文件
		loadProperties();

		// 读取自身配置信息
		this.agentName = properties.getProperty("agentName");
		this.agentType = properties.getProperty("agentType");
		this.desc = properties.getProperty("desc");
		this.port = Integer.parseInt(properties.getProperty("port"));


		// 密钥从运维获取 暂时先写死
		this.secret = properties.getProperty("secret");

		// ip地址读取 目前设计从某个文件里读取
		String frameHost = properties.getProperty("frame_host");
		this.frameRegisterUrl = frameHost + properties.getProperty("registryUrl");
		this.frameHeartUrl = frameHost + properties.getProperty("heartUrl");
		this.ip = properties.getProperty("outer_net_ip");
		this.getSecretUrl = properties.getProperty("operation_host");
		this.nodeId = properties.getProperty("node_id");
		this.url = "http://" + this.ip + ":" + this.port + properties.getProperty("url");
	}

	private void loadProperties() {
		String userDir = System.getProperties().getProperty("user.home");
		File file = new File(userDir, ".agent/properties.properties");
		boolean read = false;
		while (!read) {
			FileInputStream inputStream = null;
			InputStream stream = null;
			try {
				inputStream = new FileInputStream(file);
				stream = AgentSelf.class.getClassLoader().getResourceAsStream("netconf-agent.properties");
				properties.load(inputStream);
				properties.load(stream);
				read = true;
			} catch (Exception e) {
				log.error("代理加载配置文件失败, 捕捉到错误信息：", e);
				try {
					Thread.sleep(2 * 1000);
				} catch (InterruptedException ex) {
					log.error("线程睡眠失败！捕捉异常：", ex);
				}

			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {}
				}
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e) {}
				}
			}
		}
	}

	public static AgentSelf getInstance() {
		return agentSelf;
	}
}
