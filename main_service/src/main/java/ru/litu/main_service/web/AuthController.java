package ru.litu.main_service.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.litu.main_service.exception.SQLConstraintViolationException;
import ru.litu.main_service.user.dto.NewUserDto;
import ru.litu.main_service.user.dto.UserDto;
import ru.litu.main_service.user.dto.UserLoginDto;
import ru.litu.main_service.user.mapper.UserMapper;
import ru.litu.main_service.user.model.User;
import ru.litu.main_service.user.service.UserService;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/sign-up")
    public String getSignUp(Model model) {
        model.addAttribute("user", new NewUserDto());
        return "sign-up";
    }


    @PostMapping("/sign-up")
    public String createUserAndRedirectToLogin(@ModelAttribute("user") @Valid NewUserDto user, Model model) {

        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            model.addAttribute("errorMessage", "Пароли не совпадают.");
            return "sign-up";
        }

        try {
            userService.add(user);
        } catch (SQLConstraintViolationException e) {
            model.addAttribute("errorMessage", "Пользователь с таким именем или email уже существует.");
            return "sign-up";
        }

        return "redirect:/sign-in";
    }

    @GetMapping("/sign-in")
    public String getLogin() {
        return "sign-in";
    }
}