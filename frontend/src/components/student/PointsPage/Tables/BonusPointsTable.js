import React, { useEffect, useState } from 'react'

import moment from 'moment'
import { Spinner } from 'react-bootstrap'
import { connect } from 'react-redux'

import { TableContainer } from './TableStyle'
import { useAppSelector } from '../../../../hooks/hooks'
import StudentService from '../../../../services/student.service'
import { ERROR_OCCURRED } from '../../../../utils/constants'


function BonusPointsTable(props) {
  const [bonusPoints, setBonusPoints] = useState(undefined)
  const courseId = useAppSelector((state) => state.user.courseId)

  useEffect(() => {
    StudentService.getBonusPointsList(courseId)
      .then((response) => {
        setBonusPoints(response)
      })
      .catch(() => {
        setBonusPoints(null)
      })
  }, [])

  const tableHeaders = ['Data', 'Liczba punktów', 'Opis', 'Osoba przyznająca'].map((title, index) => (
    <th key={index} className='w-25'>
      {title}
    </th>
  ))

  return (
    <TableContainer
      className='mb-md-0 mb-5'
      striped
      bordered
      hover
      $fontColor={props.theme.font}
      $background={props.theme.primary}
      $bodyColor={props.theme.secondary}
    >
      <thead>
        <tr className='w-100'>{tableHeaders}</tr>
      </thead>
      <tbody>
        {bonusPoints === undefined ? (
          <tr>
            <td colSpan='100%' className="text-center">
              <Spinner animation="border" />
            </td>
          </tr>
        ) : bonusPoints == null || bonusPoints.length === 0 ? (
          <tr>
            <td colSpan='100%'>
              <p className="text-center h6" style={{ color: props.theme.warning }}>
                {bonusPoints ? 'Brak punktów' : ERROR_OCCURRED}
              </p>
            </td>
          </tr>
        ) : (
          bonusPoints.map((row, idx) => (
            <tr className='w-100' key={idx}>
              <td className='w-25'>{moment(row.dateMillis).format('DD.MM.YYYY, HH:mm')}</td>
              <td className='w-25'>{row.points}</td>
              <td className='w-25'>{!row.description || row.description === '' ? '-' : row.description}</td>
              <td className='w-25'>{row.professor}</td>
            </tr>
          ))
        )}
      </tbody>
    </TableContainer>
  )
}

function mapStateToProps(state) {
  const {theme} = state

  return { theme }
}
export default connect(mapStateToProps)(BonusPointsTable)
