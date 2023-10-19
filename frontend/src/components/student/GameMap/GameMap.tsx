import React, { useEffect, useState } from 'react'

import { Row, Col, Button } from 'react-bootstrap'
import { useNavigate } from 'react-router-dom'

import styles from './GameMap.module.scss'
import { ChapterResponse } from '../../../api/types'
import { useAppDispatch, useAppSelector } from '../../../hooks/hooks'
import { setSelectedChapterId } from '../../../reducers/userSlice'
import ChapterService from '../../../services/chapter.service'

const GameMap = () => {
  const navigate = useNavigate()
  const dispatch = useAppDispatch()

  const [chaptersList, setChaptersList] = useState<ChapterResponse[]>([])
  const [chosenChapterId, setChosenChapterId] = useState<number>(0)

  const courseId = useAppSelector((state: { user: { courseId: number } }) => state.user.courseId)

  useEffect(() => {
    ChapterService.getChaptersList(courseId)
      .then((response) => {
        setChaptersList(response)
        setChosenChapterId(response[0].id)
        dispatch(setSelectedChapterId(response[0].id))
      })
      .catch(() => {
        setChaptersList([])
      })
  }, [])

  const handleSelectChapter = (chapterId: number) => {
    dispatch(setSelectedChapterId(chapterId))
    setChosenChapterId(chapterId)
  }

  return (
    <div style={{ height: '100%' }}>
      <Row style={{ height: '100%' }}>
        <Col xs={12} md={9} className='p-0 w-100 h-100'>
          <div className='image-container' style={{ width: '100%', height: '100%', position: 'relative' }}>
            <img
              src='/map/medieval_town.png'
              alt='Chapter image'
              className='img-fluid'
              style={{ width: '100%', height: '100%' }}
            />
            <div className={styles.legend}>
              <p style={{ paddingBottom: '1em' }}>Wybrany rozdział: {chosenChapterId ?? 'brak'}</p>
              {chaptersList.map((chapter, index) => (
                <Button
                  key={`${chapter.name + index}`}
                  onClick={() => handleSelectChapter(chapter.id)}
                  className={styles.legendItem}
                >
                  {chapter.name}
                </Button>
              ))}
            </div>
            <Button onClick={() => navigate('/map/quests')} className={styles.questBoardButton} />
          </div>
        </Col>
      </Row>
    </div>
  )
}

export default GameMap
