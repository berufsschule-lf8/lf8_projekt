package de.szut.lf8_starter.services;

import de.szut.lf8_starter.client.EmployeeServiceClient;
import de.szut.lf8_starter.dtos.create.CreateProjectDto;
import de.szut.lf8_starter.dtos.get.GetEmployeeDto;
import de.szut.lf8_starter.dtos.get.GetProjectDto;
import de.szut.lf8_starter.entities.Project;
import de.szut.lf8_starter.entities.ProjectEmployee;
import de.szut.lf8_starter.repositories.ProjectEmployeeRepository;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.repositories.ProjectRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProjectService {

  private final ProjectRepository projectRepository;
  private final ProjectEmployeeRepository projectEmployeeRepository;
  private final EmployeeServiceClient employeeServiceClient;

  public ProjectService(ProjectRepository projectRepository, ProjectEmployeeRepository projectEmployeeRepository, EmployeeServiceClient employeeServiceClient) {
    this.projectRepository = projectRepository;
    this.projectEmployeeRepository = projectEmployeeRepository;
    this.employeeServiceClient = employeeServiceClient;
  }

  public GetProjectDto createProject(CreateProjectDto createProjectDto) {
    Project project = mapToEntity(createProjectDto);
    Project savedProject = projectRepository.save(project);
    log.info("Created project {}", savedProject.getBezeichnung());
    return mapToDto(savedProject);
  }

  public GetProjectDto addEmployeeToProject(Long projectId, Long employeeId) {
      Optional<Project> projectOptional = projectRepository.findById(projectId);
      if (projectOptional.isEmpty()) {
          log.error("Could not find project with id {}", projectId);
          return null;
      }
      GetEmployeeDto employee = employeeServiceClient.getEmployeeById(employeeId);
      if(employee == null) {
          log.error("Could not find employee with id {}", employeeId);
          return null;
      }
      List<ProjectEmployee> overlapping = projectEmployeeRepository.findOverlappingAssignments(employeeId, projectOptional.get().getStartdatum(), projectOptional.get().getGeplantesEnddatum());
      if(!overlapping.isEmpty()) {
          log.error("Employee {} is already assigned to project {}", employeeId, projectId);
          return null;
      }
      ProjectEmployee projectEmployee = new ProjectEmployee();
      projectEmployee.setProject(projectOptional.get());
      projectEmployee.setEmployeeId(employeeId);
      projectEmployee.setStartDate(projectOptional.get().getStartdatum());
      projectEmployee.setEndDate(projectOptional.get().getGeplantesEnddatum());
      projectEmployeeRepository.save(projectEmployee);
      log.info("Added employee {} to project {}", employeeId, projectId);
      return mapToDto(projectOptional.get());
  }

  public void delete(long id) {
    Optional<Project> optionalProject = projectRepository.findById(id);

    if (optionalProject.isEmpty()) {
      throw new ResourceNotFoundException("Project by id = " + id + " was not found.");
    }
    projectRepository.deleteById(id);
  }

  public List<GetProjectDto> getAllProjects() {
    return projectRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
  }

  public GetProjectDto getProjectById(Long id) {
    Optional<Project> project = projectRepository.findById(id);
    if (project.isPresent()) {
      log.info("Found project {}", project.get().getBezeichnung());
      return mapToDto(project.get());
    } else {
      log.error("Project not found with id {}", id);
      throw new ResourceNotFoundException("There is no project with id: " + id);
    }
  }

    public GetProjectDto update(Long id, CreateProjectDto updateProject) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project by id = " + id + " was not found and therefore could not be updated."
                ));

        project.setBezeichnung(updateProject.getBezeichnung());
        project.setVerantwortlicherMitarbeiterId(updateProject.getVerantwortlicherMitarbeiterId());
        project.setKundenId(updateProject.getKundenId());
        project.setKundenansprechpartner(updateProject.getKundenansprechpartner());
        project.setKommentar(updateProject.getKommentar());
        project.setStartdatum(updateProject.getStartdatum());
        project.setGeplantesEnddatum(updateProject.getGeplantesEnddatum());

        Project updatedProject = projectRepository.save(project);
        return mapToDto(project);
    }

  private Project mapToEntity(CreateProjectDto dto) {
    Project project = new Project();
    project.setBezeichnung(dto.getBezeichnung());
    project.setVerantwortlicherMitarbeiterId(dto.getVerantwortlicherMitarbeiterId());
    project.setKundenId(dto.getKundenId());
    project.setKundenansprechpartner(dto.getKundenansprechpartner());
    project.setKommentar(dto.getKommentar());
    project.setStartdatum(dto.getStartdatum());
    project.setGeplantesEnddatum(dto.getGeplantesEnddatum());
    return project;
  }

  private GetProjectDto mapToDto(Project project) {
    GetProjectDto dto = new GetProjectDto();
    dto.setId(project.getId());
    dto.setBezeichnung(project.getBezeichnung());
    dto.setVerantwortlicherMitarbeiterId(project.getVerantwortlicherMitarbeiterId());
    dto.setKundenId(project.getKundenId());
    dto.setKundenansprechpartner(project.getKundenansprechpartner());
    dto.setKommentar(project.getKommentar());
    dto.setStartdatum(project.getStartdatum());
    dto.setGeplantesEnddatum(project.getGeplantesEnddatum());
    dto.setTatsaechlichesEnddatum(project.getTatsaechlichesEnddatum());
    return dto;
  }
}
