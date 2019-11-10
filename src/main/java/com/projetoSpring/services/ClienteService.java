package com.projetoSpring.services;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.projetoSpring.domain.Cidade;
import com.projetoSpring.domain.Cliente;
import com.projetoSpring.domain.Endereco;
import com.projetoSpring.domain.enuns.Perfil;
import com.projetoSpring.domain.enuns.TipoCliente;
import com.projetoSpring.dto.ClienteDTO;
import com.projetoSpring.dto.ClienteNewDTO;
import com.projetoSpring.repositories.ClienteRepository;
import com.projetoSpring.repositories.EnderecoRepository;
import com.projetoSpring.security.UserSS;
import com.projetoSpring.services.exepitions.AuthorizationException;
import com.projetoSpring.services.exepitions.DataIntegrityException;
import com.projetoSpring.services.exepitions.ObjectNotFounExepition;

@Service
public class ClienteService {
	
	@Transactional
	public Cliente insert(Cliente obj) {

		obj.setId(null);
		obj = clienteRepository.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
	}
	
	
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private BCryptPasswordEncoder pe;
	
	@Autowired
	private S3Service s3Service;
	
	
	public Cliente find(Integer id) {
		
		UserSS user = UserService.authenticated();
		if (user==null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())) {
			throw new AuthorizationException("Acesso negado");
		}
		
		Optional<Cliente> obj = clienteRepository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFounExepition(
				"Objeto não encontrado com id = "+ id + " Tipo " + Cliente.class.getName()));
	}
	
	
	public Cliente update(Cliente obj) {
		
		Cliente newObj =find(obj.getId());
		updateData(newObj,obj);
		
		return clienteRepository.save(newObj);
	}

	
	
	
	public void delete(Integer id) {

		find(id);

		try {

			clienteRepository.deleteById(id);

		} catch (Exception e) {

			throw new DataIntegrityException("Não e possivael excluir cliente com pedidos associados");

		}

	}

	public List<Cliente> FindAll() {
		
		return clienteRepository.findAll();
	}
	
	
	public Page<Cliente>findPage(Integer page,Integer linesPages,String orderBy,String direction){
		
		PageRequest pageRequest = PageRequest.of(page, linesPages,Direction.valueOf(direction), orderBy);
		
		return clienteRepository.findAll(pageRequest);
		
	}
	
	public Cliente fromDTO(ClienteNewDTO objDTO) {
		
		Cliente cli = new Cliente(null,objDTO.getNome(),objDTO.getEmail(),objDTO.getCPFouCNPJ(),TipoCliente.toEnum(objDTO.getTipo()),pe.encode(objDTO.getSenha()));
		Cidade cid = new Cidade(objDTO.getCidadeId(),null,null);
		Endereco end = new Endereco(null,objDTO.getLogradouro(),objDTO.getNumero(),objDTO.getComplemento(),objDTO.getBairro(),objDTO.getCep(), cli, cid);
		cli.getEnderecos().add(end);
		cli.getTelefones().add(objDTO.getTelefone1());
		
		if(objDTO.getTelefone2() != null) {
			cli.getTelefones().add(objDTO.getTelefone2());
		}
	
		if(objDTO.getTelefone3()!= null) {
			cli.getTelefones().add(objDTO.getTelefone3());
		}
		 return cli;
		
	}
	
	
	
	public Cliente fromDTO(ClienteDTO objDTO) {
		
		return new Cliente(objDTO.getId(),objDTO.getNome(),objDTO.getEmail(),null,null,null);
	}
	
	private void updateData(Cliente newObj, Cliente obj) {
		
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
		
	}
	
	public URI uploadProfilePicture(MultipartFile multipartFile) {
		UserSS user = UserService.authenticated();
		if(user == null) {
			throw new AuthorizationException("Acesso Negado");
		}
		
		URI uri= s3Service.uploadFile(multipartFile);
		Cliente cli = find(user.getId());
		cli.setImageUrl(uri.toString());
		clienteRepository.save(cli);
		
		return uri;
		
	}

}
