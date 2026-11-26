package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.service.ManagedVmCreationResult;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.service.MultiCloudService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.ApplicationContext;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateHandlerTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 3})
    void clearsIpContextWhenVendorCreateFails(int vendor) {
        ApplicationContext previous = BeanCommon.getContext();
        ApplicationContext context = mock(ApplicationContext.class);
        MultiCloudService cloud = mock(MultiCloudService.class);
        when(cloud.getVendor()).thenReturn(vendor);
        when(context.getBeansOfType(MultiCloudService.class)).thenReturn(Collections.singletonMap("vendor", cloud));
        new BeanCommon().setApplicationContext(context);
        SceneElementPortBO port = new SceneElementPortBO();
        port.setIp("198.51.100.2");
        SceneElementBO element = new SceneElementBO();
        element.setVendor(vendor);
        element.setPorts(Collections.singletonList(port));
        CloudPluginSceneElementBO cloudElement = new CloudPluginSceneElementBO();
        cloudElement.setSrcElement(element);
        cloudElement.setImageUuid("115");
        MpRuntimeException failure = new MpRuntimeException("fixture create failure");
        when(cloud.createVm("115")).thenAnswer(inv -> {
            assertEquals("198.51.100.2", MultiCloudService.IP_THREADLOCAL.get());
            throw failure;
        });
        try {
            CreateHandler handler = mock(CreateHandler.class, CALLS_REAL_METHODS);
            assertSame(failure, assertThrows(MpRuntimeException.class, () -> handler.createVirtualMachine(cloudElement)));
            assertNull(MultiCloudService.IP_THREADLOCAL.get());
            assertTrue(cloudElement.getResourceId() == null || cloudElement.getResourceId().isEmpty());
        } finally {
            MultiCloudService.IP_THREADLOCAL.remove();
            new BeanCommon().setApplicationContext(previous);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void vendorSecondUsesManagedResultRatherThanTemplate(boolean verified) {
        ApplicationContext previous = BeanCommon.getContext();
        ApplicationContext context = mock(ApplicationContext.class);
        MultiCloudService cloud = mock(MultiCloudService.class);
        when(cloud.getVendor()).thenReturn(2);
        when(context.getBeansOfType(MultiCloudService.class)).thenReturn(Collections.singletonMap("vendor", cloud));
        new BeanCommon().setApplicationContext(context);
        SceneElementPortBO port = new SceneElementPortBO();
        port.setIp("198.51.100.2");
        port.setEid("port");
        SceneElementBO element = new SceneElementBO();
        element.setEid("element");
        element.setVendor(2);
        element.setPorts(Collections.singletonList(port));
        CloudPluginSceneElementBO cloudElement = new CloudPluginSceneElementBO();
        cloudElement.setSrcElement(element);
        cloudElement.setImageUuid("fixture");
        CloudScenePluginHandler plugin = mock(CloudScenePluginHandler.class);
        SceneHandler scene = mock(SceneHandler.class);
        when(plugin.getSceneHandler()).thenReturn(scene);
        when(scene.getSceneId()).thenReturn("scene");
        when(plugin.getPorts(cloudElement)).thenReturn(Collections.emptyList());
        when(cloud.createManagedVm("fixture", "scene", "element", "port", "198.51.100.2"))
                .thenReturn(new ManagedVmCreationResult("vm", verified ? "198.51.100.10" : null,
                        verified ? "02:00:00:00:00:01" : null, "255.255.255.0", "198.51.100.1"));
        try {
            CreateHandler handler = mock(CreateHandler.class, CALLS_REAL_METHODS);
            doReturn(plugin).when(handler).getPluginHandler();
            handler.createVirtualMachine(cloudElement);
            assertEquals("vm", cloudElement.getResourceId());
            assertEquals(verified ? "198.51.100.10" : "", port.getIp());
            assertEquals(verified ? "02:00:00:00:00:01" : "", port.getMacAddr());
            verify(cloud, never()).createVm(anyString());
            verify(cloud, never()).getVmIpAndMac(anyString());
            assertNull(MultiCloudService.IP_THREADLOCAL.get());
        } finally {
            MultiCloudService.IP_THREADLOCAL.remove();
            new BeanCommon().setApplicationContext(previous);
        }
    }

}
