package cn.cyberpeace.mp.cscene.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: guquan
 * @Date：2023/9/14 11:07
 */
public class FlowAttackTypeConvert {

    static Map<String, String> attackType2AttCks = new HashMap<>();

    static Map<String, List<String>> attCk2AttackTypes = new HashMap<>();

    static Map<String, String> formatTypes = new HashMap<>();

    // todo
    static {
        attackType2AttCks.put("Detection of a Network Scan", "reconnaissance");
        attackType2AttCks.put("SCORE! Get the lotion!", "reconnaissance");
        attackType2AttCks.put("Device Retrieving External IP Address Detected", "reconnaissance");
        attackType2AttCks.put("Possible Social Engineering Attempted", "reconnaissance");

        attackType2AttCks.put("access to a potentially vulnerable web application", "initial access");
        attackType2AttCks.put("Web Application Attack", "initial access");

        attackType2AttCks.put("Executable code was detected", "execution");
        attackType2AttCks.put("A system call was detected", "execution");
        attackType2AttCks.put("Targeted Malicious Activity was Detected", "execution");
        attackType2AttCks.put("Exploit Kit Activity Detected", "execution");

        attackType2AttCks.put("Attempted User Privilege Gain", "privilege escalation");
        attackType2AttCks.put("Unsuccessful User Privilege Gain", "privilege escalation");
        attackType2AttCks.put("Successful User Privilege Gain", "privilege escalation");
        attackType2AttCks.put("Attempted Administrator Privilege Gain", "privilege escalation");
        attackType2AttCks.put("Successful Administrator Privilege Gain", "privilege escalation");

        attackType2AttCks.put("Unknown Traffic", "defense evasion");
        attackType2AttCks.put("A suspicious string was detected", "defense evasion");
        attackType2AttCks.put("A suspicious filename was detected", "defense evasion");
        attackType2AttCks.put("A Network Trojan was detected", "defense evasion");
        attackType2AttCks.put("Misc activity", "defense evasion");
        attackType2AttCks.put("Misc Attack", "defense evasion");
        attackType2AttCks.put("Possibly Unwanted Program Detected", "defense evasion");

        attackType2AttCks.put("An attempted login using a suspicious username was detected", "credential access");
        attackType2AttCks.put("Attempt to login by a default username and password", "credential access");
        attackType2AttCks.put("Successful Credential Theft Detected", "credential access");

        attackType2AttCks.put("Decode of an RPC Query", "discovery");

        attackType2AttCks.put("Attempted Information Leak", "collection");
        attackType2AttCks.put("Information Leak", "collection");
        attackType2AttCks.put("Large Scale Information Leak", "collection");
        attackType2AttCks.put("Potential Corporate Privacy Violation", "collection");

        attackType2AttCks.put("Potentially Bad Traffic", "command and control");
        attackType2AttCks.put("A TCP connection was detected", "command and control");
        attackType2AttCks.put("A client was using an unusual port", "command and control");
        attackType2AttCks.put("Detection of a non-standard protocol or event", "command and control");
        attackType2AttCks.put("Generic Protocol Command Decode", "command and control");
        attackType2AttCks.put("Generic Icmp event", "command and control");
        attackType2AttCks.put("Domain Observed Used for C2 Detected", "command and control");

        attackType2AttCks.put("Attempted Denial of Service", "impact");
        attackType2AttCks.put("Denial of Service", "impact");
        attackType2AttCks.put("Detection of a Denial of Service Attack", "impact");
        attackType2AttCks.put("Crypto Currency Mining Activity Detected", "impact");

        formatTypes.put("reconnaissance", "Reconnaissance");
        formatTypes.put("initial access", "Initial access");
        formatTypes.put("execution", "Execution");
        formatTypes.put("privilege escalation", "Privilege Escalation");
        formatTypes.put("defense evasion", "Defense Evasion");
        formatTypes.put("credential access", "Credential Access");
        formatTypes.put("discovery", "Discovery");
        formatTypes.put("collection", "Collection");
        formatTypes.put("command and control", "Command And Control");
        formatTypes.put("impact", "Impact");

        for (var attck : attackType2AttCks.entrySet()) {
            if (!attCk2AttackTypes.containsKey(attck.getValue())) {
                attCk2AttackTypes.put(attck.getValue(), new ArrayList<>());
            }
            attCk2AttackTypes.get(attck.getValue()).add(attck.getKey());
        }
    }

    public static String getAttCk(String attackType) {
        return attackType2AttCks.getOrDefault(attackType, attackType);
    }

    public static List<String> getAttackTypes(String attCk) {
        return attCk2AttackTypes.getOrDefault(attCk.toLowerCase(), Arrays.asList(attCk));
    }

    public static String getFormatType(String type) {
        return formatTypes.getOrDefault(type.toLowerCase(), type);
    }

}
