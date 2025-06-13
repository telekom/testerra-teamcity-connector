/*
 * Testerra
 *
 * (C) 2024, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package io.testerra.plugins.teamcity.restapi;

import eu.tsystems.mms.tic.testframework.logging.Loggable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TeamCityRestClient implements Loggable {

    private final String tcUrl;
    private final String token;

    public TeamCityRestClient(final String tcUrl, final String token) {
        this.tcUrl = tcUrl;
        this.token = token;
    }

    public HttpResponse<String> readArtifacts(final int buildId) {

        final String path = "/app/rest/builds/id:" + buildId + "/artifacts/children?locator=recursive:true";
        try {
            log().info("Get history file path from build id {}", buildId);
            HttpRequest request = this.buildRequest(path);
            HttpResponse<String> response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() > 204) {
                log().error("Error getting history file path of build id {}:", buildId);
                log().error(response.body());
                return null;
            }
            return response;
        } catch (IOException | InterruptedException e) {
            log().error("Cannot get history file path from {}{}", this.tcUrl, path);
            log().error(e.getMessage());
            return null;
        }
    }

    public HttpResponse<String> readBuildJobs(final String buildTypeId, final String branchType, final String count) {

        String branchLocator = "name:" + branchType;
        switch (branchType.toLowerCase()) {
            case "all":
                branchLocator = "policy:ALL_BRANCHES";
                break;
            case "default":
                branchLocator = "default:true";
        }

        final String path = String.format(
                "/app/rest/buildTypes/id:%s/builds?locator=running:false,canceled:false,branch:(%s),count:%s",
                buildTypeId, branchLocator, count
        );

        try {
            log().info("Get last build id from {}", buildTypeId);
            HttpRequest request = this.buildRequest(path);
            HttpResponse<String> response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() > 204) {
                log().error("Error getting build id of {}:", buildTypeId);
                log().error(response.body());
                return null;
            }
            return response;
        } catch (IOException | InterruptedException e) {
            log().error("Cannot get build id from {}{}", this.tcUrl, path);
            log().error(e.getMessage());
            return null;
        }
    }

    private HttpRequest buildRequest(final String path) throws IOException {
        return HttpRequest.newBuilder()
                .uri(URI.create(this.tcUrl + path))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .GET()
                .build();
    }

}
