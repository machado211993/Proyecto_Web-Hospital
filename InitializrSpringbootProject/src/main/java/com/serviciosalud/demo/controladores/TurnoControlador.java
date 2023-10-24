package com.serviciosalud.demo.controladores;

import com.serviciosalud.demo.MiExcepcion.MiExcepcion;
import com.serviciosalud.demo.entidades.Usuario;
import com.serviciosalud.demo.entidades.Turno;
import com.serviciosalud.demo.entidades.Profesional;
import com.serviciosalud.demo.repositorios.PacienteRepositorio;
import com.serviciosalud.demo.repositorios.TurnoRepositorio;
import com.serviciosalud.demo.servicios.PacienteServicio;
import com.serviciosalud.demo.servicios.ProfesionalServicio;
import com.serviciosalud.demo.servicios.TurnoServicio;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/turno")
public class TurnoControlador {

    @Autowired
    ProfesionalServicio profesionalServicio;

    @Autowired
    TurnoServicio turnoServicio;

    @Autowired
    TurnoRepositorio turnoRepositorio;

    @Autowired
    PacienteRepositorio pacienteRepositorio;

    @GetMapping("/registrar/{idProfesional}")
    public String registrarTurno(@PathVariable String idProfesional, ModelMap modelo, HttpSession session) {

        Profesional profesional = profesionalServicio.getOne(idProfesional);

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        System.out.println(">>" + logueado.getNombre());

        modelo.put("profesional", profesional);

        List<String> horas = listaHoras(profesional.getDisponibilidadInicioHora(), profesional.getDisponibilidadFinHora());
        modelo.put("horas", horas);

        List<String> dias = listaDias(profesional);
        modelo.put("dias", dias);
//
//        List<String> meses = listaMeses();
//        modelo.put("meses", meses);
        modelo.put("today", LocalDate.now());
        modelo.put("lastDayOfYear", LocalDate.of(LocalDate.now().getYear(), 12, 31));

        return "registrar_turno.html";
    }

