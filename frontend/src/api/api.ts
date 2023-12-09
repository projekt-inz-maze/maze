import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

const user = localStorage.getItem('user')
const parsedUser = user ? JSON.parse(user) : null

export const api = createApi({
  reducerPath: 'api',
  baseQuery: fetchBaseQuery({
    baseUrl: 'http://localhost:8080/api',
    prepareHeaders: (headers, { getState }) => {
      const state = getState() as { apiFiles?: { endpoints: { sendFile: { definition: { body: FormData } } } } }

      // Check if the request is for 'multipart/form-data'
      if (state.apiFiles && state.apiFiles.endpoints.sendFile.definition.body instanceof FormData) {
        // If it's 'multipart/form-data', let the browser set the Content-Type automatically
        return headers
      }

      // For other requests, set 'Content-Type' manually
      headers.set('Content-Type', 'application/json')
      headers.set('Authorization', `Bearer ${parsedUser.access_token}`)
      return headers
    }
  }),
  tagTypes: ['Courses', 'Auctions', 'Grades', 'Quiz', 'Files'],
  endpoints: () => ({})
})
