/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.cloud.policies.app.health;

import com.redhat.cloud.policies.app.NotificationSystem;
import com.redhat.cloud.policies.app.PolicyEngine;
import com.redhat.cloud.policies.app.model.Policy;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Provide a /status endpoint, that returns a 200 if all is cool
 * and a 500 with list of issues if not.
 *
 * @author hrupp
 */
@Path("/status")
@RequestScoped
public class StatusEndpoint {

  @Inject
  @RestClient
  PolicyEngine engine;

  @Inject
  @RestClient
  NotificationSystem notifications;

  @GET
  @Produces("text/plain")
  public Response getStatus() {

    List<String> issues = new ArrayList<>();

    try {
      Policy.findByName("dummy", "-dummy-");
    }
    catch (Exception e) {
      issues.add(e.getMessage());
    }

    try {
      engine.findTriggersById("dummy", "dummy");
    }
    catch (Exception e) {
      issues.add(e.getMessage());
    }

    try {
      notifications.removeNotification("dummy","dummy");
    } catch (Exception e) {
      issues.add(e.getMessage());
    }

    if (!issues.isEmpty()) {
      return Response.serverError().entity(issues).build();
    }


    return Response.ok().build();
  }
}
