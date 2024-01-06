import { createSlice, PayloadAction } from '@reduxjs/toolkit'

interface userState {
  courseId: number
  selectedChapterId?: number
}

const initialState: userState = {
  courseId: 1
}

export const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    setCourseId: (state, action: PayloadAction<number>) => {
      state.courseId = action.payload
    },
    setSelectedChapterId: (state, action: PayloadAction<number>) => {
      state.selectedChapterId = action.payload
    }
  }
})

export const { setCourseId, setSelectedChapterId } = userSlice.actions
export default userSlice.reducer
