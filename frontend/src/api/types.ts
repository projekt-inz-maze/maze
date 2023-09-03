type SupportedHeroes = 'WARRIOR' | 'WIZARD' | 'PRIEST' | 'ROGUE'

export type CourseHeroDTO = {
  type: SupportedHeroes
  value: number
  coolDownMillis: number
}

export type Course = {
  id: number
  name: string
  description: string
}

export type AddCourseRequest = {
  name: string
  description: string
  heroes: CourseHeroDTO[]
}

export type AddCourseResponse = {
  id: number
}