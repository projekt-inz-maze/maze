import React, {useState} from 'react'

import {Button, Col, Form, Modal, Row} from 'react-bootstrap'

import styles from './StudentTaskModal.module.scss'
import {useSubmitTaskMutation} from '../../../api/apiAuctions'
import {StudentSubmitRequest} from '../../../api/types'

type StudentTaskModalProps = {
    activityId: number
    showDetails: boolean
    onCloseDetails: () => void
}

const StudentTaskModal = (props: StudentTaskModalProps) => {
    const [submitTask] = useSubmitTaskMutation<StudentSubmitRequest>()

    const [title, setTitle] = useState<string>('')
    const [content, setContent] = useState<string>('')

    const onTitleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setTitle(event.target?.value ?? '')
    }

    const onContentChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setContent(event.target?.value ?? '')
    }

    const handleSubmitTask = () => {
        submitTask({
            id: props.activityId,
            title,
            content
        })
        setTitle('')
        setContent('')
    }

    return (<>
        <Modal
            fullscreen
            show={props.showDetails}
            onHide={props.onCloseDetails}
            size='xl'
            className={styles.modalContainer}
            centered
        >
            <Modal.Header className={styles.modalHeader}>
                <Modal.Title className={styles.modalTitle}>
                    Twoje zadanie
                </Modal.Title>
                <button type='button' className={styles.customButtonClose} onClick={props.onCloseDetails}>
                    {/* Close button content */}
                    <span>&times;</span>
                </button>
            </Modal.Header>
            <Modal.Body>
                <Form className={styles.formContainer}>
                    <Row>
                        <Form.Group className="mb-3" controlId="exampleForm.ControlText1">
                            <Form.Label><span>Tytuł zadania (wymagane)</span></Form.Label>
                            <Form.Control as='textarea' rows={1} required onChange={onTitleChange}/>
                        </Form.Group>
                    </Row>
                    <Row>
                        <Form.Group className="mb-3" controlId="exampleForm.ControlTextarea1">
                            <Form.Label><span>Treść zadania (wymagane)</span></Form.Label>
                            <Form.Control as="textarea" rows={3} required onChange={onContentChange}/>
                        </Form.Group>
                    </Row>
                    <Row className={styles.form}>
                        <Col xs={3}>
                            <Form.Group controlId="exampleForm.ControlInput1">
                                <Form.Label><span>Załączone pliki</span></Form.Label>
                                <Form.Control type="file"/>
                            </Form.Group>
                        </Col>
                        <Col xs={3}>
                            <Form.Group id="formGridCheckbox">
                                <Form.Check type="checkbox" label="Zadanie z zajęć laboratoryjnych"/>
                            </Form.Group>
                        </Col>
                    </Row>
                </Form>
            </Modal.Body>
            <Modal.Footer className={styles.modalFooter}>
                <Button variant='primary' type='submit' className={styles.bidButton} onClick={handleSubmitTask}>
                    <span>Zgłoś zadanie</span>
                </Button>
            </Modal.Footer>
        </Modal>
    </>)
}

export default StudentTaskModal