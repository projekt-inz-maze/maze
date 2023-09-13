import React from 'react'

import {Container, Row, Col} from 'react-bootstrap'

const GameMap = () => (
    <Container fluid>
        <Row>
            <Col xs={12} md={3} className="p-0">
                <div className="image-container" style={{ paddingBottom: '75%' }}>
                    <img
                        src="/map/map-left.png"
                        alt="Image 1"
                        className="img-fluid"
                        style={{ position: 'absolute', width: '100%', height: '100%', backgroundSize: '100%'}}
                    />
                </div>
            </Col>
            <Col xs={12} md={9} className="p-0">
                <div className="image-container" style={{ paddingBottom: '25%' }}>
                    <img
                        src="/map/forest_background.jpg"
                        alt="Image 2"
                        className="img-fluid"
                        style={{ position: 'absolute', width: '100%', height: '100%' }}
                    />
                </div>
            </Col>
        </Row>
    </Container>
    )

export default GameMap
