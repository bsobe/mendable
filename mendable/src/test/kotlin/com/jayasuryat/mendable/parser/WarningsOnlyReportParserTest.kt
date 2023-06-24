/*
 * Copyright 2023 Jaya Surya Thotapalli
 *
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
package com.jayasuryat.mendable.parser

import com.google.gson.GsonBuilder
import com.jayasuryat.mendable.getExpectedJsonForWarningReport
import com.jayasuryat.mendable.getReportFromTestFile
import com.jayasuryat.mendable.model.ComposablesReport
import com.jayasuryat.mendable.model.ComposeMetricFile
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.math.roundToInt

class WarningsOnlyReportParserTest {

    private val parser = WarningsOnlyReportParser(
        backingParser = ComposableReportParser()
    )

    @Test
    fun `ComposableReportParser should parse all composables correctly`() {
        // Read and get report from app_release-composables.txt
        val mapped: List<ComposeMetricFile> = getReportFromTestFile()

        // Parsing the mapped values, this is the part being tested
        val report: ComposablesReport = parser.parse(mapped)

        val overview = report.overview
        val expectedTotalComposablesCount = 17
        val expectedRestartableCount = 13
        val expectedSkippableCount = 12
        val expectedPercentage = ((expectedSkippableCount * 100f) / expectedRestartableCount).roundToInt()
        val expectedTotalModuleCount = 2
        val expectedReportedModuleCount = 1
        val expectedSkippedModuleCount = expectedTotalModuleCount - expectedReportedModuleCount

        Assertions.assertEquals(overview.totalComposables, expectedTotalComposablesCount)
        Assertions.assertEquals(overview.restartableComposables, expectedRestartableCount)
        Assertions.assertEquals(overview.skippableComposables, expectedSkippableCount)
        Assertions.assertEquals(overview.skippablePercentage, expectedPercentage)
        Assertions.assertEquals(report.totalModulesScanned, expectedTotalModuleCount)
        Assertions.assertEquals(report.totalModulesReported, expectedReportedModuleCount)
        Assertions.assertEquals(report.totalModulesFiltered, expectedSkippedModuleCount)

        val expectedJson = getExpectedJsonForWarningReport()

        val actual = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(report)
        Assertions.assertEquals(expectedJson, actual)
    }
}