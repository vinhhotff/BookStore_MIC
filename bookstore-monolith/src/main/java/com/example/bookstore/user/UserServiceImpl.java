package com.example.bookstore.user;

import com.example.bookstore.user.UserCreationRequest;
import com.example.bookstore.user.UserResponse;
import com.example.bookstore.exception.AppException;
import com.example.bookstore.exception.ErrorCode;
import com.example.bookstore.user.UserMapper;
import com.example.bookstore.user.User;
import com.example.bookstore.role.Role;
import com.example.bookstore.role.RoleRepository;
import com.example.bookstore.user.UserRepository;
import com.example.bookstore.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserCreationRequest request) {
        // 1. Kiểm tra trùng lặp
        if (userRepository.existsByUsername(request.username())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = userMapper.toUser(request);

        // 3. Mã hóa mật khẩu trước khi lưu
        user.setPassword(passwordEncoder.encode(request.password()));

        // Tự động kiểm tra và tạo vai trò USER trong database nếu chưa tồn tại
        Role userRole = roleRepository.findById("USER").orElseGet(() -> {
            Role newRole = Role.builder()
                    .name("USER")
                    .description("User role")
                    .build();
            return roleRepository.save(newRole);
        });

        user.setRoles(Set.of(userRole));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getMyinfo() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

}