    public List<String> listaHoras(String inicioHora, String finHora) {
        List<String> listaHoras = new ArrayList<>();

        LocalTime horaInicioComparar = LocalTime.parse(inicioHora, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime horaFinComparar = LocalTime.parse(finHora, DateTimeFormatter.ofPattern("HH:mm"));

        LocalTime horaActual = horaInicioComparar;

        while (horaActual.isBefore(horaFinComparar) || horaActual.equals(horaFinComparar)) {
            System.out.println(horaActual.format(DateTimeFormatter.ofPattern("HH:mm")));
            listaHoras.add(horaActual.format(DateTimeFormatter.ofPattern("HH:mm")));
            horaActual = horaActual.plusMinutes(30);
        }
        return listaHoras;
    }

    public List<String> listaDias(Profesional profesional) {
        boolean contador = false; //auxiliar para que 

        List<String> lista = new ArrayList<>(); // lista para guardar los dias y despues setear al atributo List<String> disponibilidadDia

        DayOfWeek dia = DayOfWeek.SUNDAY; // el primer dia a comparar

        DayOfWeek diaInicioComparar = DayOfWeek.valueOf(profesional.getDisponibilidadInicioDia().toUpperCase()); // transformo en inicioDia a DayOfWeek para poder comparar
        DayOfWeek diaFinComparar = DayOfWeek.valueOf(profesional.getDisponibilidadFinDia().toUpperCase()); // transformo en finDia a DayOfWeek para poder comparar

        // Nombres de los días en español
        String[] nombresDias = {"Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};

        while (dia != diaInicioComparar && contador == false) {
            dia = dia.plus(1);  // mientras dia != de diaInicio dia ira cambiando al siguiente dia

            if (dia.equals(diaInicioComparar)) { //cuando dia sea igual a inicioDia

                for (int i = 0; i < 6; i++) { //for 7 veces max
                    if (dia != diaFinComparar) { // mientras dia no llegue a diaFin

                        lista.add(nombresDias[dia.getValue()]); // Obtener el nombre en español

                        dia = dia.plus(1);// dia ira cambiando de uno en uno
                    } else {
                        lista.add(nombresDias[dia.getValue()]);  // agrega el ultima dia que quedo fuera del primer if()

                        contador = true;  // condicion linea100 para que frene el while
                        break; //sale del for, aunque no haya llegado a la max de 7 vueltas
                    }
                }

            }
        }

        for (String x : lista) {
            System.out.println("dias>" + x.toString()); //muestra en el output los diasDisponibles ">>..."
        }
        return lista;
    }
//    public List<String> listaMeses() {
//        List<String> meses = new ArrayList<>();
//
//        // Obtener el mes actual
//        Month mesActual = LocalDate.now().getMonth();
//
//        // Recorrer los meses desde el mes actual hasta diciembre
//        for (Month mes : Month.values()) {
//            if (mes.getValue() >= mesActual.getValue()) {
//                meses.add(mes.toString());
//            }
//        }
//        return meses;
//    }

    @PostMapping("/registro")
    public String registro(@RequestParam String idProfesional, @RequestParam String idPaciente, @RequestParam(required = false) String mes,
            @RequestParam(required = false) String dia, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecha2, @RequestParam String hora, @RequestParam String motivoConsulta, ModelMap modelo,
            @RequestParam(required = false) String error) {

        try {
            turnoServicio.registrar(idPaciente, idProfesional, mes, dia, fecha2, hora, motivoConsulta);
            SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
            modelo.put("exito", "Su turno se ha registrado para el día " + formato.format(fecha2));
            return "inicio.html";
        } catch (MiExcepcion ex) {
            Profesional profesional = profesionalServicio.getOne(idProfesional);

            modelo.put("profesional", profesional);

            List<String> horas = listaHoras(profesional.getDisponibilidadInicioHora(), profesional.getDisponibilidadFinHora());
            modelo.put("horas", horas);

            List<String> dias = listaDias(profesional);
            modelo.put("dias", dias);
//
//            List<String> meses = listaMeses();
//            modelo.put("meses", meses);
            modelo.put("idProfesional", idProfesional);
            modelo.put("idPaciente", idPaciente);

            modelo.put("today", LocalDate.now());
            modelo.put("lastDayOfYear", LocalDate.of(LocalDate.now().getYear(), 12, 31));

            modelo.put("error", ex.getMessage()); //ex.getMessage() trae el mensaje de validar()

            return "registrar_turno.html";
        }
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable String id, ModelMap modelo) {
        Optional<Turno> respuesta = turnoRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Turno turno = respuesta.get();

            LocalDate fecha = LocalDate.parse(turno.getFecha());

            modelo.put("turno", turno);

//            modelo.put("mesGuardado", fecha.getMonth());
//            System.out.println("TCONT: mes: " + fecha.getMonth().toString());
//            modelo.put(("diaGuardado"), fecha.getDayOfWeek().toString());
//            System.out.println("TCONT: dia: " + fecha.getDayOfWeek());
            modelo.put("horaGuardada", turno.getHorario());
            System.out.println("TCONT: hora: " + turno.getHorario());

            List<String> horas = listaHoras(turno.getProfesional().getDisponibilidadInicioHora(), turno.getProfesional().getDisponibilidadFinHora());

            modelo.put("horas", horas);

            modelo.put("today", LocalDate.now());
            modelo.put("lastDayOfYear", LocalDate.of(LocalDate.now().getYear(), 12, 31));

            List<String> dias = listaDias(turno.getProfesional());
            modelo.put("dias", dias);
//
//            Month[] meses = Month.values();
//            modelo.put("meses", meses);
        }

        return "modificar_turno.html";
    }

    @PostMapping("/modificado/{id}")
    public String modificado(@PathVariable String id, @RequestParam String idProfesional, @RequestParam String idPaciente, @RequestParam(required = false) String mes,
            @RequestParam(required = false) String dia, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecha, @RequestParam String hora, @RequestParam("motivoConsulta") String motivoConsulta, ModelMap modelo) {

        try {
            turnoServicio.modificar(id, idPaciente, idProfesional, mes, dia, fecha, hora, motivoConsulta);

            return "inicio.html";
        } catch (MiExcepcion ex) {

            Optional<Turno> respuesta = turnoRepositorio.findById(id);

            if (respuesta.isPresent()) {
                Turno turno = respuesta.get();

                modelo.put("turno", turno);
                List<String> horas = listaHoras(turno.getProfesional().getDisponibilidadInicioHora(), turno.getProfesional().getDisponibilidadFinHora());
                modelo.put("horas", horas);
                List<String> dias = listaDias(turno.getProfesional());
                modelo.put("dias", dias);
            }

            modelo.put("today", LocalDate.now());
            modelo.put("lastDayOfYear", LocalDate.of(LocalDate.now().getYear(), 12, 31));
            modelo.put("error", ex.getMessage());

            return "modificar_turno.html";
        }
    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("idProfesional", logueado.getId());

        List<Turno> turnos = new ArrayList<>();

        if (logueado.getRol().toString().equals("PROFESIONAL")) {
            turnos = turnoServicio.listarTurnosDeUnProfesional(logueado.getId());
            modelo.addAttribute("turnos", turnos);

        } else {
            turnos = turnoServicio.listarTurnos();
            modelo.addAttribute("turnos", turnos);
        }

        List<Turno> turnosEliminar = new ArrayList<>();
        for (Turno turno : turnos) {
            if (turno.getEstado().toString().equals("ATENDIDO")) {
                turnosEliminar.add(turno); // Agregar el turno a la lista de eliminación
            }
        }

        turnos.removeAll(turnosEliminar); // Eliminar los turnos de la lista principal

        return "listar_turnos.html";
    }

    @GetMapping("/filtrar")
    public String filtraPorEspecialidad(ModelMap modelo, @Param("palabraClave") String palabraClave,
            @Param("idProfesional") String idProfesional) {
        List<Turno> turnos = new ArrayList();
        String mensaje = "...";

        if (palabraClave.equals("HOY")) {
            turnos = turnoServicio.buscarTurnosDeHoy(idProfesional);
            mensaje = "No hay turnos pendientes para el día de hoy";

        } else if (palabraClave.equals("FECHA")) {
            turnos = turnoServicio.ordenarTurnosPorFecha(idProfesional);
            mensaje = "No tiene turnos.";

        } else if (palabraClave.equals("PACIENTE")) {
            turnos = turnoServicio.ordenarTurnosPorPacientes(idProfesional);
            mensaje = "No tiene turnos..";
        }

        if (turnos.isEmpty()) {
            modelo.addAttribute("mensaje", mensaje);
        }
        modelo.addAttribute("turnos", turnos);
        modelo.addAttribute("palabraClave", palabraClave);
        modelo.addAttribute("idProfesional", idProfesional);

        return "listar_turnos.html";
    }

    @GetMapping("/cancelar/{id}")
    public String eliminar(@PathVariable String id, ModelMap modelo) {

        modelo.put("turno", turnoRepositorio.getOne(id));

        return "eliminar_turno.html";
    }

    @PostMapping("/cancelado/{id}")
    public String eliminado(@PathVariable String id) {

        turnoRepositorio.deleteById(id);
        return "inicio.html";
    }
    
     @GetMapping("/calificar/{id}")
     public String calificar (@PathVariable String id, ModelMap modelo){
     modelo.put("turno", turnoServicio.buscarTurno(id));
         return "calificacion.html";
}

      @PostMapping("/calificado/{id}")
     public String guardarCalificacion(@PathVariable String id, @RequestParam int calificacion){
        turnoServicio.guardarCalificacion(id, calificacion);
        Turno turno = turnoServicio.buscarTurno(id);
        String idProfesional = turno.getProfesional().getId();
        profesionalServicio.promedioCalificacionPorProfesional(idProfesional, calificacion);
       return "redirect:/turno/listar";
    }
     /*prueba*/
}
