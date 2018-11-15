/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
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
 */

package com.palantir.conjure.examples;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palantir.conjure.examples.resources.RecipeBookResource;
import com.palantir.conjure.java.serialization.ObjectMappers;
import com.palantir.conjure.java.server.jersey.ConjureJerseyFeature;
import com.palantir.websecurity.WebSecurityBundle;
import io.dropwizard.Application;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public final class RecipeBookApplication extends Application<RecipeBookConfiguration> {

    public static void main(String[] args) throws Exception {
        new RecipeBookApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<RecipeBookConfiguration> bootstrap) {
        ObjectMapper conjureObjectMapper = ObjectMappers.newServerObjectMapper()
                // needs discoverable subtype resolver for DW polymorphic configuration mechanism
                .setSubtypeResolver(new DiscoverableSubtypeResolver());
        bootstrap.setObjectMapper(conjureObjectMapper);
        bootstrap.addBundle(new WebSecurityBundle());
    }

    @Override
    public void run(RecipeBookConfiguration configuration, Environment environment) {
        RecipeBookResource resource = new RecipeBookResource(configuration.getRecipes());
        environment.jersey().register(resource);


        // must register ConjureJerseyFeature to map conjure error types.
        environment.jersey().register(ConjureJerseyFeature.INSTANCE);
    }
}
