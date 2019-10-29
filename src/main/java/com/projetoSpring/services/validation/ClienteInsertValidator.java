package com.projetoSpring.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.projetoSpring.domain.Cliente;
import com.projetoSpring.domain.enuns.TipoCliente;
import com.projetoSpring.dto.ClienteNewDTO;
import com.projetoSpring.repositories.ClienteRepository;
import com.projetoSpring.resources.exeption.FieldMessage;
import com.projetoSpring.services.validation.utils.BR;

public class ClienteInsertValidator implements ConstraintValidator<ClienteInsert, ClienteNewDTO> {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Override
	public void initialize(ClienteInsert ann) {
	}

	@Override
	public boolean isValid(ClienteNewDTO objDto, ConstraintValidatorContext context) {

		List<FieldMessage> list = new ArrayList<>();

		if (objDto.getTipo().equals(TipoCliente.PESSOAFISICA.getCod()) && !BR.isValidCPF(objDto.getCPFouCNPJ())) {

			list.add(new FieldMessage("CPFouCNPJ", "CPF inválido"));

		}

		if (objDto.getTipo().equals(TipoCliente.PESSOAJURIDICA.getCod()) && !BR.isValidCNPJ(objDto.getCPFouCNPJ())) {

			list.add(new FieldMessage("CPFouCNPJ", "CNPJ inválido"));

		}
		
		Cliente aux = clienteRepository.findByEmail(objDto.getEmail());
		
		if(aux != null) {
			
			list.add(new FieldMessage("email", "Email já cadastrado"));
		}
		

		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}

}
