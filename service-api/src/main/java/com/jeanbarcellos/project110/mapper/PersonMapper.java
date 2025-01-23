package com.jeanbarcellos.project110.mapper;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.jeanbarcellos.project110.dto.PersonRequest;
import com.jeanbarcellos.project110.dto.PersonResponse;
import com.jeanbarcellos.project110.entity.Person;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PersonMapper {

    private final ModelMapper modelMapper;

    public Person toEntity(PersonRequest request) {
        return this.modelMapper.map(request, Person.class);
    }

    public PersonResponse toResponse(Person person) {
        return this.modelMapper.map(person, PersonResponse.class);
    }

    public List<PersonResponse> toResponseList(List<Person> persons) {
        return persons.stream()
                .map(this::toResponse)
                .toList();
    }

    public Person copy(Person entity, PersonRequest source) {
        this.modelMapper.map(source, entity);
        // entity.setName(source.getName());
        // entity.setBirthDate(source.getBirthDate());
        return entity;
    }
}
