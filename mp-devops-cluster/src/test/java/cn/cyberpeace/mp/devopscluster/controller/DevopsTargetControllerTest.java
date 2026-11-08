package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.json.JsonHandler;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.converter.TargetConverter;
import cn.cyberpeace.mp.devopscluster.converter.TypeConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeAllConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeConvertModel;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.service.MultiCloudService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DevopsTargetControllerTest {

    private static final String URL = "/devops/cluster/target/listAll";

    private ApplicationContext previousContext;
    private Object previousSnakeJsonHandler;
    private TargetConverter targetConverter;
    private TypeConverter typeConverter;
    private Map<String, MultiCloudService> cloudServices;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        previousContext = BeanCommon.getContext();
        previousSnakeJsonHandler = ReflectionTestUtils.getField(JsonCommon.class, "snakeJsonHandler");
        ReflectionTestUtils.setField(JsonCommon.class, "snakeJsonHandler", new JsonHandler(
                new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)));
        ApplicationContext context = mock(ApplicationContext.class);
        cloudServices = new LinkedHashMap<>();
        for (int vendor = 1; vendor <= 3; vendor++) {
            MultiCloudService cloudService = mock(MultiCloudService.class);
            when(cloudService.getVendor()).thenReturn(vendor);
            when(cloudService.listAllDevices()).thenReturn(Arrays.asList(
                    "{\"vendor\":" + vendor + ",\"name\":\"Ubuntu-22.04\"}",
                    "{\"vendor\":" + vendor + ",\"name\":\"Windows-Server\"}",
                    "{\"vendor\":" + vendor + ",\"name\":\"Ubuntu-测试\"}",
                    "{\"vendor\":" + vendor + ",\"name\":null}"));
            cloudServices.put("vendor" + vendor, cloudService);
        }
        when(context.getBeansOfType(MultiCloudService.class)).thenReturn(cloudServices);
        new BeanCommon().setApplicationContext(context);
        targetConverter = mock(TargetConverter.class);
        typeConverter = mock(TypeConverter.class);
        DevopsTargetController controller = new DevopsTargetController(targetConverter, typeConverter,
                null, null, null, null, null, null);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @AfterEach
    void tearDown() {
        new BeanCommon().setApplicationContext(previousContext);
        ReflectionTestUtils.setField(JsonCommon.class, "snakeJsonHandler", previousSnakeJsonHandler);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void searchesVendorNames(int vendor) throws Exception {
        mvc.perform(get(URL).param("vendor", String.valueOf(vendor)).param("name", "ubuntu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[*].vendor", everyItem(is(vendor))))
                .andExpect(jsonPath("$.data.list[*].name", contains("Ubuntu-22.04", "Ubuntu-测试")));
        search(vendor, "测试", true)
                .andExpect(jsonPath("$.data.list[*].name", contains("Ubuntu-测试")));
        search(vendor, "missing", true)
                .andExpect(jsonPath("$.data.list", hasSize(0)));
        for (int other = 1; other <= 3; other++) {
            if (other != vendor) {
                verify(cloudServices.get("vendor" + other), never()).listAllDevices();
            }
        }
        verifyNoInteractions(targetConverter, typeConverter);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void honorsExactNameSearch(int vendor) throws Exception {
        search(vendor, "ubuntu", false)
                .andExpect(jsonPath("$.data.list", hasSize(0)));
        search(vendor, "ubuntu-22.04", false)
                .andExpect(jsonPath("$.data.list[*].name", contains("Ubuntu-22.04")));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void clearingSearchRestoresVendorList(int vendor) throws Exception {
        search(vendor, "win", true)
                .andExpect(jsonPath("$.data.list[*].name", contains("Windows-Server")));
        search(vendor, "", true)
                .andExpect(jsonPath("$.data.list", hasSize(4)))
                .andExpect(jsonPath("$.data.list[0].name", is("Ubuntu-22.04")))
                .andExpect(jsonPath("$.data.list[1].name", is("Windows-Server")))
                .andExpect(jsonPath("$.data.list[2].name", is("Ubuntu-测试")))
                .andExpect(jsonPath("$.data.list[3].name").doesNotExist());
        mvc.perform(get(URL).param("vendor", String.valueOf(vendor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list", hasSize(4)))
                .andExpect(jsonPath("$.data.list[*].vendor", everyItem(is(vendor))));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void emptyVendorListStaysEmpty(int vendor) throws Exception {
        when(cloudServices.get("vendor" + vendor).listAllDevices()).thenReturn(Collections.emptyList());
        search(vendor, "ubuntu", true).andExpect(jsonPath("$.data.list", hasSize(0)));
        search(vendor, "", true).andExpect(jsonPath("$.data.list", hasSize(0)));
    }

    @Test
    void queryWithoutVendorKeepsExistingNameFilter() throws Exception {
        TypeConvertModel first = new TypeConvertModel();
        first.setId(1);
        TypeConvertModel second = new TypeConvertModel();
        second.setId(2);
        second.setL1ParentId(1);
        when(typeConverter.findL1AndL2AndL3Type()).thenReturn(new TypeAllConvertModel(
                Collections.singletonList(first), Collections.singletonList(second), Collections.emptyList()));
        Device device = new Device();
        device.setName("Ubuntu-22.04");
        BasePageBO<Device> page = new BasePageBO<>();
        page.setList(Collections.singletonList(device));
        when(targetConverter.allTarget(any(ImagePageDTO.class))).thenReturn(page);

        mvc.perform(get(URL).param("name", "Ubuntu").param("ignoreUser", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[*].name", contains("Ubuntu-22.04")));

        verify(targetConverter).allTarget(argThat(dto -> "Ubuntu".equals(dto.getName())));
        for (MultiCloudService cloudService : cloudServices.values()) {
            verify(cloudService, never()).listAllDevices();
        }
    }

    private ResultActions search(int vendor, String name, boolean likeName) throws Exception {
        return mvc.perform(get(URL).param("vendor", String.valueOf(vendor))
                        .param("name", name).param("likeName", String.valueOf(likeName)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("MP-000000")))
                .andExpect(jsonPath("$.data.list[*].vendor", everyItem(is(vendor))));
    }
}
