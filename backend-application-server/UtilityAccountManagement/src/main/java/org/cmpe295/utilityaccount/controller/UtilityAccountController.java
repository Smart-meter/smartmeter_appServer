package org.cmpe295.utilityaccount.controller;

import org.cmpe295.utilityaccount.entity.UtilityAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.cmpe295.utilityaccount.repository.UtilityAccountRepository;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/utility-accounts")
public class UtilityAccountController {
    @Autowired
    private UtilityAccountRepository utilityAccountRepository;
    @GetMapping
    public List<UtilityAccount> getAllUtilityAccounts() {
        return utilityAccountRepository.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<UtilityAccount> getUtilityAccountById(@PathVariable Long id) {
        return utilityAccountRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<UtilityAccount> createUtilityAccount(@RequestBody UtilityAccount utilityAccount) {
        utilityAccount.setCreationDate(LocalDate.now());
        UtilityAccount savedAccount = utilityAccountRepository.save(utilityAccount);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAccount);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UtilityAccount> updateUtilityAccount(@PathVariable Long id, @RequestBody UtilityAccount utilityAccount) {
        if (!utilityAccountRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        utilityAccount.setId(id);
        UtilityAccount updatedAccount = utilityAccountRepository.save(utilityAccount);
        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtilityAccount(@PathVariable Long id) {
        if (!utilityAccountRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        utilityAccountRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
