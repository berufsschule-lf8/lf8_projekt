package de.szut.lf8_starter.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Data
@Table(name = "projects")
public class Project {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String bezeichnung;

  @Column(name = "veranwortlicher_mitarbeiter", nullable = false)
  private Long verantwortlicherMitarbeiterId;

  @Column(name = "kunden_id", nullable = false)
  private Long kundenId;

  @Column(name = "kundenansprechpartner", nullable = false)
  private String kundenansprechpartner;

  @Column(columnDefinition = "TEXT")
  private String kommentar;

  @Column(name = "startdatum", nullable = false)
  private LocalDate startdatum;

  @Column(name = "geplantes_enddatum")
  private LocalDate geplantesEnddatum;

  @Column(name = "tatsaechliches_enddatum")
  private LocalDate tatsaechlichesEnddatum;
}
