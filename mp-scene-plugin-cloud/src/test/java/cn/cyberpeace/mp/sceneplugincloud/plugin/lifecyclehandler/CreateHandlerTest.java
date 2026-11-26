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
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateHandlerTest {

    @Test
    void unrecognizedExternalProviderNeverFallsThroughToLocalCreation() {
        ApplicationContext previous = BeanCommon.getContext();
        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getBeansOfType(MultiCloudService.class)).thenReturn(Collections.emptyMap());
        new BeanCommon().setApplicationContext(context);
        try {
            SceneElementBO element = new SceneElementBO();
            element.setVendor(99);
            CloudPluginSceneElementBO cloudElement = new CloudPluginSceneElementBO();
            cloudElement.setSrcElement(element);
            CreateHandler handler = mock(CreateHandler.class, CALLS_REAL_METHODS);
            MpRuntimeException error = assertThrows(MpRuntimeException.class, () -> handler.createVirtualMachine(cloudElement));
            assertTrue(error.getMessage().contains("未找到厂商99"));
        } finally {
            new BeanCommon().setApplicationContext(previous);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1})
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
    @CsvSource({"2,true", "2,false", "3,true", "3,false"})
    void managedVendorsUseActualResultRatherThanTemplate(int vendor, boolean verified) {
        ApplicationContext previous = BeanCommon.getContext();
        ApplicationContext context = mock(ApplicationContext.class);
        MultiCloudService cloud = mock(MultiCloudService.class);
        when(cloud.getVendor()).thenReturn(vendor);
        when(context.getBeansOfType(MultiCloudService.class)).thenReturn(Collections.singletonMap("vendor", cloud));
        new BeanCommon().setApplicationContext(context);
        SceneElementPortBO port = new SceneElementPortBO();
        port.setIp("198.51.100.2");
        port.setEid("port");
        SceneElementBO element = new SceneElementBO();
        element.setEid("element");
        element.setVendor(vendor);
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
                        verified ? "02:00:00:00:00:01" : null, "255.255.255.0", "198.51.100.1", "task", verified, vendor, "fixture"));
        try {
            CreateHandler handler = mock(CreateHandler.class, CALLS_REAL_METHODS);
            doReturn(plugin).when(handler).getPluginHandler();
            if (verified) {
                handler.createVirtualMachine(cloudElement);
            } else {
                assertThrows(MpRuntimeException.class, () -> handler.createVirtualMachine(cloudElement));
                verify(scene).updateSceneElementWithPlugins(element.getId());
            }
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

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void vendorOneAddressPollingStopsOnSuccessOrLimit(boolean ready) throws Exception {
        ApplicationContext previous = BeanCommon.getContext();
        ApplicationContext context = mock(ApplicationContext.class);
        MultiCloudService cloud = mock(MultiCloudService.class);
        when(cloud.getVendor()).thenReturn(1);
        when(cloud.ipAutoGen()).thenReturn(true);
        when(cloud.createVm("fixture")).thenReturn("fixture-id");
        when(context.getBeansOfType(MultiCloudService.class)).thenReturn(Collections.singletonMap("vendor", cloud));
        when(cloud.getVmIpAndMac("fixture-id")).thenReturn(
                ready ? new String[]{"198.51.100.10", "02:00:00:00:00:01"} : null);
        new BeanCommon().setApplicationContext(context);
        SceneElementPortBO port = new SceneElementPortBO();
        port.setIp("198.51.100.2");
        port.setMacAddr("02:00:00:00:00:02");
        SceneElementBO element = new SceneElementBO();
        element.setVendor(1);
        element.setPorts(Collections.singletonList(port));
        CloudPluginSceneElementBO cloudElement = new CloudPluginSceneElementBO();
        cloudElement.setSrcElement(element);
        cloudElement.setImageUuid("fixture");
        CloudScenePluginHandler plugin = mock(CloudScenePluginHandler.class);
        SceneHandler scene = mock(SceneHandler.class);
        when(plugin.getSceneHandler()).thenReturn(scene);
        when(plugin.getPorts(cloudElement)).thenReturn(Collections.emptyList());
        try {
            CreateHandler handler = mock(CreateHandler.class, CALLS_REAL_METHODS);
            doReturn(plugin).when(handler).getPluginHandler();
            doNothing().when(handler).pauseVendorAddressPoll();
            if (ready) {
                handler.createVirtualMachine(cloudElement);
                verify(cloud).getVmIpAndMac("fixture-id");
                assertEquals("198.51.100.10", port.getIp());
            } else {
                assertThrows(MpRuntimeException.class, () -> handler.createVirtualMachine(cloudElement));
                verify(cloud, times(20)).getVmIpAndMac("fixture-id");
                verify(scene).updateSceneElementWithPlugins(element.getId());
                assertEquals("", port.getIp());
                assertEquals("", port.getMacAddr());
            }
            assertEquals("fixture-id", cloudElement.getResourceId());
        } finally {
            MultiCloudService.IP_THREADLOCAL.remove();
            new BeanCommon().setApplicationContext(previous);
        }
    }

}
