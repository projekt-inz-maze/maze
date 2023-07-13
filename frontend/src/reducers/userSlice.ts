import { createSlice, PayloadAction } from '@reduxjs/toolkit'

interface userState {
  courseId: number
}

// TODO: Due to reloading window on sidebar we reset store state. Either add persistent store or deal with sidebar.
const initialState: userState = {
  courseId: 0
}

export const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    setCourseId: (state, action: PayloadAction<number>) => {
      state.courseId = action.payload
    }
  }
})

export const { setCourseId } = userSlice.actions
export default userSlice.reducer
