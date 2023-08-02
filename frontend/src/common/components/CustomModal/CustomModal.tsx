import React, { useState } from 'react'

import { Button, Modal } from 'react-bootstrap'

import styles from './CustomModal.module.scss'
import { Role } from '../../../utils/userRole'

type CustomModalProps = {
  userRole: number
  isModalVisible: boolean
  onCloseModal: () => void
  onAddCourse: (name: string, description: string) => void
}

const CustomModal = (props: CustomModalProps) => {
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')

  const handleNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setName(event.target.value)
  }

  const handleDescriptionChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setDescription(event.target.value)
  }

  const studentModalTitle = 'Dołącz do kursu'
  const studentModalBody = (
    <>
      <p>Wpisz kod kursu, aby dołączyć do niego.</p>
      <input type='text' placeholder='Kod kursu' />
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
    if (props.onAddCourse) {
      props.onAddCourse(name, description)
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
      <Modal.Body className={styles.modalBody}>{props.userRole === Role.LOGGED_IN_AS_STUDENT ? studentModalBody : professorModalBody}</Modal.Body>
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
