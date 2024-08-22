package cn.cyberpeace.mp.agent.netconf.service;

import cn.cyberpeace.mp.agent.netconf.model.NetConfAccessInfo;
import cn.cyberpeace.mp.agent.snmp.model.Command;
import cn.cyberpeace.mp.agent.snmp.model.CommandConfig;
import cn.cyberpeace.mp.agent.snmp.model.Param;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.juniper.netconf.XML;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import java.net.NoRouteToHostException;

@Slf4j
@Service("netconfTaskService")
public class NetconfTaskServiceImpl {

    public Boolean execTask(NetConfAccessInfo netConfAccessInfo, CommandConfig config) {
        NetconfOperator operator = null;
        try {
            operator = new NetconfOperator(netConfAccessInfo.getHostIp(), netConfAccessInfo.getUsername(), netConfAccessInfo.getPassword(), Integer.parseInt(netConfAccessInfo.getPort()));
            String message = formatConfig(config.getCommands().get(0));
            XML send = operator.send(message);
            Document document = DocumentHelper.parseText(send.toString());
            // 获取根节点rpc-reply
            Element rootElement = document.getRootElement();
            // 获取根节点下的某个元素
            Element errorFlag = rootElement.element("rpc-error");
            if (null != errorFlag) {
                // 执行出错，获取错误信息
                Element errorType = errorFlag.element("error-type");
                Element errorTag = errorFlag.element("error-tag");
                String failMessage = errorType.getData() + ":" + errorTag.getData();
                log.error("代理执行失败，错误信息：" + failMessage);
            } else {
                log.info("代理执行成功，请求信息为：" + message);
            }
        } catch (Exception e) {
            log.error("代理执行失败", e);
            if (e instanceof NoRouteToHostException) {
                throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("目标节点的网络不通"));
            }
        } finally {
            operator.close();
        }

        return Boolean.TRUE;
    }

    private String formatConfig(Command command) {
        String finalCommand = command.getCommand();

        for (Param param : command.getParams()) {

            finalCommand = finalCommand.replaceAll("\\$\\{" + param.getCode() + "}", "\"" + param.getValue() + "\"");
        }

        return finalCommand;
    }
}
