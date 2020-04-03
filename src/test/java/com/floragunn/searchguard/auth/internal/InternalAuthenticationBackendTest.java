package com.floragunn.searchguard.auth.internal;

import com.floragunn.searchguard.support.ConfigConstants;
import com.floragunn.searchguard.test.helper.model.TestConfigModel;
import com.floragunn.searchguard.test.helper.model.TestDynamicConfigModel;
import com.floragunn.searchguard.test.helper.model.TestInternalUsersModel;
import com.floragunn.searchguard.user.User;
import org.elasticsearch.common.settings.Settings;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.settings.Settings.EMPTY;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;

public class InternalAuthenticationBackendTest {

    @Test
    public void user_should_exist() {
        TestConfigModel configModel = new TestConfigModel();
        HashMap<String, TestInternalUsersModel.Props> users = new HashMap<>();
        users.put("hans peter", new TestInternalUsersModel.Props());

        TestInternalUsersModel internalUsersModel = new TestInternalUsersModel(users);
        TestDynamicConfigModel dynamicConfigModel = new TestDynamicConfigModel();
        InternalAuthenticationBackend authc = new InternalAuthenticationBackend(EMPTY);

        authc.onChanged(configModel, dynamicConfigModel, internalUsersModel);

        User user = new User("hans peter");
        assertThat("User should exist", authc.exists(user), is(true));
    }

    @Test
    public void user_should_not_exist() {
        TestConfigModel configModel = new TestConfigModel();
        HashMap<String, TestInternalUsersModel.Props> users = new HashMap<>();
        users.put("michael meier", new TestInternalUsersModel.Props());

        TestInternalUsersModel internalUsersModel = new TestInternalUsersModel(users);
        TestDynamicConfigModel dynamicConfigModel = new TestDynamicConfigModel();
        InternalAuthenticationBackend authc = new InternalAuthenticationBackend(EMPTY);

        authc.onChanged(configModel, dynamicConfigModel, internalUsersModel);

        User hans_peter = new User("hans peter");
        assertThat("User should not exist", authc.exists(hans_peter), is(false));

        User michael_meier = new User("michael meier");
        assertThat("User should exist", authc.exists(michael_meier), is(true));
    }

    @Test
    public void when_user_exists_expected_roles_and_attributes_are_applied() {
        TestConfigModel configModel = new TestConfigModel();

        List<String> searchGuardRoles = Arrays.asList("SG_1");
        String description = "internal user";
        Map<String, String> attributes = new HashMap<>();
        attributes.put("age", "42");
        List<String> backendRoles = Arrays.asList("Backend role 1");

        HashMap<String, TestInternalUsersModel.Props> users = new HashMap<>();

        users.put("michael meier", new TestInternalUsersModel.Props(searchGuardRoles, description, attributes,
                backendRoles));

        TestInternalUsersModel internalUsersModel = new TestInternalUsersModel(users);
        TestDynamicConfigModel dynamicConfigModel = new TestDynamicConfigModel();
        InternalAuthenticationBackend authc = new InternalAuthenticationBackend(EMPTY);

        authc.onChanged(configModel, dynamicConfigModel, internalUsersModel);

        User user = new User("michael meier");
        assertThat("User should exist", authc.exists(user), is(true));

        assertThat(user.getSearchGuardRoles(), hasItem("SG_1"));
        assertThat(user.getCustomAttributesMap(), hasEntry("attr.internal.age", "42"));
        assertThat(user.getRoles(), hasItem("Backend role 1"));
    }

    @Test
    public void verify_authentication_is_skipped() {
        Settings settings = Settings.builder().putList(ConfigConstants.SKIP_USERS,
                Arrays.asList("michael meier")).build();
        InternalAuthenticationBackend authc = new InternalAuthenticationBackend(settings);

        assertThat(authc.isAuthenticationSkipped("michael meier"), is(true));
        assertThat(authc.isAuthenticationSkipped("alfred quack"), is(false));
    }

    @Test
    public void verifyAuthorization_is_skipped() {
        Settings settings = Settings.builder().putList(ConfigConstants.SKIP_USERS,
                Arrays.asList("alfred quack")).build();
        InternalAuthenticationBackend authz = new InternalAuthenticationBackend(settings);

        assertThat(authz.isAuthorizationSkipped("michael meier"), is(false));
        assertThat(authz.isAuthorizationSkipped("alfred quack"), is(true));
    }
}