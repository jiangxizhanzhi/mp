package cn.cyberpeace.mp.sceneplugincloud.plugin.common;

import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.util.CommandUtils;
import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.cyberpeace.mp.scene.util.CommandUtils.WINDOWS_LINE_BREAK;

/**
 * @Description
 * @Author maojun
 * @Date 2022/4/6
 */
public class CloudInitConfigCommon {
    private static final String SSH_PWAUTH = "ssh_pwauth: true";
    private static final String DISABLE_ROOT = "disable_root: 0";

    private static final Set<String> CHANGE_USER_PROTOCOL = new HashSet<>();

    static {
        CHANGE_USER_PROTOCOL.add(AccessMode.Protocol.CONSOLE);
        CHANGE_USER_PROTOCOL.add(AccessMode.Protocol.RDP);
        CHANGE_USER_PROTOCOL.add(AccessMode.Protocol.VNC);
        CHANGE_USER_PROTOCOL.add(AccessMode.Protocol.SSH);
        CHANGE_USER_PROTOCOL.add(AccessMode.Protocol.TELNET);
    }

    public static boolean canChangeUser(String protocol) {
        return CHANGE_USER_PROTOCOL.contains(protocol);
    }

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

        public Builder(String standardSystemType) {
            this.standardSystemType = standardSystemType;
            this.users = new ArrayList<>();
            this.initCommands = new ArrayList<>();
        }

        public List<String> build() {
            if (SystemTypeCommandUtils.isWindows(standardSystemType)) {
                return this.buildForWindows();
            } else {
                return this.buildForLinux();
            }
        }

        public void addUser(String username, String password) {
            this.users.add(new User(username, password));
        }

        public void addAllInitCommands(List<String> initCommands) {
            this.initCommands.addAll(initCommands);
        }

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
            if (!this.initCommands.isEmpty()) {
                list.addAll(initCommands);
            }
            return list;
        }

        private List<String> buildForLinux() {
            List<String> list = new ArrayList<>();
            if (rootPassword != null) {
                list.addAll(this.getChpasswd(rootPassword));
            }
            list.add(SSH_PWAUTH);
            list.add(DISABLE_ROOT);
            if (!this.users.isEmpty()) {
                if (CollectionUtils.isNotEmpty(this.getGroups())) {
                    list.add("groups: ");
                    list.addAll(this.getGroups());
                }
                if (CollectionUtils.isNotEmpty(this.getUsers())) {
                    list.add("users: ");
                    list.addAll(this.getUsers());
                }
            }
            if (!this.initCommands.isEmpty()) {
                list.add("runcmd:");
                List<String> splitCmds = new ArrayList<>();
                initCommands.forEach(cmd -> {
                    String[] splitResult = StringUtils.split(cmd, CommandUtils.LINUX_LINE_BREAK);
                    if (splitResult != null && splitResult.length > 0) {
                        for (String s : splitResult) {
                            splitCmds.add(s);
                        }
                    }
                });
                List<String> formatCommands = splitCmds.stream()
                        .filter(s -> StringUtils.isNotEmpty(s))
                        .map(s -> "  - " + s)
                        .collect(Collectors.toList());
                list.addAll(formatCommands);
            }
            return list;
        }

        private List<String> getChpasswd(String rootPassword) {
            return Arrays.asList(
                    "chpasswd:",
                    "  " + "list: |",
                    "  " + "  " + "root:" + rootPassword,
                    "  " + "expire: false"
            );
        }

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

        private String getGroup(String username) {
            return "  " + "- " + username;
        }

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
    }

    @AllArgsConstructor
    private static class User {
        private String username;
        private String password;
    }
}
