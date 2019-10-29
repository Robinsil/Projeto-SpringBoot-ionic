package com.projetoSpring.services;

import org.springframework.mail.SimpleMailMessage;

import com.projetoSpring.domain.Cliente;
import com.projetoSpring.domain.Pedido;

public interface EmailService {

	void sendOrderConfirmationEmail(Pedido obj);
	
	void sendEmail(SimpleMailMessage msg);
	
	void sendNewPasswordEmail(Cliente cliente, String newPass);
}