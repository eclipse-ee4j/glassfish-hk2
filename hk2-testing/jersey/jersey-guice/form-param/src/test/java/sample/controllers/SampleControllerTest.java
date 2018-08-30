/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

package sample.controllers;

import org.junit.ClassRule;
import org.junit.Test;
import sample.EmbeddedGrizzly;
import sample.MyApplication;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

/**
 * Created by yoan on 2016/06/02.
 */
public class SampleControllerTest {
    @ClassRule
    public static EmbeddedGrizzly embeddedGrizzly = new EmbeddedGrizzly(MyApplication.class);

    @Test // @org.junit.Ignore
    public void foo() throws Exception {
        String res = get("foo");
        assertThat(res, startsWith("foo"));
        assertThat(res, containsString("http://localhost:8090/"));
    }

    @Test // @org.junit.Ignore
    public void baa() throws Exception {
        MultivaluedMap<String, String> form = new MultivaluedHashMap<String, String>() {{
            add("btnName", "abc");
        }};
        String res = post("baa", form);
        assertThat(res, containsString("btnName = abc"));
        assertThat(res, containsString("http://localhost:8090/"));
    }

    @Test // @org.junit.Ignore
    public void hoge() throws Exception {
        MultivaluedMap<String, String> form = new MultivaluedHashMap<String, String>() {{
            add("btnName", "fuga");
        }};
        String res = post("hoge", form);
        assertThat(res, containsString("btnName = fuga"));
        assertThat(res, containsString("http://localhost:8090/"));
    }

    private String get(String path) {
        return ClientBuilder.newClient()
                .target(embeddedGrizzly.getBaseUri())
                .path(path)
                .request()
                .get(String.class);
    }

    private String post(String path, MultivaluedMap<String, String> form) {
        return ClientBuilder.newClient()
                .target(embeddedGrizzly.getBaseUri())
                .path(path)
                .request()
                .post(Entity.form(form), String.class);
    }

}
