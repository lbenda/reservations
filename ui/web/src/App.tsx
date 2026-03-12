import { FormEvent, useEffect, useMemo, useState } from 'react'
import './app.css'
import {
  ApiError,
  ScheduleExceptionForm,
  ScheduleExceptionItem,
  ServiceForm,
  ServiceItem,
  StaffForm,
  StaffItem,
  WeeklyScheduleForm,
  WeeklyScheduleItem,
  archiveService,
  createScheduleException,
  createService,
  createStaff,
  createWeeklySchedule,
  deleteScheduleException,
  deleteWeeklySchedule,
  emptyScheduleExceptionForm,
  emptyServiceForm,
  emptyStaffForm,
  emptyWeeklyScheduleForm,
  fetchAdminServices,
  fetchAdminStaff,
  fetchPublicServices,
  fetchScheduleExceptions,
  fetchWeeklySchedules,
  updateScheduleException,
  updateService,
  updateStaff,
  updateWeeklySchedule,
} from './api'

type FormErrors = Record<string, string>

const query = new URLSearchParams(window.location.search)
const weekdays = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']

export function App() {
  const [businessId, setBusinessId] = useState(query.get('businessId') ?? '')
  const [selectedServiceId, setSelectedServiceId] = useState(query.get('service') ?? '')
  const [showArchived, setShowArchived] = useState(false)
  const [serviceSearch, setServiceSearch] = useState('')
  const [services, setServices] = useState<ServiceItem[]>([])
  const [publicServices, setPublicServices] = useState<ServiceItem[]>([])
  const [serviceForm, setServiceForm] = useState<ServiceForm>(emptyServiceForm())
  const [editingServiceId, setEditingServiceId] = useState<string | null>(null)
  const [serviceErrors, setServiceErrors] = useState<FormErrors>({})
  const [staff, setStaff] = useState<StaffItem[]>([])
  const [staffForm, setStaffForm] = useState<StaffForm>(emptyStaffForm())
  const [editingStaffId, setEditingStaffId] = useState<string | null>(null)
  const [selectedStaffId, setSelectedStaffId] = useState<string | null>(null)
  const [staffErrors, setStaffErrors] = useState<FormErrors>({})
  const [weeklySchedules, setWeeklySchedules] = useState<WeeklyScheduleItem[]>([])
  const [scheduleForm, setScheduleForm] = useState<WeeklyScheduleForm>(emptyWeeklyScheduleForm())
  const [editingScheduleId, setEditingScheduleId] = useState<string | null>(null)
  const [scheduleErrors, setScheduleErrors] = useState<FormErrors>({})
  const [exceptions, setExceptions] = useState<ScheduleExceptionItem[]>([])
  const [exceptionForm, setExceptionForm] = useState<ScheduleExceptionForm>(emptyScheduleExceptionForm())
  const [editingExceptionId, setEditingExceptionId] = useState<string | null>(null)
  const [exceptionErrors, setExceptionErrors] = useState<FormErrors>({})
  const [banner, setBanner] = useState('')

  useEffect(() => {
    const nextQuery = new URLSearchParams()
    if (businessId) nextQuery.set('businessId', businessId)
    if (selectedServiceId) nextQuery.set('service', selectedServiceId)
    window.history.replaceState({}, '', nextQuery.toString() ? `?${nextQuery.toString()}` : window.location.pathname)
  }, [businessId, selectedServiceId])

  useEffect(() => {
    if (!businessId) {
      setServices([])
      setPublicServices([])
      setStaff([])
      setSelectedStaffId(null)
      setWeeklySchedules([])
      setExceptions([])
      return
    }
    void refreshServices(businessId, showArchived)
    void refreshPublicServices(businessId)
    void refreshStaff(businessId)
  }, [businessId, showArchived])

  useEffect(() => {
    if (!businessId || !selectedStaffId) {
      setWeeklySchedules([])
      setExceptions([])
      return
    }
    void refreshStaffDetails(businessId, selectedStaffId)
  }, [businessId, selectedStaffId])

  const selectedPublicService = publicServices.find((service) => service.id === selectedServiceId) ?? null
  const filteredServices = useMemo(
    () => services.filter((service) => service.name.toLowerCase().includes(serviceSearch.toLowerCase())),
    [serviceSearch, services],
  )
  const selectedStaff = staff.find((item) => item.id === selectedStaffId) ?? null

  async function refreshServices(nextBusinessId: string, includeArchived: boolean) {
    try {
      setServices(await fetchAdminServices(nextBusinessId, includeArchived))
    } catch (error) {
      setBanner(readError(error))
    }
  }

  async function refreshPublicServices(nextBusinessId: string) {
    try {
      const nextServices = await fetchPublicServices(nextBusinessId)
      setPublicServices(nextServices)
      if (selectedServiceId && !nextServices.some((service) => service.id === selectedServiceId)) {
        setSelectedServiceId('')
      }
    } catch (error) {
      setBanner(readError(error))
    }
  }

  async function refreshStaff(nextBusinessId: string) {
    try {
      const items = await fetchAdminStaff(nextBusinessId)
      setStaff(items)
      if (items.length === 0) {
        setSelectedStaffId(null)
      } else if (!selectedStaffId || !items.some((item) => item.id === selectedStaffId)) {
        setSelectedStaffId(items[0].id)
      }
    } catch (error) {
      setBanner(readError(error))
    }
  }

  async function refreshStaffDetails(nextBusinessId: string, staffId: string) {
    try {
      const [schedules, scheduleExceptions] = await Promise.all([
        fetchWeeklySchedules(nextBusinessId, staffId),
        fetchScheduleExceptions(nextBusinessId, staffId),
      ])
      setWeeklySchedules(schedules)
      setExceptions(scheduleExceptions)
    } catch (error) {
      setBanner(readError(error))
    }
  }

  async function handleServiceSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const errors = validateServiceForm(serviceForm)
    setServiceErrors(errors)
    if (Object.keys(errors).length > 0 || !businessId) return
    try {
      if (editingServiceId) {
        await updateService(businessId, editingServiceId, serviceForm)
        setBanner('Service updated.')
      } else {
        await createService(businessId, serviceForm)
        setBanner('Service created.')
      }
      setEditingServiceId(null)
      setServiceForm(emptyServiceForm())
      await refreshServices(businessId, showArchived)
      await refreshPublicServices(businessId)
    } catch (error) {
      applyApiErrors(error, setServiceErrors, setBanner)
    }
  }

  async function handleArchive(serviceId: string) {
    if (!businessId || !window.confirm('Archive this service?')) return
    try {
      await archiveService(businessId, serviceId)
      setBanner('Service archived.')
      await refreshServices(businessId, showArchived)
      await refreshPublicServices(businessId)
    } catch (error) {
      setBanner(readError(error))
    }
  }

  async function handleStaffSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const errors = validateStaffForm(staffForm)
    setStaffErrors(errors)
    if (Object.keys(errors).length > 0 || !businessId) return
    try {
      const saved = editingStaffId
        ? await updateStaff(businessId, editingStaffId, staffForm)
        : await createStaff(businessId, staffForm)
      setBanner(editingStaffId ? 'Staff updated.' : 'Staff created.')
      setEditingStaffId(null)
      setStaffForm(emptyStaffForm())
      await refreshStaff(businessId)
      setSelectedStaffId(saved.id)
    } catch (error) {
      applyApiErrors(error, setStaffErrors, setBanner)
    }
  }

  async function handleScheduleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!businessId || !selectedStaffId) return
    const errors = validateScheduleForm(scheduleForm)
    setScheduleErrors(errors)
    if (Object.keys(errors).length > 0) return
    try {
      if (editingScheduleId) {
        await updateWeeklySchedule(businessId, selectedStaffId, editingScheduleId, scheduleForm)
      } else {
        await createWeeklySchedule(businessId, selectedStaffId, scheduleForm)
      }
      setBanner(editingScheduleId ? 'Weekly schedule updated.' : 'Weekly schedule added.')
      setEditingScheduleId(null)
      setScheduleForm(emptyWeeklyScheduleForm())
      await refreshStaffDetails(businessId, selectedStaffId)
    } catch (error) {
      applyApiErrors(error, setScheduleErrors, setBanner)
    }
  }

  async function handleExceptionSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!businessId || !selectedStaffId) return
    const errors = validateExceptionForm(exceptionForm)
    setExceptionErrors(errors)
    if (Object.keys(errors).length > 0) return
    try {
      if (editingExceptionId) {
        await updateScheduleException(businessId, selectedStaffId, editingExceptionId, exceptionForm)
      } else {
        await createScheduleException(businessId, selectedStaffId, exceptionForm)
      }
      setBanner(editingExceptionId ? 'Schedule exception updated.' : 'Schedule exception added.')
      setEditingExceptionId(null)
      setExceptionForm(emptyScheduleExceptionForm())
      await refreshStaffDetails(businessId, selectedStaffId)
    } catch (error) {
      applyApiErrors(error, setExceptionErrors, setBanner)
    }
  }

  async function handleScheduleDelete(id: string) {
    if (!businessId || !selectedStaffId) return
    try {
      await deleteWeeklySchedule(businessId, selectedStaffId, id)
      setBanner('Weekly schedule deleted.')
      await refreshStaffDetails(businessId, selectedStaffId)
    } catch (error) {
      setBanner(readError(error))
    }
  }

  async function handleExceptionDelete(id: string) {
    if (!businessId || !selectedStaffId) return
    try {
      await deleteScheduleException(businessId, selectedStaffId, id)
      setBanner('Schedule exception deleted.')
      await refreshStaffDetails(businessId, selectedStaffId)
    } catch (error) {
      setBanner(readError(error))
    }
  }

  return (
    <main className="shell">
      <section className="hero">
        <p className="eyebrow">F-002 Staff & Working Hours</p>
        <h1>Admin CRUD for services and staff schedules in one workspace.</h1>
        <p className="lede">Services remain visible for booking, while the lower workspace manages staff and their availability rules.</p>
        <label className="field span">
          <span>Business ID</span>
          <input value={businessId} onChange={(event) => setBusinessId(event.target.value)} placeholder="UUID from admin auth context" />
        </label>
        {banner ? <div className="banner">{banner}</div> : null}
      </section>

      <section className="grid">
        <section className="panel">
          <div className="panelHeader">
            <div><p className="eyebrow">Admin</p><h2>Service management</h2></div>
            <label className="toggle"><input checked={showArchived} onChange={(event) => setShowArchived(event.target.checked)} type="checkbox" />Show archived</label>
          </div>
          <div className="toolbar">
            <input value={serviceSearch} onChange={(event) => setServiceSearch(event.target.value)} placeholder="Search by name" />
            <button className="ghost" onClick={() => void refreshServices(businessId, showArchived)} type="button">Refresh</button>
          </div>
          <div className="list">
            {filteredServices.map((service) => (
              <article className="card" key={service.id}>
                <div>
                  <strong>{service.name}</strong>
                  <p>{service.durationMinutes} min · {service.priceAmount} {service.priceCurrency}</p>
                </div>
                <div className="actions">
                  <button onClick={() => {
                    setEditingServiceId(service.id)
                    setServiceForm({
                      serviceCode: service.serviceCode ?? '',
                      name: service.name,
                      description: service.description ?? '',
                      durationMinutes: String(service.durationMinutes),
                      bufferBeforeMinutes: service.bufferBeforeMinutes == null ? '' : String(service.bufferBeforeMinutes),
                      bufferAfterMinutes: service.bufferAfterMinutes == null ? '' : String(service.bufferAfterMinutes),
                      minAdvanceMinutes: service.minAdvanceMinutes == null ? '' : String(service.minAdvanceMinutes),
                      maxAdvanceDays: service.maxAdvanceDays == null ? '' : String(service.maxAdvanceDays),
                      cancellationPolicy: service.cancellationPolicy ?? '',
                      priceAmount: service.priceAmount,
                      priceCurrency: service.priceCurrency,
                      isActive: service.isActive,
                    })
                  }} type="button">Edit</button>
                  <button className="ghost" onClick={() => void handleArchive(service.id)} type="button">Archive</button>
                </div>
              </article>
            ))}
          </div>
        </section>

        <section className="panel accent">
          <div className="panelHeader">
            <div><p className="eyebrow">{editingServiceId ? 'Edit service' : 'Create service'}</p><h2>{editingServiceId ? 'Update details' : 'New bookable item'}</h2></div>
          </div>
          <ServiceFormPanel form={serviceForm} errors={serviceErrors} onChange={setServiceForm} onSubmit={handleServiceSubmit} />
        </section>

        <section className="panel publicPanel">
          <div className="panelHeader">
            <div><p className="eyebrow">Public</p><h2>Booking step: choose service</h2></div>
            <button className="ghost" onClick={() => void refreshPublicServices(businessId)} type="button">Refresh</button>
          </div>
          <div className="publicGrid">
            {publicServices.map((service) => (
              <button className={`serviceTile ${selectedServiceId === service.id ? 'selected' : ''}`} key={service.id} onClick={() => setSelectedServiceId(service.id)} type="button">
                <strong>{service.name}</strong>
                <span>{service.durationMinutes} min</span>
                <span>{service.priceAmount} {service.priceCurrency}</span>
              </button>
            ))}
          </div>
          <div className="selection">{selectedPublicService ? `Selected: ${selectedPublicService.name}` : 'Select a service to continue.'}</div>
        </section>
      </section>

      <section className="staffGrid">
        <section className="panel">
          <div className="panelHeader">
            <div><p className="eyebrow">Admin</p><h2>Staff</h2></div>
            <button className="ghost" onClick={() => void refreshStaff(businessId)} type="button">Refresh</button>
          </div>
          <div className="list">
            {staff.map((item) => (
              <article className={`card ${selectedStaffId === item.id ? 'selectedCard' : ''}`} key={item.id}>
                <button className="cardSelect" onClick={() => setSelectedStaffId(item.id)} type="button">
                  <strong>{item.displayName}</strong>
                  <p>{item.status} · {item.email ?? 'No email'}</p>
                </button>
                <button onClick={() => {
                  setEditingStaffId(item.id)
                  setStaffForm({
                    locationId: item.locationId,
                    displayName: item.displayName,
                    email: item.email ?? '',
                    phone: item.phone ?? '',
                    bio: item.bio ?? '',
                    status: item.status,
                  })
                }} type="button">Edit</button>
              </article>
            ))}
          </div>
        </section>

        <section className="panel accent">
          <div className="panelHeader">
            <div><p className="eyebrow">{editingStaffId ? 'Edit staff' : 'Create staff'}</p><h2>{editingStaffId ? 'Update details' : 'New team member'}</h2></div>
          </div>
          <StaffFormPanel form={staffForm} errors={staffErrors} onChange={setStaffForm} onSubmit={handleStaffSubmit} />
        </section>

        <section className="panel publicPanel">
          <div className="panelHeader">
            <div><p className="eyebrow">Schedules</p><h2>{selectedStaff?.displayName ?? 'Select staff'}</h2></div>
          </div>
          {selectedStaff ? <p className="muted">Status: {selectedStaff.status}</p> : <p className="muted">Choose a staff member to manage hours.</p>}
          <div className="subgrid">
            <section className="stack">
              <h3>Weekly schedule</h3>
              <WeeklyScheduleFormPanel form={scheduleForm} errors={scheduleErrors} onChange={setScheduleForm} onSubmit={handleScheduleSubmit} />
              <div className="list">
                {weeklySchedules.map((item) => (
                  <article className="card" key={item.id}>
                    <div>
                      <strong>{weekdays[item.dayOfWeek - 1]} · {item.rangeType}</strong>
                      <p>{formatTime(item.startTime)}-{formatTime(item.endTime)}</p>
                    </div>
                    <div className="actions">
                      <button onClick={() => {
                        setEditingScheduleId(item.id)
                        setScheduleForm({
                          dayOfWeek: String(item.dayOfWeek),
                          rangeType: item.rangeType,
                          startTime: item.startTime.slice(0, 5),
                          endTime: item.endTime.slice(0, 5),
                        })
                      }} type="button">Edit</button>
                      <button className="ghost" onClick={() => void handleScheduleDelete(item.id)} type="button">Delete</button>
                    </div>
                  </article>
                ))}
              </div>
            </section>

            <section className="stack">
              <h3>Schedule exceptions</h3>
              <ExceptionFormPanel form={exceptionForm} errors={exceptionErrors} onChange={setExceptionForm} onSubmit={handleExceptionSubmit} />
              <div className="list">
                {exceptions.map((item) => (
                  <article className="card" key={item.id}>
                    <div>
                      <strong>{item.exceptionDate} · {item.rangeType}</strong>
                      <p>{item.rangeType === 'DAY_OFF' ? 'Full day off' : `${formatTime(item.startTime)}-${formatTime(item.endTime)}`}</p>
                      <p>{item.note ?? 'No note'}</p>
                    </div>
                    <div className="actions">
                      <button onClick={() => {
                        setEditingExceptionId(item.id)
                        setExceptionForm({
                          exceptionDate: item.exceptionDate,
                          rangeType: item.rangeType,
                          startTime: item.startTime?.slice(0, 5) ?? '',
                          endTime: item.endTime?.slice(0, 5) ?? '',
                          note: item.note ?? '',
                        })
                      }} type="button">Edit</button>
                      <button className="ghost" onClick={() => void handleExceptionDelete(item.id)} type="button">Delete</button>
                    </div>
                  </article>
                ))}
              </div>
            </section>
          </div>
        </section>
      </section>
    </main>
  )
}

