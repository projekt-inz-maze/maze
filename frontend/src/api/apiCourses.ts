import { api } from './api'
import { AddCourseRequest, AddCourseResponse, Course } from './types'
import { joinGroupRequest } from '../services/types/serviceTypes'

const apiCourses = api.injectEndpoints({
  endpoints: (build) => ({
    getAllCourses: build.query<Course[], void>({
      query: () => ({
        url: '/course',
        method: 'GET'
      }),
      providesTags: ['Courses']
    }),
    addNewCourse: build.mutation<AddCourseResponse, AddCourseRequest>({
      query: (body) => ({
        url: '/course',
        method: 'POST',
        body
      }),
      invalidatesTags: ['Courses']
    }),
    deleteCourse: build.mutation<void, number>({
      query: (args) => ({
        url: `/course/delete?courseId=${args}`,
        method: 'DELETE'
      }),
      invalidatesTags: ['Courses']
    }),
    updateCourse: build.mutation({
      query: (args) => ({
        url: '/course/edit',
        method: 'PUT',
        body: args
      }),
      invalidatesTags: ['Courses']
    }),
    joinCourseGroup: build.mutation<void, joinGroupRequest>({
      query: (args) => ({
        url: '/user/group/join',
        method: 'POST',
        body: args
      }),
      invalidatesTags: ['Courses']
    })
  }),
  overrideExisting: false
})

export const { useGetAllCoursesQuery, useAddNewCourseMutation, useDeleteCourseMutation, useUpdateCourseMutation, useJoinCourseGroupMutation } =
  apiCourses
