package cz.lbenda.reservation

import cz.lbenda.reservation.booking.BookingRepository
import cz.lbenda.reservation.booking.NewBooking
import cz.lbenda.reservation.catalog.NewService
import cz.lbenda.reservation.catalog.NewStaff
import cz.lbenda.reservation.catalog.NewStaffService
import cz.lbenda.reservation.catalog.NewStaffWeeklySchedule
import cz.lbenda.reservation.catalog.SampleCatalogFixtures
import cz.lbenda.reservation.catalog.ServiceRepository
import cz.lbenda.reservation.catalog.StaffRepository
import cz.lbenda.reservation.catalog.StaffScheduleRangeType
import cz.lbenda.reservation.catalog.StaffServiceRepository
import cz.lbenda.reservation.catalog.StaffWeeklyScheduleRepository
import cz.lbenda.reservation.clients.ClientRepository
import cz.lbenda.reservation.clients.NewClient
import cz.lbenda.reservation.db.TestDatabase
import cz.lbenda.reservation.util.Uuid7
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AvailabilityApiTest {
    private val json = Json { ignoreUnknownKeys = true }
    private val dsl = TestDatabase.dsl
    private val serviceRepository = ServiceRepository(dsl)
    private val staffRepository = StaffRepository(dsl)
    private val staffServiceRepository = StaffServiceRepository(dsl)
    private val staffWeeklyScheduleRepository = StaffWeeklyScheduleRepository(dsl)
    private val clientRepository = ClientRepository(dsl)
    private val bookingRepository = BookingRepository(dsl)

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
    fun `public availability returns slot list for assigned staff`() = testApplication {
        application {
            module(
                serviceCatalogService = TestServiceFactory.serviceCatalogService(),
                staffManagementService = TestServiceFactory.staffManagementService(),
                availabilitySlotService = TestServiceFactory.availabilitySlotService()
            )
        }

        val service = serviceRepository.create(
            NewService(
                id = Uuid7.new(),
                businessId = SampleCatalogFixtures.businessId,
                serviceCode = "SVC-AVAIL",
                name = "Massage 30",
                description = null,
                durationMinutes = 30,
                bufferBeforeMinutes = 0,
                bufferAfterMinutes = 0,
                minAdvanceMinutes = 0,
                maxAdvanceDays = 30,
                cancellationPolicy = null,
                priceAmount = BigDecimal("1200.00"),
                priceCurrency = "CZK",
                isActive = true
            )
        )
        val staff = staffRepository.create(
            NewStaff(
                id = Uuid7.new(),
                businessId = SampleCatalogFixtures.businessId,
                locationId = SampleCatalogFixtures.locationId,
                displayName = "Eva Staff",
                email = "eva@acme.test",
                phone = null,
                bio = null,
                status = "active"
            )
        )
        staffServiceRepository.create(
            NewStaffService(
                id = Uuid7.new(),
                staffId = staff.id,
                serviceId = service.id,
                staffServiceKey = null,
                isActive = true
            )
        )
        staffWeeklyScheduleRepository.create(
            NewStaffWeeklySchedule(
                id = Uuid7.new(),
                staffId = staff.id,
                dayOfWeek = 1,
                rangeType = StaffScheduleRangeType.WORK,
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(11, 0)
            )
        )
        val bookingClient = clientRepository.create(
            NewClient(
                id = Uuid7.new(),
                businessId = SampleCatalogFixtures.businessId,
                email = "client@acme.test",
                phone = null,
                firstName = "Alice",
                lastName = "Client",
                locale = "cs-CZ",
                notes = null,
                status = "active"
            )
        )
        bookingRepository.create(
            NewBooking(
                id = Uuid7.new(),
                businessId = SampleCatalogFixtures.businessId,
                locationId = SampleCatalogFixtures.locationId,
                serviceId = service.id,
                staffId = staff.id,
                clientId = bookingClient.id,
                publicRef = "BK-API",
                status = "confirmed",
                startAt = OffsetDateTime.of(2026, 3, 23, 10, 0, 0, 0, ZoneOffset.UTC),
                endAt = OffsetDateTime.of(2026, 3, 23, 10, 30, 0, 0, ZoneOffset.UTC),
                timezone = "UTC",
                priceAmount = BigDecimal("1200.00"),
                priceCurrency = "CZK",
                notes = null,
                clientMessage = null
            )
        )

        val response = client.get(
            "/api/public/availability" +
                "?businessId=${SampleCatalogFixtures.businessId}" +
                "&serviceId=${service.id}" +
                "&startDate=${LocalDate.of(2026, 3, 23)}" +
                "&endDate=${LocalDate.of(2026, 3, 23)}" +
                "&timezone=UTC" +
                "&slotIntervalMinutes=30"
        )

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.parseToJsonElement(response.bodyAsText()).jsonArray
        assertEquals(3, body.size)
        assertEquals("2026-03-23T09:00Z", body[0].jsonObject["startAt"]!!.jsonPrimitive.content)
        assertEquals("2026-03-23T09:30Z", body[1].jsonObject["startAt"]!!.jsonPrimitive.content)
        assertEquals("2026-03-23T10:30Z", body[2].jsonObject["startAt"]!!.jsonPrimitive.content)
        assertTrue(body.all { it.jsonObject["staffId"]!!.jsonPrimitive.content == staff.id.toString() })
    }

    @Test
    fun `public availability validates required query params`() = testApplication {
        application {
            module(
                serviceCatalogService = TestServiceFactory.serviceCatalogService(),
                staffManagementService = TestServiceFactory.staffManagementService(),
                availabilitySlotService = TestServiceFactory.availabilitySlotService()
            )
        }

        val response = client.get("/api/public/availability?businessId=${SampleCatalogFixtures.businessId}")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("serviceId"))
    }
}
