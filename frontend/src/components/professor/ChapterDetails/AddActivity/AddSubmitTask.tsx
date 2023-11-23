import AddActivity from './AddActivity'
import SubmitTaskService from '../../../../services/submitTask.service'
import {Activity} from '../../../../utils/constants'

type AddSubmitTaskProps = {
    chapterId: number
    onSuccess: boolean
}

const AddSubmitTask = (props: AddSubmitTaskProps) => (
    <AddActivity
        getActivityJson={SubmitTaskService.getFileTaskJson}
        setActivityJson={SubmitTaskService.setFileTaskJson}
        chapterId={props.chapterId}
        onSuccess={props.onSuccess}
        activityType={Activity.SUBMIT}
    />
)

export default AddSubmitTask