package it.prova.gestionesatelliti.web.controller;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.prova.gestionesatelliti.model.Satellite;
import it.prova.gestionesatelliti.model.StatoSatellite;
import it.prova.gestionesatelliti.service.SatelliteService;

@Controller
@RequestMapping(value = "/satellite")
public class SatelliteController {

	@Autowired
	private SatelliteService satelliteService;

	@GetMapping
	public ModelAndView listAll() {
		ModelAndView mv = new ModelAndView();
		List<Satellite> results = satelliteService.listAllElements();
		mv.addObject("satellite_list_attribute", results);
		mv.setViewName("satellite/list");
		return mv;
	}

	@GetMapping("/search")
	public String search() {
		return "satellite/search";
	}

	@PostMapping("/list")
	public String listByExample(Satellite example, ModelMap model) {
		List<Satellite> results = satelliteService.findByExample(example);
		model.addAttribute("satellite_list_attribute", results);
		return "satellite/list";
	}

	@GetMapping("/insert")
	public String create(Model model) {
		model.addAttribute("insert_satellite_attr", new Satellite());
		return "satellite/insert";
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("insert_satellite_attr") Satellite satellite, BindingResult result,
			RedirectAttributes redirectAttrs) {

		if (result.hasErrors())
			return "satellite/insert";
		
		//messagio di errore nel messages properties
		if(satellite.getStato() != null && satellite.getDataLancio() == null) {
			result.rejectValue("dataLancio", "dataLancio.stato.non.valorizzato");
			return "satellite/insert";
		}
		
		if(satellite.getDataLancio() != null && satellite.getStato() == null && satellite.getDataLancio().before(new Date())) {
			result.rejectValue("stato", "status.error", "lo stato deve essere valorizzato se la data lancio e' passata");
			return "satellite/insert";
		}
		
		if(satellite.getStato() == StatoSatellite.DISATTIVATO && satellite.getDataRientro() == null ) {
			result.rejectValue("dataRientro", "status.error", "se lo stato e' disattivato, la data di rientro deve essere valorizzata");
			return "satellite/insert";
		}
		
		if(satellite.getDataLancio() == null && satellite.getDataRientro() != null ) {
			result.rejectValue("dataLancio", "status.error", "la data di lancio deve essere valorizzata se lo e' la data di rientro");
			return "satellite/insert";
		}
		
		if(satellite.getDataRientro() != null && satellite.getStato() != StatoSatellite.DISATTIVATO) {
			result.rejectValue("dataRientro", "status.error", "se e' presente la data di rientro, lo stato deve essere disattivato");
			return "satellite/insert";
		}
		
		if(satellite.getDataLancio() != null && satellite.getDataLancio().after(new Date()) && satellite.getStato() != null) {
			result.rejectValue("stato", "status.error", "la data lancio e' futura, lo stato non puo essere valorizzato!");
			return "satellite/insert";
		}
		
		if(satellite.getDataLancio() != null && satellite.getDataRientro() != null && satellite.getDataLancio().after(satellite.getDataRientro())) {
			result.rejectValue("dataRientro", "status.error", "la data di rientro non puo essere inferiore alla data lancio");
			return "satellite/insert";
		}

		satelliteService.inserisciNuovo(satellite);

		redirectAttrs.addFlashAttribute("successMessage", "Operazione eseguita correttamente");
		return "redirect:/satellite";
	}

	@GetMapping("/show/{idSatellite}")
	public String show(@PathVariable(required = true) Long idSatellite, Model model) {
		model.addAttribute("show_satellite_attr", satelliteService.caricaSingoloElemento(idSatellite));
		return "satellite/show";
	}

	@GetMapping("/delete/{idSatellite}")
	public String delete(@PathVariable(required = true) Long idSatellite, Model model) {
		model.addAttribute("delete_satellite_attr", satelliteService.caricaSingoloElemento(idSatellite));
		return "satellite/delete";
	}

	@PostMapping("/executeDelete")
	public String executeDelete(@RequestParam Long idSatellite, Model model, RedirectAttributes redirectAttrs) {

		Satellite satelliteInstance=satelliteService.caricaSingoloElemento(idSatellite);
		
		if(satelliteInstance.getStato() == StatoSatellite.FISSO || satelliteInstance.getStato() == StatoSatellite.IN_MOVIMENTO) {
			redirectAttrs.addFlashAttribute("errorMessage", "Non si possono eliminare satelliti in orbita");
			return "redirect:/satellite";
		}
		
		satelliteService.rimuovi(idSatellite);

		redirectAttrs.addFlashAttribute("successMessage", "Operazione eseguita correttamente");
		return "redirect:/satellite";
	}

	@GetMapping("/update/{idSatellite}")
	public String update(@PathVariable(required = true) Long idSatellite, Model model) {
		model.addAttribute("update_satellite_attr", satelliteService.caricaSingoloElemento(idSatellite));
		return "satellite/update";
	}