function ServiceFormPanel(props: {
  form: ServiceForm
  errors: FormErrors
  onChange: (form: ServiceForm) => void
  onSubmit: (event: FormEvent<HTMLFormElement>) => void
}) {
  const { form, errors, onChange, onSubmit } = props
  return (
    <form className="form" onSubmit={onSubmit}>
      <label className="field"><span>Name</span><input value={form.name} onChange={(event) => onChange({ ...form, name: event.target.value })} />{errors.name ? <small>{errors.name}</small> : null}</label>
      <label className="field"><span>Service code</span><input value={form.serviceCode} onChange={(event) => onChange({ ...form, serviceCode: event.target.value })} /></label>
      <label className="field span"><span>Description</span><textarea value={form.description} onChange={(event) => onChange({ ...form, description: event.target.value })} rows={3} /></label>
      <label className="field"><span>Duration</span><input value={form.durationMinutes} onChange={(event) => onChange({ ...form, durationMinutes: event.target.value })} />{errors.durationMinutes ? <small>{errors.durationMinutes}</small> : null}</label>
      <label className="field"><span>Price</span><input value={form.priceAmount} onChange={(event) => onChange({ ...form, priceAmount: event.target.value })} />{errors.priceAmount ? <small>{errors.priceAmount}</small> : null}</label>
      <label className="field"><span>Currency</span><input value={form.priceCurrency} onChange={(event) => onChange({ ...form, priceCurrency: event.target.value.toUpperCase() })} />{errors.priceCurrency ? <small>{errors.priceCurrency}</small> : null}</label>
      <label className="toggle span"><input checked={form.isActive} onChange={(event) => onChange({ ...form, isActive: event.target.checked })} type="checkbox" />Active service</label>
      <button className="primary span" type="submit">Save service</button>
    </form>
  )
}

