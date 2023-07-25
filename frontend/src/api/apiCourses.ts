import { api } from './api'
import { AddCourseRequest, AddCourseResponse, Course } from './types'

const apiCourses = api.injectEndpoints({
  endpoints: (build) => ({
    getAllCourses: build.query<Course[], void>({
      query: () => ({
        url: '/course',
        method: 'GET'
      }),
      providesTags: ['Courses']
    }),
    addNewCourse: build.mutation<AddCourseRequest, AddCourseResponse>({
      query: (body) => ({
        url: '/course',
        method: 'POST',
        body
      }),
      invalidatesTags: ['Courses']
    }),
    deleteCourse: build.mutation<void, Course>({
      query: (args) => ({
        url: `/course/delete?courseId=${args.id}`,
        method: 'DELETE'
      }),
      invalidatesTags: ['Courses']
    }),
    updateCourse: build.mutation<void, Course>({
      query: (args) => ({
        url: '/course/edit',
        method: 'PUT',
        body: args
      }),
      invalidatesTags: ['Courses']
    })
  }),
  overrideExisting: false
})

export const { useGetAllCoursesQuery, useAddNewCourseMutation, useDeleteCourseMutation, useUpdateCourseMutation } =
  apiCourses
