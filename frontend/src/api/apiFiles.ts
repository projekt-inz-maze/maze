import { api } from './api'

const apiFiles = api.injectEndpoints({
  endpoints: (build) => ({
    sendFile: build.mutation<number, FormData>({
      query: (body) => ({
        url: 'task/file/result/file/add',
        method: 'POST',
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        body
      }),
      invalidatesTags: ['Files']
    })
  }),
  overrideExisting: false
})

export const { useSendFileMutation } = apiFiles