	@PostMapping("/executeUpdate")
	public String update(@Valid @ModelAttribute("update_satellite_attr") Satellite satelliteInstance,
			BindingResult result, RedirectAttributes redirectAttrs) {

		if (result.hasErrors())
			return "satellite/update";
		
		if(satelliteInstance.getStato() != null && satelliteInstance.getDataLancio() == null) {
			result.rejectValue("dataLancio", "status.error", "se lo stato e' valorizzato, la data deve essere valorizzata");
			return "satellite/update";
		}
		
		if(satelliteInstance.getDataLancio() != null && satelliteInstance.getStato() == null && satelliteInstance.getDataLancio().before(new Date())) {
			result.rejectValue("stato", "status.error", "lo stato deve essere valorizzato se la data lancio e' passata");
			return "satellite/update";
		}
		
		if(satelliteInstance.getStato() == StatoSatellite.DISATTIVATO && satelliteInstance.getDataRientro() == null ) {
			result.rejectValue("dataRientro", "status.error", "se lo stato e' disattivato, la data di rientro deve essere valorizzata");
			return "satellite/update";
		}
		
		if(satelliteInstance.getDataLancio() == null && satelliteInstance.getDataRientro() != null ) {
			result.rejectValue("dataLancio", "status.error", "la data di lancio deve essere valorizzata se lo e' la data di rientro");
			return "satellite/update";
		}
		
		if(satelliteInstance.getDataLancio() != null && satelliteInstance.getDataRientro() != null && satelliteInstance.getDataLancio().after(satelliteInstance.getDataRientro())) {
			result.rejectValue("dataRientro", "status.error", "la data di rientro non puo essere inferiore alla data lancio");
			return "satellite/update";
		}
		
		if(satelliteInstance.getStato() == StatoSatellite.IN_MOVIMENTO || satelliteInstance.getStato() == StatoSatellite.FISSO && satelliteInstance.getDataRientro() != null) {
			result.rejectValue("dataRientro", "status.error", "la data di rientro non puo essere inserita prima che il satellite rientri");
			return "satellite/update";
		}
		
		if(satelliteInstance.getStato() == StatoSatellite.DISATTIVATO && satelliteInstance.getDataRientro().after(new Date())) {
			result.rejectValue("dataRientro", "status.error", "la data di rientro non puo essere modificata poiche lo stato e' disattivato");
			return "satellite/update";
		}

		satelliteService.aggiorna(satelliteInstance);

		redirectAttrs.addFlashAttribute("successMessage", "Operazione eseguita correttamente");
		return "redirect:/satellite";
	}

	@GetMapping("/lancia/{idSatellite}")
	public String lancia(@PathVariable(required = true) Long idSatellite, Model model,
			RedirectAttributes redirectAttrs) {

		satelliteService.lancia(idSatellite);

		redirectAttrs.addFlashAttribute("successMessage", "Lancio eseguito correttamente");
		return "redirect:/satellite";
	}

	@GetMapping("/rientra/{idSatellite}")
	public String rientra(@PathVariable(required = true) Long idSatellite, Model model,
			RedirectAttributes redirectAttrs) {

		satelliteService.rientra(idSatellite);

		redirectAttrs.addFlashAttribute("successMessage", "Rientro eseguito correttamente");
		return "redirect:/satellite";
	}
	
	@GetMapping("/ricerca1")
	public String ricercaSatellitiDaDueAnniNonDisattivati(Satellite example, ModelMap model) {
		List<Satellite> results = satelliteService.satellitiLanciatiDaPiuDiDueAnni();
		model.addAttribute("satellite_list_attribute", results);
		return "satellite/list";
	}
	
	@GetMapping("/ricerca2")
	public String ricercaSatellitiDisattivatiEDataNull(Satellite example, ModelMap model) {
		List<Satellite> results = satelliteService.findAllByStatoIsDisattivatoAndDataRientroIsNull();
		model.addAttribute("satellite_list_attribute", results);
		return "satellite/list";
	}
	
	@GetMapping("/ricerca3")
	public String ricercaSatellitiFissiDieciAnniFa(Satellite example, ModelMap model) {
		List<Satellite> results = satelliteService.findAllByDataLancioLessThenAndStatoLike();
		model.addAttribute("satellite_list_attribute", results);
		return "satellite/list";
	}
	
	@GetMapping("/confermaDisabilita")
		public ModelAndView confermaDisabilita() {
			ModelAndView mv = new ModelAndView();
			List<Satellite> results = satelliteService.listAllElements();
			List<Satellite> listaDisabilita=satelliteService.disabilitaTutti();
			mv.addObject("satellite_list_attribute", results);
			mv.addObject("satellite_list_disabilita", listaDisabilita);
			mv.setViewName("satellite/confermaDisabilita");
			return mv;
	}
	
	@PostMapping("/disabilitaTutti")
	public String disabilitaTutti(RedirectAttributes redirectAttrs) {
        
		List<Satellite> listaSatelliti=satelliteService.disabilitaTutti();
		for(Satellite satelliteItem:listaSatelliti){
			satelliteItem.setStato(StatoSatellite.DISATTIVATO);
			satelliteItem.setDataRientro(new Date());
			satelliteService.aggiorna(satelliteItem);
		}
		
		redirectAttrs.addFlashAttribute("successMessage", "I satelliti sono stati disabilitati");
		return "redirect:/satellite";
	}
	
	
}
