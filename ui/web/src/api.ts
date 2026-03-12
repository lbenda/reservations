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

export type StaffItem = {
  id: string
  businessId: string
  locationId: string
  displayName: string
  email?: string | null
  phone?: string | null
  bio?: string | null
  status: string
}

export type StaffForm = {
  locationId: string
  displayName: string
  email: string
  phone: string
  bio: string
  status: string
}

export type WeeklyScheduleItem = {
  id: string
  staffId: string
  dayOfWeek: number
  rangeType: string
  startTime: string
  endTime: string
}

export type WeeklyScheduleForm = {
  dayOfWeek: string
  rangeType: string
  startTime: string
  endTime: string
}

export type ScheduleExceptionItem = {
  id: string
  staffId: string
  exceptionDate: string
  rangeType: string
  startTime?: string | null
  endTime?: string | null
  note?: string | null
}

export type ScheduleExceptionForm = {
  exceptionDate: string
  rangeType: string
  startTime: string
  endTime: string
  note: string
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

export const emptyStaffForm = (): StaffForm => ({
  locationId: '',
  displayName: '',
  email: '',
  phone: '',
  bio: '',
  status: 'active',
})

export const emptyWeeklyScheduleForm = (): WeeklyScheduleForm => ({
  dayOfWeek: '1',
  rangeType: 'WORK',
  startTime: '09:00',
  endTime: '17:00',
})

export const emptyScheduleExceptionForm = (): ScheduleExceptionForm => ({
  exceptionDate: '',
  rangeType: 'DAY_OFF',
  startTime: '',
  endTime: '',
  note: '',
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

export async function fetchAdminStaff(businessId: string): Promise<StaffItem[]> {
  const response = await fetch('/api/admin/staff', {
    headers: {
      'X-Business-Id': businessId,
    },
  })
  if (!response.ok) {
    throw await parseError(response)
  }
  return (await response.json()) as StaffItem[]
}

export async function createStaff(businessId: string, form: StaffForm): Promise<StaffItem> {
  const response = await fetch('/api/admin/staff', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Business-Id': businessId,
    },
    body: JSON.stringify({
      locationId: form.locationId,
      displayName: form.displayName,
      email: form.email.trim() || null,
      phone: form.phone.trim() || null,
      bio: form.bio.trim() || null,
      status: form.status,
    }),
  })
  if (!response.ok) {
    throw await parseError(response)
  }
  return (await response.json()) as StaffItem
}

export async function updateStaff(businessId: string, staffId: string, form: StaffForm): Promise<StaffItem> {
  const response = await fetch(`/api/admin/staff/${staffId}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      'X-Business-Id': businessId,
    },
    body: JSON.stringify({
      locationId: form.locationId,
      displayName: form.displayName,
      email: form.email.trim() || null,
      phone: form.phone.trim() || null,
      bio: form.bio.trim() || null,
      status: form.status,
    }),
  })
  if (!response.ok) {
    throw await parseError(response)
  }
  return (await response.json()) as StaffItem
}

export async function fetchWeeklySchedules(businessId: string, staffId: string): Promise<WeeklyScheduleItem[]> {
  const response = await fetch(`/api/admin/staff/${staffId}/weekly-schedules`, {
    headers: {
      'X-Business-Id': businessId,
    },
  })
  if (!response.ok) {
    throw await parseError(response)
  }
  return (await response.json()) as WeeklyScheduleItem[]
}

export async function createWeeklySchedule(
  businessId: string,
  staffId: string,
  form: WeeklyScheduleForm,
): Promise<WeeklyScheduleItem> {
  const response = await fetch(`/api/admin/staff/${staffId}/weekly-schedules`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Business-Id': businessId,
    },
    body: JSON.stringify({
      dayOfWeek: Number(form.dayOfWeek),
      rangeType: form.rangeType,
      startTime: form.startTime,
      endTime: form.endTime,
    }),
  })
  if (!response.ok) {
    throw await parseError(response)
  }
  return (await response.json()) as WeeklyScheduleItem
}

export async function updateWeeklySchedule(
  businessId: string,
  staffId: string,
  scheduleId: string,
  form: WeeklyScheduleForm,
): Promise<WeeklyScheduleItem> {
  const response = await fetch(`/api/admin/staff/${staffId}/weekly-schedules/${scheduleId}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      'X-Business-Id': businessId,
    },
    body: JSON.stringify({
      dayOfWeek: Number(form.dayOfWeek),
      rangeType: form.rangeType,
      startTime: form.startTime,
      endTime: form.endTime,
    }),
  })
  if (!response.ok) {
    throw await parseError(response)
  }
  return (await response.json()) as WeeklyScheduleItem
}

export async function deleteWeeklySchedule(businessId: string, staffId: string, scheduleId: string): Promise<void> {
  const response = await fetch(`/api/admin/staff/${staffId}/weekly-schedules/${scheduleId}`, {
    method: 'DELETE',
    headers: {
      'X-Business-Id': businessId,
    },
  })
  if (!response.ok) {
    throw await parseError(response)
  }
}

export async function fetchScheduleExceptions(businessId: string, staffId: string): Promise<ScheduleExceptionItem[]> {
  const response = await fetch(`/api/admin/staff/${staffId}/schedule-exceptions`, {
    headers: {
      'X-Business-Id': businessId,
    },
  })
  if (!response.ok) {
    throw await parseError(response)
  }
  return (await response.json()) as ScheduleExceptionItem[]
}

export async function createScheduleException(
  businessId: string,
  staffId: string,
  form: ScheduleExceptionForm,
): Promise<ScheduleExceptionItem> {
  const response = await fetch(`/api/admin/staff/${staffId}/schedule-exceptions`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Business-Id': businessId,
    },
    body: JSON.stringify({
      exceptionDate: form.exceptionDate,
      rangeType: form.rangeType,
      startTime: form.rangeType === 'DAY_OFF' ? null : form.startTime,
      endTime: form.rangeType === 'DAY_OFF' ? null : form.endTime,
      note: form.note.trim() || null,
    }),
  })
  if (!response.ok) {
    throw await parseError(response)
  }
  return (await response.json()) as ScheduleExceptionItem
}

export async function updateScheduleException(
  businessId: string,
  staffId: string,
  exceptionId: string,
  form: ScheduleExceptionForm,
): Promise<ScheduleExceptionItem> {
  const response = await fetch(`/api/admin/staff/${staffId}/schedule-exceptions/${exceptionId}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      'X-Business-Id': businessId,
    },
    body: JSON.stringify({
      exceptionDate: form.exceptionDate,
      rangeType: form.rangeType,
      startTime: form.rangeType === 'DAY_OFF' ? null : form.startTime,
      endTime: form.rangeType === 'DAY_OFF' ? null : form.endTime,
      note: form.note.trim() || null,
    }),
  })
  if (!response.ok) {
    throw await parseError(response)
  }
  return (await response.json()) as ScheduleExceptionItem
}

export async function deleteScheduleException(
  businessId: string,
  staffId: string,
  exceptionId: string,
): Promise<void> {
  const response = await fetch(`/api/admin/staff/${staffId}/schedule-exceptions/${exceptionId}`, {
    method: 'DELETE',
    headers: {
      'X-Business-Id': businessId,
    },
  })
  if (!response.ok) {
    throw await parseError(response)
  }
}
