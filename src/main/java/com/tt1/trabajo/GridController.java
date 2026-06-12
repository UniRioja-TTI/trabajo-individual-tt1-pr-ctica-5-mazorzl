package com.tt1.trabajo;

import org.slf4j.Logger;

import interfaces.InterfazContactoSim;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.DatosSimulation;
import modelo.Punto;

@Controller
public class GridController {
	private final InterfazContactoSim ics;
	private final Logger logger;
	
	public GridController(InterfazContactoSim ics, Logger logger) {
		this.ics = ics;
		this.logger = logger;
	}
	
	@GetMapping("/grid")
    public String solicitud(@RequestParam("tok") int tok, Model model) {
		DatosSimulation ds = ics.descargarDatos(tok);
        Map<String, String> colors = new HashMap<>();
		if (ds.getPuntos() != null) {
			for (Map.Entry<Integer, List<Punto>> entrada : ds.getPuntos().entrySet()) {
				int tiempo = entrada.getKey();
				for (modelo.Punto p : entrada.getValue()) {
					String clave = tiempo + "-" + p.getY() + "-" + p.getX();
					colors.put(clave, p.getColor());
				}
			}
		}

		model.addAttribute("maxTime", ds.getMaxSegundos());
		model.addAttribute("count", ds.getAnchoTablero());
		model.addAttribute("colors", colors);
		try {
			ObjectMapper mapper = new ObjectMapper();
			model.addAttribute("colorsJson", mapper.writeValueAsString(colors));
		} catch (Exception e) {
			model.addAttribute("colorsJson", "{}");
		}
		return "grid";
    }
}
