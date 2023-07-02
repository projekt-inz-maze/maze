import React from 'react'

import { faEdit, faTrash } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { connect } from 'react-redux'

import { CustomTable } from '../../../student/GameCardPage/gameCardContentsStyle'

function Table(props) {
  return (
    <CustomTable
      style={{ marginTop: 10 }}
      $fontColor={props.theme.font}
      $borderColor={props.theme.primary}
      $background={props.theme.secondary}
    >
      <thead>
        <tr>
          {props.headers.map((headElement, index) => (
            <th key={index + Date.now()}>{headElement}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {props.body.map((bodyElement, i) => (
          <tr key={i + Date.now()}>
            {bodyElement.map((element, index) => (
              <td key={index + Date.now()} className="align-middle">
                {element}
              </td>
            ))}
            <td className="align-middle">
              <FontAwesomeIcon icon={faEdit} onClick={() => props.editIconCallback(i)} />
            </td>
            {props.deleteIconCallback ? (
              <td className="align-middle">
                <FontAwesomeIcon icon={faTrash} onClick={() => props.deleteIconCallback(i)} />
              </td>
            ) : null}
          </tr>
        ))}
      </tbody>
    </CustomTable>
  )
}

function mapStateToProps(state) {
  const {theme} = state

  return { theme }
}
export default connect(mapStateToProps)(Table)
