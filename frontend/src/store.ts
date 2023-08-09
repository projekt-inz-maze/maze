import { configureStore } from '@reduxjs/toolkit'
import thunk from 'redux-thunk'

import { api } from './api/api'
import rootReducer from './reducers'

const store = configureStore({
  reducer: rootReducer,
  middleware: (getDefaultMiddleware) => [
    thunk,
    api.middleware,
    ...getDefaultMiddleware({
      serializableCheck: false,
      immutableCheck: false
    })
  ]
})

export default store

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch
