package cn.cyberpeace.mp.agent.netconf.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.ssh.SshClient;
import lombok.extern.slf4j.Slf4j;
import net.juniper.netconf.Device;
import net.juniper.netconf.XML;
import net.juniper.netconf.XMLBuilder;
import net.schmizz.sshj.SSHClient;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class NetconfOperator {

	Device device;

	public NetconfOperator(String ip, String username, String password, int port) {
		try {
			device = Device.builder().hostName(ip).userName(username).password(password).port(port)
					.strictHostKeyChecking(false).hostKeysFileName(null).timeout(30000).connectionTimeout(30000).build();
			device.connect();
		} catch (Exception e) {
			log.error("Create netconf link failed！catch error：", e);
			throw new MpRuntimeException(e.getMessage());
		}
	}

	public void close() {
		if (null != device) {
			device.close();
		}
	}

	public XML send(String xmlContent) {
		try {
			XML xmlReply = device.executeRPC(xmlContent);
			return xmlReply;
		} catch (Exception e) {
			log.error("Send xml failed! catch error:", e);
			throw new MpRuntimeException(e.getMessage());
		}
	}

	public XML sendAndClose(String xmlContent) {
		try {
			XML xmlReply = device.executeRPC(xmlContent);
			close();
			return xmlReply;
		} catch (Exception e) {
			log.error("Send xml failed! catch error:", e);
			throw new MpRuntimeException(e.getMessage());
		}
	}
}
