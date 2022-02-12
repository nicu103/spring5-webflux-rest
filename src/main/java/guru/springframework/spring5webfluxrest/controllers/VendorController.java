package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/vendors")
public class VendorController {
    private final VendorRepository vendorRepository;

    public VendorController(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @GetMapping
    Flux<Vendor> getAllVendors() {
      return vendorRepository.findAll();
    }

    @GetMapping("/{id}")
    Mono<Vendor> getVendorById(@PathVariable("id") String id) {
        return vendorRepository.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<Void> create(@RequestBody Publisher<Vendor> vendorStream) {
        return vendorRepository.saveAll(vendorStream).then();
    }

    @PutMapping("/{id}")
    Mono<Vendor> update(@PathVariable("id") String id, @RequestBody Vendor vendor) {
        vendor.setId(id);
        return vendorRepository.save(vendor);
    }

    @PatchMapping("/{id}")
    Mono<Vendor> patch(@PathVariable("id") String id, @RequestBody Vendor vendor) {
        Vendor foundVendor = vendorRepository.findById(id).block();
        boolean isChanged = false;

        if (!Objects.equals(foundVendor.getFirstName(), vendor.getFirstName())) {
            isChanged = true;
            foundVendor.setFirstName(vendor.getFirstName());
        }

        if (!Objects.equals(foundVendor.getLastName(), vendor.getLastName())) {
            isChanged = true;
            foundVendor.setLastName(vendor.getLastName());
        }

        if (isChanged) {
            return vendorRepository.save(foundVendor);
        }
        return Mono.just(foundVendor);
    }

    @DeleteMapping("/{id}")
    Mono<Void> deleteById(@PathVariable("id") String id) {
        return vendorRepository.deleteById(id);
    }
}
