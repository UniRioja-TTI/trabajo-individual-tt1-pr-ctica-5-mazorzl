package servicios;

import interfaces.InterfazContactoSim;
import modelo.DatosSimulation;
import modelo.DatosSolicitud;
import modelo.Entidad;
import modelo.Punto;
import org.springframework.stereotype.Service;
import io.swagger.client.api.ResultadosApi;
import io.swagger.client.api.SolicitudApi;
import io.swagger.client.model.ResultsResponse;
import io.swagger.client.model.Solicitud;
import io.swagger.client.model.SolicitudResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServicioContactoSim implements InterfazContactoSim {
    private final List<Entidad> entidades;

    public ServicioContactoSim() {
        entidades = new ArrayList<>();

        Entidad e1 = new Entidad();
        e1.setId(1);
        e1.setName("Alfa");
        e1.setDescripcion("Primera entidad de simulación");

        Entidad e2 = new Entidad();
        e2.setId(2);
        e2.setName("Beta");
        e2.setDescripcion("Segunda entidad de simulación");

        Entidad e3 = new Entidad();
        e3.setId(3);
        e3.setName("Gamma");
        e3.setDescripcion("Tercera entidad de simulación");

        entidades.add(e1);
        entidades.add(e2);
        entidades.add(e3);
    }

    @Override
    public int solicitarSimulation(DatosSolicitud sol) {
        try {
            SolicitudApi api = new SolicitudApi();
            api.getApiClient().setBasePath("http://consumible:8080");

            List<String> nombres = new ArrayList<>();
            List<Integer> cantidades = new ArrayList<>();

            for (Entidad entidad : entidades) {
                nombres.add(entidad.getName());
                int cantidad = sol.getNums().getOrDefault(entidad.getId(), 0);
                cantidades.add(cantidad);
            }

            Solicitud solicitud = new Solicitud();
            solicitud.setCantidadesIniciales(cantidades);
            solicitud.setNombreEntidades(nombres);

            SolicitudResponse respuesta = api.solicitudSolicitarPost(solicitud, "usuario");

            if (respuesta == null) return -1;
            return respuesta.getTokenSolicitud() != null ? respuesta.getTokenSolicitud() : -1;

        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public DatosSimulation descargarDatos(int ticket) {
        try {
            ResultadosApi api = new ResultadosApi();
            api.getApiClient().setBasePath("http://localhost:8080");

            ResultsResponse respuesta = api.resultadosPost("usuario", ticket);

            if (respuesta == null || respuesta.getData() == null)
                return new DatosSimulation();

            String[] lineas = respuesta.getData().split("\n");
            int ancho = Integer.parseInt(lineas[0].trim());

            Map<Integer, List<Punto>> puntos = new HashMap<>();
            int maxSegundos = 0;

            for (int i = 1; i < lineas.length; i++) {
                if (lineas[i].trim().isEmpty()) continue;
                String[] partes = lineas[i].trim().split(",");
                int tiempo = Integer.parseInt(partes[0]);
                int y = Integer.parseInt(partes[1]);
                int x = Integer.parseInt(partes[2]);
                String color = partes[3];

                Punto p = new Punto();
                p.setX(x);
                p.setY(y);
                p.setColor(color);

                puntos.computeIfAbsent(tiempo, k -> new ArrayList<>()).add(p);
                if (tiempo > maxSegundos) maxSegundos = tiempo;
            }

            DatosSimulation ds = new DatosSimulation();
            ds.setAnchoTablero(ancho);
            ds.setMaxSegundos(maxSegundos + 1);
            ds.setPuntos(puntos);
            return ds;

        } catch (Exception e) {
            return new DatosSimulation();
        }
    }

    @Override
    public List<Entidad> getEntities() {
        return entidades;
    }

    @Override
    public boolean isValidEntityId(int id) {
        return entidades.stream().anyMatch(e -> e.getId() == id);
    }
}