import {api} from './api'
import {AuctionResponse, BidRequest, SubmitTaskForm, TaskRequest} from './types'

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
        createNewTask: build.mutation<number, TaskRequest>({
            query: (args) => ({
                url: '/task/submit/create',
                method: 'POST',
                body: args
            }),
            invalidatesTags: ['Auctions']
        })
    }),
    overrideExisting: false
})

export const {
    useGetAuctionByIdQuery,
    useSetBidMutation,
    useCreateNewTaskMutation,
    useGetExampleTaskQuery
} = apiAuctions
