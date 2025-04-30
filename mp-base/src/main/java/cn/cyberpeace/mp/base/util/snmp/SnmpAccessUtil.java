package cn.cyberpeace.mp.base.util.snmp;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
public class SnmpAccessUtil {

    @Getter
    private String address;

    private Integer version;

    private String community;

    private SnmpV3Bo snmpV3Bo;

    private Snmp snmp;

    public static Integer timeout = 1000 * 10;

    public static final String GET = "GET";
    public static final String GET_NEXT = "GETNEXT";
    public static final String GET_BULK = "GETBULK";
    public static final String SET = "SET";
    public static final String WALK = "WALK";

    public static final Map<String, Integer> VERSION_MAP = new HashMap<>() {{
        put("v1", SnmpConstants.version1);
        put("v2c", SnmpConstants.version2c);
        put("v3", SnmpConstants.version3);
    }};

    public static final Map<Integer, Integer> SEC_LEVEL_MAP = new HashMap<>() {{
        put(1, SecurityLevel.NOAUTH_NOPRIV);
        put(2, SecurityLevel.AUTH_NOPRIV);
        put(3, SecurityLevel.AUTH_PRIV);
    }};

    public static final Map<String, OID> AUTH_PROTOCOL_MAP = new HashMap<>() {{
        put("MD5", AuthMD5.ID);
        put("SHA", AuthSHA.ID);

    }};

    public static final Map<String, OID> PRIVACY_PROTOCOL_MAP = new HashMap<>() {{
        put("DES", PrivDES.ID);
        put("AES", PrivAES128.ID);
        put("AES128", PrivAES128.ID);
    }};

    public SnmpAccessUtil(String host, Integer port, Integer version, String community) {
        this.init(host, port, version, community, null);
    }

    public SnmpAccessUtil(String host, Integer port, Integer version, SnmpV3Bo snmpV3Bo) {
        this.init(host, port, version, null, snmpV3Bo);
    }

