package ru.litu.main_service.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.litu.main_service.exception.SQLConstraintViolationException;
import ru.litu.main_service.user.dto.NewUserDto;
import ru.litu.main_service.user.mapper.UserMapper;
import ru.litu.main_service.user.service.UserService;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;
//    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    @GetMapping("/sign-up")
    public String getSignUp(Model model) {
        model.addAttribute("user", new NewUserDto());
        return "sign-up";
    }


    @PostMapping("/sign-up")
    public String createUserAndRedirectToLogin(@ModelAttribute("user") @Valid NewUserDto user, Model model) {

        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            model.addAttribute("errorMessage", "Пароли не совпадают.");
//            log.error("Пароли не совпадают");
            System.out.println("Пароли не совпадают");
            return "sign-up";
        }

        try {
            userService.add(user);
        } catch (SQLConstraintViolationException e) {
            model.addAttribute("errorMessage", "Пользователь с таким именем или email уже существует.");
            System.out.println("Пользователь с таким именем или email уже существует.");
            return "sign-up";
        }

        return "redirect:/sign-in";
    }

    @GetMapping("/sign-in")
    public String getLogin() {
        return "sign-in";
    }
}