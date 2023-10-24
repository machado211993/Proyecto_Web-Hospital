package com.serviciosalud.demo.controladores;

import com.serviciosalud.demo.MiExcepcion.MiExcepcion;
import com.serviciosalud.demo.entidades.HistoriaClinica;
import com.serviciosalud.demo.entidades.Paciente;
import com.serviciosalud.demo.entidades.Profesional;
import com.serviciosalud.demo.entidades.Turno;
import com.serviciosalud.demo.repositorios.HistoriaClinicaRepositorio;
import com.serviciosalud.demo.repositorios.TurnoRepositorio;
import com.serviciosalud.demo.repositorios.UsuarioRepositorio;
import com.serviciosalud.demo.servicios.HistoriaClinicaServicio;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/historia-clinica")
public class HistoriaClinicaControlador {

    @Autowired
    TurnoRepositorio turnoRepositorio;

    @Autowired
    HistoriaClinicaServicio historiaClinicaServicio;

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    @Autowired
    HistoriaClinicaRepositorio historiaClinicaRepositorio;

    @GetMapping("/registrar/{idTurno}")
    public String registrar(@PathVariable String idTurno, ModelMap modelo) {

        Optional<Turno> respuesta = turnoRepositorio.findById(idTurno);

        if (respuesta.isPresent()) {
            Turno turno = respuesta.get();

            modelo.addAttribute("turno", turno);
        }

        return "registrar_historiaclinica.html";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam String idTurno, @RequestParam String observaciones, ModelMap modelo) {

        historiaClinicaServicio.registrar(idTurno, observaciones);

        return "inicio.html";
    }

    @GetMapping("/paciente/{idPaciente}")
    public String listar(@PathVariable String idPaciente, ModelMap modelo) {

//        Optional<List<Turno>> respuesta = turnoRepositorio.buscarTurnosAtendidos(idPaciente);
        Paciente paciente = usuarioRepositorio.buscarUsuarioPorId(idPaciente);
        try {
            HistoriaClinica hc = historiaClinicaServicio.buscarPorPaciente(paciente);
            
            modelo.addAttribute("turnos", hc.getTurnos());
            modelo.addAttribute("paciente", paciente);

            for (Turno hc1 : hc.getTurnos()) {
                System.out.println(hc1.getFecha());
            }
            return "historia_clinica.html";
        } catch (MiExcepcion ex) {
            
            modelo.addAttribute("paciente", paciente);
            modelo.put("mensaje", ex.getMessage());
            
            return "historia_clinica.html";
        }

        
        
    }

}