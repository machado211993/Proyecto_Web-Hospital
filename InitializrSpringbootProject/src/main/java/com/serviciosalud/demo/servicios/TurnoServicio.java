package com.serviciosalud.demo.servicios;

import com.serviciosalud.demo.MiExcepcion.MiExcepcion;
import org.springframework.beans.factory.annotation.Autowired;
import com.serviciosalud.demo.entidades.Paciente;
import com.serviciosalud.demo.entidades.Profesional;
import com.serviciosalud.demo.entidades.Turno;
import com.serviciosalud.demo.enumeraciones.Estado;
import com.serviciosalud.demo.repositorios.PacienteRepositorio;
import com.serviciosalud.demo.repositorios.ProfesionalRepositorio;
import com.serviciosalud.demo.repositorios.TurnoRepositorio;
import com.serviciosalud.demo.repositorios.UsuarioRepositorio;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TurnoServicio {

    @Autowired
    ProfesionalRepositorio profesionalRepositorio;

    @Autowired
    PacienteRepositorio pacienteRepositorio;

    @Autowired
    TurnoRepositorio turnoRepositorio;

    @Transactional
    public void registrar(String idPaciente, String idProfesional, String mes, String dia, Date fecha, String hora, String motivoConsulta) throws MiExcepcion {

        Turno turno = new Turno();

        Optional<Paciente> respuestaPaciente = pacienteRepositorio.findById(idPaciente);
        Optional<Profesional> respuestaProfesional = profesionalRepositorio.findById(idProfesional);

        System.out.println("turnoServ:paci" + respuestaPaciente);
        System.out.println("turnoServ:prof" + respuestaProfesional);

        if (respuestaPaciente.isPresent() && respuestaProfesional.isPresent()) {

            Paciente paciente = respuestaPaciente.get();
            turno.setPaciente(paciente);

            Profesional profesional = respuestaProfesional.get();
            turno.setProfesional(profesional);

            LocalDate fechaLocalDate = validarFecha2(fecha, hora, profesional);
            turno.setFecha(fechaLocalDate.toString());
            //turno.setFecha(buscarFecha(dia, mes, profesional, hora).toString());
            turno.setHorario(hora);
            turno.setEstado(Estado.RESERVADO);
            turno.setMotivoDeConsulta(motivoConsulta);

            turnoRepositorio.save(turno);
        }
    }

    public void modificar(String id, String idPaciente, String idProfesional, String mes, String dia, Date fecha,
            String hora, String motivoConsulta) throws MiExcepcion {

        Optional<Turno> respuestaTurno = turnoRepositorio.findById(id);

        if (respuestaTurno.isPresent()) {

            Turno turno = respuestaTurno.get();

            Optional<Paciente> respuestaPaciente = pacienteRepositorio.findById(idPaciente);
            Optional<Profesional> respuestaProfesional = profesionalRepositorio.findById(idProfesional);

            System.out.println("turnoServ:paci" + respuestaPaciente);
            System.out.println("turnoServ:prof" + respuestaProfesional);

            if (respuestaPaciente.isPresent() && respuestaProfesional.isPresent()) {

                Paciente paciente = respuestaPaciente.get();
                turno.setPaciente(paciente);

                Profesional profesional = respuestaProfesional.get();
                turno.setProfesional(profesional);

                LocalDate fechaLocalDate = validarFecha2(fecha, hora, profesional);
                turno.setFecha(fechaLocalDate.toString());
//                turno.setFecha(buscarFecha(dia, mes, profesional, hora).toString());
                turno.setHorario(hora);
                turno.setEstado(Estado.RESERVADO);
                turno.setMotivoDeConsulta(motivoConsulta);

                turnoRepositorio.save(turno);
            }
        }
    }

//    public LocalDate buscarFecha(String dia, String mes, Profesional profesional, String hora) throws MiExcepcion {
//        // Convertir el mes a Month
//        Month mesComparar = Month.valueOf(mes.toUpperCase());
//
//        // Convertir el dia a DayOfWeek
//        DayOfWeek diaComparar = DayOfWeek.valueOf(dia.toUpperCase());
//
//        // PRIMER FECHA DEL MES
//        LocalDate fechaActual = LocalDate.of(LocalDate.now().getYear(), mesComparar, 1);
//        // ULTIMA FECHA DEL MES
//        LocalDate fechaFin = LocalDate.of(LocalDate.now().getYear(), mesComparar, mesComparar.length(false));
//
//        // variable para que continue con los siguientes dias elegidos y no se quede solo con el primero
//        boolean flag = false;
//
//        while (fechaActual.isBefore(fechaFin) || fechaActual.isEqual(fechaFin)) { //recorre todos los dias del mes elegido {ej:junio}
//
//            //optional puede recibir o no una respuesta && List<Turno> porque de una misma fecha pueda haber +deUno pero con distinta hora
//            Optional<List<Turno>> respuestaTurno = turnoRepositorio.buscarPorFecha(fechaActual.toString());
//
//            if (respuestaTurno.isPresent()) { //si existe un turno con la misma fecha en la BD...
//                List<Turno> turnoEnBD = respuestaTurno.get(); // guardo el turno encontrado en BD
//
//                flag = false;
//                flag = validarFecha(turnoEnBD, profesional, fechaActual, hora); //valido que no sea con el mismo prof. a la misma hora
//
//                // buscar solo el dia que eligio el paciente {ej: lunes}, del mes elegido {ej: junio} /// todos los lunes de junio
//                if (fechaActual.getDayOfWeek() == diaComparar) { // diaActual irá incrementando hasta que sea igual a diaComparar
//
//                    System.out.println("TServ97 " + fechaActual.toString());
//
//                    if (flag == true) {
//                        flag = false;
//                        fechaActual = fechaActual.plusDays(1); // diaActual irá incrementando de a uno
//                        System.out.println("continue !!!!!");
//                        continue;
//                    }
//
//                    //devuelve el primer dia disponible {siguiendo con el ej: el 1° lunes de junio}
//                    System.out.println("no debe estar despues del continue----");
//                    validar(fechaActual);
//                    return fechaActual;
//                }
//                System.out.println("despues del continue???");
//                fechaActual = fechaActual.plusDays(1); // diaActual irá incrementando de a uno
//
//            } else { //si no existe un turno con la misma fecha en la BD...  *no hay validarFecha()*
//
//                // buscar solo el dia que eligio el paciente {ej: lunes}, del mes elegido {ej: junio} /// todos los lunes de junio
//                if (fechaActual.getDayOfWeek() == diaComparar) { // diaActual irá incrementando hasta que sea igual a diaComparar
//
//                    System.out.println("TServ105 " + fechaActual.toString());
//
//                    validar(fechaActual);
//                    //devuelve el primer dia disponible {siguiendo con el ej: el 1° lunes de junio}
//                    return fechaActual;
//                }
//                fechaActual = fechaActual.plusDays(1); // diaActual irá incrementando de a uno
//
//            }
//        }
//        return null;
//    }
    public LocalDate validarFecha2(Date fecha, String hora, Profesional profesional) throws MiExcepcion {
        boolean contador = false;
        List<String> lista = new ArrayList<>(); // lista para guardar los dias y despues setear al atributo List<String> disponibilidadDia

        DayOfWeek dia = DayOfWeek.SUNDAY; // el primer dia a comparar

        DayOfWeek diaInicioComparar = DayOfWeek.valueOf(profesional.getDisponibilidadInicioDia().toUpperCase()); // transformo en inicioDia a DayOfWeek para poder comparar
        DayOfWeek diaFinComparar = DayOfWeek.valueOf(profesional.getDisponibilidadFinDia().toUpperCase()); // transformo en finDia a DayOfWeek para poder comparar

        while (dia != diaInicioComparar && contador == false) {
            dia = dia.plus(1);  // mientras dia != de diaInicio dia ira cambiando al siguiente dia

            if (dia.equals(diaInicioComparar)) { //cuando dia sea igual a inicioDia

                for (int i = 0; i < 6; i++) { //for 7 veces max
                    if (dia != diaFinComparar) { // mientras dia no llegue a diaFin

                        lista.add(dia.toString()); // va agregando los dias a la lista

                        dia = dia.plus(1);// dia ira cambiando de uno en uno
                    } else {
                        lista.add(dia.toString()); // agrega el ultima dia que quedo fuera del primer if()

                        contador = true;  // condicion linea100 para que frene el while
                        break; //sale del for, aunque no haya llegado a la max de 7 vueltas
                    }
                }

            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.ENGLISH);

        // Obtener el nombre del día de la semana en formato texto
        String nombreDiaSemana = sdf.format(fecha);
        System.out.println("nds" + nombreDiaSemana);
        boolean contAux = false;

        for (String aux : lista) {
            if (aux.equals(nombreDiaSemana.toUpperCase())) {
                contAux = true;
            }
        }

        if (contAux == false) {
            throw new MiExcepcion("Elegir dias en los que atienda el profesional");
        }

        List<Turno> turnos = turnoRepositorio.buscarPorProfesional(profesional.getId());

        LocalDate localDate = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        for (Turno turno : turnos) {
            if (turno.getFecha().equals(localDate.toString()) && turno.getHorario().equals(hora) ) {
                throw new MiExcepcion(turno.getFecha()+" a las "+ turno.getHorario() + "hs"
                        + "  Esta fecha ya está reservada con la misma hora," 
                        + " puede elegir otro horario de esta fecha o cambiar de fecha");
            }
        }

        System.out.println("localdate" + localDate);

        return localDate;
    }

//    public void validar(LocalDate fecha) throws MiExcepcion {
//
//        if (fecha.isBefore(LocalDate.now())) {
//            throw new MiExcepcion("La fecha que se elije ya pasó");
//        }
//    }
//    public boolean validarFecha(List<Turno> turnoEnBD, Profesional profesional, LocalDate fechaActual, String hora) throws MiExcepcion {
//        int contador = 0;
//        boolean flag = false;
//        System.out.println("VALIDAR");
//        System.out.println("fechaActual " + fechaActual);
//
//        for (Turno aux : turnoEnBD) { // recorremos uno por uno
//            System.out.println("fechaEnBD " + aux.getFecha());
//            if (aux.getProfesional().equals(profesional) && aux.getFecha().equals(fechaActual.toString()) && aux.getHorario().equals(hora)) {
//                //throw new MiExcepcion("Esa fecha ya está reservada con el mismo profesional a esa misma hora!!!!");
//                flag = true;
//            }
//        }
//        return flag;
//    }
    public List<Turno> listarTurnos() {
        return turnoRepositorio.findAll();
    }

    public List<Turno> listarTurnosDeUnProfesional(String id) {
        return turnoRepositorio.buscarPorProfesional(id);
    }

    public List<Turno> buscarTurnosDeHoy(String idProfesional) {
        Optional<List<Turno>> turnos = turnoRepositorio.buscarTurnoDeHoy(LocalDate.now().toString(), idProfesional);

        if (turnos.isPresent()) {
            return turnos.get();
        }

        return null;
    }

    public List<Turno> ordenarTurnosPorFecha(String idProfesional) {
        Optional<List<Turno>> turnos = turnoRepositorio.ordenarTurnosPorFecha(idProfesional);

        if (turnos.isPresent()) {
            List<Turno> tres = new ArrayList<>();
            tres = turnos.get();
            for (Turno aux : tres) {
                System.out.println("porFecha" + aux);
            }

            return tres;
        }

        return null;
    }

    public List<Turno> ordenarTurnosPorPacientes(String idProfesional) {
        Optional<List<Turno>> turnos = turnoRepositorio.ordenarTurnosPorPacientes(idProfesional);

        if (turnos.isPresent()) {
            return turnos.get();
        }

        return null;
    }
    
    @Transactional
    public Turno buscarTurno(String id) {
        Optional<Turno> resp = turnoRepositorio.findById(id);
        if (resp.isPresent()) {
            return resp.get();
        }
        return null;
    }


    @Transactional
    public void guardarCalificacion(String id, Integer calificacion) {
        Optional<Turno> resp = turnoRepositorio.findById(id);
        Turno turno = new Turno();
        if (resp.isPresent()) {
            turno = resp.get();
            turno.setCalificacion(calificacion);
            turnoRepositorio.save(turno);
        }
    }
}
