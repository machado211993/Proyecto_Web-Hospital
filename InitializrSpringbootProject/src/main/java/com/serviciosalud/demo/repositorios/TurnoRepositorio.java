package com.serviciosalud.demo.repositorios;

import com.serviciosalud.demo.entidades.Turno;
import com.serviciosalud.demo.entidades.Paciente;
import com.serviciosalud.demo.enumeraciones.Estado;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TurnoRepositorio extends JpaRepository<Turno, String> {

    @Query("SELECT t FROM Turno t WHERE t.fecha= :fecha")
    public Optional<List<Turno>> buscarPorFecha(@Param("fecha") String fecha);

    @Query("SELECT t FROM Turno t WHERE t.paciente.id = :id")
    public List<Turno> buscarPorPaciente(@Param("id") String idPaciente);

    @Query("SELECT t FROM Turno t WHERE t.profesional.id = :id")
    public List<Turno> buscarPorProfesional(@Param("id") String idProfesional);

    @Query("SELECT t FROM Turno t WHERE t.profesional.id = :idProfesional AND t.fecha = :fechaHoy")
    public Optional<List<Turno>> buscarTurnoDeHoy(@Param("fechaHoy") String fechaHoy, @Param("idProfesional") String idProfesional);

    @Query("SELECT t FROM Turno t WHERE t.profesional.id = :idProfesional ORDER BY t.fecha ASC, t.horario ASC")
    public Optional<List<Turno>> ordenarTurnosPorFecha( @Param("idProfesional") String idProfesional);

    @Query("SELECT t FROM Turno t WHERE t.profesional.id = :idProfesional ORDER BY t.paciente.apellido ASC")
    public Optional<List<Turno>> ordenarTurnosPorPacientes( @Param("idProfesional") String idProfesional);

    
//    @Query("SELECT l FROM Libro l WHERE l.autor.nombre = :nombre")
//    public List<Libro> buscarPorAutor(@Param("nombre") String nombre);
}
