package com.example.newboard.web.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "redirect:/articles";   // 살아있다는 신호 + 목록으로 이동
    }}