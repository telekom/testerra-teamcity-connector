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
package io.testerra.plugins.teamcity.history;

import io.testerra.plugins.teamcity.restapi.TeamCityRestClient;
import eu.tsystems.mms.tic.testframework.common.IProperties;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.utils.FileDownloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class TeamCityHistoryDownloader implements Loggable {

    private static final String REPORT_MODEL_DIRECTORY = "report-ng/model/";
    private static final String HISTORY_FILENAME = "history";

    public enum Properties implements IProperties {
        TEAMCITY_HISTORY_DOWNLOAD("tt.teamcity.history.download.active", false),
        TEAMCITY_URL("tt.teamcity.url", ""),
        TEAMCITY_REST_TOKEN("tt.teamcity.rest.token", ""),
        TEAMCITY_BUILD_TYPE_ID("tt.teamcity.buildTypeId", ""),

        // Define the type or name of the branch from which the last history file should download
        // all = all branches
        // default = only default branch
        // <any other> = value is used as a branch name
        TEAMCITY_BUILD_BRANCH("tt.teamcity.build.branch", "all"),

        // If the artifacts of a build job do not contain the history file, the history downloader will check the next 'n' build jobs
        TEAMCITY_LAST_BUILD_COUNT("tt.teamcity.build.count", "3"),
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

    /**
     * This workflow get the history file of the latest finished build job
     * and move it to the final report directory
     */
    public void downloadHistoryFileToReport() {
        if (!Properties.TEAMCITY_HISTORY_DOWNLOAD.asBool()) {
            return;
        }

        log().info("Trying to download the Report History file of the last TeamCity build...");

        try {
            final String historyFilePath = this.getHistoryFilePath();
            if (historyFilePath == null) {
                return;
            }
            Path historyFile = this.downloadHistoryFile(historyFilePath);
            Report report = Testerra.getInjector().getInstance(Report.class);
            Path finalModelDirectory = report.getFinalReportDirectory().resolve(REPORT_MODEL_DIRECTORY);
            Path finalHistoryFile = finalModelDirectory.resolve(HISTORY_FILENAME);
            if (Files.exists(finalHistoryFile) && !Files.isDirectory(finalHistoryFile)) {
                Files.delete(finalHistoryFile);
            }
            Files.createDirectories(finalModelDirectory);
            Files.move(historyFile, finalHistoryFile, StandardCopyOption.REPLACE_EXISTING);
            log().info("History file moved to {}", finalHistoryFile.toAbsolutePath());
        } catch (Throwable e) {
            log().warn("Cannot download history file to report directory: {}: {}", e.getClass(), e.getMessage());
        }
    }

    private String getHistoryFilePath() {
        final String teamCityUrl = Properties.TEAMCITY_URL.asString();
        final String restToken = Properties.TEAMCITY_REST_TOKEN.asString();
        final String buildTypeId = Properties.TEAMCITY_BUILD_TYPE_ID.asString();
        final String branchType = Properties.TEAMCITY_BUILD_BRANCH.asString();
        final String count = Properties.TEAMCITY_LAST_BUILD_COUNT.asString();

        TeamCityRestClient client = new TeamCityRestClient(teamCityUrl, restToken);
        TeamCityReadHistoryHelper helper = new TeamCityReadHistoryHelper(client);
        List<Integer> latestBuildIds = helper.findLatestBuildIds(buildTypeId, branchType, count);
        return helper.getHistoryFilePath(latestBuildIds);
    }

    private Path downloadHistoryFile(final String path) throws IOException {
        final String restToken = Properties.TEAMCITY_REST_TOKEN.asString();
        final String teamCityUrl = Properties.TEAMCITY_URL.asString();
        FileDownloader downloader = new FileDownloader();
        downloader.setConnectionConfigurator(connection -> {
            connection.setRequestProperty("Authorization", "Bearer " + restToken);
        });
        Path tempFile = Files.createTempFile(null, "_" + HISTORY_FILENAME);
        return downloader.download(teamCityUrl + path, tempFile.getFileName().toString()).toPath();
    }


}
