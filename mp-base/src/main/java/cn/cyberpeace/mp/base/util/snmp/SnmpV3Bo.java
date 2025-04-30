package cn.cyberpeace.mp.base.util.snmp;

import lombok.Data;
import org.snmp4j.smi.OID;

@Data
public class SnmpV3Bo {
    private Integer securityLevel;

    private String username;

    private OID authProtocol;

    private String authPassPhrase;

    private OID privacyProtocol;

    private String privacyPassPhrase;
}