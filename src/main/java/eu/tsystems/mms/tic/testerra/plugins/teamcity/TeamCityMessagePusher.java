/*
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
 *
 * Contributors:
 *      Eric Kubenka
 */
package eu.tsystems.mms.tic.testerra.plugins.teamcity;

import eu.tsystems.mms.tic.testframework.logging.Loggable;

/**
 * Provide methods to push teamcity log messages in a defined way.
 * <p>
 * Date: 07.05.2020
 * Time: 08:29
 *
 * @author Eric Kubenka
 */
public class TeamCityMessagePusher implements Loggable {

    public void updateProgressMessage(final String message) {

        final String teamCityMessage = "##teamcity[progressMessage '" + message + "']";
        System.out.println(teamCityMessage);
    }

    public void updateBuildStatus(final TeamCityBuildStatus optionalBuildStatus, final String message, final boolean enhance) {

        String teamCityMessage = "##teamcity[buildStatus ";
        if (optionalBuildStatus != null) {
            teamCityMessage += "status='" + optionalBuildStatus.name() + "' ";
        }

        teamCityMessage += "text='";
        if (enhance) {
            teamCityMessage += "{build.status.text} ";
        }
        teamCityMessage += message + "']";
        log().info(teamCityMessage);
        System.out.println(teamCityMessage);
    }


}
