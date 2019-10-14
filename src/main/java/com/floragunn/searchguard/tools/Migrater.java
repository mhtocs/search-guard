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

package com.floragunn.searchguard.tools;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.elasticsearch.common.collect.Tuple;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.floragunn.searchguard.DefaultObjectMapper;
import com.floragunn.searchguard.sgconf.Migration;
import com.floragunn.searchguard.sgconf.impl.CType;
import com.floragunn.searchguard.sgconf.impl.SgDynamicConfiguration;
import com.floragunn.searchguard.sgconf.impl.v7.RoleV7;
import com.floragunn.searchguard.sgconf.impl.v7.TenantV7;
import com.google.common.io.Files;

public class Migrater {

    public static void main(final String[] args) {

        final Options options = new Options();
        final HelpFormatter formatter = new HelpFormatter();
        options.addOption(Option.builder("dir").argName("directory").hasArg().required().desc("Directory containing file to be migrated").build());
        options.addOption(Option.builder("b").desc("Backup files before changing them").build());

        final CommandLineParser parser = new DefaultParser();
        try {
            final CommandLine line = parser.parse(options, args);
            
            if(line.hasOption("dir")) {
                final File dir = new File(line.getOptionValue("dir"));
                if(!migrateDirectory(dir, line.hasOption("b"))) {
                    System.exit(-1);
                } else {
                    System.exit(0);
                }
            }
        } catch (final Exception exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            formatter.printHelp("migrater.sh", options, true);
        }
        
        System.exit(-1);
    }
    
    public static boolean migrateDirectory(File dir, boolean backup) {
        if(!dir.exists()) {
            System.out.println(dir.getAbsolutePath()+" does not exist");
            return false;
        }
        
        if(!dir.isDirectory()) {
            System.out.println(dir.getAbsolutePath()+" is not a directory");
            return false;
        }
        
        File[] files = dir.listFiles(new FileFilter() {
            
            @Override
            public boolean accept(File pathname) {
                if(pathname.isFile() && pathname.getName().endsWith(".yml")) {
                    return true;
                }
                
                return false;
            }
        });
        
        boolean retVal = true;
        
        for(File file: files) {
            if(!migrateFile(file, backup)) {
                retVal = false;
            }
        }
        
        return retVal;
    }
    
    public static boolean migrateFile(File file, boolean backup) {
        final String absolutePath = file.getAbsolutePath();
        if(!file.exists()) {
            System.out.println("Skip "+absolutePath+" because it does not exist");
            return false;
        }
        
        if(!file.isFile()) {
            System.out.println("Skip "+absolutePath+" because it is a directory or a special file");
            return false;
        }

        try {
            SgDynamicConfiguration<?> val = Migration.migrateConfig(SgDynamicConfiguration.fromNode(DefaultObjectMapper.YAML_MAPPER.readTree(file), CType.CONFIG, 1, 0, 0));
            return backupAndWrite(file, val, backup);
        } catch (UnrecognizedPropertyException e) {
            //e.printStackTrace();
            //suppress
        } catch (Exception e) {
            //e.printStackTrace();
        }
        
        try {
            Tuple<SgDynamicConfiguration<RoleV7>, SgDynamicConfiguration<TenantV7>> tup = Migration.migrateRoles(SgDynamicConfiguration.fromNode(DefaultObjectMapper.YAML_MAPPER.readTree(file), CType.ROLES, 1, 0, 0), null);
            boolean roles = backupAndWrite(file, tup.v1(), backup);
            return roles && backupAndWrite(new File(file.getParent(),file.getName().replace(".yml", "")+"_tenants.yml"), tup.v2(), backup);
        } catch (Exception e) {
            //suppress
        }
        
        try {
            SgDynamicConfiguration<?> val = Migration.migrateInternalUsers(SgDynamicConfiguration.fromNode(DefaultObjectMapper.YAML_MAPPER.readTree(file), CType.INTERNALUSERS, 1, 0, 0));
            return backupAndWrite(file, val, backup);
        } catch (Exception e) {
            //suppress
        }
        
        try {
            SgDynamicConfiguration<?> val = Migration.migrateRoleMappings(SgDynamicConfiguration.fromNode(DefaultObjectMapper.YAML_MAPPER.readTree(file), CType.ROLESMAPPING, 1, 0, 0));
            return backupAndWrite(file, val, backup);
        } catch (Exception e) {
            //suppress
        }
        
        try {
            SgDynamicConfiguration<?> val = Migration.migrateActionGroups(SgDynamicConfiguration.fromNode(DefaultObjectMapper.YAML_MAPPER.readTree(file), CType.ACTIONGROUPS, 1, 0, 0));
            return backupAndWrite(file, val, backup);
        } catch (Exception e) {
            //suppress
        }
        
        try {
            SgDynamicConfiguration<?> val = Migration.migrateActionGroups(SgDynamicConfiguration.fromNode(DefaultObjectMapper.YAML_MAPPER.readTree(file), CType.ACTIONGROUPS, 0, 0, 0));
            return backupAndWrite(file, val, backup);
        } catch (Exception e) {
            //suppress
        }
        
        System.out.println("Unable to handle "+absolutePath);
        return false;
        
    }
    
    private static boolean backupAndWrite(File file, SgDynamicConfiguration<?> val, boolean backup) {
        try {
            if(val == null) {
                System.out.println("NULL object for "+file.getAbsolutePath());
                return false;
            }
            if(backup && file.exists()) {
                Files.copy(file, new File(file.getParent(), file.getName()+".bck6"));
            }
            DefaultObjectMapper.YAML_MAPPER.writeValue(file, val);
            System.out.println("Migrated (as "+val.getCType()+"/"+val.getImplementingClass()+") "+file.getAbsolutePath());
            return true;
        } catch (Exception e) {
            System.out.println("Unable to write "+file.getAbsolutePath()+". This is unexpected and we will abort migration.");
            System.out.println("    Details: "+e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}
