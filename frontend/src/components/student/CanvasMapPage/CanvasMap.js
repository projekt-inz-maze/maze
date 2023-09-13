import React, { useEffect, useRef } from 'react'

import {Container} from 'react-bootstrap'

import { GameMap } from './canvasCreator'

export default function CanvasMap() {
  const canvas = useRef(null)

  useEffect(() => {
    const map = new GameMap(canvas.current, 'warrior')
    map.run()
  }, [canvas])

  return (
    <Container>
      <canvas ref={canvas} style={{ border: '1px solid black' }} />
    </Container>
  )
}
