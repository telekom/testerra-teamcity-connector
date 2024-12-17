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
package io.testerra.plugins.teamcity.test;

import eu.tsystems.mms.tic.testerra.plugins.teamcity.TeamCityHistoryDownloader;
import eu.tsystems.mms.tic.testerra.plugins.teamcity.TeamCityRestClient;
import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.utils.FileDownloader;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class TeamCityRestClientTests extends TesterraTest implements PropertyManagerProvider {

    /**
     * To run this test you need to create a 'local.properties' file and add the following properties:
     * tt.teamcity.history.download.active=true
     * tt.teamcity.url=<teamcity-url>
     * tt.teamcity.rest.token=<bearer token>
     * tt.teamcity.buildTypeId=<build type id of your build job>
     */

    static {
        PROPERTY_MANAGER.loadProperties("local.properties");
    }

//    @BeforeSuite
//    public void initLocalProperties() {
//
//    }

    @Test
    public void downloadAHistoryFileFromTeamCity() throws IOException {
        final String url = TeamCityHistoryDownloader.Properties.TEAMCITY_URL.asString();
        final String token = TeamCityHistoryDownloader.Properties.TEAMCITY_REST_TOKEN.asString();
        final String buildTypeId = TeamCityHistoryDownloader.Properties.TEAMCITY_BUILD_TYPE_ID.asString();

        TeamCityRestClient client = new TeamCityRestClient(url, token);
        final String buildId = client.findLatestBuildId(buildTypeId);
        final String historyFilePath = client.getHistoryFilePath(buildId);
        FileDownloader downloader = new FileDownloader();
        downloader.setConnectionConfigurator(connection -> {
            connection.setRequestProperty("Authorization", "Bearer " + token);
        });
        File history = downloader.download(historyFilePath, "history");
        Assert.assertNotNull(history);
    }

}
