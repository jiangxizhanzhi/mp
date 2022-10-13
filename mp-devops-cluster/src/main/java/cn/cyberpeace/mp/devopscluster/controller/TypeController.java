package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Switches;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.TypeService;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.SerialPortConverter;
import cn.cyberpeace.mp.devopscluster.converter.SwitchConverter;
import cn.cyberpeace.mp.devopscluster.converter.TypeConverter;
import cn.cyberpeace.mp.devopscluster.converter.model.SerialPortConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.SwitchConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeAllConvertModel;
import cn.cyberpeace.mp.devopscluster.model.common.TargetType;
import cn.cyberpeace.mp.devopscluster.vo.SerialPortVO;
import cn.cyberpeace.mp.devopscluster.vo.TypeAllVO;
import cn.cyberpeace.mp.devopscluster.vo.TypeL2AllVO;
import cn.cyberpeace.mp.devopscluster.vo.common.ListResult;
import cn.cyberpeace.mp.devopscluster.vo.common.Result;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 类型管理类
 */
@Validated
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/types")
@Api(tags = "靶标管理-分类管理")
@RequiredArgsConstructor
public class TypeController {

    private static final String CCM_1 = "CCM-1";

    private final TypeConverter typeConverter;

    private final TypeService typeService;

    private final SerialPortConverter serialPortConverter;

    private final SwitchConverter switchConverter;

