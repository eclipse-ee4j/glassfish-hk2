/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.glassfish.hk2.maven;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.shared.osgi.DefaultMaven2OsgiConverter;
import org.apache.maven.shared.osgi.Maven2OsgiConverter;
import org.codehaus.plexus.util.StringUtils;

/**
 *
 * @author Romain Grecourt
 */
public class Version {

    public enum COMPONENT {
        major,
        minor,
        micro,
        qualifier
    };
    
    private static final int DIGITS_INDEX = 1;
    public static final Pattern STANDARD_PATTERN = Pattern.compile(
            "^((?:\\d+\\.)*\\d+)" // digit(s) and '.' repeated - followed by digit (version digits 1.22.0, etc)
            + "([-_])?" // optional - or _  (annotation separator)
            + "([a-zA-Z]*)" // alpha characters (looking for annotation - alpha, beta, RC, etc.)
            + "([-_])?" // optional - or _  (annotation revision separator)
            + "(\\d*)" // digits  (any digits after rc or beta is an annotation revision)
            + "(?:([-_])?(.*?))?$");  // - or _ followed everything else (build specifier)
    String orig;
    int major = 0;
    int minor = 0;
    int incremental = 0;
    String qualifier = "";

    public Version(String v) {
        orig = v;
        List<String> digits = parseDigits(v);
        major = getDigit(digits, 0);
        minor = getDigit(digits, 1);
        incremental = getDigit(digits, 2);
        if(orig.contains("-")){
            qualifier = orig.substring(orig.indexOf('-')+1);
        }
    }

    private static int getDigit(List<String> digits, int idx) {
        if (digits.size() >= idx + 1
                && digits.get(idx) != null
                && !digits.get(idx).isEmpty()) {
            return Integer.parseInt(digits.get(idx));
        }
        return 0;
    }

    private List<String> parseDigits(String vStr) {
        Matcher m = STANDARD_PATTERN.matcher(vStr);
        if (m.matches()) {
            return Arrays.asList(StringUtils.split(
                    m.group(DIGITS_INDEX),
                    "."));
        }
        return Collections.EMPTY_LIST;
    }

    public int getMajorVersion() {
        return major;
    }

    public int getMinorVersion() {
        return minor;
    }

    public int getIncrementalVersion() {
        return incremental;
    }
    
    public String getQualifier(){
        return qualifier;
    }

    private static String formatString4Osgi(String s){
        return s.replaceAll("-", "_").replaceAll("\\.", "_");
    }
    
    public String convertToOsgi(COMPONENT comToDrop) {
        Maven2OsgiConverter converter = new DefaultMaven2OsgiConverter();

        if (comToDrop != null) {
            switch (comToDrop) {
                case major: {
                    return converter.getVersion("0.0.0");
                }
                case minor: {
                    return converter.getVersion(String.valueOf(getMajorVersion()));
                }
                case micro: {
                    return converter.getVersion(String.format("%s.%s",
                            getMajorVersion(),
                            getMinorVersion()));
                }
                case qualifier: {
                    return converter.getVersion(String.format("%s.%s.%s",
                            getMajorVersion(),
                            getMinorVersion(),
                            getIncrementalVersion()));
                }
            }
        }
        
        // init version major.minor.micro
        String version = String.format("%s.%s.%s",
                getMajorVersion(),
                getMinorVersion(),
                getIncrementalVersion());
        
        // if there is a qualifier, add it
        if(!getQualifier().isEmpty()){
            version = String.format("%s.%s",
                    version,
                    formatString4Osgi(getQualifier()));
        }
        return converter.getVersion(version);
    }
}
