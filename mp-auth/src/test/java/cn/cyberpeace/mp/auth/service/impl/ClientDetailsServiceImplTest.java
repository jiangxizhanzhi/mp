 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ClientDetailsServiceImplTest.java.
 // File size 1602.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
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
//@SpringBootTest(classes = {Application.class})
public class ClientDetailsServiceImplTest {

    @Mock
    private ClientDetailsService clientDetailsService;

    @BeforeEach
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
