package com.dmartinez;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@RestController
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @GetMapping("/greet")
    public GreetResponse greet() {
       GreetResponse response = new GreetResponse(
                "Hello World!",
                List.of("Java", "GoLang", "Javascript"),
                        new Person("Alex", 28, 30_000)
       );
       return response;
    }

    public record Person(String name, int age, double savings) {}

    public record GreetResponse(
            String greet,
            List<String> favProgrammingLanguages,
            Person person
    ){}
}
