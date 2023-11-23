import {api} from './api'
import {AuctionResponse, BidRequest, StudentSubmitRequest, SubmitTaskForm} from './types'

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
        })
    }),
    overrideExisting: false
})

export const {
    useGetAuctionByIdQuery,
    useSetBidMutation,
    useSubmitTaskMutation
} = apiAuctions
