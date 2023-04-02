package ssvv.example.validation;

import ssvv.example.exceptions.ValidationException;

public interface Validator<E> {
    void validate(E entity) throws ValidationException;
}