import { api } from './api'
import {ActivityResponseInfo, ActivityToGrade, GradeTaskRequest, GradeTaskResponse} from './types'

const apiGrades = api.injectEndpoints({
  endpoints: (build) => ({
    getTasksToGrade: build.query<ActivityToGrade[], number>({
      query: (id) => ({
        url: `task/evaluate/all?courseId=${id}`,
        method: 'GET'
      }),
      providesTags: ['Grades']
    }),
    getFirstTaskToGrade: build.query<ActivityResponseInfo, number>({
      query: (id) => ({
        url: `task/evaluate/first?fileTaskId=${id}`,
        method: 'GET'
      }),
      providesTags: ['Grades']
    }),
    gradeTask: build.mutation<GradeTaskResponse, GradeTaskRequest>({
      query: (body) => ({
        url: 'feedback/professor',
        method: 'POST',
        body
      }),
      invalidatesTags: ['Grades']
    })
  }),
  overrideExisting: false
})

export const { useGetFirstTaskToGradeQuery, useGetTasksToGradeQuery, useGradeTaskMutation } = apiGrades
