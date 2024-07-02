package cn.cyberpeace.mp.sceneplugincloud.plugin.common;

import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.util.CommandUtils;
import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static cn.cyberpeace.mp.scene.util.CommandUtils.WINDOWS_LINE_BREAK;

/**
 * @ClassName CloudInitConfigCommon
 * @Description
 * @Author maojun
 * @Date 2022/4/6
 * @Version 1.0.0
 */
public class CloudInitConfigCommon {
    public static final String WINDOWS_HOSTS_PATH = "C:\\Windows\\System32\\drivers\\etc\\hosts";
    public static final String LINUX_HOSTS_PATH = "/etc/hosts";

    private static final Set<String> CHANGE_USER_PROTOCOL = new HashSet<>();

    static {
        CHANGE_USER_PROTOCOL.add(AccessMode.Protocol.CONSOLE);
        CHANGE_USER_PROTOCOL.add(AccessMode.Protocol.RDP);
        CHANGE_USER_PROTOCOL.add(AccessMode.Protocol.VNC);
        CHANGE_USER_PROTOCOL.add(AccessMode.Protocol.SSH);
        CHANGE_USER_PROTOCOL.add(AccessMode.Protocol.TELNET);
    }

    /**
     * canChangeUser
     *
     * @param protocol
     * @return boolean
     */
    public static boolean canChangeUser(String protocol) {
        return CHANGE_USER_PROTOCOL.contains(protocol);
    }

    /**
     * builder
     *
     * @param standardSystemType
     * @return
     */
    public static Builder builder(String standardSystemType) {
        return new Builder(standardSystemType);
    }

    @Setter
    @Getter
    public static class Builder {
        private String standardSystemType;
        private String rootPassword;
        private List<User> users;
        private List<String> initCommands;
        private List<String> dnsList;

        /**
         * Builder
         *
         * @param standardSystemType
         */
        public Builder(String standardSystemType) {
            this.standardSystemType = standardSystemType;
            this.users = new ArrayList<>();
            this.initCommands = new ArrayList<>();
            this.dnsList = new ArrayList<>();
        }

        /**
         * build
         *
         * @return
         */
        public List<String> build() {
            if (SystemTypeCommandUtils.isWindows(standardSystemType)) {
                return this.buildForWindows();
            } else {
                return this.buildForLinux();
            }
        }

        /**
         * addAllDns
         *
         * @param dnsList
         */
        public void addAllDns(List<String> dnsList) {
            this.dnsList.addAll(dnsList);
        }

        /**
         * addUser
         *
         * @param username
         * @param password
         */
        public void addUser(String username, String password) {
            this.users.add(new User(username, password));
        }

        /**
         * addAllInitCommands
         *
         * @param initCommands
         */
        public void addAllInitCommands(List<String> initCommands) {
            this.initCommands.addAll(initCommands);
        }

        /**
         * buildForWindows
         *
         * @return
         */
        private List<String> buildForWindows() {
            if (users == null || users.isEmpty()) {
                return initCommands;
            }
            List<String> list = new ArrayList<>();
            String userData = null;
            for (User user : users) {
                if (userData == null) {
                    userData = StringUtils.joinWith(WINDOWS_LINE_BREAK,
                            String.format("net user %s /add /y", user.username),
                            String.format("net localgroup Administrators \"%s\" /add /y", user.username),
                            String.format("net localgroup \"Remote Desktop Users\" \"%s\" /add /y", user.username),
                            String.format("net user %s \"%s\" /y", user.username, user.password));
                } else {
                    userData = StringUtils.joinWith(WINDOWS_LINE_BREAK,
                            userData,
                            String.format("net user %s /add /y", user.username),
                            String.format("net localgroup Administrators \"%s\" /add /y", user.username),
                            String.format("net localgroup \"Remote Desktop Users\" \"%s\" /add /y", user.username),
                            String.format("net user %s \"%s\" /y", user.username, user.password));
                }
            }
            list.add(userData);
            if (!this.dnsList.isEmpty()) {
                for (String d : this.dnsList) {
                    list.add(String.format("echo %s >> %s", d, WINDOWS_HOSTS_PATH));
                }
            }
            if (!this.initCommands.isEmpty()) {
                list.addAll(initCommands);
            }
            return list;
        }

        /**
         * buildForLinux
         *
         * @return
         */
        private List<String> buildForLinux() {
            List<String> list = new ArrayList<>();
            if (!this.initCommands.isEmpty()) {
                List<String> splitCmds = new ArrayList<>();
                initCommands.forEach(cmd -> {
                    String[] splitResult = StringUtils.split(cmd, CommandUtils.LINUX_LINE_BREAK);
                    if (splitResult != null && splitResult.length > 0) {
                        for (String s : splitResult) {
                            splitCmds.add(s);
                        }
                    }
                });
                list.addAll(splitCmds);
            }

            return list;
        }

        /**
         * getChpasswd
         *
         * @param rootPassword
         * @return
         */
        private List<String> getChpasswd(String rootPassword) {
            return Arrays.asList(
                    "chpasswd:",
                    "  " + "list: |",
                    "  " + "  " + "root:" + rootPassword,
                    "  " + "expire: false"
            );
        }

        /**
         * getGroups
         *
         * @return
         */
        private List<String> getGroups() {
            List<String> list = new ArrayList<>();
            for (User user : users) {
                if (user.username.equals("root")) {
                    continue;
                }
                list.add(this.getGroup(user.username));
            }
            return list;
        }

        /**
         * getUsers
         *
         * @return
         */
        private List<String> getUsers() {
            List<String> list = new ArrayList<>();
            for (User user : users) {
                if (user.username.equals("root")) {
                    continue;
                }
                list.addAll(this.getUser(user));
            }
            return list;
        }

        /**
         * getGroup
         *
         * @param username
         * @return
         */
        private String getGroup(String username) {
            return "  " + "- " + username;
        }

        /**
         * getUser
         *
         * @param user
         * @return
         */
        private List<String> getUser(User user) {
            return Arrays.asList(
                    "  " + "- name: " + user.username,
                    "  " + "  " + "primary-group: " + user.username,
                    "  " + "  " + "groups: users",
                    "  " + "  " + "shell: /bin/bash",
                    "  " + "  " + "lock_passwd: false",
                    "  " + "  " + "sudo: ALL=(ALL) NOPASSWD:ALL",
                    "  " + "  " + "plain_text_passwd: '" + user.password + "'"
            );
        }

        /**
         * getDnsList
         *
         * @param path
         * @param dnsList
         * @return
         */
        private List<String> getDnsList(String path, List<String> dnsList) {
            List<String> result = new ArrayList<>();
            result.addAll(Arrays.asList(
                    "write_files:",
                    "  " + "- path: " + path,
                    "  " + "  permissions: \"0644\"",
                    "  " + "  content: |"
            ));
            for (String dns : dnsList) {
                result.add("  " + "    " + dns);
            }
            result.add("  " + "  append: True");
            return result;
        }
    }

    @AllArgsConstructor
    private static class User {
        private String username;
        private String password;
    }
}
