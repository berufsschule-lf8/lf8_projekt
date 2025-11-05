package de.szut.lf8_starter.integration;

import static de.szut.lf8_starter.integration.ProjectIntegrationTest.createMockEmployee;
import static de.szut.lf8_starter.integration.ProjectIntegrationTest.createTestProject;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.szut.lf8_starter.client.EmployeeServiceClient;
import de.szut.lf8_starter.dtos.get.GetEmployeeDto;
import de.szut.lf8_starter.entities.Project;
import de.szut.lf8_starter.entities.ProjectEmployee;
import de.szut.lf8_starter.repositories.ProjectEmployeeRepository;
import de.szut.lf8_starter.repositories.ProjectRepository;
import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class EmployeeIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private EmployeeServiceClient employeeServiceClient;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private ProjectEmployeeRepository projectEmployeeRepository;

  @BeforeEach
  void setUp() {
    projectEmployeeRepository.deleteAll();
    projectRepository.deleteAll();
  }

  @Test
  void testGetAllProjectsByEmployeeId() throws Exception {
    GetEmployeeDto employee = createMockEmployee(1L);
    when(employeeServiceClient.getEmployeeById(1L)).thenReturn(employee);

    Project project = createTestProject("Test Project");
    Project savedProject = projectRepository.save(project);

    ProjectEmployee pe = new ProjectEmployee();
    pe.setProject(savedProject);
    pe.setEmployeeId(1L);
    pe.setStartDate(LocalDate.now());
    pe.setEndDate(LocalDate.now().plusMonths(6));
    projectEmployeeRepository.save(pe);

    mockMvc.perform(get("/api/v1/employees/1/projects"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.employeeId").value(1))
        .andExpect(jsonPath("$.projects", hasSize(1)))
        .andExpect(jsonPath("$.projects[0].projectDescription").value("Test Project"));
  }

    @Test
    void testGetAllProjectsByEmployeeIdNoProjects() throws Exception {
        GetEmployeeDto employee = createMockEmployee(1L);
        when(employeeServiceClient.getEmployeeById(1L)).thenReturn(employee);

        mockMvc.perform(get("/api/v1/employees/1/projects"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Could not find projects for employee with id: 1"));
    }

  // POSITIVFALL
  @Test
  void testDeleteEmployeeFromProject() throws Exception {

    GetEmployeeDto mockEmployee = createMockEmployee(1L);
    when(employeeServiceClient.getEmployeeById(1L)).thenReturn(mockEmployee);

    Project testProject = createTestProject("Test Project");
    Project savedProject = projectRepository.save(testProject);
    Long projectId = savedProject.getId();

    ProjectEmployee projectEmployee = new ProjectEmployee();
    projectEmployee.setProject(savedProject);
    projectEmployee.setEmployeeId(1L);
    projectEmployee.setStartDate(LocalDate.now());
    projectEmployee.setEndDate(LocalDate.now().plusMonths(3));
    projectEmployee.setQualification("Java Developer");
    projectEmployeeRepository.save(projectEmployee);

    mockMvc.perform(delete("/api/v1/projects/{projectId}/employees/{employeeId}", projectId, 1L))
            .andExpect(status().isNoContent());

    List<ProjectEmployee> employeesAfterDelete = projectEmployeeRepository.findByProjectId(projectId);

    org.assertj.core.api.Assertions.assertThat(employeesAfterDelete)
            .as("Project should have no employees after deletion")
            .isEmpty();

    List<ProjectEmployee> employeeAssignments = projectEmployeeRepository.findByEmployeeId(1L);

    org.assertj.core.api.Assertions.assertThat(employeeAssignments)
            .as("Employee should have no remaining project assignments")
            .isEmpty();
  }

  // NEGATIVFALL 1
  @Test
  void testDeleteEmployeeFromProject_ProjectNotFound() throws Exception {

    GetEmployeeDto mockEmployee = createMockEmployee(1L);
    when(employeeServiceClient.getEmployeeById(1L)).thenReturn(mockEmployee);

    // Deleting from a project that does not exist
    mockMvc.perform(delete("/api/v1/projects/{projectId}/employees/{employeeId}", 999L, 1L))
            .andExpect(status().isNotFound());
  }

  // NEGATIVFALL 2
  @Test
  void testDeleteEmployeeFromProject_EmployeeNotInProject() throws Exception {
    GetEmployeeDto mockEmployee = createMockEmployee(1L);
    when(employeeServiceClient.getEmployeeById(1L)).thenReturn(mockEmployee);

    // Creating a project but not assigning the employee
    Project testProject = createTestProject("Unassigned Project");
    Project savedProject = projectRepository.save(testProject);
    Long projectId = savedProject.getId();

    // Attempt of deleting an employee that is unassigned to the project
    mockMvc.perform(delete("/api/v1/projects/{projectId}/employees/{employeeId}", projectId, 1L))
            .andExpect(status().isNotFound());
  }
}
