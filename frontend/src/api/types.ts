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

export type ChapterResponse = {
  id: number
  name: string
  noActivities: number
  maxPoints: number
  mapSize: string
  posX: number
  posY: number
  isFulfilled: boolean
}

export type ActivityMapResponse = {
  id: number
  tasks: ActivityResponse[]
  mapSizeX: number
  mapSizeY: number
  image: any
}

export type ActivityResponse = {
  id: number
  posX: number
  posY: number
  type: string
  title: string
  points: number
  creationTime: string
  description: string
  isFulfilled: boolean
  isCompleted: boolean
  wager: null
  timeLimit: number
}

export type Requirement = {
  id: number
  name: string
  value: unknown
  type: string
  selected: boolean
}

export type ActivityRequirements = {
  isBlocked: boolean
  requirements: Requirement[]
}

export type AuctionResponse = {
  id: number
  highestBid: number
  userBid: number
  lowestAllowedBid: number
  highestAllowedBid: number
  availablePoints: number
  endDateEpochSeconds: number
  minTaskScoreToGetPoints: number
}

export type BidRequest = {
  auctionId: number
  bidValue: number
}

export type TaskRequest = {
  chapterId: number
  form: SubmitTaskForm
}

export type SubmitTaskForm = {
  activityType: string
  title: string
  description: string
  posX: number
  posY: number
  percentageForAuthor: number
  maxPointsForAuthor: number
}

export type StudentSubmitRequest = {
  id: number
  title: string
  content: string
}

export type ProfessorGradeRequest = {
  id: number
  accepted: boolean
}

export type ProfessorGradeResponse = FileTask | null

export type FileTask = {
  title: string
  description: string
  posX: number
  posY: number
  requiredKnowledge: number
  auction: Auction
  maxPoints: number
}

export type Auction = {
  minBidding: number
  maxBidding: number
  resolutionDate: number
  minScoreToGetPoints: number
}

export type ActivityResponseInfo = {
  userEmail: string
  fileTaskResponseId: number
  firstName: string
  lastName: string
  activityName: string
  isLate: boolean
  activityDetails: string
  userAnswer: string
  file: File[]
  maxPoints: number
  fileTaskId: number
  remaining: number
}

export type File = {
  id: number
  name: string
}

export type ActivityToGrade = {
  activityId: number
  toGrade: number
}

export type GradeTaskRequest = {
  taskId: number
  remarks: string
  points: number
  file: any
  fileName: string
}

export type GradeTaskResponse = {
  feedbackId: number
  fileTaskResultId: number
  studentEmail: string
  fileTaskId: number
  taskName: string
  description: string
  answer: string
  taskFiles: File[]
  points: number
  remarks: string
  feedbackFile: File
}
