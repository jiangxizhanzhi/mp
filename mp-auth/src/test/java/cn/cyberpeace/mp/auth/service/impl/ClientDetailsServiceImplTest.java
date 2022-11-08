package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.auth.service.ClientDetailsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(SpringExtension.class)
//@SpringBootTest(classes = {Application.class})
public class ClientDetailsServiceImplTest {

    @Mock
    private ClientDetailsService clientDetailsService;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

//    @Test
    public void testGetClient() {
        when(clientDetailsService.getByClientId("abc")).thenReturn(createClientDetailsModel());
        ClientDetailsModel model = clientDetailsService.getByClientId("abc");
        ClientDetailsModel model1 = clientDetailsService.getByClientId("abc");
        assertEquals("123456", model.getClientSecret());
        assertEquals("123456", model1.getClientSecret());
//        verify(clientDetailsService).getByClientId("abc1");
        verify(clientDetailsService, times(2)).getByClientId("abc");
    }


    private ClientDetailsModel createClientDetailsModel() {
        ClientDetailsModel model = new ClientDetailsModel();
        model.setClientId("abc");
        model.setClientSecret("123456");
        return model;
    }


}
