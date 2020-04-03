package com.floragunn.searchguard.test.helper.model;

import com.floragunn.searchguard.sgconf.ConfigModel;
import com.floragunn.searchguard.sgconf.SgRoles;
import com.floragunn.searchguard.user.User;
import org.elasticsearch.common.transport.TransportAddress;

import java.util.Map;
import java.util.Set;

public class TestConfigModel extends ConfigModel {

    @Override
    public Map<String, Boolean> mapTenants(User user, Set<String> roles) {
        return null;
    }

    @Override
    public Set<String> mapSgRoles(User user, TransportAddress caller) {
        return null;
    }

    @Override
    public SgRoles getSgRoles() {
        return null;
    }

    @Override
    public Set<String> getAllConfiguredTenantNames() {
        return null;
    }
}
