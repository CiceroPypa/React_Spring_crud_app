package com.example.FirstSpringRest.controllers;

import com.example.FirstSpringRest.dto.PersonDTO;
import com.example.FirstSpringRest.models.Person;
import com.example.FirstSpringRest.services.PeopleService;
import com.example.FirstSpringRest.util.PersonErrorResponse;
import com.example.FirstSpringRest.util.PersonNotCreatedException;
import com.example.FirstSpringRest.util.PersonNotFoundException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000/")
@RestController
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;
    private final ModelMapper modelMapper;

    @Autowired
    public PeopleController(PeopleService peopleService,
                            ModelMapper modelMapper) {
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<PersonDTO> getPeople(){
        return peopleService.findAll().stream().map(this::convertToPersonDTO)
                .collect(Collectors.toList());// Jackson конвертує ці об'єкти в Json
    }

    @GetMapping("/{id}")
    public PersonDTO getPerson(@PathVariable(name = "id") int id){
        return convertToPersonDTO(peopleService.findOne(id)); // Jackson конвертує ці об'єкти в Json
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid PersonDTO personDTO,
                                             BindingResult bindingResult){
        bindingResult(bindingResult);
        peopleService.save(convertToPerson(personDTO));

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@RequestBody @Valid PersonDTO personDTO,
                                             BindingResult bindingResult, @PathVariable(name = "id") int id){
        bindingResult(bindingResult);
        peopleService.update(id, convertToPerson(personDTO));

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable(name = "id") int id){
        peopleService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e){
        PersonErrorResponse response = new PersonErrorResponse(
                "Person with this id wasn't found",
                System.currentTimeMillis()
        );

        //В HTTP відповіді буде тіло і статус в заголовкі
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);// NOT_FOUND - 404 status

    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e){
        PersonErrorResponse response = new PersonErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );

        //В HTTP відповіді буде тіло і статус в заголовкі
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);// NOT_FOUND - 4XX status

    }

    private Person convertToPerson(PersonDTO personDTO){
        return modelMapper.map(personDTO, Person.class);
    }

    private PersonDTO convertToPersonDTO(Person person){
        return modelMapper.map(person, PersonDTO.class);
    }

    private void bindingResult(BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for(FieldError error : errors){
                errorMessage.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append("; ");
            }

            throw new PersonNotCreatedException(errorMessage.toString());

        }
    }
}
