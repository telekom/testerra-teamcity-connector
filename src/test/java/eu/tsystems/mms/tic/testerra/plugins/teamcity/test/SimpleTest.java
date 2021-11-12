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
 *       Eric Kubenka
 */

package eu.tsystems.mms.tic.testerra.plugins.teamcity.test;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.model.context.LogMessage;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Simple Tests for validating teamcity progess pushers
 * <p>
 * Date: 07.05.2020
 * Time: 09:40
 *
 * @author Eric Kubenka
 */
public class SimpleTest extends TesterraTest {

    private static final int TEST_DURATION = 1_000;
    private static final boolean FAIL_TESTS = PropertyManager.getBooleanProperty("test.execution.fail.tests", true);
    private static final boolean FAIL_EXPECTED_TESTS = PropertyManager.getBooleanProperty("test.execution.fail.expected.tests", true);

    @Test(priority = 1)
    public void testT01_SimplePassedTest() {
        TimerUtils.sleep(TEST_DURATION);
        Assert.assertTrue(true);
    }

    @Test(priority = 2)
    public void testT02_SimplePassedTest() {
        TimerUtils.sleep(TEST_DURATION);
        Assert.assertTrue(true);
    }

    @Test(priority = 3)
    public void testT03_SimpleFailedTest() {
        TimerUtils.sleep(TEST_DURATION);
        if (FAIL_TESTS) {
            Assert.fail("Failing for reasons");
        }
    }

    @Test(dependsOnMethods = {"testT03_SimpleFailedTest"}, priority = 4)
    public void testT04_SkippedTest() {
        TimerUtils.sleep(TEST_DURATION);
        Assert.assertTrue(true);
    }

    @Test(priority = 5)
    @Fails(ticketString = "ticket1", description = "Failing for reasons")
    public void testT05_SimpleExpectedFailedTest() {
        TimerUtils.sleep(TEST_DURATION);
        if (FAIL_EXPECTED_TESTS) {
            Assert.fail("Failing for reasons...");
        }
    }

    AtomicInteger counter = new AtomicInteger(0);

    @Test(priority = 6)
    public void testT06_RetriedTestSecondRunPassed() {
        this.counter.incrementAndGet();
        if (counter.get() == 1) {
            // Message is already defined in test.properties
            Assert.assertTrue(false, "testT05_RetriedTestSecondRunPassed");
        } else {
            Assert.assertTrue(true);
        }

    }

    @Test(priority = 999)
    public void test07_VerifyTeamCityMessages() {
        Stream<LogMessage> logMessageStream = ExecutionContextController.getCurrentExecutionContext().readMethodContextLessLogs();
        List<LogMessage> collect = logMessageStream.collect(Collectors.toList());
    }

}
