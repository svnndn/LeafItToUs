package ru.litu.main_service.user.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.litu.main_service.exception.ObjectNotFoundException;
import ru.litu.main_service.exception.SQLConstraintViolationException;
import ru.litu.main_service.user.dto.NewUserDto;
import ru.litu.main_service.user.dto.UpdateUserDto;
import ru.litu.main_service.user.dto.UserDto;
import ru.litu.main_service.user.mapper.UserMapper;
import ru.litu.main_service.user.model.Role;
import ru.litu.main_service.user.model.User;
import ru.litu.main_service.user.repository.RoleRepository;
import ru.litu.main_service.user.repository.UserRepository;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    @PersistenceContext
    private EntityManager em;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    public User addAdminUser(User user) throws SQLConstraintViolationException {

        Role role = roleRepository.getByName("ROLE_USER").orElseThrow(() -> {
            throw new ObjectNotFoundException("Role with name = 'USER' doesn't exist.");
        });

        user.setRoles(Collections.singleton(role));

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new SQLConstraintViolationException("User name and/or email already exists.");
        }
    }

    @Override
    public UserDto getById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User with id = " + userId + "doesn't exist."));
        return userMapper.userToUserDto(user);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getByIds(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());

        if (ids == null) {
            return userRepository.findAll(pageable).stream().collect(Collectors.toList());
        } else {
            return userRepository.findAllByIdIn(ids, pageable).stream().collect(Collectors.toList());
        }
    }

    @Override
    public void delete(Long userId) {
        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("User with id = " + userId + " was not found.");
        }
    }

    public User findByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new ObjectNotFoundException("User with email = " + email + " doesn't exist."));
    }

    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new ObjectNotFoundException("User with username = " + username + " doesn't exist."));
    }

    @Override
    public List<User> getUsersWithIdBiggerThan(Long idMin) {
        return em.createQuery("SELECT u FROM User u WHERE u.id > :paramId", User.class).setParameter("paramId", idMin).getResultList();
    }

    @Override
    public User add(NewUserDto newUser) throws SQLConstraintViolationException {
        User user = userMapper.newUserRequestDtoToUser(newUser);

        Role role = roleRepository.getByName("ROLE_USER").orElseThrow(() -> {
            throw new ObjectNotFoundException("Role 'USER' doesn't exist.");
        });

        user.setEmail(newUser.getEmail());
        user.setName(newUser.getName());
        user.setUsername(newUser.getUsername());
        user.setRoles(Collections.singleton(role));
        user.setPassword(newUser.getPassword());

        user = userRepository.save(user);

        log.info("Создан пользователь с id = " + user.getId());

        return user;
    }

    @Override
    public User update(Long id, UpdateUserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User with id = " + id + " doesn't exist."));

        userMapper.updateUserFromDto(dto, user);
        return userRepository.save(user);
    }

}