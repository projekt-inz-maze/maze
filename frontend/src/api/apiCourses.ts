import { api } from './api'

const apiCourses = api.injectEndpoints({
  endpoints: (build) => ({
    getAllCourses: build.query({
      query: () => ({
        url: '/course',
        method: 'GET'
      })
    })
  }),
  overrideExisting: false
})

export const { useGetAllCoursesQuery } = apiCourses