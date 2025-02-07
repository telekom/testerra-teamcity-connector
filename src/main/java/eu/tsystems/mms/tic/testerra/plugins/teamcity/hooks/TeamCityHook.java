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
package eu.tsystems.mms.tic.testerra.plugins.teamcity.hooks;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import eu.tsystems.mms.tic.testerra.plugins.teamcity.history.TeamCityHistoryDownloader;
import eu.tsystems.mms.tic.testerra.plugins.teamcity.listener.TeamCityEventListener;
import eu.tsystems.mms.tic.testerra.plugins.teamcity.worker.TeamCityStatusReportWorker;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.hooks.ModuleHook;

/**
 * Registering Workers for pushing status messages to teamcity while running a testerra test job
 * <p>
 * Date: 07.05.2020
 * Time: 08:24
 *
 * @author Eric Kubenka
 */
public class TeamCityHook extends AbstractModule implements ModuleHook {

    @Override
    public void init() {
        EventBus eventBus = Testerra.getEventBus();
        eventBus.register(new TeamCityEventListener());
        eventBus.register(new TeamCityStatusReportWorker());

        new TeamCityHistoryDownloader().downloadHistoryFileToReport();
    }

    @Override
    public void terminate() {
        // nothing to here
    }
}
