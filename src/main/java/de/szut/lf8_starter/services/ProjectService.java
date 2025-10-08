package de.szut.lf8_starter.services;

import de.szut.lf8_starter.dtos.create.CreateProjectDto;
import de.szut.lf8_starter.dtos.get.GetProjectDto;
import de.szut.lf8_starter.entities.Project;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.repositories.ProjectRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ProjectService {

  private final ProjectRepository projectRepository;

  public ProjectService(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  public GetProjectDto createProject(CreateProjectDto createProjectDto) {
    Project project = mapToEntity(createProjectDto);
    Project savedProject = projectRepository.save(project);
    log.info("Created project {}", savedProject.getBezeichnung());
    return mapToDto(savedProject);
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
