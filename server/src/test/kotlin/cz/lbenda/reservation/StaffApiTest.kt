package cz.lbenda.reservation

import cz.lbenda.reservation.catalog.SampleCatalogFixtures
import cz.lbenda.reservation.db.TestDatabase
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StaffApiTest {
    private val json = Json { ignoreUnknownKeys = true }

    @BeforeEach
    fun reset() {
        TestDatabase.resetTenantAccess()
        SampleCatalogFixtures.createBusiness(TestDatabase.dsl)
        SampleCatalogFixtures.createLocation(TestDatabase.dsl)
    }

    @AfterAll
    fun closeDatabase() {
        TestDatabase.close()
    }

    @Test
    fun `admin api supports staff create list get update schedules and exceptions`() = testApplication {
        application {
            module(
                serviceCatalogService = TestServiceFactory.serviceCatalogService(),
                staffManagementService = TestServiceFactory.staffManagementService()
            )
        }

        val createResponse = client.post("/api/admin/staff") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "locationId": "${SampleCatalogFixtures.locationId}",
                  "displayName": "Eva Staff",
                  "email": "eva@acme.test",
                  "phone": "+420123456789",
                  "bio": "Senior therapist",
                  "status": "active"
                }
                """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.Created, createResponse.status)
        val staffId = json.parseToJsonElement(createResponse.bodyAsText()).jsonObject["id"]!!.jsonPrimitive.content

        val listResponse = client.get("/api/admin/staff") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
        }
        assertEquals(HttpStatusCode.OK, listResponse.status)
        assertEquals(1, json.parseToJsonElement(listResponse.bodyAsText()).jsonArray.size)

        val getResponse = client.get("/api/admin/staff/$staffId") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
        }
        assertEquals(HttpStatusCode.OK, getResponse.status)

        val patchResponse = client.patch("/api/admin/staff/$staffId") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "locationId": "${SampleCatalogFixtures.locationId}",
                  "displayName": "Eva Lead",
                  "email": "eva.lead@acme.test",
                  "phone": "+420987654321",
                  "bio": "Lead therapist",
                  "status": "inactive"
                }
                """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.OK, patchResponse.status)
        assertTrue(patchResponse.bodyAsText().contains("Eva Lead"))

        val createScheduleResponse = client.post("/api/admin/staff/$staffId/weekly-schedules") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "dayOfWeek": 1,
                  "rangeType": "WORK",
                  "startTime": "09:00",
                  "endTime": "17:00"
                }
                """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.Created, createScheduleResponse.status)
        val scheduleId = json.parseToJsonElement(createScheduleResponse.bodyAsText()).jsonObject["id"]!!.jsonPrimitive.content

        val listSchedulesResponse = client.get("/api/admin/staff/$staffId/weekly-schedules") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
        }
        assertEquals(HttpStatusCode.OK, listSchedulesResponse.status)
        assertEquals(1, json.parseToJsonElement(listSchedulesResponse.bodyAsText()).jsonArray.size)

        val updateScheduleResponse = client.patch("/api/admin/staff/$staffId/weekly-schedules/$scheduleId") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "dayOfWeek": 1,
                  "rangeType": "BREAK",
                  "startTime": "12:00",
                  "endTime": "13:00"
                }
                """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.OK, updateScheduleResponse.status)
        assertTrue(updateScheduleResponse.bodyAsText().contains("\"rangeType\":\"BREAK\""))

        val createExceptionResponse = client.post("/api/admin/staff/$staffId/schedule-exceptions") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "exceptionDate": "2026-03-20",
                  "rangeType": "DAY_OFF",
                  "note": "Vacation"
                }
                """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.Created, createExceptionResponse.status)
        val exceptionId = json.parseToJsonElement(createExceptionResponse.bodyAsText()).jsonObject["id"]!!.jsonPrimitive.content

        val listExceptionsResponse = client.get("/api/admin/staff/$staffId/schedule-exceptions") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
        }
        assertEquals(HttpStatusCode.OK, listExceptionsResponse.status)
        assertEquals(1, json.parseToJsonElement(listExceptionsResponse.bodyAsText()).jsonArray.size)

        val updateExceptionResponse = client.patch("/api/admin/staff/$staffId/schedule-exceptions/$exceptionId") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "exceptionDate": "2026-03-21",
                  "rangeType": "WORK",
                  "startTime": "10:00",
                  "endTime": "14:00",
                  "note": "Saturday shift"
                }
                """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.OK, updateExceptionResponse.status)
        assertTrue(updateExceptionResponse.bodyAsText().contains("\"rangeType\":\"WORK\""))

        val deleteScheduleResponse = client.delete("/api/admin/staff/$staffId/weekly-schedules/$scheduleId") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
        }
        assertEquals(HttpStatusCode.NoContent, deleteScheduleResponse.status)

        val deleteExceptionResponse = client.delete("/api/admin/staff/$staffId/schedule-exceptions/$exceptionId") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
        }
        assertEquals(HttpStatusCode.NoContent, deleteExceptionResponse.status)
    }

    @Test
    fun `admin api returns staff validation errors`() = testApplication {
        application {
            module(
                serviceCatalogService = TestServiceFactory.serviceCatalogService(),
                staffManagementService = TestServiceFactory.staffManagementService()
            )
        }

        val response = client.post("/api/admin/staff") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "locationId": "${SampleCatalogFixtures.locationId}",
                  "displayName": "",
                  "email": "not-an-email",
                  "status": "broken"
                }
                """.trimIndent()
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("Validation failed"))
        assertTrue(body.contains("displayName"))
        assertTrue(body.contains("status"))
    }
}
