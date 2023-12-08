import { Form } from 'react-bootstrap'

export function ProfessorFileService({ setFile, setFileName, fileRef }) {
  const saveFile = (event) => {
    const filename = event.target.value.split(/(\\|\/)/g).pop()
    setFileName(filename)
    setFile(event.target.files[0])
  }

  return (
    <Form.Group>
      <Form.Label>
        <span>Załącz pliki:</span>
      </Form.Label>
      <Form.Control ref={fileRef} type='file' onChange={saveFile} />
    </Form.Group>
  )
}
