package com.pandalkai.ecommerce.controller;

import com.pandalkai.ecommerce.dto.*;
import com.pandalkai.ecommerce.entity.Usuario;
import com.pandalkai.ecommerce.repository.UsuarioRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final JwtEncoder jwtEncoder;

    public AuthController(
            UsuarioRepository repository,
            PasswordEncoder encoder,
            JwtEncoder jwtEncoder) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody RegistroDTO dto) {

        if (dto.nombre() == null || dto.nombre().isBlank() ||
            dto.email() == null || dto.email().isBlank() ||
            dto.password() == null || dto.password().length() < 6) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Datos invalidos");
        }

        if (repository.existsByEmail(dto.email())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "El correo ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.nombre());
        usuario.setEmail(dto.email());
        usuario.setPassword(encoder.encode(dto.password()));
        usuario.setRol("USER");

        Usuario guardado = repository.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of("mensaje", "Usuario registrado",
                   "id", guardado.getId(),
                   "nombre", guardado.getNombre())
        );
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginDTO dto) {

        if (dto.email() == null || dto.password() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Credenciales incompletas");
        }

        Usuario usuario = repository.findByEmail(dto.email())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Credenciales invalidas"));

        if (!encoder.matches(dto.password(), usuario.getPassword())) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Credenciales invalidas");
        }

        Instant ahora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("be-ecommerce-pandalkai")
            .subject(usuario.getEmail())
            .issuedAt(ahora)
            .expiresAt(ahora.plus(2, ChronoUnit.HOURS))
            .claim("rol", usuario.getRol())
            .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        String token = jwtEncoder.encode(
            JwtEncoderParameters.from(header, claims)
        ).getTokenValue();

        return Map.of("token", token, "tipo", "Bearer");
    }
}
