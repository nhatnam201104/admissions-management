package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.UserService;
import com.example.managementadmissionwf.dal.entity.RoleUser;
import com.example.managementadmissionwf.dal.entity.Users;
import com.example.managementadmissionwf.dal.repository.UserRepository;
import com.example.managementadmissionwf.dto.User.*;
import com.example.managementadmissionwf.dto.common.ApiResponse;
import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse<Paging<GetUserResponse>> getUsers(GetUserRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getLimit());
        String keyword = request.getKeyword() != null ? request.getKeyword().trim() : "";
        String roleStr = request.getRole();

        Page<Users> page;
        Integer excludeId = request.getCurrentUserId();

        if (!roleStr.isEmpty() && !roleStr.equals("Tất cả")) {
            RoleUser role = RoleUser.valueOf(roleStr);
            if (keyword.isEmpty()) {
                if (excludeId != null) {
                    page = userRepository.findAllByIdNotAndRole(excludeId, role, pageable);
                } else {
                    page = userRepository.findByRole(role, pageable);
                }
            } else {
                if (excludeId != null) {
                    page = userRepository.findByRoleAndKeywordExcludingId(role, keyword, excludeId, pageable);
                } else {
                    page = userRepository.findByRoleAndKeyword(role, keyword, pageable);
                }
            }
        } else {
            if (keyword.isEmpty()) {
                if (excludeId != null) {
                    page = userRepository.findAllByIdNot(excludeId, pageable);
                } else {
                    page = userRepository.findAll(pageable);
                }
            } else {
                if (excludeId != null) {
                    page = userRepository.findByKeywordExcludingId(keyword, excludeId, pageable);
                } else {
                    page = userRepository.findByFullnameContainingOrUsernameContainingOrEmailContaining(
                            keyword, keyword, keyword, pageable);
                }
            }
        }

        List<GetUserResponse> data = userMapper.toGetUserResponses(page.getContent());

        Paging<GetUserResponse> paging = Paging.<GetUserResponse>builder()
                .page(request.getPage())
                .limit(request.getLimit())
                .totalPages(page.getTotalPages())
                .totalItems(page.getTotalElements())
                .hasNext(page.hasNext())
                .data(data)
                .build();

        return ApiResponse.<Paging<GetUserResponse>>builder()
                .code(200)
                .message("Thành công")
                .data(paging)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<CreateUserResponse> createUser(CreateUserRequest request) {
        // Block creating ADMIN users
        RoleUser requestedRole = RoleUser.valueOf(request.getRole());
        if (requestedRole == RoleUser.ADMIN) {
            return ApiResponse.<CreateUserResponse>builder()
                    .code(403)
                    .message("Không được phép tạo tài khoản ADMIN")
                    .build();
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            return ApiResponse.<CreateUserResponse>builder()
                    .code(400)
                    .message("Username đã tồn tại")
                    .build();
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.<CreateUserResponse>builder()
                    .code(400)
                    .message("Email đã tồn tại")
                    .build();
        }

        Users user = Users.builder()
                .fullname(request.getFullname())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(requestedRole)
                .build();

        user = userRepository.save(user);
        CreateUserResponse response = userMapper.toCreateResponse(user);

        return ApiResponse.<CreateUserResponse>builder()
                .code(201)
                .message("Tạo người dùng thành công")
                .data(response)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<UpdateUserResponse> updateUser(UpdateUserRequest request) {
        Users existing = userRepository.findById(request.getId()).orElse(null);
        if (existing == null) {
            return ApiResponse.<UpdateUserResponse>builder()
                    .code(404)
                    .message("Người dùng không tồn tại")
                    .build();
        }

        // Block updating to ADMIN role
        RoleUser requestedRole = RoleUser.valueOf(request.getRole());
        if (requestedRole == RoleUser.ADMIN) {
            return ApiResponse.<UpdateUserResponse>builder()
                    .code(403)
                    .message("Không được phép cập nhật thành role ADMIN")
                    .build();
        }

        if (userRepository.existsByUsername(request.getUsername()) && !existing.getUsername().equals(request.getUsername())) {
            return ApiResponse.<UpdateUserResponse>builder()
                    .code(400)
                    .message("Username đã tồn tại")
                    .build();
        }
        if (userRepository.existsByEmail(request.getEmail()) && !existing.getEmail().equals(request.getEmail())) {
            return ApiResponse.<UpdateUserResponse>builder()
                    .code(400)
                    .message("Email đã tồn tại")
                    .build();
        }

        existing.setFullname(request.getFullname());
        existing.setEmail(request.getEmail());
        existing.setUsername(request.getUsername());
        existing.setRole(requestedRole);

        existing = userRepository.save(existing);
        UpdateUserResponse response = userMapper.toUpdateResponse(existing);

        return ApiResponse.<UpdateUserResponse>builder()
                .code(200)
                .message("Cập nhật người dùng thành công")
                .data(response)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteUser(Integer id) {
        if (!userRepository.existsByIdAndIsDeletedFalse(id)) {
            return ApiResponse.<Void>builder()
                    .code(404)
                    .message("Người dùng không tồn tại")
                    .build();
        }

        userRepository.softDeleteById(id);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Xóa người dùng thành công")
                .build();
    }
}
