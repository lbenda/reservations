export type ServiceItem = {
  id: string
  businessId: string
  serviceCode?: string | null
  name: string
  description?: string | null
  durationMinutes: number
  bufferBeforeMinutes?: number | null
  bufferAfterMinutes?: number | null
  minAdvanceMinutes?: number | null
  maxAdvanceDays?: number | null
  cancellationPolicy?: string | null
  priceAmount: string
  priceCurrency: string
  isActive: boolean
}

export type ServiceForm = {
  serviceCode: string
  name: string
  description: string
  durationMinutes: string
  bufferBeforeMinutes: string
  bufferAfterMinutes: string
  minAdvanceMinutes: string
  maxAdvanceDays: string
  cancellationPolicy: string
  priceAmount: string
  priceCurrency: string
  isActive: boolean
}

export type ApiError = {
  message: string
  errors?: Array<{ field: string; message: string }>
}

export const emptyServiceForm = (): ServiceForm => ({
  serviceCode: '',
  name: '',
  description: '',
  durationMinutes: '60',
  bufferBeforeMinutes: '',
  bufferAfterMinutes: '',
  minAdvanceMinutes: '',
  maxAdvanceDays: '',
  cancellationPolicy: '',
  priceAmount: '0.00',
  priceCurrency: 'CZK',
  isActive: true,
})

const parseError = async (response: Response): Promise<ApiError> => {
  try {
    return (await response.json()) as ApiError
  } catch {
    return { message: `Request failed with status ${response.status}` }
  }
}

const asNumber = (value: string): number | null =>
  value.trim() === '' ? null : Number(value)

const toPayload = (form: ServiceForm) => ({
  serviceCode: form.serviceCode.trim() || null,
  name: form.name,
  description: form.description.trim() || null,
  durationMinutes: Number(form.durationMinutes),
  bufferBeforeMinutes: asNumber(form.bufferBeforeMinutes),
  bufferAfterMinutes: asNumber(form.bufferAfterMinutes),
  minAdvanceMinutes: asNumber(form.minAdvanceMinutes),
  maxAdvanceDays: asNumber(form.maxAdvanceDays),
  cancellationPolicy: form.cancellationPolicy.trim() || null,
  priceAmount: form.priceAmount,
  priceCurrency: form.priceCurrency,
  isActive: form.isActive,
})

export async function fetchAdminServices(businessId: string, includeArchived: boolean): Promise<ServiceItem[]> {
  const query = includeArchived ? '' : '?isActive=true'
  const response = await fetch(`/api/admin/services${query}`, {
    headers: {
      'X-Business-Id': businessId,
    },
  })
  if (!response.ok) {
    throw await parseError(response)
  }
  return (await response.json()) as ServiceItem[]
}

export async function createService(businessId: string, form: ServiceForm): Promise<ServiceItem> {
  const response = await fetch('/api/admin/services', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Business-Id': businessId,
    },
    body: JSON.stringify(toPayload(form)),
  })
  if (!response.ok) {
    throw await parseError(response)
  }
  return (await response.json()) as ServiceItem
}

export async function updateService(
  businessId: string,
  serviceId: string,
  form: ServiceForm,
): Promise<ServiceItem> {
  const response = await fetch(`/api/admin/services/${serviceId}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      'X-Business-Id': businessId,
    },
    body: JSON.stringify(toPayload(form)),
  })
  if (!response.ok) {
    throw await parseError(response)
  }
  return (await response.json()) as ServiceItem
}

export async function archiveService(businessId: string, serviceId: string): Promise<ServiceItem> {
  const response = await fetch(`/api/admin/services/${serviceId}/archive`, {
    method: 'POST',
    headers: {
      'X-Business-Id': businessId,
    },
  })
  if (!response.ok) {
    throw await parseError(response)
  }
  return (await response.json()) as ServiceItem
}

export async function fetchPublicServices(businessId: string): Promise<ServiceItem[]> {
  const response = await fetch(`/api/public/services?businessId=${businessId}`)
  if (!response.ok) {
    throw await parseError(response)
  }
  return (await response.json()) as ServiceItem[]
}
