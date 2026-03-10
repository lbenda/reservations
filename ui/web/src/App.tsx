import { FormEvent, startTransition, useEffect, useMemo, useState } from 'react'
import './app.css'
import {
  ApiError,
  ServiceForm,
  ServiceItem,
  archiveService,
  createService,
  emptyServiceForm,
  fetchAdminServices,
  fetchPublicServices,
  updateService,
} from './api'

type FormErrors = Record<string, string>

const query = new URLSearchParams(window.location.search)

export function App() {
  const [businessId, setBusinessId] = useState(query.get('businessId') ?? '')
  const [selectedServiceId, setSelectedServiceId] = useState(query.get('service') ?? '')
  const [showArchived, setShowArchived] = useState(false)
  const [search, setSearch] = useState('')
  const [services, setServices] = useState<ServiceItem[]>([])
  const [publicServices, setPublicServices] = useState<ServiceItem[]>([])
  const [form, setForm] = useState<ServiceForm>(emptyServiceForm())
  const [editingId, setEditingId] = useState<string | null>(null)
  const [formErrors, setFormErrors] = useState<FormErrors>({})
  const [banner, setBanner] = useState<string>('')
  const [loadingAdmin, setLoadingAdmin] = useState(false)
  const [loadingPublic, setLoadingPublic] = useState(false)

  useEffect(() => {
    const nextQuery = new URLSearchParams()
    if (businessId) {
      nextQuery.set('businessId', businessId)
    }
    if (selectedServiceId) {
      nextQuery.set('service', selectedServiceId)
    }
    const nextUrl = nextQuery.toString() ? `?${nextQuery.toString()}` : window.location.pathname
    window.history.replaceState({}, '', nextUrl)
  }, [businessId, selectedServiceId])

  useEffect(() => {
    if (!businessId) {
      setServices([])
      setPublicServices([])
      return
    }
    void loadAdminServices(businessId, showArchived)
    void loadPublicServices(businessId)
  }, [businessId, showArchived])

  const filteredServices = useMemo(
    () => services.filter((service) => service.name.toLowerCase().includes(search.toLowerCase())),
    [search, services],
  )

  const selectedPublicService = publicServices.find((service) => service.id === selectedServiceId) ?? null

  async function loadAdminServices(nextBusinessId: string, includeArchived: boolean) {
    setLoadingAdmin(true)
    try {
      const nextServices = await fetchAdminServices(nextBusinessId, includeArchived)
      startTransition(() => {
        setServices(nextServices)
      })
    } catch (error) {
      setBanner(readError(error))
    } finally {
      setLoadingAdmin(false)
    }
  }

  async function loadPublicServices(nextBusinessId: string) {
    setLoadingPublic(true)
    try {
      const nextServices = await fetchPublicServices(nextBusinessId)
      startTransition(() => {
        setPublicServices(nextServices)
        if (selectedServiceId && !nextServices.some((service) => service.id === selectedServiceId)) {
          setSelectedServiceId('')
        }
      })
    } catch (error) {
      setBanner(readError(error))
    } finally {
      setLoadingPublic(false)
    }
  }

  function startEdit(service: ServiceItem) {
    setEditingId(service.id)
    setForm({
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
    setFormErrors({})
  }

  function resetForm() {
    setEditingId(null)
    setForm(emptyServiceForm())
    setFormErrors({})
  }

  function validateForm(nextForm: ServiceForm): FormErrors {
    const errors: FormErrors = {}
    if (!nextForm.name.trim()) errors.name = 'Name is required.'
    if (Number(nextForm.durationMinutes) <= 0) errors.durationMinutes = 'Duration must be greater than 0.'
    if (Number(nextForm.priceAmount) < 0) errors.priceAmount = 'Price must be greater than or equal to 0.'

    for (const field of ['bufferBeforeMinutes', 'bufferAfterMinutes', 'minAdvanceMinutes', 'maxAdvanceDays'] as const) {
      if (nextForm[field].trim() !== '' && Number(nextForm[field]) < 0) {
        errors[field] = 'Value must be greater than or equal to 0.'
      }
    }

    if (!/^[A-Za-z]{3}$/.test(nextForm.priceCurrency.trim())) {
      errors.priceCurrency = 'Currency must be a 3-letter code.'
    }
    return errors
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const nextErrors = validateForm(form)
    setFormErrors(nextErrors)
    if (Object.keys(nextErrors).length > 0 || !businessId) {
      if (!businessId) setBanner('Business ID is required before saving services.')
      return
    }

    try {
      if (editingId) {
        await updateService(businessId, editingId, form)
        setBanner('Service updated.')
      } else {
        await createService(businessId, form)
        setBanner('Service created.')
      }
      resetForm()
      await loadAdminServices(businessId, showArchived)
      await loadPublicServices(businessId)
    } catch (error) {
      applyApiErrors(error)
    }
  }

  async function handleArchive(serviceId: string) {
    if (!businessId || !window.confirm('Archive this service?')) {
      return
    }
    try {
      await archiveService(businessId, serviceId)
      setBanner('Service archived.')
      await loadAdminServices(businessId, showArchived)
      await loadPublicServices(businessId)
      if (editingId === serviceId) {
        resetForm()
      }
    } catch (error) {
      setBanner(readError(error))
    }
  }

  function applyApiErrors(error: unknown) {
    const apiError = error as ApiError
    setBanner(apiError.message ?? 'Request failed.')
    const nextErrors = Object.fromEntries((apiError.errors ?? []).map((item) => [item.field, item.message]))
    setFormErrors(nextErrors)
  }

  return (
    <main className="shell">
      <section className="hero">
        <p className="eyebrow">F-001 Service Catalog</p>
        <h1>Admin CRUD and public selection in one workspace.</h1>
        <p className="lede">
          The left side manages bookable services. The right side shows exactly what customers will see.
        </p>
        <label className="field span">
          <span>Business ID</span>
          <input value={businessId} onChange={(event) => setBusinessId(event.target.value)} placeholder="UUID from admin auth context" />
        </label>
        {banner ? <div className="banner">{banner}</div> : null}
      </section>

      <section className="grid">
        <section className="panel">
          <div className="panelHeader">
            <div>
              <p className="eyebrow">Admin</p>
              <h2>Service management</h2>
            </div>
            <label className="toggle">
              <input
                checked={showArchived}
                onChange={(event) => setShowArchived(event.target.checked)}
                type="checkbox"
              />
              Show archived
            </label>
          </div>

          <div className="toolbar">
            <input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search by name" />
            <button className="ghost" onClick={() => void loadAdminServices(businessId, showArchived)} type="button">
              Refresh
            </button>
          </div>

          <div className="list">
            {loadingAdmin ? <p className="muted">Loading services…</p> : null}
            {!loadingAdmin && filteredServices.length === 0 ? <p className="muted">No services loaded.</p> : null}
            {filteredServices.map((service) => (
              <article className="card" key={service.id}>
                <div>
                  <strong>{service.name}</strong>
                  <p>{service.durationMinutes} min · {service.priceAmount} {service.priceCurrency}</p>
                  <p className={service.isActive ? 'status active' : 'status archived'}>
                    {service.isActive ? 'Active' : 'Archived'}
                  </p>
                </div>
                <div className="actions">
                  <button onClick={() => startEdit(service)} type="button">Edit</button>
                  <button className="ghost" onClick={() => void handleArchive(service.id)} type="button">
                    Archive
                  </button>
                </div>
              </article>
            ))}
          </div>
        </section>

        <section className="panel accent">
          <div className="panelHeader">
            <div>
              <p className="eyebrow">{editingId ? 'Edit service' : 'Create service'}</p>
              <h2>{editingId ? 'Update details' : 'New bookable item'}</h2>
            </div>
            {editingId ? (
              <button className="ghost" onClick={resetForm} type="button">
                Cancel edit
              </button>
            ) : null}
          </div>

          <form className="form" onSubmit={handleSubmit}>
            <label className="field">
              <span>Name</span>
              <input value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} />
              {formErrors.name ? <small>{formErrors.name}</small> : null}
            </label>
            <label className="field">
              <span>Service code</span>
              <input value={form.serviceCode} onChange={(event) => setForm({ ...form, serviceCode: event.target.value })} />
            </label>
            <label className="field span">
              <span>Description</span>
              <textarea value={form.description} onChange={(event) => setForm({ ...form, description: event.target.value })} rows={3} />
            </label>
            <label className="field">
              <span>Duration (min)</span>
              <input value={form.durationMinutes} onChange={(event) => setForm({ ...form, durationMinutes: event.target.value })} />
              {formErrors.durationMinutes ? <small>{formErrors.durationMinutes}</small> : null}
            </label>
            <label className="field">
              <span>Price</span>
              <input value={form.priceAmount} onChange={(event) => setForm({ ...form, priceAmount: event.target.value })} />
              {formErrors.priceAmount ? <small>{formErrors.priceAmount}</small> : null}
            </label>
            <label className="field">
              <span>Currency</span>
              <input value={form.priceCurrency} onChange={(event) => setForm({ ...form, priceCurrency: event.target.value.toUpperCase() })} />
              {formErrors.priceCurrency ? <small>{formErrors.priceCurrency}</small> : null}
            </label>
            <label className="field">
              <span>Buffer before</span>
              <input value={form.bufferBeforeMinutes} onChange={(event) => setForm({ ...form, bufferBeforeMinutes: event.target.value })} />
              {formErrors.bufferBeforeMinutes ? <small>{formErrors.bufferBeforeMinutes}</small> : null}
            </label>
            <label className="field">
              <span>Buffer after</span>
              <input value={form.bufferAfterMinutes} onChange={(event) => setForm({ ...form, bufferAfterMinutes: event.target.value })} />
              {formErrors.bufferAfterMinutes ? <small>{formErrors.bufferAfterMinutes}</small> : null}
            </label>
            <label className="field">
              <span>Min advance</span>
              <input value={form.minAdvanceMinutes} onChange={(event) => setForm({ ...form, minAdvanceMinutes: event.target.value })} />
              {formErrors.minAdvanceMinutes ? <small>{formErrors.minAdvanceMinutes}</small> : null}
            </label>
            <label className="field">
              <span>Max advance days</span>
              <input value={form.maxAdvanceDays} onChange={(event) => setForm({ ...form, maxAdvanceDays: event.target.value })} />
              {formErrors.maxAdvanceDays ? <small>{formErrors.maxAdvanceDays}</small> : null}
            </label>
            <label className="field span">
              <span>Cancellation policy</span>
              <textarea
                value={form.cancellationPolicy}
                onChange={(event) => setForm({ ...form, cancellationPolicy: event.target.value })}
                rows={2}
              />
            </label>
            <label className="toggle span">
              <input checked={form.isActive} onChange={(event) => setForm({ ...form, isActive: event.target.checked })} type="checkbox" />
              Active service
            </label>
            <button className="primary span" type="submit">
              {editingId ? 'Save changes' : 'Create service'}
            </button>
          </form>
        </section>

        <section className="panel publicPanel">
          <div className="panelHeader">
            <div>
              <p className="eyebrow">Public</p>
              <h2>Booking step: choose service</h2>
            </div>
            <button className="ghost" onClick={() => void loadPublicServices(businessId)} type="button">
              Refresh
            </button>
          </div>

          {loadingPublic ? <p className="muted">Loading public catalog…</p> : null}
          <div className="publicGrid">
            {publicServices.map((service) => (
              <button
                className={`serviceTile ${selectedServiceId === service.id ? 'selected' : ''}`}
                key={service.id}
                onClick={() => setSelectedServiceId(service.id)}
                type="button"
              >
                <strong>{service.name}</strong>
                <span>{service.durationMinutes} min</span>
                <span>{service.priceAmount} {service.priceCurrency}</span>
                <small>{service.description ?? 'No description yet.'}</small>
              </button>
            ))}
          </div>
          {selectedPublicService ? (
            <div className="selection">
              Selected: {selectedPublicService.name} ({selectedPublicService.durationMinutes} min)
            </div>
          ) : (
            <div className="selection muted">Select a service to continue to date and time selection.</div>
          )}
        </section>
      </section>
    </main>
  )
}

function readError(error: unknown): string {
  const apiError = error as ApiError
  return apiError.message ?? 'Request failed.'
}
