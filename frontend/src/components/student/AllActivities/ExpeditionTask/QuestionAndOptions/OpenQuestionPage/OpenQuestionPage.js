import React, { useRef } from 'react'

import { Col, Row } from 'react-bootstrap'
import { connect } from 'react-redux'

import { UserAnswerArea } from './OpenQuestionStyle'
import answerSaver from '../answerSaver'
import { ButtonRow, QuestionCard } from '../QuestionAndOptionsStyle'


function fitAreaToContent(text) {
  const maxHeight = 300 // px
  text.style.height = '0'
  text.style.height = `${Math.min(text.scrollHeight, maxHeight)  }px`
}

function OpenQuestionPage(props) {
  const userAnswer = useRef()
  const saveAnswer = () => {
    answerSaver(userAnswer.current.value, props.question.type, props.expeditionId, props.question.id, props.reloadInfo)
  }

  return (
    <Row>
      <Col xs={12}>
        <QuestionCard className='h-auto py-5' $background={props.theme.primary} $fontColor={props.theme.font}>
          <div>{props.question.hint}</div>
          <div>
            <p>{props.question.content}</p>
          </div>
          <div>Punkty: {props.question.points}</div>
        </QuestionCard>
      </Col>
      <Col xs={12}>
        <UserAnswerArea
          ref={userAnswer}
          $borderColor={props.theme.primary}
          placeholder='Twoja odpowiedź...'
          onInput={() => fitAreaToContent(userAnswer.current)}
         />
      </Col>
      <ButtonRow className='w-50' $background={props.theme.success}>
        <button style={{ marginBottom: '50px' }} onClick={() => saveAnswer()}>
          Wyślij
        </button>
      </ButtonRow>
    </Row>
  )
}

function mapStateToProps(state) {
  const {theme} = state

  return { theme }
}
export default connect(mapStateToProps)(OpenQuestionPage)
