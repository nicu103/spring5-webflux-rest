package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    Flux<Category> list() {
        return categoryRepository.findAll();
    }

    @GetMapping("/{id}")
    Mono<Category> getById(@PathVariable String id) {
        return categoryRepository.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<Void> create(@RequestBody Publisher<Category> categoryStream) {
        return categoryRepository.saveAll(categoryStream).then();
    }

    @PutMapping("/{id}")
    Mono<Category> update(@PathVariable("id") String id, @RequestBody Category category) {
        category.setId(id);
        return categoryRepository.save(category);
    }

    @PatchMapping("/{id}")
    Mono<Category> patch(@PathVariable("id") String id, @RequestBody Category category) {
        Category foundCategory = categoryRepository.findById(id).block();
        if (!Objects.equals(foundCategory.getDescription(), category.getDescription())) {
            foundCategory.setDescription(category.getDescription());
            return  categoryRepository.save(foundCategory);
        }
        return Mono.just(foundCategory);
    }

    @DeleteMapping("/{id}")
    Mono<Void> deleteById(@PathVariable("id") String id) {
        return categoryRepository.deleteById(id);
    }
}
