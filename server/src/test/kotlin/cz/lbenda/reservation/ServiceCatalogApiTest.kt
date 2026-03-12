package cz.lbenda.reservation

import cz.lbenda.reservation.catalog.SampleCatalogFixtures
import cz.lbenda.reservation.db.TestDatabase
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
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
class ServiceCatalogApiTest {
    private val json = Json { ignoreUnknownKeys = true }

    @BeforeEach
    fun reset() {
        TestDatabase.resetTenantAccess()
        SampleCatalogFixtures.createBusiness(TestDatabase.dsl)
    }

    @AfterAll
    fun closeDatabase() {
        TestDatabase.close()
    }

    @Test
    fun `admin api supports create list get update and archive`() = testApplication {
        application {
            module(
                serviceCatalogService = TestServiceFactory.serviceCatalogService(),
                staffManagementService = TestServiceFactory.staffManagementService()
            )
        }

        val createResponse = client.post("/api/admin/services") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "serviceCode": "SVC-100",
                  "name": "Massage 60",
                  "description": "Relaxing massage",
                  "durationMinutes": 60,
                  "bufferBeforeMinutes": 5,
                  "bufferAfterMinutes": 10,
                  "minAdvanceMinutes": 120,
                  "maxAdvanceDays": 30,
                  "cancellationPolicy": "24 hours notice",
                  "priceAmount": "1200.00",
                  "priceCurrency": "CZK",
                  "isActive": true
                }
                """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.Created, createResponse.status)
        val createdJson = json.parseToJsonElement(createResponse.bodyAsText()).jsonObject
        val serviceId = createdJson["id"]!!.jsonPrimitive.content

        val listResponse = client.get("/api/admin/services") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
        }
        assertEquals(HttpStatusCode.OK, listResponse.status)
        val listJson = json.parseToJsonElement(listResponse.bodyAsText()).jsonArray
        assertEquals(1, listJson.size)

        val getResponse = client.get("/api/admin/services/$serviceId") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
        }
        assertEquals(HttpStatusCode.OK, getResponse.status)

        val patchResponse = client.patch("/api/admin/services/$serviceId") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "serviceCode": "SVC-200",
                  "name": "Massage 90",
                  "description": "Deep tissue massage",
                  "durationMinutes": 90,
                  "bufferBeforeMinutes": 10,
                  "bufferAfterMinutes": 15,
                  "minAdvanceMinutes": 60,
                  "maxAdvanceDays": 14,
                  "cancellationPolicy": "12 hours notice",
                  "priceAmount": "1800.00",
                  "priceCurrency": "CZK",
                  "isActive": true
                }
                """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.OK, patchResponse.status)
        assertTrue(patchResponse.bodyAsText().contains("Massage 90"))

        val archiveResponse = client.post("/api/admin/services/$serviceId/archive") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
        }
        assertEquals(HttpStatusCode.OK, archiveResponse.status)
        assertTrue(archiveResponse.bodyAsText().contains("\"isActive\":false"))

        val activeListResponse = client.get("/api/admin/services?isActive=true") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
        }
        assertEquals(HttpStatusCode.OK, activeListResponse.status)
        assertEquals(0, json.parseToJsonElement(activeListResponse.bodyAsText()).jsonArray.size)
    }

    @Test
    fun `admin api enforces business scoping and public api returns only active services`() = testApplication {
        application {
            module(
                serviceCatalogService = TestServiceFactory.serviceCatalogService(),
                staffManagementService = TestServiceFactory.staffManagementService()
            )
        }

        val createResponse = client.post("/api/admin/services") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "serviceCode": "SVC-100",
                  "name": "Massage 60",
                  "description": "Relaxing massage",
                  "durationMinutes": 60,
                  "priceAmount": "1200.00",
                  "priceCurrency": "CZK",
                  "isActive": true
                }
                """.trimIndent()
            )
        }
        val serviceId = json.parseToJsonElement(createResponse.bodyAsText()).jsonObject["id"]!!.jsonPrimitive.content

        val otherBusinessResponse = client.get("/api/admin/services/$serviceId") {
            header("X-Business-Id", "00000000-0000-0000-0000-000000000999")
        }
        assertEquals(HttpStatusCode.NotFound, otherBusinessResponse.status)

        client.post("/api/admin/services/$serviceId/archive") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
        }

        val publicResponse = client.get("/api/public/services?businessId=${SampleCatalogFixtures.businessId}") {
            header(HttpHeaders.Accept, ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.OK, publicResponse.status)
        assertEquals(0, json.parseToJsonElement(publicResponse.bodyAsText()).jsonArray.size)
    }

    @Test
    fun `admin api returns validation errors`() = testApplication {
        application {
            module(
                serviceCatalogService = TestServiceFactory.serviceCatalogService(),
                staffManagementService = TestServiceFactory.staffManagementService()
            )
        }

        val response = client.post("/api/admin/services") {
            header("X-Business-Id", SampleCatalogFixtures.businessId.toString())
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "serviceCode": "SVC-100",
                  "name": "",
                  "description": "Relaxing massage",
                  "durationMinutes": 0,
                  "priceAmount": "-1.00",
                  "priceCurrency": "BAD",
                  "isActive": true
                }
                """.trimIndent()
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("Validation failed"))
        assertTrue(body.contains("durationMinutes"))
        assertTrue(body.contains("priceAmount"))
    }
}
