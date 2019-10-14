/*
 * Copyright 2015-2017 floragunn GmbH
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

package com.floragunn.searchguard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.elasticsearch.common.settings.Settings;
import org.junit.Assert;
import org.junit.Test;

import com.floragunn.searchguard.ssl.util.SSLConfigConstants;
import com.floragunn.searchguard.test.DynamicSgConfig;
import com.floragunn.searchguard.test.SingleClusterTest;
import com.floragunn.searchguard.test.helper.file.FileHelper;
import com.floragunn.searchguard.test.helper.rest.RestHelper;
import com.floragunn.searchguard.test.helper.rest.RestHelper.HttpResponse;
import com.floragunn.searchguard.tools.SearchGuardAdmin;

public class SgAdminTests extends SingleClusterTest {
    
    @Test
    public void testSgAdmin() throws Exception {
        setup(Settings.EMPTY, null, Settings.EMPTY, false);
        
        final String prefix = getResourceFolder()==null?"":getResourceFolder()+"/";
        
        List<String> argsAsList = new ArrayList<>();
        argsAsList.add("-ts");
        argsAsList.add(FileHelper.getAbsoluteFilePathFromClassPath(prefix+"truststore.jks").toFile().getAbsolutePath());
        argsAsList.add("-ks");
        argsAsList.add(FileHelper.getAbsoluteFilePathFromClassPath(prefix+"kirk-keystore.jks").toFile().getAbsolutePath());
        argsAsList.add("-p");
        argsAsList.add(String.valueOf(clusterInfo.nodePort));
        argsAsList.add("-cn");
        argsAsList.add(clusterInfo.clustername);
        argsAsList.add("-cd");
        argsAsList.add(new File("./sgconfig").getAbsolutePath());
        argsAsList.add("-nhnv");
        
        
        int returnCode  = SearchGuardAdmin.execute(argsAsList.toArray(new String[0]));
        Assert.assertEquals(0, returnCode);
        
        RestHelper rh = nonSslRestHelper();
        
        Assert.assertEquals(HttpStatus.SC_OK, (rh.executeGetRequest("_searchguard/health?pretty")).getStatusCode());
    }
    
    @Test
    public void testSgAdminV6Update() throws Exception {
        setup(Settings.EMPTY, null, Settings.EMPTY, false);
        
        final String prefix = getResourceFolder()==null?"":getResourceFolder()+"/";
        
        List<String> argsAsList = new ArrayList<>();
        argsAsList.add("-ts");
        argsAsList.add(FileHelper.getAbsoluteFilePathFromClassPath(prefix+"truststore.jks").toFile().getAbsolutePath());
        argsAsList.add("-ks");
        argsAsList.add(FileHelper.getAbsoluteFilePathFromClassPath(prefix+"kirk-keystore.jks").toFile().getAbsolutePath());
        argsAsList.add("-p");
        argsAsList.add(String.valueOf(clusterInfo.nodePort));
        argsAsList.add("-cn");
        argsAsList.add(clusterInfo.clustername);
        argsAsList.add("-cd");
        argsAsList.add(new File("./legacy/sgconfig_v6").getAbsolutePath());
        argsAsList.add("-nhnv");
        
        
        int returnCode  = SearchGuardAdmin.execute(argsAsList.toArray(new String[0]));
        Assert.assertNotEquals(0, returnCode);
        
        RestHelper rh = nonSslRestHelper();
        
        Assert.assertEquals(HttpStatus.SC_SERVICE_UNAVAILABLE, (rh.executeGetRequest("_searchguard/health?pretty")).getStatusCode());
    }
    
    @Test
    public void testSgAdminRegularUpdate() throws Exception {
        setup(Settings.EMPTY, new DynamicSgConfig(), Settings.EMPTY, true);
        
        final String prefix = getResourceFolder()==null?"":getResourceFolder()+"/";
        
        List<String> argsAsList = new ArrayList<>();
        argsAsList.add("-ts");
        argsAsList.add(FileHelper.getAbsoluteFilePathFromClassPath(prefix+"truststore.jks").toFile().getAbsolutePath());
        argsAsList.add("-ks");
        argsAsList.add(FileHelper.getAbsoluteFilePathFromClassPath(prefix+"kirk-keystore.jks").toFile().getAbsolutePath());
        argsAsList.add("-p");
        argsAsList.add(String.valueOf(clusterInfo.nodePort));
        argsAsList.add("-cn");
        argsAsList.add(clusterInfo.clustername);
        argsAsList.add("-cd");
        argsAsList.add(new File("./sgconfig").getAbsolutePath());
        argsAsList.add("-nhnv");
        
        
        int returnCode  = SearchGuardAdmin.execute(argsAsList.toArray(new String[0]));
        Assert.assertEquals(0, returnCode);
        
        RestHelper rh = nonSslRestHelper();
        HttpResponse res;
        
        Assert.assertEquals(HttpStatus.SC_OK, (res = rh.executeGetRequest("_searchguard/health?pretty")).getStatusCode());
        System.out.println(res.getBody());
        assertContains(res, "*UP*");
        assertContains(res, "*strict*");
        assertNotContains(res, "*DOWN*");
    }
    
    @Test
    public void testSgAdminSingularV7Updates() throws Exception {
        setup(Settings.EMPTY, new DynamicSgConfig(), Settings.EMPTY, true);
        
        final String prefix = getResourceFolder()==null?"":getResourceFolder()+"/";
        
        List<String> argsAsList = new ArrayList<>();
        argsAsList.add("-ts");
        argsAsList.add(FileHelper.getAbsoluteFilePathFromClassPath(prefix+"truststore.jks").toFile().getAbsolutePath());
        argsAsList.add("-ks");
        argsAsList.add(FileHelper.getAbsoluteFilePathFromClassPath(prefix+"kirk-keystore.jks").toFile().getAbsolutePath());
        argsAsList.add("-p");
        argsAsList.add(String.valueOf(clusterInfo.nodePort));
        argsAsList.add("-cn");
        argsAsList.add(clusterInfo.clustername);
        argsAsList.add("-f");
        argsAsList.add(new File("./sgconfig/sg_config.yml").getAbsolutePath());
        argsAsList.add("-t");
        argsAsList.add("config");
        argsAsList.add("-nhnv");
        
        
        int returnCode  = SearchGuardAdmin.execute(argsAsList.toArray(new String[0]));
        Assert.assertEquals(0, returnCode);
        
        argsAsList = new ArrayList<>();
        argsAsList.add("-ts");
        argsAsList.add(FileHelper.getAbsoluteFilePathFromClassPath(prefix+"truststore.jks").toFile().getAbsolutePath());
        argsAsList.add("-ks");
        argsAsList.add(FileHelper.getAbsoluteFilePathFromClassPath(prefix+"kirk-keystore.jks").toFile().getAbsolutePath());
        argsAsList.add("-p");
        argsAsList.add(String.valueOf(clusterInfo.nodePort));
        argsAsList.add("-cn");
        argsAsList.add(clusterInfo.clustername);
        argsAsList.add("-f");
        argsAsList.add(new File("./sgconfig/sg_roles_mapping.yml").getAbsolutePath());
        argsAsList.add("-t");
        argsAsList.add("rolesmapping");
        argsAsList.add("-nhnv");
        
        
        returnCode  = SearchGuardAdmin.execute(argsAsList.toArray(new String[0]));
        Assert.assertEquals(0, returnCode);
        
        argsAsList = new ArrayList<>();
        argsAsList.add("-ts");
        argsAsList.add(FileHelper.getAbsoluteFilePathFromClassPath(prefix+"truststore.jks").toFile().getAbsolutePath());
        argsAsList.add("-ks");
        argsAsList.add(FileHelper.getAbsoluteFilePathFromClassPath(prefix+"kirk-keystore.jks").toFile().getAbsolutePath());
        argsAsList.add("-p");
        argsAsList.add(String.valueOf(clusterInfo.nodePort));
        argsAsList.add("-cn");
        argsAsList.add(clusterInfo.clustername);
        argsAsList.add("-f");
        argsAsList.add(new File("./sgconfig/sg_tenants.yml").getAbsolutePath());
        argsAsList.add("-t");
        argsAsList.add("tenants");
        argsAsList.add("-nhnv");
        
        
        returnCode  = SearchGuardAdmin.execute(argsAsList.toArray(new String[0]));
        Assert.assertEquals(0, returnCode);
        
        RestHelper rh = nonSslRestHelper();
        HttpResponse res;
        
        Assert.assertEquals(HttpStatus.SC_OK, (res = rh.executeGetRequest("_searchguard/health?pretty")).getStatusCode());
        System.out.println(res.getBody());
        assertContains(res, "*UP*");
        assertContains(res, "*strict*");
        assertNotContains(res, "*DOWN*");
    }
    
    @Test
    public void testSgAdminSingularV6Updates() throws Exception {
        setup(Settings.EMPTY, new DynamicSgConfig(), Settings.EMPTY, true);
        
        final String prefix = getResourceFolder()==null?"":getResourceFolder()+"/";
        
        List<String> argsAsList = new ArrayList<>();
        argsAsList.add("-ts");
        argsAsList.add(FileHelper.getAbsoluteFilePathFromClassPath(prefix+"truststore.jks").toFile().getAbsolutePath());
        argsAsList.add("-ks");
        argsAsList.add(FileHelper.getAbsoluteFilePathFromClassPath(prefix+"kirk-keystore.jks").toFile().getAbsolutePath());
        argsAsList.add("-p");
        argsAsList.add(String.valueOf(clusterInfo.nodePort));
        argsAsList.add("-cn");
        argsAsList.add(clusterInfo.clustername);
        argsAsList.add("-f");
        argsAsList.add(new File("./legacy/sgconfig_v6/sg_config.yml").getAbsolutePath());
        argsAsList.add("-t");
        argsAsList.add("config");
        argsAsList.add("-nhnv");
        
        int returnCode  = SearchGuardAdmin.execute(argsAsList.toArray(new String[0]));
        Assert.assertNotEquals(0, returnCode);

        
        RestHelper rh = nonSslRestHelper();
        HttpResponse res;
        
        Assert.assertEquals(HttpStatus.SC_OK, (res = rh.executeGetRequest("_searchguard/health?pretty")).getStatusCode());
        System.out.println(res.getBody());
        assertContains(res, "*UP*");
        assertContains(res, "*strict*");
        assertNotContains(res, "*DOWN*");
    }
    
    @Test
    public void testSgAdminInvalidYml() throws Exception {
        setup(Settings.EMPTY, new DynamicSgConfig(), Settings.EMPTY, true);
        
        final String prefix = getResourceFolder()==null?"":getResourceFolder()+"/";
        
        List<String> argsAsList = new ArrayList<>();
        argsAsList.add("-ts");
        argsAsList.add(FileHelper.getAbsoluteFilePathFromClassPath(prefix+"truststore.jks").toFile().getAbsolutePath());
        argsAsList.add("-ks");
        argsAsList.add(FileHelper.getAbsoluteFilePathFromClassPath(prefix+"kirk-keystore.jks").toFile().getAbsolutePath());
        argsAsList.add("-p");
        argsAsList.add(String.valueOf(clusterInfo.nodePort));
        argsAsList.add("-cn");
        argsAsList.add(clusterInfo.clustername);
        argsAsList.add("-f");
        argsAsList.add(FileHelper.getAbsoluteFilePathFromClassPath(prefix+"sg_roles_invalidxcontent.yml").toFile().getAbsolutePath());
        argsAsList.add("-t");
        argsAsList.add("roles");
        argsAsList.add("-nhnv");
        
        
        int returnCode  = SearchGuardAdmin.execute(argsAsList.toArray(new String[0]));
        Assert.assertNotEquals(0, returnCode);
        
        RestHelper rh = nonSslRestHelper();
        HttpResponse res;
        
        Assert.assertEquals(HttpStatus.SC_OK, (res = rh.executeGetRequest("_searchguard/health?pretty")).getStatusCode());
        System.out.println(res.getBody());
        assertContains(res, "*UP*");
        assertContains(res, "*strict*");
        assertNotContains(res, "*DOWN*");
    }
    
    @Test
    public void testSgAdminReloadInvalidConfig() throws Exception {        
        final Settings settings = Settings.builder()
                .put(SSLConfigConstants.SEARCHGUARD_SSL_HTTP_CLIENTAUTH_MODE, "REQUIRE")
                .put("searchguard.ssl.http.enabled",true)
                .put("searchguard.ssl.http.keystore_filepath", FileHelper.getAbsoluteFilePathFromClassPath("node-0-keystore.jks"))
                .put("searchguard.ssl.http.truststore_filepath", FileHelper.getAbsoluteFilePathFromClassPath("truststore.jks"))
                .build();
        setup(Settings.EMPTY, new DynamicSgConfig(), settings, true);
        final RestHelper rh = restHelper(); //ssl resthelper

        rh.enableHTTPClientSSL = true;
        rh.trustHTTPServerCertificate = true;
        rh.sendHTTPClientCertificate = true;
        rh.keystore = "kirk-keystore.jks";
        System.out.println(rh.executePutRequest("searchguard/"+getType()+"/roles", FileHelper.loadFile("sg_roles_invalidxcontent.yml")).getBody());;
        Assert.assertEquals(HttpStatus.SC_OK, rh.executePutRequest("searchguard/"+getType()+"/roles", "{\"roles\":\"dummy\"}").getStatusCode());
        
        
        final String prefix = getResourceFolder()==null?"":getResourceFolder()+"/";
        
        List<String> argsAsList = new ArrayList<>();
        argsAsList.add("-ts");
        argsAsList.add(FileHelper.getAbsoluteFilePathFromClassPath(prefix+"truststore.jks").toFile().getAbsolutePath());
        argsAsList.add("-ks");
        argsAsList.add(FileHelper.getAbsoluteFilePathFromClassPath(prefix+"kirk-keystore.jks").toFile().getAbsolutePath());
        argsAsList.add("-p");
        argsAsList.add(String.valueOf(clusterInfo.nodePort));
        argsAsList.add("-cn");
        argsAsList.add(clusterInfo.clustername);
        argsAsList.add("-rl");
        argsAsList.add("-nhnv");
        
        
        int returnCode  = SearchGuardAdmin.execute(argsAsList.toArray(new String[0]));
        Assert.assertNotEquals(0, returnCode);
        
        HttpResponse res;
        
        Assert.assertEquals(HttpStatus.SC_OK, (res = rh.executeGetRequest("_searchguard/health?pretty")).getStatusCode());
        assertContains(res, "*UP*");
        assertContains(res, "*strict*");
        assertNotContains(res, "*DOWN*");
    }
    
    @Test
    public void testSgAdminValidateConfig() throws Exception {                
        List<String> argsAsList = new ArrayList<>();
        argsAsList.add("-cd");
        argsAsList.add(new File("./sgconfig").getAbsolutePath());
        argsAsList.add("-vc");
        
        int returnCode  = SearchGuardAdmin.execute(argsAsList.toArray(new String[0]));
        Assert.assertEquals(0, returnCode);
        
        argsAsList = new ArrayList<>();
        argsAsList.add("-f");
        argsAsList.add(new File("./sgconfig/sg_roles.yml").getAbsolutePath());
        argsAsList.add("-vc");
        
        returnCode  = SearchGuardAdmin.execute(argsAsList.toArray(new String[0]));
        Assert.assertEquals(0, returnCode);
        
        argsAsList = new ArrayList<>();
        argsAsList.add("-f");
        argsAsList.add(new File("./src/main/resources/static_config/static_roles.yml").getAbsolutePath());
        argsAsList.add("-vc");
        
        returnCode  = SearchGuardAdmin.execute(argsAsList.toArray(new String[0]));
        Assert.assertEquals(0, returnCode);
        
        argsAsList = new ArrayList<>();
        argsAsList.add("-f");
        argsAsList.add(new File("./src/main/resources/static_config/static_action_groups.yml").getAbsolutePath());
        argsAsList.add("-vc");
        
        returnCode  = SearchGuardAdmin.execute(argsAsList.toArray(new String[0]));
        Assert.assertEquals(0, returnCode);
        
        argsAsList = new ArrayList<>();
        argsAsList.add("-f");
        argsAsList.add(new File("./src/main/resources/static_config/static_tenants.yml").getAbsolutePath());
        argsAsList.add("-vc");
        
        returnCode  = SearchGuardAdmin.execute(argsAsList.toArray(new String[0]));
        Assert.assertEquals(0, returnCode);
        
        argsAsList = new ArrayList<>();
        argsAsList.add("-f");
        argsAsList.add(new File("./sgconfig/sg_roles.yml").getAbsolutePath());
        argsAsList.add("-vc");
        argsAsList.add("-t");
        argsAsList.add("config");
        
        returnCode  = SearchGuardAdmin.execute(argsAsList.toArray(new String[0]));
        Assert.assertNotEquals(0, returnCode);
        
        argsAsList = new ArrayList<>();
        argsAsList.add("-ks");
        argsAsList.add(new File("./sgconfig").getAbsolutePath());
        argsAsList.add("-vc");
        
        returnCode  = SearchGuardAdmin.execute(argsAsList.toArray(new String[0]));
        Assert.assertNotEquals(0, returnCode);
        
        argsAsList = new ArrayList<>();
        argsAsList.add("-cd");
        argsAsList.add(new File("./legacy/sgconfig_v6").getAbsolutePath());
        argsAsList.add("-vc");
        
        returnCode  = SearchGuardAdmin.execute(argsAsList.toArray(new String[0]));
        Assert.assertNotEquals(0, returnCode);
        
        argsAsList = new ArrayList<>();
        argsAsList.add("-cd");
        argsAsList.add(new File("./legacy/sgconfig_v6").getAbsolutePath());
        argsAsList.add("-vc");
        argsAsList.add("6");
        
        returnCode  = SearchGuardAdmin.execute(argsAsList.toArray(new String[0]));
        Assert.assertEquals(0, returnCode);
        
        argsAsList = new ArrayList<>();
        argsAsList.add("-cd");
        argsAsList.add(new File("./sgconfig").getAbsolutePath());
        argsAsList.add("-vc");
        argsAsList.add("8");
        
        returnCode  = SearchGuardAdmin.execute(argsAsList.toArray(new String[0]));
        Assert.assertNotEquals(0, returnCode);
    }
}
