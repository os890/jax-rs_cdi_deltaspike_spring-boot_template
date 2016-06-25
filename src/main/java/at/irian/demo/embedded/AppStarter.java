/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package at.irian.demo.embedded;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.core.api.config.ConfigResolver;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

@EnableAutoConfiguration
public class AppStarter {
    //open
    // http://localhost:8080/rest/hello to test it
    //and/or
    // http://localhost:8080/rest/hello-cdi
    public static void main(String[] args) {
        new SpringApplicationBuilder(AppStarter.class).run(args);
    }

    @Bean
    public ServletRegistrationBean exposeServletRegistrationBean() {
        String basePath = "/" + ConfigResolver.getPropertyValue("basePath") + "/*";
        ServletRegistrationBean registration = new ServletRegistrationBean(new ServletContainer(), basePath);
        registration.addInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, JerseyInitialization.class.getName());
        return registration;
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        Integer port = Integer.parseInt(ConfigResolver.getPropertyValue("httpPort"));
        return container -> container.setPort(port);
    }

    @Bean
    public ServletContextListener contextListener() {
        final CdiContainer cdiContainer = CdiContainerLoader.getCdiContainer();
        return new ServletContextListener() {
            @Override
            public void contextInitialized(ServletContextEvent sce) {
                cdiContainer.boot();
            }

            @Override
            public void contextDestroyed(ServletContextEvent sce) {
                cdiContainer.shutdown();
            }
        };
    }
}
