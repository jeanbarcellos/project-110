package com.jeanbarcellos.project110.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonResponse implements Serializable {

    private Long id;

    private String name;

    private LocalDate birthDate;

}
