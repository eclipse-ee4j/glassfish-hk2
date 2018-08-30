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

import sample.model.Param;
import sample.util.SampleUtil;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by yoan on 2016/06/02.
 */
@Path("/")
@Produces(MediaType.TEXT_HTML)
public class SampleController {

    @Inject private SampleUtil sampleUtil;

    @Path("foo")
    @GET
    public String foo() {
        return "foo" +
                "<form action=baa method=POST><input type=submit name=btnName value=go></form>" +
                "<form action=hoge method=POST><input type=submit name=btnName value=gogogo></form>" +
                sampleUtil.getBaseUri();
    }

    @Path("baa")
    @POST
    public String baa(@FormParam("btnName") String btnName) {
        return "foo btnName = " + btnName + ", uri = " + sampleUtil.getBaseUri();
    }

    @Path("hoge")
    @POST
    public String hoge(@BeanParam Param param) {
        return "hoge btnName = " + param.getBtnName() + ", uri = " + sampleUtil.getBaseUri();
    }

}
