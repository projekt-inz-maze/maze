import { QuizResults } from '../api/types'

export const convertMilisecondsToMinutes = (miliseconds: number): number => Math.floor(miliseconds / 60000)

export const getMaxValuePersonality = (personality: QuizResults) => {
  const values = [
    { name: 'Zabójca', type: personality?.KILLER },
    { name: 'Odkrywca', type: personality?.EXPLORER },
    { name: 'Zdobywca', type: personality?.ACHIEVER },
    { name: 'Towarzyski / Społecznościowiec', type: personality?.SOCIALIZER }
  ]

  const maxEntry = values.reduce((max, current) => ((current.type ?? 0) > (max.type ?? 0) ? current : max), {
    name: '',
    type: -Infinity
  })

  return maxEntry?.name
}

export const getGreetingForPersonality = (personality: QuizResults): string => {
  const maxValuePersonality = getMaxValuePersonality(personality)

  if (maxValuePersonality === 'Zabójca') {
    return 'Cześć, Zabójco!'
  }
  if (maxValuePersonality === 'Odkrywca') {
    return 'Cześć, Odkrywco!'
  }
  if (maxValuePersonality === 'Zdobywca') {
    return 'Cześć, Zdobywco!'
  }
  if (maxValuePersonality === 'Towarzyski / Społecznościowiec') {
    return 'Cześć, Towarzyszu!'
  }

  return 'Cześć!'
}
