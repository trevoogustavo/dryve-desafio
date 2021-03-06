package com.dryve.dryvecarros.controler;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dryve.dryvecarros.dto.VeiculoDTO;
import com.dryve.dryvecarros.dto.VeiculoResponseDTO;
import com.dryve.dryvecarros.exception.ErroNegocialException;
import com.dryve.dryvecarros.service.IVeiculoService;

@RestController
@RequestMapping( "/veiculos")
public class VeiculoController {

	@Autowired
	private IVeiculoService service;

	
	@GetMapping()
	@ResponseBody
	public ResponseEntity<VeiculoResponseDTO> buscaVeiculoPorPlaca(@RequestParam String placa) throws ErroNegocialException {
		return new ResponseEntity<VeiculoResponseDTO>(service.buscaPorPlaca(placa), HttpStatus.OK);
	}
	
	@GetMapping(path = "/paginado")
	@ResponseBody
	public ResponseEntity<Page<VeiculoResponseDTO>> buscaTodosPorMarca(@RequestParam Long idMarca, @PageableDefault(value = 10, page = 0) Pageable pageable) throws ErroNegocialException {
		return ResponseEntity.ok(service.listaVeiculosPorMarca(idMarca, pageable));
	}
	
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<VeiculoResponseDTO> salva(@RequestBody @Valid VeiculoDTO dto) throws ErroNegocialException {
		VeiculoResponseDTO response = service.salva(dto);
		return new ResponseEntity<VeiculoResponseDTO>(response, HttpStatus.CREATED);
	}

	@PutMapping(path = "{placa}")
	public ResponseEntity<VeiculoResponseDTO> atualizaParcilmente(@PathVariable String placa, @RequestBody VeiculoDTO dto) throws ErroNegocialException {
		return ResponseEntity.ok(service.atualiza(dto, placa));
	}

}
