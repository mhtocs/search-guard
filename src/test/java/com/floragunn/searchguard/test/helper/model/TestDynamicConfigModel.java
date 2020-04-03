package com.floragunn.searchguard.test.helper.model;

import com.floragunn.searchguard.auth.AuthDomain;
import com.floragunn.searchguard.auth.AuthFailureListener;
import com.floragunn.searchguard.auth.AuthorizationBackend;
import com.floragunn.searchguard.auth.blocking.ClientBlockRegistry;
import com.floragunn.searchguard.sgconf.DynamicConfigModel;
import com.google.common.collect.Multimap;

import java.net.InetAddress;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public class TestDynamicConfigModel extends DynamicConfigModel {

    @Override
    public SortedSet<AuthDomain> getRestAuthDomains() {
        return null;
    }

    @Override
    public Set<AuthorizationBackend> getRestAuthorizers() {
        return null;
    }

    @Override
    public SortedSet<AuthDomain> getTransportAuthDomains() {
        return null;
    }

    @Override
    public Set<AuthorizationBackend> getTransportAuthorizers() {
        return null;
    }

    @Override
    public String getTransportUsernameAttribute() {
        return null;
    }

    @Override
    public boolean isAnonymousAuthenticationEnabled() {
        return false;
    }

    @Override
    public boolean isXffEnabled() {
        return false;
    }

    @Override
    public String getInternalProxies() {
        return null;
    }

    @Override
    public String getRemoteIpHeader() {
        return null;
    }

    @Override
    public boolean isRestAuthDisabled() {
        return false;
    }

    @Override
    public boolean isInterTransportAuthDisabled() {
        return false;
    }

    @Override
    public boolean isRespectRequestIndicesEnabled() {
        return false;
    }

    @Override
    public String getKibanaServerUsername() {
        return null;
    }

    @Override
    public String getKibanaIndexname() {
        return null;
    }

    @Override
    public boolean isKibanaMultitenancyEnabled() {
        return false;
    }

    @Override
    public boolean isDnfofEnabled() {
        return false;
    }

    @Override
    public boolean isMultiRolespanEnabled() {
        return false;
    }

    @Override
    public String getFilteredAliasMode() {
        return null;
    }

    @Override
    public String getHostsResolverMode() {
        return null;
    }

    @Override
    public boolean isDnfofForEmptyResultsEnabled() {
        return false;
    }

    @Override
    public List<AuthFailureListener> getIpAuthFailureListeners() {
        return null;
    }

    @Override
    public Multimap<String, AuthFailureListener> getAuthBackendFailureListeners() {
        return null;
    }

    @Override
    public List<ClientBlockRegistry<InetAddress>> getIpClientBlockRegistries() {
        return null;
    }

    @Override
    public Multimap<String, ClientBlockRegistry<String>> getAuthBackendClientBlockRegistries() {
        return null;
    }
}
