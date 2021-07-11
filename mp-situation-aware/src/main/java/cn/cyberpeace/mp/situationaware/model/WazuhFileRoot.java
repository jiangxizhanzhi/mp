package cn.cyberpeace.mp.situationaware.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class WazuhFileRoot {
    private Syscheck syscheck;
    private Agent agent;
    private Manager manager;
    private Rule rule;
    private Decoder decoder;

    @JSONField(name = "full_log")
    private String fullLog;
    private Input input;

    private String location;
    private String id;
    private Date timestamp;

    private CommonData data;

    public Syscheck getSyscheck() {
        return syscheck;
    }

    public void setSyscheck(Syscheck syscheck) {
        this.syscheck = syscheck;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public Decoder getDecoder() {
        return decoder;
    }

    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    public String getFullLog() {
        return fullLog;
    }

    public void setFullLog(String fullLog) {
        this.fullLog = fullLog;
    }

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public static class Input {

        private String type;

        public void setType(String type) {
            this.type = type;
        }
        public String getType() {
            return this.type;
        }
    }

    public static class Decoder {

        private String name;

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return this.name;
        }
    }


    public static class Rule {
        private boolean mail;
        private int level;
        @JSONField(name = "pci_dss")
        private List<String> pciDss;
        private List<String> hipaa;
        private List<String> tsc;
        private String description;
        private List<String> groups;
        @JSONField(name = "nist_800_53")
        private List<String> gdpr;
        private int firedtimes;

        private String id;
        private List<String> gpg13;

        public void setMail(boolean mail) {
            this.mail = mail;
        }
        public boolean getMail() {
            return this.mail;
        }
        public void setLevel(int level) {
            this.level = level;
        }
        public int getLevel() {
            return this.level;
        }
        public void setPciDss(List<String> pciDss) {
            this.pciDss = pciDss;
        }
        public List<String> getPciDss() {
            return this.pciDss;
        }
        public void setHipaa(List<String> hipaa) {
            this.hipaa = hipaa;
        }
        public List<String> getHipaa() {
            return this.hipaa;
        }
        public void setTsc(List<String> tsc) {
            this.tsc = tsc;
        }
        public List<String> getTsc() {
            return this.tsc;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public String getDescription() {
            return this.description;
        }
        public void setGroups(List<String> groups) {
            this.groups = groups;
        }
        public List<String> getGroups() {
            return this.groups;
        }

        public void setGdpr(List<String> gdpr) {
            this.gdpr = gdpr;
        }

        public List<String> getGdpr() {
            return this.gdpr;
        }
        public void setFiredtimes(int firedtimes) {
            this.firedtimes = firedtimes;
        }
        public int getFiredtimes() {
            return this.firedtimes;
        }

        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return this.id;
        }
        public void setGpg13(List<String> gpg13) {
            this.gpg13 = gpg13;
        }
        public List<String> getGpg13() {
            return this.gpg13;
        }
    }

    public static class Manager {

        private String name;

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return this.name;
        }
    }


    public class Agent {

        private String ip;
        private String name;
        private String id;

        public void setIp(String ip) {
            this.ip = ip;
        }
        public String getIp() {
            return this.ip;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return this.name;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return this.id;
        }
    }

    public class Syscheck {

        @JSONField(name = "size_before")
        private String sizeBefore;
        @JSONField(name = "uname_after")
        private String unameAfter;
        @JSONField(name = "mtime_after")
        private Date mtimeAfter;
        @JSONField(name = "inode_before")
        private long inodeBefore;
        @JSONField(name = "size_after")
        private String sizeAfter;
        @JSONField(name = "gid_after")
        private String gidAfter;
        @JSONField(name = "md5_before")
        private String md5Before;
        @JSONField(name = "sha256_before")
        private String sha256Before;
        @JSONField(name = "mtime_before")
        private Date mtimeBefore;
        private String mode;
        private String path;
        @JSONField(name = "sha1_after")
        private String sha1After;
        @JSONField(name = "changed_attributes")
        private List<String> changedAttributes;
        @JSONField(name = "gname_after")
        private String gnameAfter;
        @JSONField(name = "uid_after")
        private String uidAfter;
        @JSONField(name = "perm_after")
        private String permAfter;
        private String event;
        @JSONField(name = "md5_after")
        private String md5After;
        @JSONField(name = "sha1_before")
        private String sha1Before;
        @JSONField(name = "sha256_after")
        private String sha256After;
        @JSONField(name = "inode_after")
        private long inodeAfter;

        public void setSizeBefore(String sizeBefore) {
            this.sizeBefore = sizeBefore;
        }
        public String getSizeBefore() {
            return this.sizeBefore;
        }
        public void setUnameAfter(String unameAfter) {
            this.unameAfter = unameAfter;
        }
        public String getUnameAfter() {
            return this.unameAfter;
        }
        public void setMtimeAfter(Date mtimeAfter) {
            this.mtimeAfter = mtimeAfter;
        }
        public Date getMtimeAfter() {
            return this.mtimeAfter;
        }
        public void setInodeBefore(long inodeBefore) {
            this.inodeBefore = inodeBefore;
        }
        public long getInodeBefore() {
            return this.inodeBefore;
        }
        public void setSizeAfter(String sizeAfter) {
            this.sizeAfter = sizeAfter;
        }
        public String getSizeAfter() {
            return this.sizeAfter;
        }
        public void setGidAfter(String gidAfter) {
            this.gidAfter = gidAfter;
        }
        public String getGidAfter() {
            return this.gidAfter;
        }
        public void setMd5Before(String md5Before) {
            this.md5Before = md5Before;
        }
        public String getMd5Before() {
            return this.md5Before;
        }
        public void setSha256Before(String sha256Before) {
            this.sha256Before = sha256Before;
        }
        public String getSha256Before() {
            return this.sha256Before;
        }
        public void setMtimeBefore(Date mtimeBefore) {
            this.mtimeBefore = mtimeBefore;
        }
        public Date getMtimeBefore() {
            return this.mtimeBefore;
        }
        public void setMode(String mode) {
            this.mode = mode;
        }
        public String getMode() {
            return this.mode;
        }
        public void setPath(String path) {
            this.path = path;
        }
        public String getPath() {
            return this.path;
        }
        public void setSha1After(String sha1After) {
            this.sha1After = sha1After;
        }
        public String getSha1After() {
            return this.sha1After;
        }
        public void setChangedAttributes(List<String> changedAttributes) {
            this.changedAttributes = changedAttributes;
        }
        public List<String> getChangedAttributes() {
            return this.changedAttributes;
        }
        public void setGnameAfter(String gnameAfter) {
            this.gnameAfter = gnameAfter;
        }
        public String getGnameAfter() {
            return this.gnameAfter;
        }
        public void setUidAfter(String uidAfter) {
            this.uidAfter = uidAfter;
        }
        public String getUidAfter() {
            return this.uidAfter;
        }
        public void setPermAfter(String permAfter) {
            this.permAfter = permAfter;
        }
        public String getPermAfter() {
            return this.permAfter;
        }
        public void setEvent(String event) {
            this.event = event;
        }
        public String getEvent() {
            return this.event;
        }
        public void setMd5After(String md5After) {
            this.md5After = md5After;
        }
        public String getMd5After() {
            return this.md5After;
        }
        public void setSha1Before(String sha1Before) {
            this.sha1Before = sha1Before;
        }
        public String getSha1Before() {
            return this.sha1Before;
        }
        public void setSha256After(String sha256After) {
            this.sha256After = sha256After;
        }
        public String getSha256After() {
            return this.sha256After;
        }
        public void setInodeAfter(long inodeAfter) {
            this.inodeAfter = inodeAfter;
        }
        public long getInodeAfter() {
            return this.inodeAfter;
        }
    }

    @Data
    public static class CommonData {
        private Win win;

        private Sca sca;

        private String sicLaunchId;

        @Data
        public static class Win {
            private Eventdata eventdata;
            private System system;

            @Data
            public static class Eventdata {
                private String subjectLogonId;
                private String subjectDomainName;
                private String targetLinkedLogonId;
                private String impersonationLevel;
                private String authenticationPackageName;
                private String targetLogonId;
                private String logonProcessName;
                private String logonGuid;
                private String targetUserName;
                private String keyLength;
                private String elevatedToken;
                private String subjectUserSid;
                private String processId;
                private String processName;
                private String targetDomainName;
                private String targetUserSid;
                private String virtualAccount;
                private String logonType;
                private String subjectUserName;
            }

            @Data
            public static class System {
                private String eventID;
                private String keywords;
                private String providerGuid;
                private String level;
                private String channel;
                private String opcode;
                private String message;
                private String version;
                private String systemTime;
                private String eventRecordID;
                private String threadID;
                private String computer;
                private String task;
                private String processID;
                private String severityValue;
                private String providerName;
            }
        }
    }

    @Data
    public static class Sca {
        private String score;
        @JSONField(name = "total_checks")
        private String totalChecks;
        private String file;
        @JSONField(name = "policy_id")
        private String policyId;
        private String invalid;
        private String description;
        @JSONField(name = "scan_id")
        private String scanId;
        private String passed;
        private String failed;
        private String type;
        private String policy;
        private Check check;

        @Data
        public static class Check {
            private String remediation;
            private List<String> registry;
            private String reason;
            private Compliance compliance;
            private String description;
            private String id;
            private String title;
            private String rationale;
            private String status;

            @Data
            public static class Compliance {
                @JSONField(name = "cis_csc")
                private String cisCsc;
            }
        }
    }
}
