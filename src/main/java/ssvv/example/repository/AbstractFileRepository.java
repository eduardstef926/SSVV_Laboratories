package ssvv.example.repository;

import ssvv.example.domain.HasID;
import ssvv.example.exceptions.*;
import ssvv.example.validation.Validator;

public abstract class AbstractFileRepository<ID, E extends HasID<ID>> extends AbstractCRUDRepository<ID,E>{
    protected String filename;

    public AbstractFileRepository(Validator<E> validator, String filename) {
        super(validator);
        this.filename = filename;
    }

    protected abstract void loadFromFile();
    protected abstract void writeToFile(E entity);
    protected abstract void writeToFileAll();

    @Override
    public Iterable<E> findAll() {
        loadFromFile();
        return super.findAll();
    }

    @Override
    public E save(E entity) throws ValidationException, AlreadyExistingEntityException {
        E result = super.save(entity);
        writeToFile(entity);
        return result;
    }

    @Override
    public E delete(ID id) {
        E result = super.delete(id);
        writeToFileAll();

        return result;
    }

    @Override
    public E update(E newEntity) {
        E result = super.update(newEntity);
        writeToFileAll();

        return result;
    }
}