    /**
     * 查询所有分类
     * @return
     */
    @ApiOperation("查询所有靶标分类")
    @GetMapping("/target-categories")
    public Result<TypeAllVO> targetCategories(){
        TypeAllConvertModel roles = typeConverter.findRoles();
        TypeAllConvertModel entityRoles = typeConverter.findEntityRoles();
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        TypeAllConvertModel language = typeConverter.findLanguage();
        TypeAllConvertModel systemArchitecure = typeConverter.findSystemArchitecure();
        TypeAllConvertModel snmpVersion = typeConverter.findSnmpVersion();
        TypeAllConvertModel imageType = typeConverter.findImageType();
        TypeAllVO allResponse = new TypeAllVO();
        List<SerialPortConvertModel> serialPortConvertModels = serialPortConverter.list();
        allResponse.setSerialPortRespons(Lists.newArrayList(new SerialPortVO(CCM_1,CCM_1,serialPortConvertModels)));
        allResponse.setSwitchConvertModels(switchConverter.treeList());
        allResponse.setAccessProtocolsPorts(typeService.findAccessProtocolsPort().getList());
        allResponse.setSnmpVersions(Objects.nonNull(snmpVersion) ? snmpVersion.getIdStringModelList() : null);
        allResponse.setRole(Objects.nonNull(roles) ? roles.getIdIntegerModelList() : null);
        allResponse.setEntityRole(Objects.nonNull(entityRoles) ? entityRoles.getIdIntegerModelList() : null);
        allResponse.setFirstTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getFirstTypeList() : null);
        allResponse.setSecondTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getSecondTypeList() : null);
        allResponse.setThirdTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getThirdTypeList() : null);
        allResponse.setLanguage(Objects.nonNull(language) ? language.getIdStringModelList() : null);
        allResponse.setSystemArchitecure(Objects.nonNull(systemArchitecure) ?
                systemArchitecure.getIdStringModelList() : null);
        allResponse.setImageType(Objects.nonNull(imageType) ? imageType.getIdStringModelList() : null);
        return new Result<>(allResponse);
    }

    /**
     * 查询所有分类
     * @return
     */
    @ApiOperation("查询所有镜像管理分类")
    @ApiImplicitParam(name = "isAll", value = "是否全部", dataTypeClass = Boolean.class)
    @GetMapping("/image-mg-categories")
    public Result<TypeAllVO> imageCategories(Boolean isAll){
        /*
        if (Objects.isNull(isAll)){
            isAll = Boolean.FALSE;
        }
        TypeAllConvertModel roles = typeConverter.findRoles(isAll);
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type(isAll);
        TypeAllConvertModel imageType = typeConverter.findImageType(isAll);
        TypeAllConvertModel diskFormat = typeConverter.findDiskFormat();
        TypeAllVO allResponse = new TypeAllVO();
        allResponse.setRole(Objects.nonNull(roles) ? roles.getIdIntegerModelList() : null);
        allResponse.setFirstTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getFirstTypeList() : null);
        allResponse.setSecondTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getSecondTypeList() : null);
        allResponse.setThirdTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getThirdTypeList() : null);
        allResponse.setImageType(Objects.nonNull(imageType) ? imageType.getIdStringModelList() : null);
        allResponse.setHwDiskBusList(typeConverter.findHwDiskBus());
        allResponse.setHwVifModelList(typeConverter.findHwVifModel());
        allResponse.setHwVideoModelList(typeConverter.findHwVideoModel());
        allResponse.setDiskFormat(Objects.nonNull(diskFormat) ? diskFormat.getIdStringModelList() : null);
        */

        TypeAllVO allResponse = mockTypeVOs();
        return new Result<>(allResponse);
    }

    private TypeAllVO mockTypeVOs() {
        String arrStr = "{\n" +
                "        \"first_type_list\": [\n" +
                "            {\n" +
                "                \"id\": 1,\n" +
                "                \"name\": \"操作系统\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 2,\n" +
                "                \"name\": \"应用系统\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 3,\n" +
                "                \"name\": \"网络设备\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 4,\n" +
                "                \"name\": \"安全设备\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": null\n" +
                "            }\n" +
                "        ],\n" +
                "        \"second_type_list\": [\n" +
                "            {\n" +
                "                \"id\": 1,\n" +
                "                \"name\": \"WINDOWS\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 2,\n" +
                "                \"name\": \"LINUX\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 3,\n" +
                "                \"name\": \"UNIX\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 4,\n" +
                "                \"name\": \"BSD\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 5,\n" +
                "                \"name\": \"OTHER\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 30,\n" +
                "                \"name\": \"网络服务\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 2\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 31,\n" +
                "                \"name\": \"中间件\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 2\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 32,\n" +
                "                \"name\": \"数据库\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 2\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 33,\n" +
                "                \"name\": \"软件开发\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 2\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 34,\n" +
                "                \"name\": \"基础镜像\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 2\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 35,\n" +
                "                \"name\": \"运维工具\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 2\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 36,\n" +
                "                \"name\": \"邮件服务器\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 2\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 37,\n" +
                "                \"name\": \"文件服务器\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 2\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 38,\n" +
                "                \"name\": \"企业办公\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 2\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 39,\n" +
                "                \"name\": \"统计分析\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 2\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 40,\n" +
                "                \"name\": \"负载均衡\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 2\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 41,\n" +
                "                \"name\": \"应用框架\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 2\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 42,\n" +
                "                \"name\": \"Web服务器\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 2\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 43,\n" +
                "                \"name\": \"其他\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 2\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 50,\n" +
                "                \"name\": \"交换机\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 3\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 51,\n" +
                "                \"name\": \"路由器\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 3\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 52,\n" +
                "                \"name\": \"防火墙\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 3\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 53,\n" +
                "                \"name\": \"外部网络\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 3\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 54,\n" +
                "                \"name\": \"NPC\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 3\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 500,\n" +
                "                \"name\": \"IDS\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 4\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 501,\n" +
                "                \"name\": \"WAF\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 4\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 502,\n" +
                "                \"name\": \"VPN\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 4\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 503,\n" +
                "                \"name\": \"行为管理\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 4\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 504,\n" +
                "                \"name\": \"安全审计\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 4\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 505,\n" +
                "                \"name\": \"防火墙\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 4\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 506,\n" +
                "                \"name\": \"IPS\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 4\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 507,\n" +
                "                \"name\": \"SIEM\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 4\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 508,\n" +
                "                \"name\": \"其他\",\n" +
                "                \"l2_parent_id\": null,\n" +
                "                \"l1_parent_id\": 4\n" +
                "            }\n" +
                "        ],\n" +
                "        \"third_type_list\": [\n" +
                "            {\n" +
                "                \"id\": 97,\n" +
                "                \"name\": \"WindowsXP\",\n" +
                "                \"l2_parent_id\": 1,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 98,\n" +
                "                \"name\": \"Windows-Server2016\",\n" +
                "                \"l2_parent_id\": 1,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 99,\n" +
                "                \"name\": \"Windows8\",\n" +
                "                \"l2_parent_id\": 1,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 100,\n" +
                "                \"name\": \"Windows-Server2008\",\n" +
                "                \"l2_parent_id\": 1,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 101,\n" +
                "                \"name\": \"Windows-Server2003\",\n" +
                "                \"l2_parent_id\": 1,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 102,\n" +
                "                \"name\": \"Windows Vista\",\n" +
                "                \"l2_parent_id\": 1,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 103,\n" +
                "                \"name\": \"Windows-Server2019\",\n" +
                "                \"l2_parent_id\": 1,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 104,\n" +
                "                \"name\": \"Windows-Server2012\",\n" +
                "                \"l2_parent_id\": 1,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 105,\n" +
                "                \"name\": \"Windows-Server2010\",\n" +
                "                \"l2_parent_id\": 1,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 106,\n" +
                "                \"name\": \"Windows10\",\n" +
                "                \"l2_parent_id\": 1,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 107,\n" +
                "                \"name\": \"Windows7\",\n" +
                "                \"l2_parent_id\": 1,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 108,\n" +
                "                \"name\": \"Windows-Server2000\",\n" +
                "                \"l2_parent_id\": 1,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 301,\n" +
                "                \"name\": \"Gentoo\",\n" +
                "                \"l2_parent_id\": 2,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 302,\n" +
                "                \"name\": \"Mint\",\n" +
                "                \"l2_parent_id\": 2,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 303,\n" +
                "                \"name\": \"Arch\",\n" +
                "                \"l2_parent_id\": 2,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 304,\n" +
                "                \"name\": \"Ubuntu\",\n" +
                "                \"l2_parent_id\": 2,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 305,\n" +
                "                \"name\": \"Deepin\",\n" +
                "                \"l2_parent_id\": 2,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 306,\n" +
                "                \"name\": \"CentOS\",\n" +
                "                \"l2_parent_id\": 2,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 307,\n" +
                "                \"name\": \"Debian\",\n" +
                "                \"l2_parent_id\": 2,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 308,\n" +
                "                \"name\": \"Rocky\",\n" +
                "                \"l2_parent_id\": 2,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 309,\n" +
                "                \"name\": \"Kali\",\n" +
                "                \"l2_parent_id\": 2,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 310,\n" +
                "                \"name\": \"Fedora\",\n" +
                "                \"l2_parent_id\": 2,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 311,\n" +
                "                \"name\": \"Android\",\n" +
                "                \"l2_parent_id\": 2,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 312,\n" +
                "                \"name\": \"Redhat\",\n" +
                "                \"l2_parent_id\": 2,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 313,\n" +
                "                \"name\": \"SUSE\",\n" +
                "                \"l2_parent_id\": 2,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 314,\n" +
                "                \"name\": \"其他\",\n" +
                "                \"l2_parent_id\": 2,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 1,\n" +
                "                \"name\": \"AIX\",\n" +
                "                \"l2_parent_id\": 3,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 2,\n" +
                "                \"name\": \"HP-UX\",\n" +
                "                \"l2_parent_id\": 3,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 3,\n" +
                "                \"name\": \"Solaris\",\n" +
                "                \"l2_parent_id\": 3,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 4,\n" +
                "                \"name\": \"IRIX\",\n" +
                "                \"l2_parent_id\": 3,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 5,\n" +
                "                \"name\": \"A/UX\",\n" +
                "                \"l2_parent_id\": 3,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 6,\n" +
                "                \"name\": \"Xenix\",\n" +
                "                \"l2_parent_id\": 3,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 201,\n" +
                "                \"name\": \"FreeBSD\",\n" +
                "                \"l2_parent_id\": 4,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 202,\n" +
                "                \"name\": \"OpenBSD\",\n" +
                "                \"l2_parent_id\": 4,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 203,\n" +
                "                \"name\": \"NetBSD\",\n" +
                "                \"l2_parent_id\": 4,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 204,\n" +
                "                \"name\": \"DragonFly BSD\",\n" +
                "                \"l2_parent_id\": 4,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 401,\n" +
                "                \"name\": \"IOS\",\n" +
                "                \"l2_parent_id\": 5,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 402,\n" +
                "                \"name\": \"MacOS\",\n" +
                "                \"l2_parent_id\": 5,\n" +
                "                \"l1_parent_id\": 1\n" +
                "            }\n" +
                "        ],\n" +
                "        \"role\": [\n" +
                "            {\n" +
                "                \"id\": 3,\n" +
                "                \"name\": \"外部网络\",\n" +
                "                \"parent_id\": null,\n" +
                "                \"child\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 4,\n" +
                "                \"name\": \"终端节点\",\n" +
                "                \"parent_id\": null,\n" +
                "                \"child\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 6,\n" +
                "                \"name\": \"网关节点\",\n" +
                "                \"parent_id\": null,\n" +
                "                \"child\": [\n" +
                "                    {\n" +
                "                        \"id\": 21,\n" +
                "                        \"name\": \"路由器\",\n" +
                "                        \"parent_id\": 6,\n" +
                "                        \"child\": null\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": 22,\n" +
                "                        \"name\": \"交换机\",\n" +
                "                        \"parent_id\": 6,\n" +
                "                        \"child\": null\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": 23,\n" +
                "                        \"name\": \"防火墙\",\n" +
                "                        \"parent_id\": 6,\n" +
                "                        \"child\": null\n" +
                "                    }\n" +
                "                ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 7,\n" +
                "                \"name\": \"NPC\",\n" +
                "                \"parent_id\": null,\n" +
                "                \"child\": null\n" +
                "            }\n" +
                "        ],\n" +
                "        \"entity_role\": null,\n" +
                "        \"category\": null,\n" +
                "        \"deploy_type\": null,\n" +
                "        \"install_mode\": null,\n" +
                "        \"image_type\": [\n" +
                "            {\n" +
                "                \"id\": \"vm\",\n" +
                "                \"name\": \"虚拟机\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": \"container\",\n" +
                "                \"name\": \"容器\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": \"entity\",\n" +
                "                \"name\": \"Entity\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": \"simple\",\n" +
                "                \"name\": \"简易型\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"language\": null,\n" +
                "        \"system_architecure\": null,\n" +
                "        \"disk_format\": [\n" +
                "            {\n" +
                "                \"id\": \"qcow2\",\n" +
                "                \"name\": \"disk_format_qcow2\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": \"vmdk\",\n" +
                "                \"name\": \"disk_format_vmdk\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": \"vdi\",\n" +
                "                \"name\": \"disk_format_vdi\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": \"raw\",\n" +
                "                \"name\": \"disk_format_raw\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"access_type\": null,\n" +
                "        \"service_categories\": null,\n" +
                "        \"service_start_types\": null,\n" +
                "        \"installer_platforms\": null,\n" +
                "        \"access_protocols_ports\": null,\n" +
                "        \"switch_convert_models\": null,\n" +
                "        \"serial_port_respons\": null,\n" +
                "        \"snmp_versions\": null,\n" +
                "        \"hw_disk_bus_list\": [\n" +
                "            \"ide\",\n" +
                "            \"scsi\",\n" +
                "            \"uml\",\n" +
                "            \"usb\",\n" +
                "            \"virtio\",\n" +
                "            \"xen\"\n" +
                "        ],\n" +
                "        \"hw_vif_model_list\": [\n" +
                "            \"e1000\",\n" +
                "            \"ne2k_pci\",\n" +
                "            \"pcnet\",\n" +
                "            \"rtl8139\",\n" +
                "            \"virtio\"\n" +
                "        ],\n" +
                "        \"hw_video_model_list\": [\n" +
                "            \"cirrus\",\n" +
                "            \"qxl\",\n" +
                "            \"vga\",\n" +
                "            \"vmvga\",\n" +
                "            \"xen\"\n" +
                "        ]\n" +
                "    }";
        TypeAllVO typeAllVO = JSONObject.parseObject(arrStr, TypeAllVO.class);
        return typeAllVO;
    }


    /**
     * 查询所有分类
     * @return
     */
    @ApiOperation("查询所有分类")
    @GetMapping("/categories")
    public Result<TypeAllVO> list(){
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        if (Objects.isNull(l1AndL2AndL3Type)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO(l1AndL2AndL3Type.getFirstTypeList(),
                l1AndL2AndL3Type.getSecondTypeList(),l1AndL2AndL3Type.getThirdTypeList());
        return new Result<>(response);
    }

    /**
     * 查询所有分类
     * @return
     */
    @ApiOperation("查询所有二级分类")
    @ApiImplicitParam(name = "firstTypeId", value = "一级分类id", dataTypeClass = Integer.class)
    @GetMapping("/l2-categories")
    public Result<TypeL2AllVO> l2List(@RequestParam(value = "firstTypeId", required = false) Integer firstTypeId){
        ListResult<TargetType> l1AndL2AndL3Type = typeConverter.findL1AndL2Type();
        if (Objects.isNull(l1AndL2AndL3Type)){
            return new Result<>();
        }
        if (Objects.nonNull(firstTypeId)) {
            List<TargetType> l2AndL3TypeList = l1AndL2AndL3Type.getList();
            l2AndL3TypeList = l2AndL3TypeList.stream().filter(type -> Objects.equals(firstTypeId, type.getId())).collect(Collectors.toList());
            l1AndL2AndL3Type.setList(l2AndL3TypeList);
        }
        return new Result<>(new TypeL2AllVO(l1AndL2AndL3Type.getList()));
    }


    /**
     * 查询所有角色
     * @return
     */
    @ApiOperation("查询所有角色")
    @GetMapping("/roles")
    public Result<TypeAllVO>  roles(){
        TypeAllConvertModel model = typeConverter.findRoles();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setRole(model.getIdIntegerModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有部署类型
     * @return
     */
    @ApiOperation("查询所有部署类型")
    @GetMapping("/deploy_type")
    public Result<TypeAllVO>  deployType(){
        TypeAllConvertModel model = typeConverter.findDeployType();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setDeployType(model.getIdIntegerModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有虚拟类型
     * @return
     */
    @ApiOperation("查询所有虚拟类型")
    @GetMapping("/image-types")
    public Result<TypeAllVO> imageType(){
        TypeAllConvertModel model = typeConverter.findImageType();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setImageType(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有语言
     * @return
     */
    @ApiOperation("查询所有语言")
    @GetMapping("/language")
    public Result<TypeAllVO> language(){
        TypeAllConvertModel model = typeConverter.findLanguage();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setLanguage(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有系统架构
     * @return
     */
    @ApiOperation("查询所有系统架构")
    @GetMapping("/system-architecure")
    public Result<TypeAllVO> systemArchitecure(){
        TypeAllConvertModel model = typeConverter.findSystemArchitecure();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setSystemArchitecure(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询支持的磁盘格式
     * @return
     */
    @ApiOperation("查询支持的磁盘格式")
    @GetMapping("/disk-format")
    public Result<TypeAllVO> diskFormat(){
        TypeAllConvertModel model = typeConverter.findDiskFormat();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setDiskFormat(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有访问类型
     * @return
     */
    @ApiOperation("查询所有访问类型")
    @GetMapping("/access-type")
    public Result<TypeAllVO> accessType(){
        TypeAllConvertModel model = typeConverter.findAccessType();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setAccessType(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有服务分类
     * @return
     */
    @ApiOperation("查询所有服务管理分类")
    @GetMapping("/service-mg-categories")
    public Result<TypeAllVO> serviceMgCategories(){
        TypeAllConvertModel serviceCategories = typeConverter.findServiceCategories();
        TypeAllConvertModel deployType = typeConverter.findDeployType();
        TypeAllConvertModel installerPlatforms = typeConverter.findInstallerPlatforms();

        TypeAllVO response = new TypeAllVO();
        response.setDeployType(Objects.nonNull(deployType) ?  deployType.getIdIntegerModelList() : null);
        response.setServiceCategories(Objects.nonNull(serviceCategories) ?
                serviceCategories.getIdStringModelList() : null);
        response.setInstallerPlatforms(Objects.nonNull(installerPlatforms) ? installerPlatforms.getIdStringModelList() : null);
        return new Result<>(response);
    }

    /**
     * 查询所有实装管理分类
     * @return
     */
    @ApiOperation("查询所有实装管理分类")
    @GetMapping("/entity-mg-categories")
    public Result<TypeAllVO> entityMgCategories(){
        TypeAllConvertModel entityRoles = typeConverter.findEntityRoles();
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        TypeAllConvertModel language = typeConverter.findLanguage();
        TypeAllConvertModel systemArchitecure = typeConverter.findSystemArchitecure();
        TypeAllConvertModel snmpVersion = typeConverter.findSnmpVersion();
        TypeAllVO allResponse = new TypeAllVO();
        List<SerialPortConvertModel> serialPortConvertModels = serialPortConverter.list();
        allResponse.setSerialPortRespons(Lists.newArrayList(new SerialPortVO(CCM_1,CCM_1,serialPortConvertModels)));
        allResponse.setSwitchConvertModels(switchConverter.treeList());
        allResponse.setAccessProtocolsPorts(typeService.findAccessProtocolsPort().getList());
        allResponse.setSnmpVersions(Objects.nonNull(snmpVersion) ? snmpVersion.getIdStringModelList() : null);
        allResponse.setEntityRole(Objects.nonNull(entityRoles) ? entityRoles.getIdIntegerModelList() : null);
        allResponse.setFirstTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getFirstTypeList() : null);
        allResponse.setSecondTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getSecondTypeList() : null);
        allResponse.setThirdTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getThirdTypeList() : null);
        allResponse.setLanguage(Objects.nonNull(language) ? language.getIdStringModelList() : null);
        allResponse.setSystemArchitecure(Objects.nonNull(systemArchitecure) ?
                systemArchitecure.getIdStringModelList() : null);
        return new Result<>(allResponse);
    }

    /**
     * 查询所有服务分类
     * @return
     */
    @ApiOperation("查询所有服务分类")
    @GetMapping("/service-categories")
    public Result<TypeAllVO> serviceCategories(){
        TypeAllConvertModel model = typeConverter.findServiceCategories();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setServiceCategories(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有服务管理-启动参数类型
     * @return
     */
    @ApiOperation("查询启动参数类型")
    @GetMapping("/service-start-type")
    public Result<TypeAllVO> serviceStartType(){
        TypeAllConvertModel model = typeConverter.findServiceStartType();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setServiceStartTypes(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有平台
     * @return
     */
    @ApiOperation("查询所有平台数据")
    @GetMapping("/installer-platforms")
    public Result<TypeAllVO> installerPlatforms(){
        TypeAllConvertModel model = typeConverter.findInstallerPlatforms();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setInstallerPlatforms(model.getIdStringModelList());
        return new Result<>(response);

    }

    @ApiOperation("查询串口配置")
    @GetMapping("/serial-port-list")
    public Result<List<SerialPortVO>> serialPortList(){
        List<SerialPortConvertModel> list = serialPortConverter.list();
        return new Result<>(Lists.newArrayList(new SerialPortVO(CCM_1,CCM_1,list)));
    }

    @ApiOperation("查询交换机部分信息数据")
    @GetMapping("/switch-tree-list")
    public Result<List<SwitchConvertModel>> treeList(){
        return new Result<>(switchConverter.treeList());
    }

    @ApiOperation("查询交换机所有信息")
    @GetMapping("/switch-list")
    public Result<List<Switches>> switchList(){
        return new Result<>(switchConverter.list());
    }
}
