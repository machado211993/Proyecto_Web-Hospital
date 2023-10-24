package com.serviciosalud.demo.controladores;

import com.serviciosalud.demo.MiExcepcion.MiExcepcion;
import com.serviciosalud.demo.entidades.Profesional;
import com.serviciosalud.demo.enumeraciones.Especialidad;
import com.serviciosalud.demo.repositorios.ProfesionalRepositorio;
import com.serviciosalud.demo.repositorios.UsuarioRepositorio;
import com.serviciosalud.demo.servicios.ProfesionalServicio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/profesional")
public class ProfesionalControlador {

    @Autowired
    ProfesionalServicio profesionalServicio;

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    @Autowired
    ProfesionalRepositorio profesionalRepositorio;

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable String id, ModelMap modelo) {

        modelo.put("profesional", profesionalServicio.getOne(id));

        return "modificar_profesional.html";
    }

    @PostMapping("/modificado/{idProfesional}")
    public String modificado(MultipartFile archivo, @PathVariable String idProfesional, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecha, @RequestParam String nombre, @RequestParam String apellido,
            @RequestParam(required = false) Integer dni, @RequestParam String email, @RequestParam(required = false) Integer telefono,
            @RequestParam String sexo, @RequestParam String password, @RequestParam String password2, @RequestParam Long matricula,
            @RequestParam String especialidad, @RequestParam String modalidad, @RequestParam Double precio, @RequestParam String inicioDia, @RequestParam String finDia,
            @RequestParam String inicioHora, @RequestParam String finHora, @RequestParam Double calificacion, @RequestParam String localidad, @RequestParam String obraSocial,
            @RequestParam Long telefonoLaboral, @RequestParam String descripcion, @RequestParam String nombreEstablecimiento, ModelMap modelo) {

        try {
            profesionalServicio.actualizarProfesional(archivo, fecha, idProfesional, nombre, apellido, dni, email, telefono, sexo, password,
                    password2, matricula, especialidad, modalidad, precio, inicioDia, finDia, inicioHora, finHora, calificacion, localidad, obraSocial, telefonoLaboral, descripcion,
                    nombreEstablecimiento, Boolean.TRUE);

            /*return "redirect:../../inicio";*/
            modelo.put("exito", "El perfil se ha actualizado correctamente"); // El perfil se ha actualizado correctamente
            return "inicio.html";

        } catch (MiExcepcion ex) {
            modelo.put("error", ex.getMessage());

            return "modificar_profesional.html";
        }
    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo) {

        List<Profesional> profesionales = profesionalServicio.listaProfesinales();
        List<String> inicioDiaTraducido = new ArrayList<>();
        List<String> finDiaTraducido = new ArrayList<>();
   
        for (Profesional aux : profesionales) {
            inicioDiaTraducido.add(traducirDia(aux.getDisponibilidadInicioDia()));
            finDiaTraducido.add(traducirDia(aux.getDisponibilidadFinDia()));
        }
        modelo.addAttribute("inicioDiaTraducido", inicioDiaTraducido);
        modelo.addAttribute("finDiaTraducido", finDiaTraducido);
        modelo.addAttribute("profesionales", profesionales);

        return "listar_profesionales.html";
    }

    @GetMapping("/filtrar")
    public String filtraPorEspecialidad(ModelMap modelo, @Param("palabraEspecialidad") String palabraEspecialidad, @Param("palabraFiltro") String palabraFiltro) {

        List<Profesional> profesionales = new ArrayList<>();
        Especialidad especialidad = null;

        if (palabraEspecialidad.equals("") || palabraFiltro.equals("")) {
            profesionales = profesionalServicio.listaProfesinales();

            List<String> inicioDiaTraducido = new ArrayList<>();
            List<String> finDiaTraducido = new ArrayList<>();

            for (Profesional aux : profesionales) {
                inicioDiaTraducido.add(traducirDia(aux.getDisponibilidadInicioDia()));
                finDiaTraducido.add(traducirDia(aux.getDisponibilidadFinDia()));
            }
            modelo.addAttribute("inicioDiaTraducido", inicioDiaTraducido);
            modelo.addAttribute("finDiaTraducido", finDiaTraducido);
            modelo.addAttribute("profesionales", profesionales);

            modelo.addAttribute("mensaje", "  ***Debe elegir una opción en ambos campos si desea filtrar su búsqueda");

            return "listar_profesionales.html";
        }

        if (palabraEspecialidad.equals("TODOS")) {

            switch (palabraFiltro) {
                case "PRECIO":
                    profesionales = profesionalServicio.ordenarPorPrecio();
                    break;
                case "CALIFICACION":
                    profesionales = profesionalServicio.ordenarPorCalificacion();
                    break;
                case "NINGUNO":
                    profesionales = profesionalServicio.listaProfesinales();
                    break;
                default:
                    break;
            }

        } else {

            for (Especialidad aux : Especialidad.values()) {
                if (aux.toString().equals(palabraEspecialidad)) {
                    especialidad = aux;
                }
            }

            switch (palabraFiltro) {
                case "PRECIO":
                    profesionales = profesionalServicio.ordenarPorPrecioFiltro(especialidad);
                    break;
                case "CALIFICACION":
                    profesionales = profesionalServicio.ordenarPorCalificacionFiltro(especialidad);
                    break;
                case "NINGUNO":
                    profesionales = usuarioRepositorio.buscarPorEspecialidad(especialidad);
                    break;
                default:
                    break;
            }
        }

        List<String> inicioDiaTraducido = new ArrayList<>();
        List<String> finDiaTraducido = new ArrayList<>();

        for (Profesional aux : profesionales) {
            inicioDiaTraducido.add(traducirDia(aux.getDisponibilidadInicioDia()));
            finDiaTraducido.add(traducirDia(aux.getDisponibilidadFinDia()));
        }
        modelo.addAttribute("inicioDiaTraducido", inicioDiaTraducido);
        modelo.addAttribute("finDiaTraducido", finDiaTraducido);
        modelo.addAttribute("profesionales", profesionales);
        modelo.addAttribute("palabraFiltro", palabraFiltro);
        modelo.addAttribute("palabraEspecialidad", palabraEspecialidad);

        return "listar_profesionales.html";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id, ModelMap modelo) {

        modelo.put("profesional", profesionalServicio.getOne(id));
        return "eliminar_profesional.html";
    }

    @PostMapping("/eliminado/{id}")
    public String eliminado(@PathVariable String id, String nombre, ModelMap modelo) {

        profesionalServicio.borrarPorId(id);

        return "inicio.html";
    }

    public String traducirDia(String dia) {
        String diaTraducido;

        switch (dia.toLowerCase()) {
            case "monday":
                diaTraducido = "Lunes";
                break;
            case "tuesday":
                diaTraducido = "Martes";
                break;
            case "wednesday":
                diaTraducido = "Miércoles";
                break;
            case "thursday":
                diaTraducido = "Jueves";
                break;
            case "friday":
                diaTraducido = "Viernes";
                break;
            case "saturday":
                diaTraducido = "Sábado";
                break;
            case "sunday":
                diaTraducido = "Domingo";
                break;
            default:
                diaTraducido = dia; // Mantener el mismo valor si no se encuentra la traducción
                break;
        }

        return diaTraducido;
    }

}
