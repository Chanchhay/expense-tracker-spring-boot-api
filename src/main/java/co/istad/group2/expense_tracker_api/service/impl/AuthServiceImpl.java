package co.istad.group2.expense_tracker_api.service.impl;

import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.domain.enums.Role;
import co.istad.group2.expense_tracker_api.dto.request.createReq.LoginRequest;
import co.istad.group2.expense_tracker_api.dto.request.createReq.RegisterRequest;
import co.istad.group2.expense_tracker_api.exception.BadRequestException;
import co.istad.group2.expense_tracker_api.exception.ConflictException;
import co.istad.group2.expense_tracker_api.exception.NotFoundException;
import co.istad.group2.expense_tracker_api.repository.UserRepository;
import co.istad.group2.expense_tracker_api.service.AuthService;
import co.istad.group2.expense_tracker_api.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already exists");
        }

        if (!request.password().equals(request.confirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        User user = new User();
        String name = request.name().trim().replaceAll("\\s+", " ");
        user.setName(name);
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                savedUser.getEmail(),
                savedUser.getPassword(),
                Set.of(new SimpleGrantedAuthority("ROLE_" + savedUser.getRole().name()))
        );

        return jwtService.generateToken(
                userDetails,
                savedUser.getRole().name(),
                savedUser.getId()
        );
    }

    @Override
    public String login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("User not found"));

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Set.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        return jwtService.generateToken(
                userDetails,
                user.getRole().name(),
                user.getId()
        );
    }

}