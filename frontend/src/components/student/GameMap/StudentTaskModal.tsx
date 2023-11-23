import React from 'react'

import {Button, Col, Form, Modal, Row} from 'react-bootstrap'

import styles from './StudentTaskModal.module.scss'
import {useCreateNewTaskMutation, useGetExampleTaskQuery} from '../../../api/apiAuctions'
import {TaskRequest} from '../../../api/types'

type StudentTaskModalProps = {
    // chapterId: number
    showDetails: boolean
    onCloseDetails: () => void
}

const StudentTaskModal = (props: StudentTaskModalProps) => {
    const [createNewTask] = useCreateNewTaskMutation<TaskRequest>()
    const {data} = useGetExampleTaskQuery()

    const handleSubmitTask = () => {
        console.log(data)
        // createNewTask({
        //     chapterId: 1,
        //     form: {
        //         activityType: 'SUBMIT',
        //         title: 'Tytuł',
        //         description: 'Opis',
        //         posX: 3,
        //         posY: 2,
        //         percentageForAuthor: 10,
        //         maxPointsForAuthor: 50
        //     }
        // })
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
                        <Form.Group className="mb-3" controlId="exampleForm.ControlTextarea1">
                            <Form.Label><span>Treść zadania (wymagane)</span></Form.Label>
                            <Form.Control as="textarea" rows={3}/>
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
                <Button variant='primary' className={styles.bidButton} onClick={handleSubmitTask}>
                    <span>Zgłoś zadanie</span>
                </Button>
            </Modal.Footer>
        </Modal>
    </>)
}

export default StudentTaskModal