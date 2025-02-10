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
 *
 * Contributors:
 *      Eric Kubenka
 */

package io.testerra.plugins.teamcity.listener;

import com.google.common.eventbus.Subscribe;

import io.testerra.plugins.teamcity.TeamCityMessagePusher;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.events.TestStatusUpdateEvent;
import eu.tsystems.mms.tic.testframework.report.ITestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.context.RunConfig;
import eu.tsystems.mms.tic.testframework.report.utils.IExecutionContextController;
import org.apache.commons.lang3.StringUtils;

/**
 * Listener to react for method context update events
 * <p>
 * Date: 07.05.2020
 * Time: 09:01
 *
 * @author Eric Kubenka
 */
public class TeamCityEventListener implements TestStatusUpdateEvent.Listener {

    private static final TeamCityMessagePusher messagePusher = new TeamCityMessagePusher();

    @Override
    @Subscribe
    public void onTestStatusUpdate(TestStatusUpdateEvent event) {
        ITestStatusController statusController = Testerra.getInjector().getInstance(ITestStatusController.class);
        IExecutionContextController executionContextController = Testerra.getInjector().getInstance(IExecutionContextController.class);

        String counterInfoMessage = statusController.getCounterInfoMessage();

        if (StringUtils.isBlank(counterInfoMessage)) {
            counterInfoMessage = "Running";
        }

        RunConfig runConfig = executionContextController.getExecutionContext().getRunConfig();
        final String teamCityMessage = runConfig.getReportName() + " " + runConfig.RUNCFG + ": " + counterInfoMessage;
        messagePusher.updateProgressMessage(teamCityMessage);
    }
}
