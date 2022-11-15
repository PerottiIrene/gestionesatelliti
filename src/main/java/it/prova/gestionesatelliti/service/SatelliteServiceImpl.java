package it.prova.gestionesatelliti.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.prova.gestioneimpiegati.model.Impiegato;
import it.prova.gestionesatelliti.model.Satellite;
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
	public void aggiorna(Satellite satelliteInstance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inserisciNuovo(Satellite satelliteInstance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Transactional
	public void rimuovi(Long idSatellite) {
		repository.deleteById(idSatellite);
		
	}

}
