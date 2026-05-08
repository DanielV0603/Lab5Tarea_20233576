package com.example.lab5.dto;

import java.time.LocalDate;

public interface InvoiceDto {
    Integer getId();
    String getType();
    LocalDate getDate();
    String getCustomerName();
    Double getTotal();
}
