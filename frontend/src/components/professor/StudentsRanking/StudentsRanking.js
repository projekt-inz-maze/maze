import React, { useCallback, useEffect, useState } from 'react'

import { debounce } from 'lodash/function'
import {Container, Form} from 'react-bootstrap'

import StudentPointsModal from './StudentPointsModal'
import { useAppSelector } from '../../../hooks/hooks'
import RankingService from '../../../services/ranking.service'
import Ranking from '../../general/Ranking/Ranking'

function StudentsRanking() {
  const [ranking, setRanking] = useState(undefined)
  const [filteredList, setFilteredList] = useState(undefined)
  const [filterQuery, setFilterQuery] = useState(undefined)
  const [isStudentPointsModalOpen, setIsStudentPointsModalOpen] = useState(false)
  const [chosenStudentEmail, setChosenStudentEmail] = useState(null)

  const courseId = useAppSelector((state) => state.user.courseId)

  const onInfoIconClick = useCallback((student) => {
    setIsStudentPointsModalOpen(true)
    setChosenStudentEmail(student.email)
  }, [])

  useEffect(() => {
    RankingService.getGlobalRankingList(courseId)
      .then((response) => {
        setRanking(response)
        setFilteredList(response)
      })
      .catch(() => {
        setRanking(null)
        setFilteredList(null)
      })
  }, [])

  useEffect(() => {
    if (filterQuery) {
      RankingService.getFilteredRanking(filterQuery)
        .then((response) => {
          setFilteredList(response)
        })
        .catch(() => {
          setFilteredList(null)
        })
    } else {
      setFilteredList(ranking)
    }
  }, [filterQuery, ranking])

  const filterList = debounce((query) => {
    setFilterQuery(query)
  }, 300)

  return (
    <Container fluid>
      <Form.Group className="py-3 px-4">
        <Form.Control
          type="text"
          placeholder="Wyszukaj po grupie lub studencie lub typie bohatera..."
          onChange={(e) => filterList(e.target.value)}
        />
      </Form.Group>
      <Ranking rankingList={filteredList} iconCallback={onInfoIconClick} />
      <StudentPointsModal
        show={isStudentPointsModalOpen}
        setModalOpen={setIsStudentPointsModalOpen}
        studentEmail={chosenStudentEmail}
      />
    </Container>
  )
}

export default StudentsRanking
