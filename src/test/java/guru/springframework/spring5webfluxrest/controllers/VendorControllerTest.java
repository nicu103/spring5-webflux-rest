package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class VendorControllerTest {
    WebTestClient webTestClient;
    VendorRepository vendorRepository;
    VendorController vendorController;

    @Before
    public void setUp() {
        vendorRepository = Mockito.mock(VendorRepository.class);
        vendorController = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    public void getAllVendors() {
        BDDMockito.given(vendorRepository.findAll())
                .willReturn(
                        Flux.just(
                                Vendor.builder().firstName("Joe").lastName("Buck").build(),
                                Vendor.builder().firstName("Jenny").lastName("Buck").build()
                        )
                );
        webTestClient
                .get()
                .uri("/api/v1/vendors")
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    public void getVendorById() {
        BDDMockito.given((vendorRepository.findById(anyString())))
                .willReturn(
                        Mono.just(
                                Vendor.builder().firstName("Jenny").lastName("Buck").build()
                        )
                );
        webTestClient
                .get()
                .uri("/api/v1/vendors/someId")
                .exchange()
                .expectBody(Vendor.class)
                .value(hasProperty("firstName", equalTo("Jenny")))
                .value(hasProperty("lastName", equalTo("Buck")));
    }

    @Test
    public void create() {
        BDDMockito.given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Vendor.builder().build()));
        Mono<Vendor> vendorToSave = Mono.just(Vendor.builder()
                .firstName("Joe")
                .lastName("Buck")
                .build());
        webTestClient.post()
                .uri("/api/v1/vendors")
                .body(vendorToSave, Vendor.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void update() {
        BDDMockito.given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorToSave = Mono.just(
                Vendor.builder()
                        .firstName("Joe")
                        .lastName("Buck")
                        .build());

        webTestClient.put()
                .uri("/api/v1/vendors/someId")
                .body(vendorToSave, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void patchWithChanges() {
        BDDMockito.given((vendorRepository.findById(anyString())))
                .willReturn(
                        Mono.just(
                                Vendor.builder().firstName("Jenny").lastName("Buck").build()
                        )
                );

        BDDMockito.given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorToSave = Mono.just(
                Vendor.builder()
                        .firstName("Janine")
                        .lastName("Buck")
                        .build());

        webTestClient.patch()
                .uri("/api/v1/vendors/someId")
                .body(vendorToSave, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(vendorRepository).save(any());
    }

    @Test
    public void patcNoChanges() {
        BDDMockito.given((vendorRepository.findById(anyString())))
                .willReturn(
                        Mono.just(
                                Vendor.builder().firstName("Jenny").lastName("Buck").build()
                        )
                );

        BDDMockito.given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorToSave = Mono.just(
                Vendor.builder()
                        .firstName("Jenny")
                        .lastName("Buck")
                        .build());

        webTestClient.patch()
                .uri("/api/v1/vendors/someId")
                .body(vendorToSave, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(vendorRepository, never()).save(any());
    }

    @Test
    public void delete() {
        webTestClient
                .delete()
                .uri("/api/v1/vendors/someId")
                .exchange()
                .expectStatus()
                .isOk();
        verify(vendorRepository).deleteById(anyString());
    }
}