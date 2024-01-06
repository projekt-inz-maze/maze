import {api} from './api'
import {AuctionResponse, BidRequest, ProfessorGradeRequest, StudentSubmitRequest, SubmitTaskForm} from './types'

const apiAuctions = api.injectEndpoints({
    endpoints: (build) => ({
        getAuctionById: build.query<AuctionResponse, number>({
            query: (id) => ({
                url: `/auction/${id}`,
                method: 'GET'
            }),
            providesTags: ['Auctions']
        }),
        setBid: build.mutation<void, BidRequest>({
            query: (args) => ({
                url: '/auction/bid',
                method: 'POST',
                body: args
            }),
            invalidatesTags: ['Auctions']
        }),
        getExampleTask: build.query<SubmitTaskForm, void>({
            query: () => ({
                    url: '/task/submit/create',
                    method: 'GET'
                }
            ), providesTags: ['Auctions']
        }),
        submitTask: build.mutation<number, StudentSubmitRequest>({
            query: (body) => ({
                url: '/task/submit/result',
                method: 'POST',
                body
            }), invalidatesTags: ['Auctions']
        }),
        giveGradeTask: build.mutation<number, ProfessorGradeRequest>({
            query: (body) => ({
                url: `/task/submit/result?id=${body.id}&accepted=${body.accepted}`,
                method: 'POST'
            }), invalidatesTags: ['Auctions']
        })
    }),
    overrideExisting: false
})

export const {
    useGetAuctionByIdQuery,
    useSetBidMutation,
    useSubmitTaskMutation
} = apiAuctions