function StaffFormPanel(props: {
  form: StaffForm
  errors: FormErrors
  onChange: (form: StaffForm) => void
  onSubmit: (event: FormEvent<HTMLFormElement>) => void
}) {
  const { form, errors, onChange, onSubmit } = props
  return (
    <form className="form" onSubmit={onSubmit}>
      <label className="field"><span>Display name</span><input value={form.displayName} onChange={(event) => onChange({ ...form, displayName: event.target.value })} />{errors.displayName ? <small>{errors.displayName}</small> : null}</label>
      <label className="field"><span>Location ID</span><input value={form.locationId} onChange={(event) => onChange({ ...form, locationId: event.target.value })} />{errors.locationId ? <small>{errors.locationId}</small> : null}</label>
      <label className="field"><span>Email</span><input value={form.email} onChange={(event) => onChange({ ...form, email: event.target.value })} />{errors.email ? <small>{errors.email}</small> : null}</label>
      <label className="field"><span>Phone</span><input value={form.phone} onChange={(event) => onChange({ ...form, phone: event.target.value })} /></label>
      <label className="field"><span>Status</span><select value={form.status} onChange={(event) => onChange({ ...form, status: event.target.value })}><option value="active">active</option><option value="inactive">inactive</option></select>{errors.status ? <small>{errors.status}</small> : null}</label>
      <label className="field span"><span>Bio</span><textarea value={form.bio} onChange={(event) => onChange({ ...form, bio: event.target.value })} rows={3} /></label>
      <button className="primary span" type="submit">Save staff</button>
    </form>
  )
}

