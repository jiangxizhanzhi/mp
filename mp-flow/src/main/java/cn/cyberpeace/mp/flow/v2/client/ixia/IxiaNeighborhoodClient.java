package cn.cyberpeace.mp.flow.v2.client.ixia;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/31
 */
@Slf4j
public class IxiaNeighborhoodClient {
    private IxiaHttpClient ixiaHttpClient;

    public IxiaNeighborhoodClient(IxiaHttpClient ixiaHttpClient) {
        this.ixiaHttpClient = ixiaHttpClient;
    }

    public void addNeighborhood(BaseCreateData baseCreateData) {
        var response = ixiaHttpClient.ixiaPost(IxiaUrlConstant.CREATE_NEIGHBORHOOD, baseCreateData, null, CreateNeighborHoodResult.class, new TypeReference<CreateNeighborHoodResult>() {
        });
        log.info("Load neighborhood response:{}", response);
    }

    public void saveNeighborhood(CreateNeighborHoodData createNeighborHoodData) {
        // todo 名称是必要的，其他的两个参数"regenerateOldStyle": <bool>，"force": <bool>先不传
        var response = ixiaHttpClient.ixiaPost(IxiaUrlConstant.SAVE_NEIGHBORHOOD, createNeighborHoodData, null, AddSuperFlowResult.class, new TypeReference<AddSuperFlowResult>() {
        });
        log.info("Save neighborhood response:{}", response);
    }

    public void addIpRouterToNeighborhood(IpRouterData IpRouter) {
        var response = ixiaHttpClient.ixiaPost(IxiaUrlConstant.ADD_IP_ROUTER_TO_NEIGHBORHOOD, IpRouter, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Add ip router to neighborhood response:{}", response);
    }

    public void addInterfaceToNeighborhood(InterfaceData addInterface) {
        var response = ixiaHttpClient.ixiaPost(IxiaUrlConstant.ADD_INTERFACE_TO_NEIGHBORHOOD, addInterface, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Add interface to neighborhood response:{}", response);
    }

    public void addExternalHostsToNeighborhood(ExternalHostsData addExternalHosts) {
        var response = ixiaHttpClient.ixiaPost(IxiaUrlConstant.ADD_EXTERNAL_HOSTS_TO_NEIGHBORHOOD, addExternalHosts, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Add external hosts to neighborhood response:{}", response);
    }

    public void addStaticHostsToNeighborhood(StaticHostsData addStaticHosts) {
        var response = ixiaHttpClient.ixiaPost(IxiaUrlConstant.ADD_STATIC_HOSTS_TO_NEIGHBORHOOD, addStaticHosts, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Add static hosts to neighborhood response:{}", response);
    }

    public void changeStaticHosts(StaticHostsData staticHostsData) {
        var response = ixiaHttpClient.ixiaPatch(IxiaUrlConstant.CONFIG_STATIC_HOSTS, staticHostsData, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Change static hosts to neighborhood response:{}", response);
    }

    public void changeInterface(InterfaceData interfaceData) {
        var response = ixiaHttpClient.ixiaPatch(IxiaUrlConstant.CONFIG_INTERFACE, interfaceData, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Change interface to neighborhood response:{}", response);
    }

    public void changeExternalHosts(ExternalHostsData externalHostsData) {
        var response = ixiaHttpClient.ixiaPatch(IxiaUrlConstant.CONFIG_EXTERNAL_HOSTS, externalHostsData, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Change external hosts to neighborhood response:{}", response);
    }

}
