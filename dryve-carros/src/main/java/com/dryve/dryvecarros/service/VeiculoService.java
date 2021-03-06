package com.dryve.dryvecarros.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dryve.dryvecarros.adapter.FipeIntegracaoRest;
import com.dryve.dryvecarros.config.RabbitMQConfig;
import com.dryve.dryvecarros.dto.VeiculoDTO;
import com.dryve.dryvecarros.dto.VeiculoResponseDTO;
import com.dryve.dryvecarros.exception.EnumMensagensErro;
import com.dryve.dryvecarros.exception.ErroNegocialException;
import com.dryve.dryvecarros.exception.ObjetoNaoEncontradoException;
import com.dryve.dryvecarros.mapper.VeiculoMapper;
import com.dryve.dryvecarros.modelo.Veiculo;
import com.dryve.dryvecarros.repository.VeiculoRepository;
import com.google.gson.Gson;

@Service
public class VeiculoService implements IVeiculoService{

    @Autowired
    private VeiculoRepository veiculoRepository;
    @Autowired
    private IModeloService modeloService;
    @Autowired
    private VeiculoMapper mapper;
    
    @Autowired
    private FipeIntegracaoRest fipeIntegracaoRest;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public VeiculoResponseDTO salva(VeiculoDTO dto) throws ErroNegocialException {
        validaVeiculoNaBase(dto.getPlaca());
        Veiculo entity = mapper.toEntity(dto);
        entity.setDataCadastro(LocalDate.now());
        entity.setPrecoFipe(fipeIntegracaoRest.consultaPrecoFipe(String.valueOf(dto.getIdarca()), dto.getIdModelo(), dto.getAno()));
        entity.setModelo(modeloService.buscaModeloPorFipeId(dto.getIdModelo()));
        VeiculoResponseDTO retorno = mapper.toResponseDTO(veiculoRepository.save(entity));
        enviaBroadCastRabbitMQ(retorno);
           return retorno ;
    }
    
    private void enviaBroadCastRabbitMQ(VeiculoResponseDTO dto) {
    	try {
    		 Gson gson = new Gson();
			rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,"", gson.toJson(dto));
		} catch (AmqpException e) {
			System.out.println(e.getMessage()); 
		}
    }

    public void validaVeiculoNaBase(String placa) throws ErroNegocialException {
        Optional<Veiculo> veiculoOptional = veiculoRepository.findByPlaca(placa);
        if(veiculoOptional.isPresent()){
            throw new ErroNegocialException(EnumMensagensErro.VEICULO_CADASTRADO_NA_BASE);
        }
    }

    //@Cacheable("veiculo")
    @Override
    public VeiculoResponseDTO buscaPorPlaca(String placa) throws ObjetoNaoEncontradoException {
        Optional<Veiculo> optionalVeiculo = Optional.ofNullable(veiculoRepository.findByPlaca(placa)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(EnumMensagensErro.VEICULO_NAO_ENCONTRADO)));
        return mapper.toResponseDTO(optionalVeiculo.get());
    }

    //@Cacheable("veiculos-page")
    @Override
    public Page<VeiculoResponseDTO> listaVeiculosPorMarca(Long idMarca, Pageable pageable) throws ErroNegocialException {
        Page<VeiculoResponseDTO> veiculos = veiculoRepository.selectByIdMarcaAndPage(idMarca, pageable).map(
                v -> mapper.toResponseDTO(v)
        );
        return veiculos;
    }

    @Override
    public VeiculoResponseDTO atualiza(VeiculoDTO dto, String placa) throws ObjetoNaoEncontradoException {
        Optional<Veiculo> optionalVeiculo = Optional.ofNullable(veiculoRepository.findByPlaca(placa)
                .orElseThrow(()-> new ObjetoNaoEncontradoException(EnumMensagensErro.VEICULO_NAO_ENCONTRADO)));
        return mapper.toResponseDTO(veiculoRepository.save(mapper.toEntityUpdate(dto, optionalVeiculo.get()))) ;
    }


}