function WeeklyScheduleFormPanel(props: {
  form: WeeklyScheduleForm
  errors: FormErrors
  onChange: (form: WeeklyScheduleForm) => void
  onSubmit: (event: FormEvent<HTMLFormElement>) => void
}) {
  const { form, errors, onChange, onSubmit } = props
  return (
    <form className="miniForm" onSubmit={onSubmit}>
      <label className="field"><span>Day</span><select value={form.dayOfWeek} onChange={(event) => onChange({ ...form, dayOfWeek: event.target.value })}>{weekdays.map((day, index) => <option key={day} value={String(index + 1)}>{day}</option>)}</select>{errors.dayOfWeek ? <small>{errors.dayOfWeek}</small> : null}</label>
      <label className="field"><span>Type</span><select value={form.rangeType} onChange={(event) => onChange({ ...form, rangeType: event.target.value })}><option value="WORK">WORK</option><option value="BREAK">BREAK</option></select></label>
      <label className="field"><span>Start</span><input type="time" value={form.startTime} onChange={(event) => onChange({ ...form, startTime: event.target.value })} />{errors.startTime ? <small>{errors.startTime}</small> : null}</label>
      <label className="field"><span>End</span><input type="time" value={form.endTime} onChange={(event) => onChange({ ...form, endTime: event.target.value })} />{errors.endTime ? <small>{errors.endTime}</small> : null}</label>
      <button className="primary span" type="submit">Save weekly schedule</button>
    </form>
  )
}