    public static SnmpAccessUtil initSnmpAccessUtilFromTaskRequest(SnmpAccessInfo snmpAccessInfo) {

        SnmpAccessUtil snmpAccessUtil = null;
        try {
            if (VERSION_MAP.get(snmpAccessInfo.getVersion()).equals(SnmpConstants.version3)) {
                SnmpV3Bo snmpV3Bo = new SnmpV3Bo();
                snmpV3Bo.setUsername(snmpAccessInfo.getUsername());
                snmpV3Bo.setSecurityLevel(SEC_LEVEL_MAP.get(snmpAccessInfo.getSecLevel()));
                snmpV3Bo.setAuthProtocol(AUTH_PROTOCOL_MAP.get(snmpAccessInfo.getAuthProtocol().toUpperCase()));
                snmpV3Bo.setAuthPassPhrase(snmpAccessInfo.getAuthPassPhrase());
                snmpV3Bo.setPrivacyProtocol(PRIVACY_PROTOCOL_MAP.get(snmpAccessInfo.getPrivacyProtocol().toUpperCase()));
                snmpV3Bo.setPrivacyPassPhrase(snmpAccessInfo.getPrivacyPassPhrase());

                snmpAccessUtil = new SnmpAccessUtil(
                        snmpAccessInfo.getHostIp(),
                        snmpAccessInfo.getPort() != null ? Integer.parseInt(snmpAccessInfo.getPort()) : 161,
                        VERSION_MAP.get(snmpAccessInfo.getVersion()),
                        snmpV3Bo
                );
            } else {
                snmpAccessUtil = new SnmpAccessUtil(
                        snmpAccessInfo.getHostIp(),
                        snmpAccessInfo.getPort() != null ? Integer.parseInt(snmpAccessInfo.getPort()) : 161,
                        VERSION_MAP.get(snmpAccessInfo.getVersion()),
                        snmpAccessInfo.getCommunity()
                );
            }
            log.info(String.format("Snmp %s 连接尝试...", snmpAccessUtil.getAddress()));
        } catch (Exception e) {
            if (snmpAccessUtil != null) {
                log.error(String.format("Snmp %s 连接失败！捕捉异常信息 %s", snmpAccessUtil.getAddress(), e.getMessage()));
            } else {
                log.error(String.format("Snmp管理端创建失败！捕捉异常信息 %s", e.getMessage()));
            }
        }
        if (Objects.isNull(snmpAccessUtil)) {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("Snmp客户端创建失败！"));
        }
        return snmpAccessUtil;
    }

    /**
     * 设置SNMP连接属性
     * @param host 主机名/IP
     * @param port 端口
     * @param version 常量, SnmpConstants.version1/version2c/version3
     * @param community 共同体名称，默认"public"
     * @param snmpV3Bo version3使用，认证相关
     */
    @SneakyThrows
    private void init(String host, Integer port, Integer version, String community, SnmpV3Bo snmpV3Bo) {
        setAddress(host, port);
        this.version = version;
        this.community = StringUtils.isEmpty(community) ? "public" : community;

        DefaultUdpTransportMapping defaultUdpTransportMapping = new DefaultUdpTransportMapping();
        this.snmp = new Snmp(defaultUdpTransportMapping);
        if (this.version == SnmpConstants.version3) {
            this.snmpV3Bo = snmpV3Bo;
            USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
            SecurityModels.getInstance().addSecurityModel(usm);
        }
        this.snmp.listen();
    }

    /**
     * snmpd地址, 格式："protocol:ip address/port", 例 -> udp:10.100.1.16/161
     */
    private void setAddress(String host, Integer port) {
        this.address = String.format("udp:%s/%s", host, port);
    }

    public void checkConn(String address) {
        if (exec(GET, ".1.3.6.1.2.1.1.1.0").isEmpty()) {
            close();
            throw new MpRuntimeException("测试获取主机信息失败.");
        }else {
            log.info(String.format("Snmp %s 连接成功...", address));
        }
    }

    @SneakyThrows
    public void close() {
        this.snmp.close();
    }

    /**
     * 向网络节点snmpd发送请求
     * @param type 请求类型
     * @param oidList objectIds
     * @param values {@link #SET}使用，长度、索引与oidList一致
     * @return List<JSONObject>
     */
    public List<JSONObject> exec(String type, List<String> oidList, List<String> values) {
        List<JSONObject> result = Lists.newArrayList();
        if (type.equals(GET)) {
            result = this.snmpGet(oidList);
        } else if (type.equals(GET_NEXT)) {
            result = this.snmpGetNext(oidList);
        } else if (type.equals(GET_BULK)) {
            result = this.snmpGetBulk(oidList.get(0));
        } else if (type.equals(SET)) {
            result = this.snmpSet(oidList, values);
        } else if (type.equals(WALK)) {
            result = this.snmpWalk(oidList.get(0));
        }

        return result;
    }

    public List<JSONObject> exec(String type, String oid, String value) {
        return exec(type, Collections.singletonList(oid), Collections.singletonList(value));
    }

    public List<JSONObject> exec(String type, String oid) {
        return exec(type, Collections.singletonList(oid));
    }

    public List<JSONObject> exec(String type, List<String> oidList) {
        return exec(type, oidList, null);
    }

    private void isNullAndThrow(ResponseEvent responseEvent) {
        if (isNull(responseEvent)) {
            log.error("Response is empty.");
            throw new MpRuntimeException("Response is empty.");
        }

        if (responseEvent.getResponse().getErrorStatus() != PDU.noError) {
            log.error(String.format("Error: %s", responseEvent.getResponse().getErrorStatusText()));
            throw new MpRuntimeException(String.format("Error: %s", responseEvent.getResponse().getErrorStatusText()));
        }
    }
    private boolean isNull(ResponseEvent responseEvent) {
        return responseEvent == null || responseEvent.getResponse() == null;
    }

    private List<JSONObject> collectionResult(PDU response) {
        List<JSONObject> result = Lists.newArrayList();
        if (response == null) {
            return result;
        }

        response.getVariableBindings().forEach(variableBinding -> {
            JSONObject item = new JSONObject();
            item.put("OID", variableBinding.getOid().toDottedString());
            item.put("value", variableBinding.getVariable().toString());
            result.add(item);
        });
        return result;
    }

    private <T extends Target> T createTarget() {
        Target target;
        if (this.version == SnmpConstants.version3) {
            target = new UserTarget();
            if (this.snmpV3Bo != null) {
                target.setSecurityLevel(snmpV3Bo.getSecurityLevel());
                target.setSecurityName(new OctetString(snmpV3Bo.getUsername()));

                UsmUser usmUser = new UsmUser(
                        new OctetString(snmpV3Bo.getUsername()),
                        snmpV3Bo.getAuthProtocol(),
                        snmpV3Bo.getAuthPassPhrase() != null ? new OctetString(snmpV3Bo.getAuthPassPhrase()): null,
                        snmpV3Bo.getPrivacyProtocol(),
                        snmpV3Bo.getPrivacyPassPhrase() != null ? new OctetString(snmpV3Bo.getPrivacyPassPhrase()) : null
                );
                this.snmp.getUSM().addUser(usmUser.getSecurityName(), usmUser);
            }

        } else {
            target = new CommunityTarget();
            ((CommunityTarget) target).setCommunity(new OctetString(this.community));
        }

        target.setAddress(GenericAddress.parse(this.address));
        target.setTimeout(timeout);
        target.setVersion(this.version);
        target.setRetries(1);

        return (T) target;
    }

    private PDU createPDU(List<String> oidList, List<String> values) {
        PDU pdu;

        if (this.version == SnmpConstants.version1) {
            pdu = new PDUv1();
        } else if (this.version == SnmpConstants.version2c) {
            pdu = new PDU();
        } else if (this.version == SnmpConstants.version3) {
            pdu = new ScopedPDU();
        } else {
            throw new MpRuntimeException("Invalid version.");
        }

        pdu.setErrorIndex(2);
        for (int i = 0; i < oidList.size(); i++) {
            String oid = oidList.get(i);
            if (values != null && !values.isEmpty()) {
                String v = values.get(i);
                Variable variable = snmpGetVariable(oid);
                try {
                    if (variable instanceof UnsignedInteger32) {
                        pdu.add(new VariableBinding(
                                new OID(oid),
                                BeanUtils.instantiateClass(variable.getClass().getConstructor(long.class), Long.parseLong(v))
                        ));
                    } else if (variable instanceof Integer32) {
                        pdu.add(new VariableBinding(
                                new OID(oid),
                                BeanUtils.instantiateClass(variable.getClass().getConstructor(int.class), Integer.parseInt(v))
                        ));
                    } else {
                        pdu.add(new VariableBinding(new OID(oid), new OctetString(v)));
                    }
                } catch (NoSuchMethodException e) {
                    throw new MpRuntimeException("无效Set参数。");
                }
            } else {
                pdu.add(new VariableBinding(new OID(oid)));
            }
        }

        return pdu;
    }

    private PDU createPDU(List<String> oidList) {
        return createPDU(oidList, null);
    }

    @SneakyThrows
    private Variable snmpGetVariable(String oid) {
        Target target = createTarget();
        PDU pdu = createPDU(Collections.singletonList(oid));
        ResponseEvent responseEvent = this.snmp.get(pdu, target);
        isNullAndThrow(responseEvent);

        return responseEvent.getResponse().getVariableBindings().get(0).getVariable();
    }

    private List<JSONObject> snmpGet(List<String> oidList) {
        try {
            Target target = createTarget();
            PDU pdu = createPDU(oidList);

            ResponseEvent responseEvent = this.snmp.get(pdu, target);
            isNullAndThrow(responseEvent);

            return collectionResult(responseEvent.getResponse());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("snmp请求失败"));
        }
    }

    @SneakyThrows
    private List<JSONObject> snmpGetNext(List<String> oidList) {
        Target target = createTarget();
        PDU pdu = createPDU(oidList);

        ResponseEvent responseEvent = this.snmp.getNext(pdu, target);
        isNullAndThrow(responseEvent);

        return collectionResult(responseEvent.getResponse());
    }

    @SneakyThrows
    private List<JSONObject> snmpSet(List<String> oidList, List<String> values) {
        Target target = createTarget();
        PDU pdu = createPDU(oidList, values);

        ResponseEvent responseEvent = this.snmp.set(pdu, target);
        isNullAndThrow(responseEvent);

        return collectionResult(responseEvent.getResponse());
    }

    @SneakyThrows
    private List<JSONObject> snmpGetBulk(String oid) {
        Target target = createTarget();

        PDU pdu = createPDU(Collections.singletonList(oid));

        if (target.getVersion() != SnmpConstants.version1) {
            pdu.setMaxRepetitions(Integer.MAX_VALUE);
            pdu.setNonRepeaters(0);
        }
        ResponseEvent responseEvent = this.snmp.getBulk(pdu, target);
        isNullAndThrow(responseEvent);

        return collectionResult(responseEvent.getResponse());
    }

    @SneakyThrows
    public List<JSONObject> snmpWalk(String oid) {

        List<JSONObject> result = Lists.newArrayList();

        Target target = this.createTarget();
        PDU pdu = createPDU(Collections.singletonList(oid));
        String indexOid = pdu.get(0).getOid().toDottedString();
        boolean isNextMatched = true;

        while (isNextMatched) {
            ResponseEvent responseEvent = this.snmp.getNext(pdu, target);
            if (this.isNull(responseEvent)) {
                break;
            }

            PDU itemResponse = responseEvent.getResponse();

            String nextOid = null;
            Vector<? extends VariableBinding> vbs = itemResponse.getVariableBindings();
            if (vbs.isEmpty()) {
                break;
            }
            for (VariableBinding vb : vbs) {
                JSONObject item = new JSONObject();
                nextOid = vb.getOid().toDottedString();
                if (!nextOid.startsWith(indexOid)) {
                    isNextMatched = false;
                    break;
                }

                item.put("OID", nextOid);
                item.put("value", vb.getVariable().toString());
                result.add(item);
            }

            if (!isNextMatched) {
                break;
            }
            pdu.clear();
            pdu.add(new VariableBinding(new OID(nextOid)));
        }

        return result;
    }
}

