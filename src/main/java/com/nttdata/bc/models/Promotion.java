package com.nttdata.bc.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Promotion {
    private String id;

    @NotEmpty(message = "El campo title es requerido.")
    private String title;

    @NotEmpty(message = "El campo storeName es requerido.")
    private String storeName;

    @DecimalMin(value = "1.0", message = "El campo amountPay debe tener un valor mínimo de '1.0'.")
    @Digits(integer = 10, fraction = 3, message = "El campo amountPay tiene un formato no válido (#####.000).")
    @NotNull(message = "El campo amountPay es requerido.")
    private BigDecimal amountPay; // monto a pagar

    @DecimalMin(value = "1.0", message = "El campo discountPercentage debe tener un valor mínimo de '1.0'.")
    @Digits(integer = 10, fraction = 3, message = "El campo discountPercentage tiene un formato no válido (#####.000).")
    @NotNull(message = "El campo discountPercentage es requerido.")
    private BigDecimal discountPercentage; // porcentaje de descuento

    @DecimalMin(value = "1.0", message = "El campo originalAmount debe tener un valor mínimo de '1.0'.")
    @Digits(integer = 10, fraction = 3, message = "El campo originalAmount tiene un formato no válido (#####.000).")
    @NotNull(message = "El campo originalAmount es requerido.")
    private BigDecimal originalAmount; // monto original

    @NotEmpty(message = "El campo description es requerido.")
    private String description;

    @Digits(integer = 10, fraction = 0, message = "El campo validityInterval debe ser un número.")
    @NotNull(message = "El campo validityInterval es requerido.")
    private int validityInterval; // intervalo de vigencia de la promoción

    @Digits(integer = 10, fraction = 0, message = "El campo stock debe ser un número.")
    @NotNull(message = "El campo stock es requerido.")
    private int stock;

    @NotEmpty(message = "El campo imageUrl es requerido.")
    private String imageUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updateddAt;
}
