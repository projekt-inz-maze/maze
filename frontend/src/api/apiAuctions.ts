import { api } from './api'
import { AuctionResponse, BidRequest } from './types'

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
    })
  }),
  overrideExisting: false
})

export const { useGetAuctionByIdQuery, useSetBidMutation } = apiAuctions
