package cn.cyberpeace.mp.extensions.flyway.dm;

import org.flywaydb.core.internal.plugin.PluginRegister;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@Component
public class FlywayInit implements ServletContextInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        registerFlywayDmPlugin();
    }

    private void registerFlywayDmPlugin() {
        PluginRegister.REGISTERED_PLUGINS.add(new DmDatabaseType());
    }

}
