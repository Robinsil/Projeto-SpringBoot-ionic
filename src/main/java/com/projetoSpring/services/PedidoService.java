package com.projetoSpring.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projetoSpring.domain.Cliente;
import com.projetoSpring.domain.ItemPedido;
import com.projetoSpring.domain.PagamentoComBoleto;
import com.projetoSpring.domain.Pedido;
import com.projetoSpring.domain.enuns.EstadoPagamento;
import com.projetoSpring.repositories.ItemPedidoRepository;
import com.projetoSpring.repositories.PagamentoRepository;
import com.projetoSpring.repositories.PedidoRepository;
import com.projetoSpring.security.UserSS;
import com.projetoSpring.services.exepitions.AuthorizationException;
import com.projetoSpring.services.exepitions.DataIntegrityException;
import com.projetoSpring.services.exepitions.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private boletoService boletoService;

	@Autowired
	private PagamentoRepository pagamentoRepository;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private ItemPedidoRepository itemPedidoRepository;
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private EmailService emailService;

	public Pedido find(Integer id) {
		Optional<Pedido> obj = pedidoRepository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado com id = " + id + " Tipo " + Pedido.class.getName()));

	}

	@Transactional
	public Pedido insert(Pedido obj) {

		obj.setId(null);
		obj.setInstante(new Date());
		obj.setCliente(clienteService.find(obj.getCliente().getId()));
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);

		if (obj.getPagamento() instanceof PagamentoComBoleto) {

			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());

		}
		obj = pedidoRepository.save(obj);
		pagamentoRepository.save(obj.getPagamento());
		for (ItemPedido ip : obj.getItens()) {

			ip.setDesconto(0.0);
			ip.setProduto(produtoService.find(ip.getProduto().getId()));
			ip.setPreco(ip.getProduto().getPreco());
			ip.setPedido(obj);

		}

		itemPedidoRepository.saveAll(obj.getItens());
		
		emailService.sendOrderConfirmationEmail(obj);
		return obj;

	}

	public Pedido update(Pedido obj) {

		Pedido Obj = find(obj.getId());
		update(obj);

		return pedidoRepository.save(Obj);

	}

	public void delete(Integer id) {
		find(id);

		try {

			pedidoRepository.deleteById(id);

		} catch (Exception e) {

			throw new DataIntegrityException("Não e possivael excluir uma categoria com produtos associados");

		}

	}

	public List<Pedido> FindAll() {

		return pedidoRepository.findAll();
	}

	public Page<Pedido> findPage(Integer page, Integer linesPages, String orderBy, String direction) {
		
		UserSS user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}
		
		PageRequest pageRequest = PageRequest.of(page, linesPages, Direction.valueOf(direction), orderBy);
		Cliente cliente =  clienteService.find(user.getId());
		return pedidoRepository.findByCliente(cliente, pageRequest);
	}
	
	
}













