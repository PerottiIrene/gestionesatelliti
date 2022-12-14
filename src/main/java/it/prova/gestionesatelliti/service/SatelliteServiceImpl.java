package it.prova.gestionesatelliti.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.apache.jasper.tagplugins.jstl.core.ForEach;
import org.aspectj.weaver.NewConstructorTypeMunger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.prova.gestionesatelliti.model.Satellite;
import it.prova.gestionesatelliti.model.StatoSatellite;
import it.prova.gestionesatelliti.repository.SatelliteRepository;

@Service
public class SatelliteServiceImpl implements SatelliteService{
	
	@Autowired
	private SatelliteRepository repository;

	@Override
	@Transactional(readOnly = true)
	public List<Satellite> listAllElements() {
		return (List<Satellite>) repository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Satellite caricaSingoloElemento(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void aggiorna(Satellite satelliteInstance) {
		repository.save(satelliteInstance);
	}

	@Override
	@Transactional
	public void inserisciNuovo(Satellite satelliteInstance) {
		repository.save(satelliteInstance);
	}

	@Override
	@Transactional
	public void rimuovi(Long idSatellite) {
		repository.deleteById(idSatellite);
		
	}

	@Override
	@Transactional(readOnly = true)
	public List<Satellite> findByExample(Satellite example) {
		Specification<Satellite> specificationCriteria = (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<Predicate>();

			if (StringUtils.isNotEmpty(example.getDenominazione()))
				predicates.add(cb.like(cb.upper(root.get("denominazione")), "%" + example.getDenominazione().toUpperCase() + "%"));

			if (StringUtils.isNotEmpty(example.getCodice()))
				predicates.add(cb.like(cb.upper(root.get("codice")), "%" + example.getCodice().toUpperCase() + "%"));

			if (example.getStato() != null)
				predicates.add(cb.equal(root.get("stato"), example.getStato()));

			if (example.getDataLancio() != null)
				predicates.add(cb.greaterThanOrEqualTo(root.get("dataLancio"), example.getDataLancio()));
			
			if (example.getDataRientro() != null)
				predicates.add(cb.greaterThanOrEqualTo(root.get("dataRientro"), example.getDataRientro()));

			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};

		return repository.findAll(specificationCriteria);
	}

	@Override
	public void lancia(Long id) {
		Satellite satelliteInstance = repository.findById(id).orElse(null);
		
		satelliteInstance.setDataLancio(new Date());
		satelliteInstance.setStato(StatoSatellite.IN_MOVIMENTO);
		repository.save(satelliteInstance);
		
	}

	@Override
	public void rientra(Long id) {
        Satellite satelliteInstance = repository.findById(id).orElse(null);
		
		satelliteInstance.setDataRientro(new Date());
		satelliteInstance.setStato(StatoSatellite.DISATTIVATO);
		repository.save(satelliteInstance);
	}

	@Override
	public List<Satellite> satellitiLanciatiDaPiuDiDueAnni() {
		
		Date dataConfronto = new Date();
		dataConfronto.setYear(dataConfronto.getYear()-2);
		return repository.satellitiLanciatiDaPiuDiDueAnni(dataConfronto);
	}

	@Override
	public List<Satellite> findAllByStatoIsDisattivatoAndDataRientroIsNull() {
		
		return repository.findAllByStatoLikeAndDataRientroIsNull(StatoSatellite.DISATTIVATO);
	}

	@Override
	public List<Satellite> findAllByDataLancioLessThenAndStatoLike() {
		
		Date dataConfronto = new Date();
		dataConfronto.setYear(dataConfronto.getYear()-10);
		
		return repository.findAllByDataLancioBeforeAndStatoLike(dataConfronto, StatoSatellite.FISSO);
		
	}

	@Override
	public List<Satellite> disabilitaTutti() {
		Date dataLancio= new Date();
		Date dataRientro= new Date();
		List<Satellite> satellitiNonRientrati=repository.satellitiNonAncoraRientratiEConStatoInMovimentoOFisso(dataLancio, dataRientro);
		
		return satellitiNonRientrati;
		
	}
	

}
