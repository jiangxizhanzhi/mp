package cn.cyberpeace.mp.agent.netconf.service.impl;

import cn.cyberpeace.mp.agent.netconf.controller.request.NetConfAccessInfo;
import cn.cyberpeace.mp.agent.netconf.netconf.NetconfOperator;
import cn.cyberpeace.mp.agent.snmp.service.Command;
import cn.cyberpeace.mp.agent.snmp.service.CommandConfig;
import cn.cyberpeace.mp.agent.snmp.service.Param;
import lombok.extern.slf4j.Slf4j;
import net.juniper.netconf.XML;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

@Slf4j
@Service("netconfTaskService")
public class NetconfTaskServiceImpl {

    public Boolean execTask(NetConfAccessInfo netConfAccessInfo, CommandConfig config) {
        NetconfOperator operator = new NetconfOperator(netConfAccessInfo.getHostIp(), netConfAccessInfo.getUsername(), netConfAccessInfo.getPassword(), Integer.parseInt(netConfAccessInfo.getPort()));
        String message = formatConfig(config.getCommands().get(0));
        try {
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
