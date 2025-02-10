/*
 * Testerra
 *
 * (C) 2025, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
package io.testerra.plugins.teamcity.restapi;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TeamCityRestResponseParser {

    private static final Logger log = LoggerFactory.getLogger(TeamCityRestResponseParser.class);

    private TeamCityRestResponseParser() {
    }

    public static List<Integer> getBuildIdList(final String json) {
        return parseElements(json, "$..id", Integer.class);
    }

    public static String getCountOfBuilds(final String json) {
        return parseElement(json, "$['count']");
    }

    public static String readPathOfHistory(final String json) {
        return parseElement(json, "$['file'][?(@['name'] == 'history')]['content']['href']");
    }

    public static String parseElement(final String json, final String jsonPath) {
        try {
            DocumentContext jsonContext = JsonPath.parse(json);
            Object value = jsonContext.read(jsonPath);
            if (value instanceof List && !((List) value).isEmpty()) {
                return ((List<Object>) value).get(0).toString();
            }
            return value.toString();
        } catch (Exception e) {
            log.error("Error parsing json element: {}", e.getMessage());
            return "";
        }
    }

    public static <T> List<T> parseElements(final String json, final String jsonPath, Class<T> clazz) {
        try {
            DocumentContext jsonContext = JsonPath.parse(json);
            return jsonContext.read(jsonPath);
        } catch (Exception e) {
            log.error("Error parsing json element: {}", e.getMessage());
            return List.of();
        }
    }

}
