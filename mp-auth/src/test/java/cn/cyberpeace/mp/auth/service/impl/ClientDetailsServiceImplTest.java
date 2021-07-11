package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.auth.service.ClientDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)

public class ClientDetailsServiceImplTest {

    @Mock
    private ClientDetailsService clientDetailsService;

    @BeforeEach
    public void before() {
        MockitoAnnotations.initMocks(this);
    }


    public void testGetClient() {
        when(clientDetailsService.getByClientId("abc")).thenReturn(createClientDetailsModel());
        ClientDetailsModel model = clientDetailsService.getByClientId("abc");
        ClientDetailsModel model1 = clientDetailsService.getByClientId("abc");
        assertEquals("123456", model.getClientSecret());
        assertEquals("123456", model1.getClientSecret());

        verify(clientDetailsService, times(2)).getByClientId("abc");
    }


    private ClientDetailsModel createClientDetailsModel() {
        ClientDetailsModel model = new ClientDetailsModel();
        model.setClientId("abc");
        model.setClientSecret("123456");
        return model;
    }


}
