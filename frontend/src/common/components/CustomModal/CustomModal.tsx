import React, { useState } from 'react'

import { Button, Modal } from 'react-bootstrap'

import styles from './CustomModal.module.scss'
import { AddCourseRequest } from '../../../api/types'
import { joinGroupRequest } from '../../../services/types/serviceTypes'
import { Role } from '../../../utils/userRole'

type CustomModalProps = {
  userRole: number
  isModalVisible: boolean
  onCloseModal: () => void
  onAddCourse: (props: AddCourseRequest) => void
  onJoinCourse: (props: joinGroupRequest) => void
}

const CustomModal = (props: CustomModalProps) => {
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [invitationCode, setInvitationCode] = useState('')

  const handleNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setName(event.target.value)
  }

  const handleDescriptionChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setDescription(event.target.value)
  }

  const handleInvitationCodeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setInvitationCode(event.target.value)
  }

  const studentModalTitle = 'Dołącz do kursu'
  const studentModalBody = (
    <>
      <p>Wpisz kod kursu, aby dołączyć do niego.</p>
      <input type='text' placeholder='Kod kursu' value={invitationCode} onChange={handleInvitationCodeChange} />
    </>
  )
  const professorModalTitle = 'Dodaj kurs'
  const professorModalBody = (
    <>
      <div>
        <label htmlFor='name'>Nazwa:</label>
        <input type='text' id='name' value={name} onChange={handleNameChange} />
      </div>
      <div>
        <label htmlFor='description'>Opis:</label>
        <input type='text' id='description' value={description} onChange={handleDescriptionChange} />
      </div>
    </>
  )

  const handleSaveChanges = () => {
    if (props.userRole === Role.LOGGED_IN_AS_TEACHER) {
      props.onAddCourse({
        name,
        description,
        heroes: [
          {
            type: 'WARRIOR',
            value: 42,
            coolDownMillis: 1000
          },
          {
            type: 'WIZARD',
            value: 37,
            coolDownMillis: 1500
          },
          {
            type: 'PRIEST',
            value: 37,
            coolDownMillis: 1500
          },
          {
            type: 'ROGUE',
            value: 37,
            coolDownMillis: 1500
          }
        ]
      })
    } else if (props.userRole === Role.LOGGED_IN_AS_STUDENT) {
      props.onJoinCourse({ invitationCode, heroType: 'WARRIOR' }) // TODO: Add dropdown with hero type select.
    }
    setName('')
    setDescription('')
    props.onCloseModal()
  }

  const handleCloseModal = () => {
    props.onCloseModal()
    setName('')
    setDescription('')
  }

  return (
    <Modal show={props.isModalVisible}>
      <Modal.Header closeButton onHide={handleCloseModal}>
        <Modal.Title>
          {props.userRole === Role.LOGGED_IN_AS_STUDENT ? studentModalTitle : professorModalTitle}
        </Modal.Title>
      </Modal.Header>
      <Modal.Body
        className={styles.modalBody}>{props.userRole === Role.LOGGED_IN_AS_STUDENT ? studentModalBody : professorModalBody}</Modal.Body>
      <Modal.Footer>
        <Button variant='secondary' onClick={handleCloseModal}>
          Zamknij
        </Button>
        <Button variant='primary' onClick={handleSaveChanges}>
          Zapisz zmiany
        </Button>
      </Modal.Footer>
    </Modal>
  )
}

export default CustomModal
