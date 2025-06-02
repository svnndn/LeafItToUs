package ru.litu.calendar_service.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.litu.calendar_service.task.Task;

@Controller
@RequestMapping("/")
public class TemplateController {
    @GetMapping("/calendar/{userId}")
    public String calendar(@PathVariable("userId") long userId, Model model) {
        Task task = new Task();
        model.addAttribute(task);
        model.addAttribute("userId", userId);
        return "calendare";
    }
}
