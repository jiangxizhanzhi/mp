package cn.cyberpeace.mp.situationaware.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

public class WazuhMonitorRoot<T> {

    private Agent agent;
    private Manager manager;
    private Rule rule;
    private Decoder decoder;
    @JSONField(name = "full_log")
    private String fullLog;
    private Input input;
    private Date timestamp;
    private String location;
    private String id;
    private T data;

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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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

    public static class Rule{
        private int firedtimes;
        private boolean mail;
        private int level;
        private String description;
        private List<String> groups;
        private String id;

        public int getFiredtimes() {
            return firedtimes;
        }

        public void setFiredtimes(int firedtimes) {
            this.firedtimes = firedtimes;
        }

        public boolean isMail() {
            return mail;
        }

        public void setMail(boolean mail) {
            this.mail = mail;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getGroups() {
            return groups;
        }

        public void setGroups(List<String> groups) {
            this.groups = groups;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class Manager{
        private String name;

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return this.name;
        }
    }

    public static class Agent{
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
