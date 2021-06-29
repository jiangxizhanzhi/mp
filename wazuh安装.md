# wazuh 安装

## wazuh-agent 安装

### linux 安装

1. https://documentation.wazuh.com/current/installation-guide/packages-list.html#linux 下载对应系统对应cpu架构的安装包

2. 配置/var/ossec/etc/ossec.conf,修改MANAGER_IP为wazuh-manager的ip地址
   ```xml
   <client>
   	<server>
   		<address>MANAGER_IP</address>
   		...
   		</server>
   </client>
   ```
   
3. 执行 systemctl start wazuh-agent

4. 查看https://192.168.5.125/app/wazuh#/agents-preview/?_g=(filters:!(),refreshInterval:(pause:!t,value:0),time:(from:now-24h,to:now))&_a=(columns:!(_source),filters:!(('$state':(isImplicit:!t,store:appState),meta:(alias:!n,disabled:!f,index:'wazuh-alerts-*',key:manager.name,negate:!f,params:(query:localhost.localdomain),removable:!f,type:phrase),query:(match:(manager.name:(query:localhost.localdomain,type:phrase))))),index:'wazuh-alerts-*',interval:auto,query:(language:kuery,query:''),sort:!()) 查看状态，如下图表示成功
  ![img.png](img/agents.png)

5. 远程命令 安装

