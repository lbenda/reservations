package cz.lbenda.reservation.catalog

import java.math.BigDecimal
import java.util.UUID

data class ServiceCommand(
    val serviceCode: String?,
    val name: String,
    val description: String?,
    val durationMinutes: Int,
    val bufferBeforeMinutes: Int?,
    val bufferAfterMinutes: Int?,
    val minAdvanceMinutes: Int?,
    val maxAdvanceDays: Int?,
    val cancellationPolicy: String?,
    val priceAmount: BigDecimal,
    val priceCurrency: String,
    val isActive: Boolean
)

data class ServiceValidationError(
    val field: String,
    val message: String
)

class ServiceValidationException(
    val errors: List<ServiceValidationError>
) : IllegalArgumentException("Service validation failed")

interface ServiceCatalogService {
    fun create(businessId: UUID, command: ServiceCommand): Service
    fun get(businessId: UUID, serviceId: UUID): Service?
    fun list(businessId: UUID, isActive: Boolean? = null): List<Service>
    fun update(businessId: UUID, serviceId: UUID, command: ServiceCommand): Service?
    fun archive(businessId: UUID, serviceId: UUID): Service?
}

class DefaultServiceCatalogService(
    private val repository: ServiceRepository
) : ServiceCatalogService {
    override fun create(businessId: UUID, command: ServiceCommand): Service {
        validate(command)
        return repository.create(
            NewService(
                id = UUID.randomUUID(),
                businessId = businessId,
                serviceCode = command.serviceCode,
                name = command.name.trim(),
                description = command.description?.trim()?.ifBlank { null },
                durationMinutes = command.durationMinutes,
                bufferBeforeMinutes = command.bufferBeforeMinutes,
                bufferAfterMinutes = command.bufferAfterMinutes,
                minAdvanceMinutes = command.minAdvanceMinutes,
                maxAdvanceDays = command.maxAdvanceDays,
                cancellationPolicy = command.cancellationPolicy?.trim()?.ifBlank { null },
                priceAmount = command.priceAmount,
                priceCurrency = command.priceCurrency.trim().uppercase(),
                isActive = command.isActive
            )
        )
    }

    override fun get(businessId: UUID, serviceId: UUID): Service? =
        repository.findById(businessId, serviceId)

    override fun list(businessId: UUID, isActive: Boolean?): List<Service> =
        repository.listByBusiness(businessId, isActive)

    override fun update(businessId: UUID, serviceId: UUID, command: ServiceCommand): Service? {
        validate(command)
        return repository.update(
            businessId = businessId,
            id = serviceId,
            update = ServiceUpdate(
                serviceCode = command.serviceCode,
                name = command.name.trim(),
                description = command.description?.trim()?.ifBlank { null },
                durationMinutes = command.durationMinutes,
                bufferBeforeMinutes = command.bufferBeforeMinutes,
                bufferAfterMinutes = command.bufferAfterMinutes,
                minAdvanceMinutes = command.minAdvanceMinutes,
                maxAdvanceDays = command.maxAdvanceDays,
                cancellationPolicy = command.cancellationPolicy?.trim()?.ifBlank { null },
                priceAmount = command.priceAmount,
                priceCurrency = command.priceCurrency.trim().uppercase(),
                isActive = command.isActive
            )
        )
    }

    override fun archive(businessId: UUID, serviceId: UUID): Service? =
        repository.archive(businessId, serviceId)

    private fun validate(command: ServiceCommand) {
        val errors = buildList {
            if (command.name.isBlank()) {
                add(ServiceValidationError("name", "must not be blank"))
            }
            if (command.durationMinutes <= 0) {
                add(ServiceValidationError("durationMinutes", "must be greater than 0"))
            }
            if ((command.bufferBeforeMinutes ?: 0) < 0) {
                add(ServiceValidationError("bufferBeforeMinutes", "must be greater than or equal to 0"))
            }
            if ((command.bufferAfterMinutes ?: 0) < 0) {
                add(ServiceValidationError("bufferAfterMinutes", "must be greater than or equal to 0"))
            }
            if ((command.minAdvanceMinutes ?: 0) < 0) {
                add(ServiceValidationError("minAdvanceMinutes", "must be greater than or equal to 0"))
            }
            if ((command.maxAdvanceDays ?: 0) < 0) {
                add(ServiceValidationError("maxAdvanceDays", "must be greater than or equal to 0"))
            }
            if (command.priceAmount < BigDecimal.ZERO) {
                add(ServiceValidationError("priceAmount", "must be greater than or equal to 0"))
            }
            if (!command.priceCurrency.matches(Regex("^[A-Za-z]{3}$"))) {
                add(ServiceValidationError("priceCurrency", "must be a 3-letter ISO 4217 code"))
            }
        }

        if (errors.isNotEmpty()) {
            throw ServiceValidationException(errors)
        }
    }
}
