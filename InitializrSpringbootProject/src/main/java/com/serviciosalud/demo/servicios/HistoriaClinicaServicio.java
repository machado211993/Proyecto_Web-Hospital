package com.serviciosalud.demo.servicios;

import com.serviciosalud.demo.MiExcepcion.MiExcepcion;
import com.serviciosalud.demo.entidades.HistoriaClinica;
import com.serviciosalud.demo.entidades.Paciente;
import com.serviciosalud.demo.entidades.Profesional;
import com.serviciosalud.demo.entidades.Turno;
import com.serviciosalud.demo.enumeraciones.Estado;
import com.serviciosalud.demo.repositorios.HistoriaClinicaRepositorio;
import com.serviciosalud.demo.repositorios.TurnoRepositorio;
import com.serviciosalud.demo.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoriaClinicaServicio {

    @Autowired
    TurnoRepositorio turnoRepositorio;

    @Autowired
    HistoriaClinicaRepositorio historiaClinicaRepositorio;

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    public void registrar(String idTurno, String observaciones) {

        Optional<Turno> respuesta = turnoRepositorio.findById(idTurno); //buscar el turno

        HistoriaClinica historiaClinica = new HistoriaClinica();
        List<Turno> turnos = new ArrayList<>();
        
        if (respuesta.isPresent()) { //si turno esta presente (siempre está presente)
            Turno turno = respuesta.get();  // se trae la respuesta 
            turno.setEstado(Estado.ATENDIDO); // el estado del turno pasa a ser 'ATENDIDO'
            turno.setObservaciones(observaciones); // seteamos las observaciones al turno atendido

            // se busca si ya exista una historia clinica del paciente de ese turno
            Optional<HistoriaClinica> respuestaHc = historiaClinicaRepositorio.buscarPorPaciente(turno.getPaciente());

            if (respuestaHc.isPresent()) { //si esta presente la hc
                historiaClinica = respuestaHc.get(); //se trae la hc
                historiaClinica.getTurnos().add(turno); // unicamente agregamos el turno a la List de turnos

                historiaClinicaRepositorio.save(historiaClinica); // guardamos la hc otra vez

            } else { // si no esta presente la historia clinica, si es la 1° vez que se va a crear una hc con este paciente:

                turnos.add(turno);  // creamos una lista de turnos y agregamos el turno
                historiaClinica.setTurnos(turnos); // seteamos la lista de turnos anterior a la hc

                historiaClinica.setPaciente(turno.getPaciente());  //seteamos el paciente

                historiaClinicaRepositorio.save(historiaClinica); //guardamos la hc por 1° vez
            }
        }

    }

    public HistoriaClinica buscarPorPaciente(Paciente paciente) throws MiExcepcion {
        Optional<HistoriaClinica> respuesta = historiaClinicaRepositorio.buscarPorPaciente(paciente);

        if (respuesta.isPresent()) {
            return respuesta.get();
        } else {
            throw new MiExcepcion("No hay historia clínica de este Paciente");
        }
    }

}