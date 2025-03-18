package com.cobblemonbrasil.org.api_vincular;

import org.springframework.web.bind.annotation.*;

@RequestMapping
@RestController
public class TestController {

    @GetMapping("/vincular")
    public String estouAqui() {
        return "Estou aqui" ;
    }

}
