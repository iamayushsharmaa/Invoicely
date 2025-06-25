package com.example.saas.addinvoices.controller;

import com.example.saas.addinvoices.models.Invoice;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Filter;
import org.springframework.web.bind.annotation.*;

@RestController("/api")
@RequiredArgsConstructor
public class AddInovicesController {

    @PostMapping("/invoices")
    public boolean addInvoice(@RequestBody Invoice invoiceRequest) {
        return true;
    }

    @GetMapping("/invoices/{id}")
    public void getInvoice(@PathVariable Long id){

    }

    @GetMapping("/invoices/")
    public void filterInvoices(){

    }


    @GetMapping("/invoices/{id}/pdf")
    public void generateInvoicePdf(@PathVariable Long id){

    }

    @GetMapping("/invoices/{id}/send-email")
    public void sendInvoiceEmail(){

    }

    @PutMapping("/invoices/{id}/status")
    public void updateInvoiceStatus(){

    }
}