function ExceptionFormPanel(props: {
  form: ScheduleExceptionForm
  errors: FormErrors
  onChange: (form: ScheduleExceptionForm) => void
  onSubmit: (event: FormEvent<HTMLFormElement>) => void
}) {
  const { form, errors, onChange, onSubmit } = props
  return (
    <form className="miniForm" onSubmit={onSubmit}>
      <label className="field"><span>Date</span><input type="date" value={form.exceptionDate} onChange={(event) => onChange({ ...form, exceptionDate: event.target.value })} />{errors.exceptionDate ? <small>{errors.exceptionDate}</small> : null}</label>
      <label className="field"><span>Type</span><select value={form.rangeType} onChange={(event) => onChange({ ...form, rangeType: event.target.value })}><option value="DAY_OFF">DAY_OFF</option><option value="WORK">WORK</option><option value="BREAK">BREAK</option></select></label>
      <label className="field"><span>Start</span><input type="time" disabled={form.rangeType === 'DAY_OFF'} value={form.startTime} onChange={(event) => onChange({ ...form, startTime: event.target.value })} />{errors.startTime ? <small>{errors.startTime}</small> : null}</label>
      <label className="field"><span>End</span><input type="time" disabled={form.rangeType === 'DAY_OFF'} value={form.endTime} onChange={(event) => onChange({ ...form, endTime: event.target.value })} />{errors.endTime ? <small>{errors.endTime}</small> : null}</label>
      <label className="field span"><span>Note</span><input value={form.note} onChange={(event) => onChange({ ...form, note: event.target.value })} /></label>
      <button className="primary span" type="submit">Save exception</button>
    </form>
  )
}

