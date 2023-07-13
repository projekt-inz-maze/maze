export type chapterEditData = {
  chapterId: number
  editionForm: any
}

export type newChapterData = {
  name: string
  sizeX: number
  sizeY: number
  imageId: number
  posX: number
  posY: number
}

export type addGroupRequest = {
  groupName: string
  groupKey: string
}

export type professorFeedback = {
  taskId: number
  remarks: string
  points: number
  file: any
  fileName: string
}