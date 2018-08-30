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

package sample;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.rules.ExternalResource;

import java.net.URI;

public class EmbeddedGrizzly extends ExternalResource {

    private final URI baseUri;
    private final Class<? extends ResourceConfig> clazz;
    private HttpServer server;

    public EmbeddedGrizzly(Class<? extends ResourceConfig> clazz) {
        this("http://localhost:8090/", clazz);
    }

    public EmbeddedGrizzly(String baseUri, Class<? extends ResourceConfig> clazz) {
        this(URI.create(baseUri), clazz);
    }

    public EmbeddedGrizzly(URI baseUri, Class<? extends ResourceConfig> clazz) {
        this.baseUri = baseUri;
        this.clazz = clazz;
    }

    @Override
    protected void before() throws Throwable {
        this.server = GrizzlyHttpServerFactory.createHttpServer(baseUri, false);

        final WebappContext context = new WebappContext("webapp", "");

        ServletRegistration servletRegistration = context.addServlet("ServletContainer", ServletContainer.class);
        servletRegistration.addMapping("/*");
        servletRegistration.setInitParameter("javax.ws.rs.Application", clazz.getCanonicalName());

        context.deploy(server);

        server.start();
    }

    @Override
    protected void after() {
        server.shutdownNow();
    }

    public URI getBaseUri() {
        return baseUri;
    }
}
