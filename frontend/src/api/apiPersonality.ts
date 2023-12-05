import { api } from './api'
import { QuizQuestion, QuizResults } from './types'

const apiPersonality = api.injectEndpoints({
  endpoints: (build) => ({
    getPersonalityQuiz: build.query<QuizQuestion[], void>({
      query: () => ({
        url: 'personality/quiz',
        method: 'GET'
      }),
      providesTags: ['Quiz']
    }),
    sendPersonalityQuizResults: build.mutation<void, QuizResults>({
      query: (body) => ({
        url: 'personality/quiz',
        method: 'POST',
        body
      }),
      invalidatesTags: ['Quiz']
    })
  }),
  overrideExisting: false
})

export const { useGetPersonalityQuizQuery, useSendPersonalityQuizResultsMutation } = apiPersonality
