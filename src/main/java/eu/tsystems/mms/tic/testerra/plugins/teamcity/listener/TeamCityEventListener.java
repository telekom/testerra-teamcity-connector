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

package eu.tsystems.mms.tic.testerra.plugins.teamcity.listener;

import com.google.common.eventbus.Subscribe;
import static eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController.getCurrentExecutionContext;
import eu.tsystems.mms.tic.testerra.plugins.teamcity.TeamCityMessagePusher;
import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SynchronizableContext;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;

/**
 * Listener to react for method context update events
 * <p>
 * Date: 07.05.2020
 * Time: 09:01
 *
 * @author Eric Kubenka
 */
public class TeamCityEventListener implements ContextUpdateEvent.Listener {

    private static final TeamCityMessagePusher messagePusher = new TeamCityMessagePusher();

    @Override
    @Subscribe
    public void onContextUpdate(ContextUpdateEvent event) {
        final SynchronizableContext contextData = event.getContext();

        if (contextData instanceof MethodContext) {
            String counterInfoMessage = TestStatusController.getCounterInfoMessage();

            if (StringUtils.isStringEmpty(counterInfoMessage)) {
                counterInfoMessage = "Running";
            }

            final String teamCityMessage =
                    getCurrentExecutionContext().runConfig.getReportName() + " " + getCurrentExecutionContext().runConfig.RUNCFG + ": " +
                            counterInfoMessage;
            messagePusher.updateProgressMessage(teamCityMessage);
        }
    }
}
