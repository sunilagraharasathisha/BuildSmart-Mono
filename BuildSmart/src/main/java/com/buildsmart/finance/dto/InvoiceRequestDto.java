package com.buildsmart.finance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InvoiceRequestDto {

    @NotBlank(message = "Invoice number is required")
    private String invoiceNumber;
}
