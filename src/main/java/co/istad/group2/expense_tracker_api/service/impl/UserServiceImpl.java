package co.istad.group2.expense_tracker_api.service.impl;

import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.dto.request.RegisterRequest;
import co.istad.group2.expense_tracker_api.dto.response.UserResponse;
import co.istad.group2.expense_tracker_api.repository.UserRepository;
import co.istad.group2.expense_tracker_api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse create(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE,
                    "Category with id " + registerRequest.email() + " already exists");
        }
        if (!registerRequest.password().equals(registerRequest.confirmPassword())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Password and confirm password do not match");
        }
        User user = new User();
        user.setName(registerRequest.name());
        user.setEmail(registerRequest.email());
        // hashing password
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user = userRepository.save(user);

        return UserResponse.builder().
                uuid(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdDate(user.getCreatedAt())
                .build();
    }
}
