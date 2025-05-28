package ru.litu.calendar_service.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.litu.calendar_service.task.Task;

@Controller
@RequestMapping("/")
public class TemplateController {
    @GetMapping("/calendar")
    public String calendar(Model model) {
        Task task = new Task();
        model.addAttribute(task);
        return "calendare";
    }
}
