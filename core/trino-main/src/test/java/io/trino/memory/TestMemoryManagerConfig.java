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
 */
package io.trino.memory;

import com.google.common.collect.ImmutableMap;
import io.airlift.units.DataSize;
import io.airlift.units.Duration;
import org.testng.annotations.Test;

import java.util.Map;

import static io.airlift.configuration.testing.ConfigAssertions.assertFullMapping;
import static io.airlift.configuration.testing.ConfigAssertions.assertRecordedDefaults;
import static io.airlift.configuration.testing.ConfigAssertions.recordDefaults;
import static io.airlift.units.DataSize.Unit.GIGABYTE;
import static io.trino.memory.MemoryManagerConfig.LowMemoryKillerPolicy.NONE;
import static io.trino.memory.MemoryManagerConfig.LowMemoryKillerPolicy.TOTAL_RESERVATION_ON_BLOCKED_NODES;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TestMemoryManagerConfig
{
    @Test
    public void testDefaults()
    {
        assertRecordedDefaults(recordDefaults(MemoryManagerConfig.class)
                .setLowMemoryKillerPolicy(TOTAL_RESERVATION_ON_BLOCKED_NODES)
                .setKillOnOutOfMemoryDelay(new Duration(5, MINUTES))
                .setMaxQueryMemory(DataSize.of(20, GIGABYTE))
                .setMaxQueryTotalMemory(DataSize.of(40, GIGABYTE))
                .setFaultTolerantExecutionTaskMemory(DataSize.of(4, GIGABYTE)));
    }

    @Test
    public void testExplicitPropertyMappings()
    {
        Map<String, String> properties = ImmutableMap.<String, String>builder()
                .put("query.low-memory-killer.policy", "none")
                .put("query.low-memory-killer.delay", "20s")
                .put("query.max-memory", "2GB")
                .put("query.max-total-memory", "3GB")
                .put("fault-tolerant-execution-task-memory", "2GB")
                .buildOrThrow();

        MemoryManagerConfig expected = new MemoryManagerConfig()
                .setLowMemoryKillerPolicy(NONE)
                .setKillOnOutOfMemoryDelay(new Duration(20, SECONDS))
                .setMaxQueryMemory(DataSize.of(2, GIGABYTE))
                .setMaxQueryTotalMemory(DataSize.of(3, GIGABYTE))
                .setFaultTolerantExecutionTaskMemory(DataSize.of(2, GIGABYTE));

        assertFullMapping(properties, expected);
    }
}
