package com.healthy.gym.task.service;

import com.healthy.gym.task.data.document.TaskDocument;
import com.healthy.gym.task.data.document.UserDocument;
import com.healthy.gym.task.data.repository.TaskDAO;
import com.healthy.gym.task.data.repository.UserDAO;
import com.healthy.gym.task.dto.BasicUserInfoDTO;
import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.enums.GymRole;
import com.healthy.gym.task.exception.EmployeeNotFoundException;
import com.healthy.gym.task.exception.ManagerNotFoundException;
import com.healthy.gym.task.exception.RetroDueDateException;
import com.healthy.gym.task.pojo.request.ManagerOrderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CreateTaskServiceUnitTest {

    @Autowired
    private TaskService taskService;

    @MockBean
    private TaskDAO taskDAO;

    @MockBean
    private UserDAO userDAO;

    private String employeeId;
    private String managerId;
    private String taskId;

    @Test
    void shouldCreateTask_whenValidRequest() throws ManagerNotFoundException, EmployeeNotFoundException, RetroDueDateException {

        employeeId = UUID.randomUUID().toString();
        managerId = UUID.randomUUID().toString();
        taskId = UUID.randomUUID().toString();

        String title = "Przykładowe zadanie";
        String description = "Opis przykładowego zadania";
        var now = LocalDate.now();
        LocalDate dueDate = now.plusMonths(1);

        //request
        ManagerOrderRequest managerOrderRequest = new ManagerOrderRequest();
        managerOrderRequest.setEmployeeId(employeeId);
        managerOrderRequest.setTitle(title);
        managerOrderRequest.setDescription(description);
        managerOrderRequest.setDueDate(dueDate.toString());

        //DB documents
        String employeeName = "Jan";
        String employeeSurname = "Kowalski";
        UserDocument employeeDocument = new UserDocument();
        employeeDocument.setName(employeeName);
        employeeDocument.setSurname(employeeSurname);
        employeeDocument.setUserId(employeeId);
        employeeDocument.setGymRoles(List.of(GymRole.EMPLOYEE));
        employeeDocument.setId("507f1f77bcf86cd799435213");

        String managerName = "Adam";
        String managerSurname = "Nowak";
        UserDocument managerDocument = new UserDocument();
        managerDocument.setName(managerName);
        managerDocument.setSurname(managerSurname);
        managerDocument.setUserId(managerId);
        managerDocument.setGymRoles(List.of(GymRole.MANAGER));
        managerDocument.setId("507f1f77bcf86cd799435002");

        TaskDocument taskDocument = new TaskDocument();
        taskDocument.setTaskId(taskId);
        taskDocument.setManager(managerDocument);
        taskDocument.setEmployee(employeeDocument);
        taskDocument.setTitle(title);
        taskDocument.setDescription(description);
        taskDocument.setLastOrderUpdateDate(now);
        taskDocument.setDueDate(dueDate);
        taskDocument.setEmployeeAccept(AcceptanceStatus.NO_ACTION);
        taskDocument.setManagerAccept(AcceptanceStatus.NO_ACTION);

        //response
        TaskDTO taskResponse = new TaskDTO(
                taskId,
                new BasicUserInfoDTO(managerId, managerName, managerSurname),
                new BasicUserInfoDTO(employeeId, employeeName, employeeSurname),
                title,
                description,
                null,
                null,
                now,
                dueDate,
                null,
                AcceptanceStatus.NO_ACTION,
                AcceptanceStatus.NO_ACTION
        );

        //when
        when(userDAO.findByGymRolesContaining(GymRole.MANAGER)).thenReturn(managerDocument);
        when(userDAO.findByUserId(employeeId)).thenReturn(employeeDocument);
        when(taskDAO.save(any())).thenReturn(taskDocument);

        //then
        assertThat(taskService.createTask(managerOrderRequest)).isEqualTo(taskResponse);
    }

    @Test
    void shouldNotCreateTask_whenManagerNotExist(){
        //before
        //request
        ManagerOrderRequest managerOrderRequest = new ManagerOrderRequest();
        managerOrderRequest.setEmployeeId(employeeId);
        managerOrderRequest.setTitle("Sample title");
        managerOrderRequest.setDescription("Sample description");
        managerOrderRequest.setDueDate(LocalDate.now().plusMonths(1).toString());

        //when
        when(userDAO.findByGymRolesContaining(GymRole.MANAGER)).thenReturn(null);

        //then
        assertThatThrownBy(() ->
                taskService.createTask(managerOrderRequest)
        ).isInstanceOf(ManagerNotFoundException.class);
    }

    @Test
    void shouldNotCreateTask_whenEmployeeNotExist(){
        //before
        //request
        String invalidEmployeeId = UUID.randomUUID().toString();
        ManagerOrderRequest managerOrderRequest = new ManagerOrderRequest();
        managerOrderRequest.setEmployeeId(invalidEmployeeId);
        managerOrderRequest.setTitle("Sample title");
        managerOrderRequest.setDescription("Sample description");
        managerOrderRequest.setDueDate(LocalDate.now().plusMonths(1).toString());


        //DB documents
        String managerName = "Adam";
        String managerSurname = "Nowak";
        UserDocument managerDocument = new UserDocument();
        managerDocument.setName(managerName);
        managerDocument.setSurname(managerSurname);
        managerDocument.setUserId(managerId);
        managerDocument.setGymRoles(List.of(GymRole.MANAGER));
        managerDocument.setId("507f1f77bcf86cd799435002");

        //when
        when(userDAO.findByGymRolesContaining(GymRole.MANAGER)).thenReturn(managerDocument);
        when(userDAO.findByUserId(employeeId)).thenReturn(null);

        //then
        assertThatThrownBy(() ->
                taskService.createTask(managerOrderRequest)
        ).isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void shouldNotCreateTask_whenUserNotEmployee(){
        //before
        employeeId = UUID.randomUUID().toString();
        //request
        String invalidEmployeeId = UUID.randomUUID().toString();
        ManagerOrderRequest managerOrderRequest = new ManagerOrderRequest();
        managerOrderRequest.setEmployeeId(invalidEmployeeId);
        managerOrderRequest.setTitle("Sample title");
        managerOrderRequest.setDescription("Sample description");
        managerOrderRequest.setDueDate(LocalDate.now().plusMonths(1).toString());


        //DB documents
        String managerName = "Adam";
        String managerSurname = "Nowak";
        UserDocument managerDocument = new UserDocument();
        managerDocument.setName(managerName);
        managerDocument.setSurname(managerSurname);
        managerDocument.setUserId(managerId);
        managerDocument.setGymRoles(List.of(GymRole.MANAGER));
        managerDocument.setId("507f1f77bcf86cd799435002");

        String notEmployeeName = "Jan";
        String notEmployeeSurname = "Kowalski";
        UserDocument notEmployeeDocument = new UserDocument();
        notEmployeeDocument.setName(notEmployeeName);
        notEmployeeDocument.setSurname(notEmployeeSurname);
        notEmployeeDocument.setUserId(employeeId);
        notEmployeeDocument.setGymRoles(List.of(GymRole.USER));
        notEmployeeDocument.setId("507f1f77bcf86cd799435213");

        //when
        when(userDAO.findByGymRolesContaining(GymRole.MANAGER)).thenReturn(managerDocument);
        when(userDAO.findByUserId(employeeId)).thenReturn(notEmployeeDocument);

        //then
        assertThatThrownBy(() ->
                taskService.createTask(managerOrderRequest)
        ).isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void shouldNotCreateTask_whenRetroDueDate(){
        //before
        employeeId = UUID.randomUUID().toString();

        //request
        String invalidEmployeeId = UUID.randomUUID().toString();
        ManagerOrderRequest managerOrderRequest = new ManagerOrderRequest();
        managerOrderRequest.setEmployeeId(employeeId);
        managerOrderRequest.setTitle("Sample title");
        managerOrderRequest.setDescription("Sample description");
        managerOrderRequest.setDueDate(LocalDate.now().minusDays(1).toString());


        //DB documents
        String managerName = "Adam";
        String managerSurname = "Nowak";
        UserDocument managerDocument = new UserDocument();
        managerDocument.setName(managerName);
        managerDocument.setSurname(managerSurname);
        managerDocument.setUserId(managerId);
        managerDocument.setGymRoles(List.of(GymRole.MANAGER));
        managerDocument.setId("507f1f77bcf86cd799435002");

        String employeeName = "Jan";
        String employeeSurname = "Kowalski";
        UserDocument employeeDocument = new UserDocument();
        employeeDocument.setName(employeeName);
        employeeDocument.setSurname(employeeSurname);
        employeeDocument.setUserId(employeeId);
        employeeDocument.setGymRoles(List.of(GymRole.EMPLOYEE));
        employeeDocument.setId("507f1f77bcf86cd799435213");

        //when
        when(userDAO.findByGymRolesContaining(GymRole.MANAGER)).thenReturn(managerDocument);
        when(userDAO.findByUserId(employeeId)).thenReturn(employeeDocument);

        //then
        assertThatThrownBy(() ->
                taskService.createTask(managerOrderRequest)
        ).isInstanceOf(RetroDueDateException.class);
    }
}