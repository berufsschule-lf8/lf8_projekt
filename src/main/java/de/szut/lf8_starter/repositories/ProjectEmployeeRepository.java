package de.szut.lf8_starter.repositories;

import de.szut.lf8_starter.entities.ProjectEmployee;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectEmployeeRepository extends JpaRepository<ProjectEmployee, Long> {

  @Query("SELECT pe FROM ProjectEmployee pe WHERE pe.employeeId = :employeeId " +
      "AND pe.startDate <= :endDate AND pe.endDate >= :startDate")
  List<ProjectEmployee> findOverlappingAssignments(
      @Param("employeeId") Long employeeId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate
  );

  List<ProjectEmployee> findByProjectId(Long projectId);

  List<ProjectEmployee> findByEmployeeId(Long employeeId);

  @Query("SELECT pe FROM ProjectEmployee pe WHERE pe.employeeId = :employeeId AND pe.project.id = :projectId")
  Optional<ProjectEmployee> findByProjectIdAndEmployeeId(Long projectId, Long employeeId);
}
