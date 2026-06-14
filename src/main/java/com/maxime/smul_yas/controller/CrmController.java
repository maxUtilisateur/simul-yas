package com.maxime.smul_yas.controller;

import com.maxime.smul_yas.dto.crm_dto.CreditBalanceResponseDto;
import com.maxime.smul_yas.dto.crm_dto.RechargeCreditDto;
import com.maxime.smul_yas.dto.crm_dto.TransfererCreditDto;
import com.maxime.smul_yas.dto.crm_dto.TmoneyAccountResponseDto;
import com.maxime.smul_yas.service.CrmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/crm")
@RequiredArgsConstructor
public class CrmController {

    private final CrmService crmService;

    @PostMapping("/recharger")
    public ResponseEntity<CreditBalanceResponseDto> rechargerCredit(@RequestBody RechargeCreditDto dto) {
        return ResponseEntity.ok(crmService.rechargerCredit(dto));
    }

    @PostMapping("/transferer")
    public ResponseEntity<CreditBalanceResponseDto> transfererCredit(@RequestBody TransfererCreditDto dto) {
        return ResponseEntity.ok(crmService.transfererCredit(dto));
    }

    @GetMapping("/solde-credit")
    public ResponseEntity<CreditBalanceResponseDto> consulterSoldeCredit(@RequestParam String phone) {
        return ResponseEntity.ok(crmService.consulterSoldeCredit(phone));
    }

    @GetMapping("/solde-tmoney")
    public ResponseEntity<TmoneyAccountResponseDto> consulterSoldeTmoney(@RequestParam String phone) {
        return ResponseEntity.ok(crmService.consulterSoldeTmoney(phone));
    }
}
