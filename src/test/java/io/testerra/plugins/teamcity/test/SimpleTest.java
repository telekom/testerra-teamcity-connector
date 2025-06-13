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
 *       Martin Großmann
 */

package io.testerra.plugins.teamcity.test;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import nl.altindag.console.ConsoleCaptor;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple Tests for validating teamcity progess pushers
 * <p>
 * This tests should results as follows:
 * <p>
 * *** Stats: Test Methods Count: 8 (7 relevant)
 * *** Stats: Failed: 1
 * *** Stats: Retried: 1
 * *** Stats: Expected Failed: 1
 * *** Stats: Skipped: 1
 * *** Stats: Passed: 4 ? Recovered: 1
 *
 * @author Martin Großmann
 */
public class SimpleTest extends TesterraTest implements PropertyManagerProvider {

    static {
        PROPERTY_MANAGER.loadProperties("local.properties");
    }

    private static final int TEST_DURATION = 1_000;

    private static ConsoleCaptor consoleCaptor;

    @BeforeSuite
    public void beforeSuite() {
        consoleCaptor = new ConsoleCaptor();
    }

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
        Assert.fail("Failing for reasons");
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
        Assert.fail("Failing for reasons...");
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
        List<String> standardOutput = consoleCaptor.getStandardOutput();

        // Assert all printed TeamCity messages
        Assert.assertTrue(standardOutput.contains("##teamcity[progressMessage 'Test report tcc-test: Running']"));
        Assert.assertTrue(standardOutput.contains("##teamcity[progressMessage 'Test report tcc-test: 1 Passed']"));
        Assert.assertTrue(standardOutput.contains("##teamcity[progressMessage 'Test report tcc-test: 2 Passed']"));
        Assert.assertTrue(standardOutput.contains("##teamcity[progressMessage 'Test report tcc-test: 1 Failed, 2 Passed']"));
        Assert.assertTrue(standardOutput.contains("##teamcity[progressMessage 'Test report tcc-test: 1 Failed, 1 Skipped, 2 Passed']"));
        Assert.assertTrue(standardOutput.contains("##teamcity[progressMessage 'Test report tcc-test: 1 Failed, 1 Expected Failed, 1 Skipped, 2 Passed']"));
        Assert.assertTrue(standardOutput.contains("##teamcity[progressMessage 'Test report tcc-test: 1 Retried, 1 Failed, 1 Expected Failed, 1 Skipped, 2 Passed']"));
        Assert.assertTrue(standardOutput.contains("##teamcity[progressMessage 'Test report tcc-test: 1 Retried, 1 Failed, 1 Expected Failed, 1 Skipped, 3 Passed']"));

    }

}
