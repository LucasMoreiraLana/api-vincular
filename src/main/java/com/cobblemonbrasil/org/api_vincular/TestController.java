package com.cobblemonbrasil.org.api_vincular;

import org.springframework.web.bind.annotation.*;

@RequestMapping
@RestController
public class TestController {

    @PostMapping("/vincular")
    public String helloWorld(@RequestBody String body) {
        return body;
    }

}
