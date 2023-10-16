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
  courseId: number
}

export type addGroupRequest = {
  groupName: string
  groupKey: string
  courseId: number
}

export type joinGroupRequest = {
  invitationCode: string
  heroType: string
}
