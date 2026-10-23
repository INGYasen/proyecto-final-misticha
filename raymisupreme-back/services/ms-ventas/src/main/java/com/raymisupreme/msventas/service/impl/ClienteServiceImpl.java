package com.raymisupreme.msventas.service.impl;

import com.raymisupreme.msventas.dto.ClienteRequest;
import com.raymisupreme.msventas.dto.ClienteResponse;
import com.raymisupreme.msventas.entity.ClienteEntity;
import com.raymisupreme.msventas.mapper.ClienteMapper;
import com.raymisupreme.msventas.repository.ClienteRepository;
import com.raymisupreme.msventas.service.ClienteService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public ClienteResponse crear(ClienteRequest request) {
        if (request.getKeycloakUserId() != null) {
            var existente = clienteRepository.findByKeycloakUserId(request.getKeycloakUserId());
            if (existente.isPresent()) return ClienteMapper.toResponse(existente.get());
        }
        var porEmail = clienteRepository.findByEmailIgnoreCase(request.getEmail());
        if (porEmail.isPresent()) {
            ClienteEntity entity = porEmail.get();
            ClienteMapper.copyToEntity(request, entity);
            return ClienteMapper.toResponse(clienteRepository.save(entity));
        }
        ClienteEntity entity = ClienteMapper.toEntity(request);
        ClienteEntity guardado = clienteRepository.save(entity);
        return ClienteMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse obtenerPorId(Long idCliente) {
        ClienteEntity entity = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + idCliente));
        return ClienteMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse obtenerActual(String keycloakUserId, String email) {
        return clienteRepository.findByKeycloakUserId(keycloakUserId)
                .or(() -> clienteRepository.findByEmailIgnoreCase(email))
                .map(ClienteMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado para el usuario autenticado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll().stream()
                .map(ClienteMapper::toResponse)
                .toList();
    }

    @Override
    public ClienteResponse actualizar(Long idCliente, ClienteRequest request) {
        ClienteEntity entity = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + idCliente));
        ClienteMapper.copyToEntity(request, entity);
        ClienteEntity actualizado = clienteRepository.save(entity);
        return ClienteMapper.toResponse(actualizado);
    }

    @Override
    public void eliminar(Long idCliente) {
        if (!clienteRepository.existsById(idCliente)) {
            throw new IllegalArgumentException("Cliente no encontrado: " + idCliente);
        }
        clienteRepository.deleteById(idCliente);
    }
}
