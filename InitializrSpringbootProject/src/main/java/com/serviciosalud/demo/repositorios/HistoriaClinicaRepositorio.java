package com.serviciosalud.demo.repositorios;

import com.serviciosalud.demo.entidades.HistoriaClinica;
import com.serviciosalud.demo.entidades.Paciente;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoriaClinicaRepositorio extends JpaRepository<HistoriaClinica, String>{

    @Query("SELECT h FROM HistoriaClinica h WHERE h.paciente = :idPaciente")
    public Optional<HistoriaClinica> buscarPorPaciente( @Param("idPaciente") Paciente Paciente);

}