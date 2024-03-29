import React, { useEffect, useState } from 'react'

import { Spinner } from 'react-bootstrap'
import { connect } from 'react-redux'

import { useAppSelector } from '../../../../hooks/hooks'
import GroupService from '../../../../services/group.service'
import { ERROR_OCCURRED } from '../../../../utils/constants'
import { TableContainer } from '../../../student/PointsPage/Tables/TableStyle'

const tableHeaders = ['Nr', 'Nazwa grupy', 'Liczba uczestników', 'Kod']

function GroupsTable(props) {
  const [tableContent, setTableContent] = useState(undefined)

  const courseId = useAppSelector((state) => state.user.courseId)

  const updateTableContent = () => {
    GroupService.getGroups(courseId)
      .then((response) => {
        setTableContent(response)
      })
      .catch(() => setTableContent(null))
  }

  useEffect(() => {
    updateTableContent()
    props.setRefreshFunction(() => updateTableContent)
    // I want this code to run ONLY ONCE, I don't care what this warning says, you are free to change it and see the consequences
    // eslint-disable-next-line
  }, [])

  const TableBody = (tableContent) =>
    tableContent.map((row, idx) => (
      <tr key={idx}>
        <td>{idx + 1}</td>
        <td>{row.name}</td>
        <td>{row.studentsCount}</td>
        <td>{row.invitationCode}</td>
      </tr>
    ))

  return (
    <TableContainer
      $fontColor={props.theme.font}
      $background={props.theme.primary}
      $bodyColor={props.theme.secondary}
      className='mb-0'
    >
      <thead>
        <tr>
          {tableHeaders.map((header, index) => (
            <th key={index}>{header}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {tableContent === undefined ? (
          <tr>
            <td colSpan={4} className='text-center'>
              <Spinner animation='border' />
            </td>
          </tr>
        ) : tableContent === null ? (
          <tr>
            <td colSpan={4} className='text-center'>
              {ERROR_OCCURRED}
            </td>
          </tr>
        ) : (
          TableBody(tableContent)
        )}
      </tbody>
    </TableContainer>
  )
}

function mapStateToProps(state) {
  const { theme } = state

  return { theme }
}

export default connect(mapStateToProps)(GroupsTable)
