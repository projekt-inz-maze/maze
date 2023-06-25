import React from 'react'

import moment from 'moment'
import { Spinner } from 'react-bootstrap'
import { connect } from 'react-redux'

import { TableContainer } from './TableStyle'
import { ERROR_OCCURRED, getActivityTypeName } from '../../../../utils/constants'

function LastPointsTable(props) {
  const tableHeaders = ['Data', 'Liczba punktów', 'Typ aktywności', 'Nazwa/opis aktywności'].map((title, index) => (
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
        {props.pointsList === undefined ? (
          <tr>
            <td colSpan='100%' className="text-center">
              <Spinner animation="border" />
            </td>
          </tr>
        ) : props.pointsList == null || props.pointsList.length === 0 ? (
          <tr>
            <td colSpan='100%'>
              <p className="text-center h6" style={{ color: props.theme.warning }}>
                {props.pointsList ? 'Brak punktów' : ERROR_OCCURRED}
              </p>
            </td>
          </tr>
        ) : (
          props.pointsList.map((row, idx) => (
            <tr className='w-100' key={idx}>
              <td className='w-25'>{moment(row.dateMillis).format('DD.MM.YYYY, HH:mm')}</td>
              <td className='w-25'>{row.pointsReceived ?? 'Nieocenione'}</td>
              <td className='w-25'>{getActivityTypeName(row.activityType)}</td>
              <td className='w-25'>{row.activityName}</td>
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
export default connect(mapStateToProps)(LastPointsTable)
