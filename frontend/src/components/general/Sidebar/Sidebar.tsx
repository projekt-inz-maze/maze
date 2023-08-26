import React from 'react'

import { faFire } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { Container, Nav } from 'react-bootstrap'
import Navbar from 'react-bootstrap/Navbar'
import { Link, useNavigate } from 'react-router-dom'

import styles from './Sidebar.module.scss'
import { logout } from '../../../actions/auth'


type SidebarProps = {
    sidebarTitles: { name: string, navigateTo: string }[]
    userSubtitles: { name: string, navigateTo: string }[]
}

const Sidebar = (props: SidebarProps) => {
    const navigate = useNavigate()

    return (
        <Navbar expand='sm' className={`${styles.navbar} bg-body-tertiary`}>
            <Container fluid style={{ width: '100%', margin: 0, padding: 0 }}>
                <Navbar.Toggle aria-controls='basic-navbar-nav' />
                <Navbar.Collapse id='basic-navbar-nav' className={styles.flow}>
                    <Nav className={`${styles.upContainer}`}>
                        <Nav.Link href='#home' className={styles.navBrand}>
                            <FontAwesomeIcon icon={faFire} size='2x' style={{ color: '#ffb30d' }} />
                            <span>Maze</span>
                        </Nav.Link>
                        <div className={styles.upContainer}>
                            {props.sidebarTitles.map((link) => (
                                <div key={link.navigateTo}>
                                    <Nav.Link
                                        as={Link}
                                        to={link.navigateTo}
                                        onClick={link.name === 'Wyloguj' ? logout(navigate) : undefined}
                                    >
                                        <span>{link.name}</span>
                                    </Nav.Link>
                                </div>
                            ))}
                        </div>
                    </Nav>
                    <Nav className={styles.downContainer}>
                        {props.userSubtitles.map((link) => (
                            <div key={`${link.navigateTo}userSubtitle`}>
                                <Nav.Link
                                    as={Link}
                                    to={link.navigateTo}
                                >
                                    <span>{link.name}</span>
                                </Nav.Link>
                            </div>
                        ))}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    )
}

export default Sidebar