function validateServiceForm(nextForm: ServiceForm): FormErrors {
  const errors: FormErrors = {}
  if (!nextForm.name.trim()) errors.name = 'Name is required.'
  if (Number(nextForm.durationMinutes) <= 0) errors.durationMinutes = 'Duration must be greater than 0.'
  if (Number(nextForm.priceAmount) < 0) errors.priceAmount = 'Price must be greater than or equal to 0.'
  if (!/^[A-Za-z]{3}$/.test(nextForm.priceCurrency.trim())) errors.priceCurrency = 'Currency must be a 3-letter code.'
  return errors
}

function validateStaffForm(nextForm: StaffForm): FormErrors {
  const errors: FormErrors = {}
  if (!nextForm.locationId.trim()) errors.locationId = 'Location ID is required.'
  if (!nextForm.displayName.trim()) errors.displayName = 'Display name is required.'
  if (nextForm.email.trim() && !nextForm.email.includes('@')) errors.email = 'Enter a valid email.'
  return errors
}

function validateScheduleForm(nextForm: WeeklyScheduleForm): FormErrors {
  const errors: FormErrors = {}
  if (Number(nextForm.dayOfWeek) < 1 || Number(nextForm.dayOfWeek) > 7) errors.dayOfWeek = 'Use 1-7.'
  if (!nextForm.startTime) errors.startTime = 'Start time is required.'
  if (!nextForm.endTime) errors.endTime = 'End time is required.'
  if (nextForm.startTime && nextForm.endTime && nextForm.startTime >= nextForm.endTime) errors.endTime = 'End time must be later than start time.'
  return errors
}

function validateExceptionForm(nextForm: ScheduleExceptionForm): FormErrors {
  const errors: FormErrors = {}
  if (!nextForm.exceptionDate) errors.exceptionDate = 'Date is required.'
  if (nextForm.rangeType !== 'DAY_OFF') {
    if (!nextForm.startTime) errors.startTime = 'Start time is required.'
    if (!nextForm.endTime) errors.endTime = 'End time is required.'
    if (nextForm.startTime && nextForm.endTime && nextForm.startTime >= nextForm.endTime) errors.endTime = 'End time must be later than start time.'
  }
  return errors
}

function applyApiErrors(
  error: unknown,
  setErrors: (errors: FormErrors) => void,
  setBanner: (value: string) => void,
) {
  const apiError = error as ApiError
  setBanner(apiError.message ?? 'Request failed.')
  setErrors(Object.fromEntries((apiError.errors ?? []).map((item) => [item.field, item.message])))
}

function readError(error: unknown): string {
  const apiError = error as ApiError
  return apiError.message ?? 'Request failed.'
}

function formatTime(value?: string | null): string {
  return value ? value.slice(0, 5) : '--:--'
}
