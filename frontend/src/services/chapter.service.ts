import { chapterEditData, newChapterData } from './types/serviceTypes'
import {
  GET_CHAPTER_INFO,
  GET_CHAPTER,
  POST_CHAPTER_CREATE,
  GET_FILE_CHAPTER_IMAGES,
  GET_FILE,
  DELETE_CHAPTER,
  PUT_CHAPTER_EDIT,
  GET_CHAPTER_REQUIREMENTS,
  POST_CHAPTER_REQUIREMENTS_UPDATE
} from './urls'
import { axiosApiDelete, axiosApiGet, axiosApiPost, axiosApiPut } from '../utils/axios'

class ChapterService {
  getChaptersList(courseId: number) {
    return axiosApiGet(`${GET_CHAPTER}?courseId=${courseId}`).catch((error) => {
      throw error
    })
  }

  getChapterDetails(chapterId: number) {
    return axiosApiGet(GET_CHAPTER_INFO, { id: chapterId }).catch((error) => {
      throw error
    })
  }

  sendNewChapterData({ name, sizeX, sizeY, imageId, posX, posY }: newChapterData) {
    return axiosApiPost(POST_CHAPTER_CREATE, {
      name,
      sizeX,
      sizeY,
      imageId,
      posX,
      posY
    }).catch((error) => {
      throw error
    })
  }

  sendEditChapterData({ chapterId, editionForm }: chapterEditData) {
    return axiosApiPut(PUT_CHAPTER_EDIT, {
      chapterId,
      editionForm
    }).catch((error) => {
      throw error
    })
  }

  getChapterImagesList() {
    return axiosApiGet(GET_FILE_CHAPTER_IMAGES, {}).catch((error) => {
      throw error
    })
  }

  getChapterImage({ imageId }: any) {
    return axiosApiGet(GET_FILE, { id: imageId }).catch((error) => {
      throw error
    })
  }

  deleteChapter(chapterId: number) {
    return axiosApiDelete(DELETE_CHAPTER, { chapterID: chapterId }).catch((error) => {
      throw error
    })
  }

  getRequirements(chapterId: number) {
    return axiosApiGet(GET_CHAPTER_REQUIREMENTS, { chapterId }).catch((error) => {
      throw error
    })
  }

  setRequirements(chapterId: number, requirements: any, isBlocked: any) {
    return axiosApiPost(POST_CHAPTER_REQUIREMENTS_UPDATE, {
      chapterId,
      isBlocked,
      requirements
    }).catch((error) => {
      throw error
    })
  }
}

export default new ChapterService()
