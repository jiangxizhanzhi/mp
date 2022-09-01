package cn.cyberpeace.mp.proxy.lf;

import cn.cyberpeace.mp.proxy.ProxyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class LfProxyInit implements WebMvcConfigurer {

    @Autowired
    private ProxyConfig proxyConfig;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        var lf = proxyConfig.getLf();
        if (lf == null || lf.isEmpty()) {
            return;
        }

        for (var lfProxyConfig : lf) {
            registry.addResourceHandler(lfProxyConfig.getUrl()).addResourceLocations(lfProxyConfig.getLocation());
        }
    }

}
