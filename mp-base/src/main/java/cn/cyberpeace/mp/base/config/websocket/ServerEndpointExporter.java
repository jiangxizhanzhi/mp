package cn.cyberpeace.mp.base.config.websocket;

import cn.hutool.core.annotation.AnnotationUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.support.WebApplicationObjectSupport;

import javax.servlet.ServletContext;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.util.*;

/**
 * from org.springframework.web.socket.server.standard.ServerEndpointExporter
 */
public class ServerEndpointExporter extends WebApplicationObjectSupport
        implements InitializingBean, SmartInitializingSingleton {

    @Nullable
    private List<Class<?>> annotatedEndpointClasses;

    @Nullable
    private ServerContainer serverContainer;


    /**
     * Explicitly list annotated endpoint types that should be registered on startup. This
     * can be done if you wish to turn off a Servlet container's scan for endpoints, which
     * goes through all 3rd party jars in the, and rely on Spring configuration instead.
     * @param annotatedEndpointClasses {@link ServerEndpoint}-annotated types
     */
    public void setAnnotatedEndpointClasses(Class<?>... annotatedEndpointClasses) {
        this.annotatedEndpointClasses = Arrays.asList(annotatedEndpointClasses);
    }

    /**
     * Set the JSR-356 {@link ServerContainer} to use for endpoint registration.
     * If not set, the container is going to be retrieved via the {@code ServletContext}.
     */
    public void setServerContainer(@Nullable ServerContainer serverContainer) {
        this.serverContainer = serverContainer;
    }

    /**
     * Return the JSR-356 {@link ServerContainer} to use for endpoint registration.
     */
    @Nullable
    protected ServerContainer getServerContainer() {
        return this.serverContainer;
    }

    @Override
    protected void initServletContext(ServletContext servletContext) {
        if (this.serverContainer == null) {
            this.serverContainer =
                    (ServerContainer) servletContext.getAttribute("javax.websocket.server.ServerContainer");
        }
    }

    @Override
    protected boolean isContextRequired() {
        return false;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.state(getServerContainer() != null, "javax.websocket.server.ServerContainer not available");
    }

    @Override
    public void afterSingletonsInstantiated() {
        registerEndpoints();
    }


    /**
     * Actually register the endpoints. Called by {@link #afterSingletonsInstantiated()}.
     */
    protected void registerEndpoints() {
        Set<Class<?>> endpointClasses = new LinkedHashSet<>();
        if (this.annotatedEndpointClasses != null) {
            endpointClasses.addAll(this.annotatedEndpointClasses);
        }

        ApplicationContext context = getApplicationContext();
        if (context != null) {
            String[] customEndpointBeanNames = context.getBeanNamesForAnnotation(ServerEndpoint.class);
            for (String beanName : customEndpointBeanNames) {
                var endpointClass = context.getType(beanName);
                var serverEndpoint = endpointClass.getAnnotation(ServerEndpoint.class);
                if (ServerEndpointConfig.Configurator.class.equals(serverEndpoint.configurator())) {
                    AnnotationUtil.setValue(serverEndpoint, "configurator", WebSocketConfig.Configurator.class);
                }
                endpointClasses.add(endpointClass);
            }
        }

        for (Class<?> endpointClass : endpointClasses) {
            registerEndpoint(endpointClass);
        }

        if (context != null) {
            Map<String, ServerEndpointConfig> endpointConfigMap = context.getBeansOfType(ServerEndpointConfig.class);
            for (ServerEndpointConfig endpointConfig : endpointConfigMap.values()) {
                registerEndpoint(endpointConfig);
            }
        }
    }

    private void registerEndpoint(Class<?> endpointClass) {
        ServerContainer serverContainer = getServerContainer();
        Assert.state(serverContainer != null,
                "No ServerContainer set. Most likely the server's own WebSocket ServletContainerInitializer " +
                        "has not run yet. Was the Spring ApplicationContext refreshed through a " +
                        "org.springframework.web.context.ContextLoaderListener, " +
                        "i.e. after the ServletContext has been fully initialized?");
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Registering @ServerEndpoint class: " + endpointClass);
            }
            serverContainer.addEndpoint(endpointClass);
        }
        catch (DeploymentException ex) {
            throw new IllegalStateException("Failed to register @ServerEndpoint class: " + endpointClass, ex);
        }
    }

    private void registerEndpoint(ServerEndpointConfig endpointConfig) {
        ServerContainer serverContainer = getServerContainer();
        Assert.state(serverContainer != null, "No ServerContainer set");
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Registering ServerEndpointConfig: " + endpointConfig);
            }
            serverContainer.addEndpoint(endpointConfig);
        }
        catch (DeploymentException ex) {
            throw new IllegalStateException("Failed to register ServerEndpointConfig: " + endpointConfig, ex);
        }
    }

}
