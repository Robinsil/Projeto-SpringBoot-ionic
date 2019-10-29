package com.projetoSpring.dto;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import com.projetoSpring.domain.Cliente;
import com.projetoSpring.services.validation.ClienteUpdate;

@ClienteUpdate
public class ClienteDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	@NotEmpty(message ="preenchimento obrigatório")
	@Length(min =5,max= 25,message = "o tamanho deve ser entre 5 e 25 caracteres")
	private String nome;
	
	@NotEmpty(message ="preenchimento obrigatório")
	@Email(message ="email invalido")
	private String email;
	
	public ClienteDTO(){
	}

	public  ClienteDTO(Cliente obj) {
		
		id = obj.getId();
		nome = obj.getNome();
		email= obj.getEmail();	
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	
}
