import React from 'react'

import { Button, Modal } from 'react-bootstrap'

type CustomModalProps = {
  title: string
  body: string | JSX.Element
  isModalVisible: boolean
  onCloseModal: () => void
}

const CustomModal = (props: CustomModalProps) => (
  <Modal show={props.isModalVisible}>
    <Modal.Header closeButton onHide={props.onCloseModal}>
      <Modal.Title>{props.title}</Modal.Title>
    </Modal.Header>
    <Modal.Body>{props.body}</Modal.Body>
    <Modal.Footer>
      <Button variant='secondary' onClick={props.onCloseModal}>Close</Button>
      <Button variant='primary'>Save Changes</Button>
    </Modal.Footer>
  </Modal>
)

export default CustomModal
