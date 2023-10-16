import { useEffect, useState } from 'react'

import { Tab } from 'react-bootstrap'
import { connect } from 'react-redux'

import { GradesContent, TabsContainer } from './GradeListAndExportStyles'
import UsersTable from './UsersTable'
import { useAppSelector } from '../../../hooks/hooks'
import GroupService from '../../../services/group.service'
import Loader from '../../general/Loader/Loader'

function GradeListAndExport(props) {
  const [allGroups, setAllGroups] = useState(undefined)

  const courseId = useAppSelector((state) => state.user.courseId)

  useEffect(() => {
    GroupService.getGroups(courseId)
      .then((response) => setAllGroups(response))
      .catch(() => setAllGroups(null))
  }, [])

  return (
    <GradesContent>
      <TabsContainer
        $background={props.theme.success}
        $linkColor={props.theme.primary}
        $fontColor={props.theme.background}
      >
        <Tab eventKey='wszyscy' title='Wszyscy'>
          <UsersTable />
        </Tab>

        {allGroups
          ? allGroups.map((group, index) => (
              <Tab key={index + group.name} title={group.name} eventKey={group.name}>
                <UsersTable groupId={group.id} groupName={group.name} />
              </Tab>
            ))
          : allGroups === undefined && <Loader />}
      </TabsContainer>
    </GradesContent>
  )
}

function mapStateToProps(state) {
  const { theme } = state
  return {
    theme
  }
}

export default connect(mapStateToProps)(GradeListAndExport)
