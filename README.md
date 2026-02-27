# Project 110

Projeto para testes do cache

## Features

- `dev-0001` Inicial
- `dev-0002` Melhorias

## Entidades

```java

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Entity
@DynamicInsert
@DynamicUpdate
@Table(schema = "project110", name = "category")
public class Category {

    @Id
    @GeneratedValue(generator = "category_id_seq_generator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "category_id_seq_generator", schema = "project110", sequenceName = "category_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
}


```
