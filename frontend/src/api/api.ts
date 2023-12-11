import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

const user = localStorage.getItem('user')
const parsedUser = user ? JSON.parse(user) : null

export const BASE_URL = 'http://localhost:8080/api'

export const api = createApi({
  reducerPath: 'api',
  baseQuery: fetchBaseQuery({
    baseUrl: BASE_URL,
    prepareHeaders: (headers) => {
      headers.set('Authorization', `Bearer ${parsedUser.access_token}`)
      return headers
    }
  }),
  tagTypes: ['Courses', 'Auctions', 'Grades', 'Quiz', 'Files'],
  endpoints: () => ({})
})
