package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
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
    @ValueSource(ints = {1, 2, 3})
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
}
