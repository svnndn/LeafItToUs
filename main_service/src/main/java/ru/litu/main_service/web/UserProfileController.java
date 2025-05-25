package ru.litu.main_service.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.litu.main_service.user.dto.NewUserDto;

@Controller
@RequestMapping("/users/profile")
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {

    @GetMapping
    public String getUserProfile(Model model) {
        model.addAttribute("user", new NewUserDto());
        return "profile";
    }
}
