package com.floragunn.searchguard.test.helper.model;

import com.floragunn.searchguard.sgconf.InternalUsersModel;

import java.util.*;

public class TestInternalUsersModel extends InternalUsersModel {

    private final Map<String, Props> users;

    public TestInternalUsersModel(Map<String, Props> users) {
        this.users = users;
    }

    public static class Props {
        private final List<String> searchGuardRoles;
        private final String description;
        private final Map<String, String> attributes;
        private final List<String> backendRoles;

        public Props(List<String> searchGuardRoles, String description, Map<String, String> attributes, List<String> backendRoles) {
            this.searchGuardRoles = searchGuardRoles;
            this.description = description;
            this.attributes = attributes;
            this.backendRoles = backendRoles;
        }

        public Props() {
            searchGuardRoles = Collections.emptyList();
            description = "";
            attributes = Collections.emptyMap();
            backendRoles = Collections.emptyList();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Props props = (Props) o;
            return Objects.equals(searchGuardRoles, props.searchGuardRoles) &&
                    Objects.equals(description, props.description) &&
                    Objects.equals(attributes, props.attributes) &&
                    Objects.equals(backendRoles, props.backendRoles);
        }

        @Override
        public int hashCode() {
            return Objects.hash(searchGuardRoles, description, attributes, backendRoles);
        }
    }

    @Override
    public boolean exists(String user) {
        return users.containsKey(user);
    }

    @Override
    public List<String> getBackenRoles(String user) {
        return users.getOrDefault(user, new Props()).backendRoles;
    }

    @Override
    public Map<String, String> getAttributes(String user) {
        return users.getOrDefault(user, new Props()).attributes;
    }

    @Override
    public String getDescription(String user) {
        return users.getOrDefault(user, new Props()).description;
    }

    @Override
    public String getHash(String user) {
        return String.valueOf(users.getOrDefault(user, new Props()).hashCode());
    }

    @Override
    public List<String> getSearchGuardRoles(String user) {
        return users.getOrDefault(user, new Props()).searchGuardRoles;
    }
}
