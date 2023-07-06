import { createSlice, PayloadAction } from '@reduxjs/toolkit'

interface userState {
  courseId: number
}

const initialState: userState = {
  courseId: -1
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
