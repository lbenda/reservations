import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { App } from './App'

const activeService = {
  id: 'svc-1',
  businessId: 'business-1',
  serviceCode: 'SVC-1',
  name: 'Massage 60',
  description: 'Relaxing massage',
  durationMinutes: 60,
  bufferBeforeMinutes: 5,
  bufferAfterMinutes: 10,
  minAdvanceMinutes: 120,
  maxAdvanceDays: 30,
  cancellationPolicy: '24 hours notice',
  priceAmount: '1200.00',
  priceCurrency: 'CZK',
  isActive: true,
}

const archivedService = {
  ...activeService,
  id: 'svc-2',
  name: 'Massage 90',
  isActive: false,
}

const staffMember = {
  id: 'staff-1',
  businessId: 'business-1',
  locationId: 'location-1',
  displayName: 'Eva Staff',
  email: 'eva@acme.test',
  phone: '+420123456789',
  bio: 'Senior therapist',
  status: 'active',
}

const weeklySchedule = {
  id: 'schedule-1',
  staffId: 'staff-1',
  dayOfWeek: 1,
  rangeType: 'WORK',
  startTime: '09:00:00',
  endTime: '17:00:00',
}

const dayOffException = {
  id: 'exception-1',
  staffId: 'staff-1',
  exceptionDate: '2026-03-20',
  rangeType: 'DAY_OFF',
  startTime: null,
  endTime: null,
  note: 'Vacation',
}

describe('App', () => {
  beforeEach(() => {
    window.history.replaceState({}, '', '/')
    window.confirm = vi.fn(() => true)
  })

  it('creates, edits and archives a service while loading staff workspace', async () => {
    const services = [{ ...activeService }]
    const staff = [{ ...staffMember }]
    const schedules = [{ ...weeklySchedule }]
    const exceptions = [{ ...dayOffException }]

    const fetchMock = vi.fn(async (input: RequestInfo | URL, init?: RequestInit) => {
      const url = String(input)
      const method = init?.method ?? 'GET'

      if (url === '/api/admin/staff' && method === 'GET') return okJson(staff)
      if (url === '/api/admin/staff/staff-1/weekly-schedules' && method === 'GET') return okJson(schedules)
      if (url === '/api/admin/staff/staff-1/schedule-exceptions' && method === 'GET') return okJson(exceptions)

      if (url.startsWith('/api/admin/services') && method === 'GET') {
        return okJson(services.filter((service) => !url.includes('isActive=true') || service.isActive))
      }
      if (url === '/api/public/services?businessId=business-1') {
        return okJson(services.filter((service) => service.isActive))
      }
      if (url === '/api/admin/services' && method === 'POST') {
        return okJson(services[0], 201)
      }
      if (url === '/api/admin/services/svc-1' && method === 'PATCH') {
        services[0] = { ...services[0], name: 'Massage 75' }
        return okJson(services[0])
      }
      if (url === '/api/admin/services/svc-1/archive' && method === 'POST') {
        services[0] = { ...services[0], isActive: false }
        return okJson(services[0])
      }

      return okJson([])
    })
    vi.stubGlobal('fetch', fetchMock)

    render(<App />)
    const user = userEvent.setup()

    await user.click(screen.getByPlaceholderText('UUID from admin auth context'))
    await user.keyboard('business-1')

    await waitFor(() => expect(fetchMock).toHaveBeenCalled())

    await user.clear(screen.getByLabelText('Name'))
    await user.type(screen.getByLabelText('Name'), 'Massage 60')
    await user.click(screen.getByRole('button', { name: 'Save service' }))

    await waitFor(() => expect(screen.getByText('Service created.')).toBeInTheDocument())
    await user.click(screen.getAllByRole('button', { name: 'Edit' })[0])
    await user.clear(screen.getByLabelText('Name'))
    await user.type(screen.getByLabelText('Name'), 'Massage 75')
    await user.click(screen.getByRole('button', { name: 'Save service' }))

    await waitFor(() => expect(screen.getByText('Service updated.')).toBeInTheDocument())
    await user.click(screen.getByRole('button', { name: 'Archive' }))

    await waitFor(() => expect(screen.getByText('Service archived.')).toBeInTheDocument())
    expect(screen.getAllByText('Eva Staff').length).toBeGreaterThan(0)
    expect(screen.getByText('Vacation')).toBeInTheDocument()
  })

  it('shows only active public services, persists selection, and renders staff schedules', async () => {
    const fetchMock = vi.fn(async (input: RequestInfo | URL) => {
      const url = String(input)
      if (url.startsWith('/api/admin/services')) return okJson([activeService, archivedService])
      if (url === '/api/public/services?businessId=business-1') return okJson([activeService])
      if (url === '/api/admin/staff') return okJson([staffMember])
      if (url === '/api/admin/staff/staff-1/weekly-schedules') return okJson([weeklySchedule])
      if (url === '/api/admin/staff/staff-1/schedule-exceptions') return okJson([dayOffException])
      return okJson([])
    })
    vi.stubGlobal('fetch', fetchMock)

    render(<App />)
    const user = userEvent.setup()

    await user.click(screen.getByPlaceholderText('UUID from admin auth context'))
    await user.keyboard('business-1')

    await waitFor(() => expect(screen.getByRole('button', { name: /Massage 60 60 min 1200.00 CZK/i })).toBeInTheDocument())
    expect(screen.queryByRole('button', { name: /Massage 90 60 min 1200.00 CZK/i })).not.toBeInTheDocument()
    expect(screen.getByText('Mon · WORK')).toBeInTheDocument()

    await user.click(screen.getByRole('button', { name: /Massage 60 60 min 1200.00 CZK/i }))
    expect(window.location.search).toContain('service=svc-1')
  })
})

function okJson(data: unknown, status = 200): Response {
  return new Response(JSON.stringify(data), {
    status,
    headers: { 'Content-Type': 'application/json' },
  })
}
