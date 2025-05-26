package ru.litu.forum_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ForumController {

    @GetMapping("/index")
    public String getIndex() {
        return "qu";
    }
}
