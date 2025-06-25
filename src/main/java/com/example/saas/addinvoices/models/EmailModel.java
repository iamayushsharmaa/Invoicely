package com.example.saas.addinvoices.models;

import lombok.Data;

@Data
public class EmailModel {
    private Long id;
    private String to;
    private String message;
}
