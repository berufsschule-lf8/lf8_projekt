package de.szut.lf8_starter.repositories;

import de.szut.lf8_starter.entities.ProjectEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectEmployeeRepository extends JpaRepository<ProjectEmployee, Long> {
    @Query("SELECT pe FROM ProjectEmployee pe WHERE pe.employeeId = :employeeId " +
            "AND pe.startDate <= :endDate AND pe.endDate >= :startDate")
    List<ProjectEmployee> findOverlappingAssignments(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
