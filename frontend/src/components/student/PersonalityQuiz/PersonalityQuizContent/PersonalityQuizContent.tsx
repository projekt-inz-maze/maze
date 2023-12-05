import { QuizQuestion } from '../../../../api/types'

type PersonalityQuizContentProps = {
  quiz: QuizQuestion[]
}
const PersonalityQuizContent = (props: PersonalityQuizContentProps) => {
  const placeholder = 'Content'
  const { quiz } = props

  return <div>{placeholder}</div>
}

export default PersonalityQuizContent
