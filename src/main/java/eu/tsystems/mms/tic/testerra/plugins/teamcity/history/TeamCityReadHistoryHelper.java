/*
 * Testerra
 *
 * (C) 2025, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testerra.plugins.teamcity.history;

import eu.tsystems.mms.tic.testerra.plugins.teamcity.restapi.TeamCityRestClient;
import eu.tsystems.mms.tic.testerra.plugins.teamcity.restapi.TeamCityRestResponseParser;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import org.apache.commons.lang3.StringUtils;

import java.net.http.HttpResponse;
import java.util.List;

public class TeamCityReadHistoryHelper implements Loggable {

    private TeamCityRestClient client;

    public TeamCityReadHistoryHelper(TeamCityRestClient client) {
        this.client = client;
    }

    public List<Integer> findLatestBuildIds(final String buildTypeId, final String branchType, final String count) {
        HttpResponse<String> response = client.readBuildJobs(buildTypeId, branchType, count);
        if(response == null) {
            return List.of();
        }

        String foundJobs = TeamCityRestResponseParser.getCountOfBuilds(response.body());
        if (StringUtils.isBlank(foundJobs) || "0".equals(foundJobs)) {
            log().warn("Cannot find a build id of buildtype {} and branch locator ({})", buildTypeId, branchType);
            return List.of();
        }

        return TeamCityRestResponseParser.getBuildIdList(response.body());
    }

    public String getHistoryFilePath(final List<Integer> buildIds) {
        for (Integer buildId : buildIds) {
            HttpResponse<String> response = client.readArtifacts(buildId);

            if (response == null) {
                break;
            }
            final String path = TeamCityRestResponseParser.readPathOfHistory(response.body());
            if (StringUtils.isNotBlank(path) && !"[]".equals(path)) {
                return path;
            }
        }
        log().warn("Cannot find history file of build ids {}", buildIds);
        return null;
    }
}
