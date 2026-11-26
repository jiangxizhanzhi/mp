package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.service.MultiCloudService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.ApplicationContext;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VendorPowerHandlerTest {
    private static class Fixture implements AutoCloseable {
        final ApplicationContext previous = BeanCommon.getContext();
        final MultiCloudService cloud = mock(MultiCloudService.class);
        final CloudScenePluginHandler plugin = mock(CloudScenePluginHandler.class);
        final CloudPluginSceneElementBO element = new CloudPluginSceneElementBO();
        final CloudAccessMode mode = new CloudAccessMode();
        final StartHandler start = mock(StartHandler.class, CALLS_REAL_METHODS);
        final StopHandler stop = mock(StopHandler.class, CALLS_REAL_METHODS);
        final RestartHandler restart = mock(RestartHandler.class, CALLS_REAL_METHODS);

        Fixture(int vendor) {
            ApplicationContext context = mock(ApplicationContext.class);
            when(context.getBeansOfType(MultiCloudService.class)).thenReturn(Collections.singletonMap("vendor", cloud));
            when(cloud.getVendor()).thenReturn(vendor);
            new BeanCommon().setApplicationContext(context);
            SceneElementBO src = new SceneElementBO();
            src.setId("node");
            src.setEid("element");
            src.setVendor(vendor);
            element.setSrcElement(src);
            element.setResourceId("vm");
            element.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE);
            CloudPluginSceneElementPortBO port = new CloudPluginSceneElementPortBO();
            mode.setProtocol("vnc");
            mode.setAvailable(true);
            port.setAccessModes(Collections.singletonList(mode));
            when(plugin.getPorts(element)).thenReturn(Collections.singletonList(port));
            SceneHandler scene = mock(SceneHandler.class);
            when(plugin.getSceneHandler()).thenReturn(scene);
            when(scene.getSceneId()).thenReturn("scene");
            for (HandlerCommon handler : new HandlerCommon[]{start, stop, restart}) {
                doReturn(plugin).when(handler).getPluginHandler();
            }
        }

        @Override public void close() {
            new BeanCommon().setApplicationContext(previous);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void routesAllThreeOperationsAndUpdatesConsoleState(int vendor) {
        try (Fixture f = new Fixture(vendor)) {
            f.stop.stopElement(f.element);
            assertFalse(f.mode.getAvailable());
            f.start.startElement(f.element);
            assertTrue(f.mode.getAvailable());
            f.restart.restartElement(f.element);
            assertTrue(f.mode.getAvailable());
            verify(f.cloud).stopVm("vm");
            verify(f.cloud).startVm("vm");
            verify(f.cloud).restartVm("vm");
            verify(f.plugin).overElementProcess("node", SceneElementModel.Event.STOP);
            verify(f.plugin).overElementProcess("node", SceneElementModel.Event.START);
            verify(f.plugin).overElementProcess("node", SceneElementModel.Event.RESTART);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void rejectedRestartDoesNotMarkAvailableOrSuccessful(int vendor) {
        try (Fixture f = new Fixture(vendor)) {
            doThrow(new MpRuntimeException("fixture failure")).when(f.cloud).restartVm("vm");
            assertThrows(MpRuntimeException.class, () -> f.restart.restartElement(f.element));
            assertFalse(f.mode.getAvailable());
            verify(f.plugin, never()).overElementProcess("node", SceneElementModel.Event.RESTART);
            verify(f.plugin).errorElement("node", SceneElementModel.Event.RESTART, "fixture failure");
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void deleteDispatchIncludesOriginalSceneAndElementAssociation(int vendor) {
        try (Fixture f = new Fixture(vendor)) {
            DeleteHandler handler = mock(DeleteHandler.class, CALLS_REAL_METHODS);
            doReturn(f.plugin).when(handler).getPluginHandler();
            SceneHandler scene = mock(SceneHandler.class);
            when(f.plugin.getSceneHandler()).thenReturn(scene);
            when(scene.getSceneId()).thenReturn("scene");
            f.element.getSrcElement().setEid("element");
            assertTrue(handler.deleteVirtualMachine(f.element));
            verify(f.cloud).deleteManagedVm("vm", "scene", "element");
        }
    }
}
