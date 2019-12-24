package com.projetoSpring.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.projetoSpring.domain.Categoria;
import com.projetoSpring.dto.CategoriaDTO;
import com.projetoSpring.repositories.CategoriaRepository;
import com.projetoSpring.services.exepitions.DataIntegrityException;
import com.projetoSpring.services.exepitions.ObjectNotFoundException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository categoriaRepository;

	public Categoria find(Integer id) {
		Optional<Categoria> obj = categoriaRepository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Categoria.class.getName()));
		
	}

	public Categoria insert(Categoria obj) {

		obj.setId(null);
		return categoriaRepository.save(obj);
	}

	public Categoria update(Categoria obj) {
		
	    Categoria newObj =find(obj.getId());
		updateData(newObj,obj);
		
		return categoriaRepository.save(newObj);
	}

	
	public void delete(Integer id) {

		find(id);

		try {

			categoriaRepository.deleteById(id);

		} catch (Exception e) {

			throw new DataIntegrityException("Não e possivael excluir uma categoria com produtos associados");

		}

	}

	public List<Categoria> FindAll() {
		
		return categoriaRepository.findAll();
	}
	
	
	public Page<Categoria>findPage(Integer page,Integer linesPages,String orderBy,String direction){
		
		PageRequest pageRequest = PageRequest.of(page, linesPages,Direction.valueOf(direction), orderBy);
		
		return categoriaRepository.findAll(pageRequest);
		
	}
	
	public Categoria fromDTO(CategoriaDTO objDTO) {
		
		return new Categoria(objDTO.getId(),objDTO.getNome());
	}
	
	
	private void updateData(Categoria newObj, Categoria obj) {
		
		newObj.setNome(obj.getNome());
		
		
	}
	
	

}












