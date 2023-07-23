import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

const user = localStorage.getItem('user')
const parsedUser = user ? JSON.parse(user) : null

export const api = createApi({
  reducerPath: 'api',
  baseQuery: fetchBaseQuery({
    baseUrl: 'http://localhost:8080',
    prepareHeaders: (headers) => {
      headers.set('Authorization', `Bearer ${parsedUser.token}`)
      return headers
    }
  }),
  endpoints: () => ({})
})
