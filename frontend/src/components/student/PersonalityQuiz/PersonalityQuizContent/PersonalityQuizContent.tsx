import { Field, Formik } from 'formik'
import { Container, Form } from 'react-bootstrap'

import styles from './PersonalityQuizContent.module.scss'
import { QuizQuestion } from '../../../../api/types'

type PersonalityQuizContentProps = {
  quiz: QuizQuestion[]
}

const PersonalityQuizContent = (props: PersonalityQuizContentProps) => {
  const initialValues: Record<string, Record<string, boolean>> = {}

  props.quiz.forEach((question, index) => {
    initialValues[`question${index + 1}`] = {}

    question.answers.forEach((answer, answerIndex) => {
      initialValues[`question${index + 1}`][`answer${answerIndex + 1}`] = false
    })
  })

  return (
    <Container>
      <Formik
        initialValues={initialValues}
        onSubmit={(values) => {
          console.log('User answers:', values)
        }}
      >
        <Form>
          {props.quiz.map((question, index) => (
            <div key={index} className={styles.testQuestionContainer}>
              <p className={styles.testQuestion}>{question.question}</p>
              {question.answers.map((answer, answerIndex) => (
                <div key={answerIndex}>
                  <label className={styles.radioLabel}>
                    <Field
                      type='radio'
                      name={`question${index + 1}`}
                      value={`answer${answerIndex + 1}`}
                      className={styles.radioInput}
                    />
                    {answer.content}
                  </label>
                </div>
              ))}
            </div>
          ))}
          <button type='submit'>Submit</button>
        </Form>
      </Formik>
    </Container>
  )
}

export default PersonalityQuizContent
