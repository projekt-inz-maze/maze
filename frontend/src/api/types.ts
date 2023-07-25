export type Course = {
  id: number
  name: string
  description: string
}

export type AddCourseRequest = {
  name: string
  description: string
}

export type AddCourseResponse = {
  id: number
}