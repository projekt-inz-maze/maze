import React, {useCallback, useState} from 'react'

import {Button, Modal, ModalBody, ModalFooter, ModalHeader, Tab} from 'react-bootstrap'
import {connect} from 'react-redux'

import AddCombatTask from './AddActivity/AddCombatTask'
import AddGraphTask from './AddActivity/AddGraphTask'
import AddInfoTask from './AddActivity/AddInfoTask'
import AddSubmitTask from './AddActivity/AddSubmitTask'
import AddSurveyTask from './AddActivity/AddSurveyTask'
import {Activity, getActivityTypeName} from '../../../utils/constants'
import {isMobileView} from '../../../utils/mobileHelper'
import {TabsContainer} from '../ParticipantsPage/ParticipantsStyles'

function AddActivityModal(props) {
    const activities = Object.keys(Activity)
    const [isSuccessModalOpen, setIsSuccessModalOpen] = useState(false)

    const getActivityTab = useCallback(
        (activityType) => {
            const onCancel = () => props.setShow(false)
            const onSuccess = () => {
                props.setShow(false)
                setIsSuccessModalOpen(true)
            }

            switch (activityType) {
                case Activity.EXPEDITION:
                    return <AddGraphTask chapterId={props.chapterId} onSuccess={onSuccess} onCancel={onCancel}/>
                case Activity.TASK:
                    return <AddCombatTask chapterId={props.chapterId} onSuccess={onSuccess} onCancel={onCancel}/>
                case Activity.INFO:
                    return <AddInfoTask chapterId={props.chapterId} onSuccess={onSuccess} onCancel={onCancel}/>
                case Activity.SURVEY:
                    return <AddSurveyTask chapterId={props.chapterId} onSuccess={onSuccess} onCancel={onCancel}/>
                case Activity.SUBMIT:
                    return <AddSubmitTask chapterId={props.chapterId} onSuccess={onSuccess} onCancel={onCancel}/>
                default:
                    return <></>
            }
        },
        [props]
    )

    const onSuccessModalClose = () => {
        setIsSuccessModalOpen(false)
        props.onSuccess()
    }

    return (
        <>
            <Modal show={props.showModal} onHide={() => props.setShow(false)} size="xl">
                <ModalHeader closeButton>
                    <h5>Dodaj nową aktywność</h5>
                </ModalHeader>
                <ModalBody style={{padding: isMobileView() ? '1rem 0' : '1rem'}}>
                    <TabsContainer
                        $background={props.theme.success}
                        $fontColor={props.theme.background}
                        $linkColor={props.theme.primary}
                        defaultActiveKey={Activity.EXPEDITION}
                    >
                        {activities.map(
                            (activity, index) =>
                                activity !== Activity.ADDITIONAL && (
                                    <Tab title={getActivityTypeName(activity)} eventKey={activity} key={index + Date.now()}>
                                        {getActivityTab(activity)}
                                    </Tab>
                                )
                        )}
                    </TabsContainer>
                </ModalBody>
            </Modal>
            <Modal show={isSuccessModalOpen} onHide={() => setIsSuccessModalOpen(false)}>
                <ModalHeader>
                    <h5>Aktywność dodana pomyślnie</h5>
                </ModalHeader>
                <ModalBody>
                    <p>Twoja aktywność została pomyślnie zapisana w bazie danych.</p>
                </ModalBody>
                <ModalFooter>
                    <Button onClick={onSuccessModalClose}>Zakończ</Button>
                </ModalFooter>
            </Modal>
        </>
    )
}

function mapStateToProps(state) {
    const {theme} = state
    return {
        theme
    }
}

export default connect(mapStateToProps)(AddActivityModal)
