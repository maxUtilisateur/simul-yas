package com.maxime.smul_yas.controller;

import com.maxime.smul_yas.dto.offres_dto.AcheterForfaitDto;
import com.maxime.smul_yas.dto.offres_dto.ActiveForfaitResponseDto;
import com.maxime.smul_yas.dto.offres_dto.OffreResponseDto;
import com.maxime.smul_yas.enums.CategoriesType;
import com.maxime.smul_yas.service.OffresService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/offres")
@RequiredArgsConstructor
public class  OffresController {

    private final OffresService offresService;

    @GetMapping
    public ResponseEntity<List<OffreResponseDto>> afficherForfaits() {
        return ResponseEntity.ok(offresService.afficherForfaits());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoriesType>> afficherCategories() {
        return ResponseEntity.ok(offresService.afficherCategories());
    }

    @PostMapping("/acheter")
    public ResponseEntity<ActiveForfaitResponseDto> acheterForfait(@RequestBody AcheterForfaitDto dto) {
        return ResponseEntity.ok(offresService.acheterForfait(dto));
    }

    @GetMapping("/solde-forfaits")
    public ResponseEntity<List<ActiveForfaitResponseDto>> consulterSoldeForfaits(@RequestParam String phone) {
        return ResponseEntity.ok(offresService.consulterSoldeForfaits(phone));
    }
}
