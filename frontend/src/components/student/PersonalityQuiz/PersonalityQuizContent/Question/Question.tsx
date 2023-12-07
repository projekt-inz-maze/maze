import React, { useState } from 'react'

import { Button } from 'react-bootstrap'

import styles from './Question.module.scss'
import { QuizQuestion } from '../../../../../api/types'

type QuestionProps = {
  question: QuizQuestion
  onAnswer: (arg: number) => void
}

const Question = (props: QuestionProps) => {
  const [checked, setChecked] = useState<string>('')

  return (
    <div className={styles.testQuestionContainer}>
      <p className={styles.testQuestion}>{props.question.question}</p>
      <div key='0'>
        <label className={styles.radioLabel}>
          <input
            type='radio'
            name='question'
            checked={checked === '0'}
            value='0'
            className={styles.radioInput}
            onChange={(event) => setChecked(event.target.value)}
          />
          {props.question.answers[0].content}
        </label>
      </div>
      <div key='1'>
        <label className={styles.radioLabel}>
          <input
            type='radio'
            name='question'
            checked={checked === '1'}
            value='1'
            className={styles.radioInput}
            onChange={(event) => setChecked(event.target.value)}
          />
          {props.question.answers[1].content}
        </label>
      </div>
      <Button
        variant='primary'
        type='submit'
        className={styles.acceptButton}
        onClick={() => props.onAnswer(parseInt(checked, 10))}
      >
        Następne pytanie
      </Button>
    </div>
  )
}

export default Question
