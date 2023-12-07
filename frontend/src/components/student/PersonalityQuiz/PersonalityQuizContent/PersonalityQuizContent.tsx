import { useState } from 'react'

import { Button, Container } from 'react-bootstrap'

import styles from './PersonalityQuizContent.module.scss'
import Question from './Question/Question'
import { useSendPersonalityQuizResultsMutation } from '../../../../api/apiPersonality'
import { QuizQuestion, QuizResults } from '../../../../api/types'

type PersonalityQuizContentProps = {
  quiz: QuizQuestion[]
  setShowQuestions: () => void
}

const PersonalityQuizContent = (props: PersonalityQuizContentProps) => {
  const [sendResults] = useSendPersonalityQuizResultsMutation()
  const [currentQuestion, setCurrentQuestion] = useState<number>(0)
  const [socializerValue, setSocializerValue] = useState<number>(0)
  const [killerValue, setKillerValue] = useState<number>(0)
  const [achieverValue, setAchieverValue] = useState<number>(0)
  const [explorerValue, setExplorerValue] = useState<number>(0)

  const handleSendResults = async () => {
    const results: QuizResults = {
      SOCIALIZER: socializerValue,
      KILLER: killerValue,
      EXPLORER: explorerValue,
      ACHIEVER: achieverValue
    }
    await sendResults(results)
    props.setShowQuestions()
  }

  const handleAnswer = (id: number) => {
    const answer = props.quiz[currentQuestion].answers[id].personalityType
    if (answer === 'SOCIALIZER') {
      setSocializerValue(socializerValue + 1)
    }
    if (answer === 'KILLER') {
      setKillerValue(killerValue + 1)
    }
    if (answer === 'EXPLORER') {
      setExplorerValue(explorerValue + 1)
    }
    if (answer === 'ACHIEVER') {
      setAchieverValue(achieverValue + 1)
    }
    setCurrentQuestion(currentQuestion + 1)
  }

  return (
    <Container className={styles.container}>
      {currentQuestion < props.quiz.length ? (
        <Question question={props.quiz[currentQuestion]} onAnswer={handleAnswer} />
      ) : (
        <div className={styles.info}>
          <p>Wynik testu osobowości oraz jego edycja będą dostępne w zakładce &quot;Ustawienia&quot;.</p>
          <Button variant='primary' type='submit' className={styles.acceptButton} onClick={() => handleSendResults()}>
            Prześlij odpowiedź
          </Button>
        </div>
      )}
    </Container>
  )
}

export default PersonalityQuizContent
