package com.jeanbarcellos.project110.entity;

import java.io.Serializable;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Entity
@DynamicUpdate
@Table(schema = "project110", name = "category")
public class Category implements Serializable {

    @Id
    @GeneratedValue(generator = "category_id_seq_generator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "category_id_seq_generator", schema = "project110", sequenceName = "category_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
}
