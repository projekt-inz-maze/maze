import { Container } from 'react-bootstrap'

import { QuizQuestion } from '../../../../api/types'

type PersonalityQuizContentProps = {
  quiz: QuizQuestion[]
}
const PersonalityQuizContent = (props: PersonalityQuizContentProps) => {
  const placeholder = 'Content'

  return (
    <Container>
      {props.quiz.map((question) => (
        <div>
          <p>{question.question}</p>
          <div>
            {question.answers.map((answer) => (
              <p>{answer.content}</p>
            ))}
          </div>
        </div>
      ))}
    </Container>
  )
}

export default PersonalityQuizContent
