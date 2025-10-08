package de.szut.lf8_starter.controller;

import de.szut.lf8_starter.dtos.create.CreateProjectDto;
import de.szut.lf8_starter.dtos.get.GetProjectDto;
import de.szut.lf8_starter.services.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

  private final ProjectService projectService;

  public ProjectController(ProjectService projectService) {
    this.projectService = projectService;
  }


  @PostMapping
  public ResponseEntity<GetProjectDto> createProject(@Valid @RequestBody CreateProjectDto createProjectDto) {
    GetProjectDto createProject = projectService.createProject(createProjectDto);
    return new ResponseEntity<>(createProject, HttpStatus.CREATED);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(code=HttpStatus.NO_CONTENT)
  public void deleteProjectById(@PathVariable final Long id){
    this.projectService.delete(id);
  }
}
