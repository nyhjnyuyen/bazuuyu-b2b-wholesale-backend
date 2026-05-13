package com.bazuuyu.b2b.auth.service;

import com.bazuuyu.b2b.auth.dto.LoginRequest;
import com.bazuuyu.b2b.auth.dto.RegisterRequest;
import com.bazuuyu.b2b.core.dto.RegisterResponse;
import com.bazuuyu.b2b.auth.repository.UserRepository;
import com.bazuuyu.b2b.core.dto.TokenResponse;
import com.bazuuyu.b2b.core.entity.Role;
import com.bazuuyu.b2b.core.entity.User;
import com.bazuuyu.b2b.core.enums.UserAccountStatus;
import com.bazuuyu.b2b.core.exception.ConflictException;
import com.bazuuyu.b2b.core.exception.ForbiddenException;
import com.bazuuyu.b2b.core.exception.NotFoundException;
import com.bazuuyu.b2b.core.security.AuthenticatedUser;
import com.bazuuyu.b2b.core.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("USERNAME_EXISTS", "Username already exists.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("EMAIL_EXISTS", "Email already exists.");
        }

        validateRoleRegistration(request.getRole());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setCompanyName(request.getCompanyName());
        user.setContactPerson(request.getContactPerson());
        user.setPhone(request.getPhone());
        user.setBusinessType(request.getBusinessType());
        user.setAccountStatus(request.getRole() == Role.WHOLESALE_BUYER
                ? UserAccountStatus.PENDING_APPROVAL
                : UserAccountStatus.APPROVED);

        User savedUser = userRepository.save(user);
        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getAccountStatus()
        );
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new BadCredentialsException("Invalid username/email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username/email or password.");
        }

        return toTokenResponse(user, jwtUtil.generateToken(user));
    }

    public TokenResponse getCurrentUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "User not found."));
        return toTokenResponse(user, null);
    }

    private TokenResponse toTokenResponse(User user, String accessToken) {
        return new TokenResponse(
                accessToken,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getAccountStatus()
        );
    }

    private void validateRoleRegistration(Role requestedRole) {
        if (requestedRole == Role.WHOLESALE_BUYER) {
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new ForbiddenException(
                    "ADMIN_PRIVILEGES_REQUIRED",
                    "Only an authenticated admin can register admin or sales manager accounts."
            );
        }

        if (user.role() != Role.ADMIN) {
            throw new ForbiddenException(
                    "ADMIN_PRIVILEGES_REQUIRED",
                    "Only admins can register admin or sales manager accounts."
            );
        }
    }
}
