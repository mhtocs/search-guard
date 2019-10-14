/*
 * Copyright 2018 floragunn GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.floragunn.searchguard.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;

import com.floragunn.searchguard.sgconf.ConfigModel;
import com.floragunn.searchguard.sgconf.DynamicConfigModel;
import com.floragunn.searchguard.sgconf.InternalUsersModel;
import com.floragunn.searchguard.sgconf.DynamicConfigFactory.DCFListener;
import com.floragunn.searchguard.support.ConfigConstants;

public class CompatConfig implements DCFListener {

    private final Logger log = LogManager.getLogger(getClass());
    private final Settings staticSettings;
    private DynamicConfigModel dcf;

    public CompatConfig(final Environment environment) {
        super();
        this.staticSettings = environment.settings(); 
    }
    
    @Override
    public void onChanged(ConfigModel cm, DynamicConfigModel dcm, InternalUsersModel ium) {
        this.dcf = dcf;
        log.debug("dynamicSgConfig updated?: {}", (dcf != null));
    }
    
    //true is default
    public boolean restAuthEnabled() {
        final boolean restInitiallyDisabled = staticSettings.getAsBoolean(ConfigConstants.SEARCHGUARD_UNSUPPORTED_DISABLE_REST_AUTH_INITIALLY, false);
        
        if(restInitiallyDisabled) {
            if(dcf == null) {
                if(log.isTraceEnabled()) {
                    log.trace("dynamicSgConfig is null, initially static restDisabled");
                }
                return false;
            } else {
                final boolean restDynamicallyDisabled = dcf.isRestAuthDisabled();// dynamicSgConfig.dynamic.disable_rest_auth;
                if(log.isTraceEnabled()) {
                    log.trace("searchguard.dynamic.disable_rest_auth {}", restDynamicallyDisabled);
                }
                return !restDynamicallyDisabled;
            }
        } else {
            return true;
        }

    }
    
    //true is default
    public boolean transportInterClusterAuthEnabled() {
        final boolean interClusterAuthInitiallyDisabled = staticSettings.getAsBoolean(ConfigConstants.SEARCHGUARD_UNSUPPORTED_DISABLE_INTERTRANSPORT_AUTH_INITIALLY, false);
        
        if(interClusterAuthInitiallyDisabled) {
            if(dcf == null) {
                if(log.isTraceEnabled()) {
                    log.trace("dynamicSgConfig is null, initially static interClusterAuthDisabled");
                }
                return false;
            } else {
                final boolean interClusterAuthDynamicallyDisabled = dcf.isInterTransportAuthDisabled();//dynamicSgConfig.dynamic.disable_intertransport_auth;
                if(log.isTraceEnabled()) {
                    log.trace("searchguard.dynamic.disable_intertransport_auth {}", interClusterAuthDynamicallyDisabled);
                }
                return !interClusterAuthDynamicallyDisabled;
            }
        } else {
            return true;
        }
    }
}
