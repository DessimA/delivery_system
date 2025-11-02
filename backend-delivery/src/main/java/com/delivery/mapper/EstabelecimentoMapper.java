package com.delivery.mapper;

import com.delivery.dto.EstabelecimentoRequestDTO;
import com.delivery.dto.EstabelecimentoResponseDTO;
import com.delivery.model.Estabelecimento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EstabelecimentoMapper {

    Estabelecimento toEstabelecimento(EstabelecimentoRequestDTO dto);

    EstabelecimentoResponseDTO toEstabelecimentoResponseDTO(Estabelecimento estabelecimento);
}
