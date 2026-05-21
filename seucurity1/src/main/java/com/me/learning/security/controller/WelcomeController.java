/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 19/05/2026
 * Usage    :
 * Since    : Version 1.0
 */
package com.me.learning.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/welcome")
public class WelcomeController {

    @GetMapping
    public String welcome () {
        return "Welcome to Spring Security";
    }
}
