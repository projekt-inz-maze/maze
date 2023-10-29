package com.example.api.unit.service.activity.result;

import com.example.api.activity.result.dto.SaveFileToFileTaskResultForm;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.security.AuthenticationService;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.util.model.File;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.task.filetask.FileTaskRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.util.repository.FileRepository;
import com.example.api.activity.result.service.FileTaskResultService;
import com.example.api.validator.UserValidator;
import com.example.api.activity.validator.ActivityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class FileTaskResultServiceTest {
    private FileTaskResultService fileTaskResultService;
    @Mock private FileTaskResultRepository fileTaskResultRepository;
    @Mock private FileTaskRepository fileTaskRepository;
    @Mock private UserRepository userRepository;
    @Mock private FileRepository fileRepository;
    @Mock private UserValidator userValidator;
    @Mock private AuthenticationService authService;
    @Mock private Authentication authentication;
    @Mock private ActivityValidator activityValidator;
    FileTaskResult result;
    FileTask fileTask;
    @Captor  ArgumentCaptor<Long> idArgumentCaptor;
    @Captor ArgumentCaptor<String> stringArgumentCaptor;
    @Captor ArgumentCaptor<User> userArgumentCaptor;
    @Captor ArgumentCaptor<FileTask> fileTaskArgumentCaptor;
    @Captor ArgumentCaptor<FileTaskResult> fileTaskResultArgumentCaptor;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        fileTaskResultService = new FileTaskResultService(
                fileTaskResultRepository,
                fileTaskRepository,
                userRepository,
                fileRepository,
                userValidator,
                null,
                activityValidator
        );
        fileTask = new FileTask();
        result = new FileTaskResult();
        result.setFileTask(fileTask);
    }

    @Test
    public void saveFileTaskResult() {
        //given
        //when
        fileTaskResultService.saveFileTaskResult(result);
        //then
        verify(fileTaskResultRepository).save(fileTaskResultArgumentCaptor.capture());
        FileTaskResult capturedResult = fileTaskResultArgumentCaptor.getValue();
        assertThat(capturedResult).isEqualTo(result);
    }

    @Test
    public void saveFileToFileTaskResultWhenFileIsNull() throws WrongUserTypeException, EntityNotFoundException, IOException {
        //given
        User user = new User();
        user.setEmail("random@email.com");
        user.setAccountType(AccountType.STUDENT);
        SaveFileToFileTaskResultForm form = new SaveFileToFileTaskResultForm(
                fileTask.getId(),
                "",
                new MockMultipartFile("randomName", new byte[1024]),
                null
        );
        given(authService.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("random@email.com");
        given(fileTaskRepository.findFileTaskById(fileTask.getId())).willReturn(fileTask);
        given(userRepository.findUserByEmail(user.getEmail())).willReturn(user);
        given(fileTaskResultRepository.findFileTaskResultByFileTaskAndUser(fileTask, user)).willReturn(result);

        //when
        fileTaskResultService.saveFileToFileTaskResult(form);

        //then
        verify(fileTaskRepository).findFileTaskById(idArgumentCaptor.capture());
        verify(userRepository).findUserByEmail(stringArgumentCaptor.capture());
        verify(fileTaskResultRepository).findFileTaskResultByFileTaskAndUser(fileTaskArgumentCaptor.capture(),
                userArgumentCaptor.capture());
        Long capturedId = idArgumentCaptor.getValue();
        String capturedEmail = stringArgumentCaptor.getValue();
        User capturedUser = userArgumentCaptor.getValue();
        FileTask capturedFileTask = fileTaskArgumentCaptor.getValue();
        assertThat(capturedId).isEqualTo(fileTask.getId());
        assertThat(capturedEmail).isEqualTo(user.getEmail());
        assertThat(capturedUser).isEqualTo(user);
        assertThat(capturedFileTask).isEqualTo(fileTask);
    }

    @Test
    public void saveFileToFileTaskResultWhenResultIsNull() throws WrongUserTypeException, EntityNotFoundException, IOException {
        //given
        User user = new User();
        user.setEmail("random@email.com");
        user.setAccountType(AccountType.STUDENT);
        SaveFileToFileTaskResultForm form = new SaveFileToFileTaskResultForm(
                fileTask.getId(),
                "",
                new MockMultipartFile("randomName", new byte[1024]),
                null
        );
        given(authService.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("random@email.com");
        given(fileTaskRepository.findFileTaskById(fileTask.getId())).willReturn(fileTask);
        given(userRepository.findUserByEmail(user.getEmail())).willReturn(user);
        given(fileTaskResultRepository.findFileTaskResultByFileTaskAndUser(fileTask, user)).willReturn(null);

        //when
        fileTaskResultService.saveFileToFileTaskResult(form);

        //then
        verify(fileTaskRepository, times(2)).findFileTaskById(idArgumentCaptor.capture());
        verify(authService, times(1)).getAuthentication();
        Long capturedId = idArgumentCaptor.getValue();
        String capturedEmail = stringArgumentCaptor.getValue();
        assertThat(capturedId).isEqualTo(fileTask.getId());
        assertThat(capturedEmail).isEqualTo(user.getEmail());
    }

    @Test
    public void saveFileToFileTaskResultWhenOpenAnswerIsNull() throws WrongUserTypeException, EntityNotFoundException, IOException {
        //given
        User user = new User();
        user.setEmail("random@email.com");
        user.setAccountType(AccountType.STUDENT);
        SaveFileToFileTaskResultForm form = new SaveFileToFileTaskResultForm(
                fileTask.getId(),
                null,
                new MockMultipartFile("randomName", new byte[1024]),
                ""
        );
        given(authService.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("random@email.com");
        given(fileTaskRepository.findFileTaskById(fileTask.getId())).willReturn(fileTask);
        given(userRepository.findUserByEmail(user.getEmail())).willReturn(user);
        given(fileTaskResultRepository.findFileTaskResultByFileTaskAndUser(fileTask, user)).willReturn(result);

        //when
        fileTaskResultService.saveFileToFileTaskResult(form);

        //then
        verify(fileTaskRepository).findFileTaskById(idArgumentCaptor.capture());
        verify(userRepository).findUserByEmail(stringArgumentCaptor.capture());
        verify(fileTaskResultRepository).findFileTaskResultByFileTaskAndUser(fileTaskArgumentCaptor.capture(),
                userArgumentCaptor.capture());
        Long capturedId = idArgumentCaptor.getValue();
        String capturedEmail = stringArgumentCaptor.getValue();
        User capturedUser = userArgumentCaptor.getValue();
        FileTask capturedFileTask = fileTaskArgumentCaptor.getValue();
        assertThat(capturedId).isEqualTo(fileTask.getId());
        assertThat(capturedEmail).isEqualTo(user.getEmail());
        assertThat(capturedUser).isEqualTo(user);
        assertThat(capturedFileTask).isEqualTo(fileTask);
    }

    @Test
    public void deleteFileFromFileTask() throws WrongUserTypeException, EntityNotFoundException {
        //given
        User user = new User();
        user.setEmail("random@email.com");
        user.setAccountType(AccountType.STUDENT);
        given(authService.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("random@email.com");
        given(fileTaskRepository.findFileTaskById(fileTask.getId())).willReturn(fileTask);
        given(userRepository.findUserByEmail(user.getEmail())).willReturn(user);
        given(fileTaskResultRepository.findFileTaskResultByFileTaskAndUser(fileTask, user)).willReturn(result);
        result.getFiles().add(new File());

        //when
        fileTaskResultService.deleteFileFromFileTask(fileTask.getId(), 0);

        //then
        verify(fileTaskRepository).findFileTaskById(idArgumentCaptor.capture());
        verify(userRepository).findUserByEmail(stringArgumentCaptor.capture());
        verify(fileTaskResultRepository).findFileTaskResultByFileTaskAndUser(fileTaskArgumentCaptor.capture(),
                userArgumentCaptor.capture());
        Long capturedId = idArgumentCaptor.getValue();
        String capturedEmail = stringArgumentCaptor.getValue();
        User capturedUser = userArgumentCaptor.getValue();
        FileTask capturedFileTask = fileTaskArgumentCaptor.getValue();
        assertThat(capturedId).isEqualTo(fileTask.getId());
        assertThat(capturedEmail).isEqualTo(user.getEmail());
        assertThat(capturedUser).isEqualTo(user);
        assertThat(capturedFileTask).isEqualTo(fileTask);
    }

    @Test
    public void getFileById() throws EntityNotFoundException {
        //given
        File file = new File();
        file.setFile(new byte[1024]);
        given(fileRepository.findFileById(file.getId())).willReturn(file);

        //when
        fileTaskResultService.getFileById(file.getId());

        //then
        verify(fileRepository).findFileById(idArgumentCaptor.capture());
        Long capturedId = idArgumentCaptor.getValue();
        assertThat(capturedId).isEqualTo(file.getId());
    }
}
