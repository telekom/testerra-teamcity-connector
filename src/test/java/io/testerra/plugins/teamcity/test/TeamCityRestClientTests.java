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

import eu.tsystems.mms.tic.testerra.plugins.teamcity.TeamCityRestClient;
import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.utils.FileDownloader;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class TeamCityRestClientTests extends TesterraTest implements PropertyManagerProvider {

    /**
     * To run this test you need to create a 'local.properties' file and add the following properties:
     * tt.teamcity.test.tc.url=<teamcity-url>
     * tt.teamcity.test.tc.token=<bearer token>
     * tt.teamcity.test.tc.buildtypeid=<build type id of your build job>
     */
    @BeforeSuite
    public void initLocalProperties() {
        PROPERTY_MANAGER.loadProperties("local.properties");
    }

    @Test
    public void downloadAHistoryFileFromTeamCity() throws IOException {
        final String url = PROPERTY_MANAGER.getProperty("tt.teamcity.test.tc.url");
        final String token = PROPERTY_MANAGER.getProperty("tt.teamcity.test.tc.token");
        final String buildTypeId = PROPERTY_MANAGER.getProperty("tt.teamcity.test.tc.buildtypeid");

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
