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
package eu.tsystems.mms.tic.testerra.plugins.teamcity;

import eu.tsystems.mms.tic.testframework.common.IProperties;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.utils.FileDownloader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class TeamCityHistoryDownloader implements Loggable {

    private static final String REPORT_MODEL_DIRECTORY = "report-ng/model/";

    public enum Properties implements IProperties {
        TEAMCITY_HISTORY_DOWNLOAD("tt.teamcity.history.download.active", false),
        TEAMCITY_URL("tt.teamcity.url", ""),
        TEAMCITY_REST_TOKEN("tt.teamcity.rest.token", ""),
        TEAMCITY_BUILD_TYPE_ID("tt.teamcity.buildTypeId", ""),
        ;

        private final String property;
        private final Object defaultValue;

        Properties(String property, Object defaultValue) {
            this.property = property;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return property;
        }

        @Override
        public Object getDefault() {
            return defaultValue;
        }
    }

    public TeamCityHistoryDownloader() {

    }

    /**
     * This workflow get the history file of the latest finished build job
     * and move it to the final report directory
     */
    public void downloadHistoryFileToReport() {
        if (!Properties.TEAMCITY_HISTORY_DOWNLOAD.asBool()) {
            return;
        }

        try {
            final String historyFilePath = this.getHistoryFilePath();
            if (historyFilePath == null) {
                return;
            }
            File historyFile = this.downloadHistoryFile(historyFilePath);
            Report report = Testerra.getInjector().getInstance(Report.class);
            File finalReportDirectory = report.getFinalReportDirectory();

            // TODO: Delete old one
            FileUtils.moveFile(historyFile, new File(finalReportDirectory, REPORT_MODEL_DIRECTORY));
        } catch (IOException e) {
            log().warn("Cannot download history file to report directory: {}", e.getMessage());
        }
    }

    private String getHistoryFilePath() {
        final String teamCityUrl = Properties.TEAMCITY_URL.asString();
        final String restToken = Properties.TEAMCITY_REST_TOKEN.asString();
        final String buildTypeId = Properties.TEAMCITY_BUILD_TYPE_ID.asString();

        TeamCityRestClient client = new TeamCityRestClient(teamCityUrl, restToken);
        final String buildId = client.findLatestBuildId(buildTypeId);
        return client.getHistoryFilePath(buildId);
    }

    private File downloadHistoryFile(final String path) throws IOException {
        final String restToken = Properties.TEAMCITY_REST_TOKEN.asString();
        FileDownloader downloader = new FileDownloader();
        downloader.setConnectionConfigurator(connection -> {
            connection.setRequestProperty("Authorization", "Bearer " + restToken);
        });
        return downloader.download(path, "history");
    }


}
