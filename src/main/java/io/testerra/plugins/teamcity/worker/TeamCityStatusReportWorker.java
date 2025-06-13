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

package io.testerra.plugins.teamcity.worker;

import com.google.common.eventbus.Subscribe;
import io.testerra.plugins.teamcity.TeamCityBuildStatus;
import io.testerra.plugins.teamcity.TeamCityMessagePusher;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.events.ExecutionFinishEvent;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.ITestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.context.RunConfig;
import eu.tsystems.mms.tic.testframework.report.utils.IExecutionContextController;

/**
 * Reports global build status to teamcity
 * <p>
 * Date: 07.05.2020
 * Time: 09:23
 *
 * @author Eric Kubenka
 */
public class TeamCityStatusReportWorker implements ExecutionFinishEvent.Listener {

    private static final TeamCityMessagePusher messagePusher = new TeamCityMessagePusher();
    private static final boolean FAILURE_CORRIDOR_ACTIVE = Testerra.Properties.FAILURE_CORRIDOR_ACTIVE.asBool();

    @Override
    @Subscribe
    public void onExecutionFinish(ExecutionFinishEvent event) {
        ITestStatusController statusController = Testerra.getInjector().getInstance(ITestStatusController.class);
        RunConfig runConfig = Testerra.getInjector().getInstance(IExecutionContextController.class).getExecutionContext().getRunConfig();

        String statusMessage = runConfig.getReportName() + " " + runConfig.RUNCFG + ": ";
        statusMessage += statusController.getCounterInfoMessage() + " ";

        // There is a difference in build status depending on failure corridor active
        // If corridor is active, we have to get these values and if corridor matches, we report a success
        // If corridor is not active, we have to get the failed-test-counter. If it's equals ZERO, we can reeturn a success
        if (FAILURE_CORRIDOR_ACTIVE) {
            statusMessage += FailureCorridor.getStatusMessage();
            if (FailureCorridor.isCorridorMatched()) {
                messagePusher.updateBuildStatus(TeamCityBuildStatus.SUCCESS, statusMessage, false);
            } else {
                messagePusher.updateBuildStatus(TeamCityBuildStatus.FAILURE, statusMessage, false);
            }
        } else {
            if (statusController.getTestsFailed() == 0) {
                messagePusher.updateBuildStatus(TeamCityBuildStatus.SUCCESS, statusMessage, false);
            } else {
                messagePusher.updateBuildStatus(TeamCityBuildStatus.FAILURE, statusMessage, false);
            }
        }
    }
}
