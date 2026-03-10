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

describe('App', () => {
  beforeEach(() => {
    window.history.replaceState({}, '', '/')
    window.confirm = vi.fn(() => true)
  })

  it('creates, edits and archives a service', async () => {
    const services = [{ ...activeService }]
    const fetchMock = vi.fn(async (input: RequestInfo | URL, init?: RequestInit) => {
      const url = String(input)
      const method = init?.method ?? 'GET'

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
    await user.click(screen.getByRole('button', { name: 'Create service' }))

    await waitFor(() => expect(screen.getByText('Service created.')).toBeInTheDocument())
    await user.click(screen.getByRole('button', { name: 'Edit' }))
    await user.clear(screen.getByLabelText('Name'))
    await user.type(screen.getByLabelText('Name'), 'Massage 75')
    await user.click(screen.getByRole('button', { name: 'Save changes' }))

    await waitFor(() => expect(screen.getByText('Service updated.')).toBeInTheDocument())
    await user.click(screen.getByRole('button', { name: 'Archive' }))

    await waitFor(() => expect(screen.getByText('Service archived.')).toBeInTheDocument())
  })

  it('shows only active public services and persists selection in query params', async () => {
    const fetchMock = vi.fn(async (input: RequestInfo | URL) => {
      const url = String(input)
      if (url.startsWith('/api/admin/services')) {
        return okJson([activeService, archivedService])
      }
      return okJson([activeService])
    })
    vi.stubGlobal('fetch', fetchMock)

    render(<App />)
    const user = userEvent.setup()

    await user.click(screen.getByPlaceholderText('UUID from admin auth context'))
    await user.keyboard('business-1')

    await waitFor(() => expect(screen.getByRole('button', { name: /Massage 60/i })).toBeInTheDocument())
    expect(screen.queryByRole('button', { name: /Massage 90/i })).not.toBeInTheDocument()

    await user.click(screen.getByRole('button', { name: /Massage 60/i }))
    expect(window.location.search).toContain('service=svc-1')
  })
})

function okJson(data: unknown, status = 200): Response {
  return new Response(JSON.stringify(data), {
    status,
    headers: { 'Content-Type': 'application/json' },
  })
}
