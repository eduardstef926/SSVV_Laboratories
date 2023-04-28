package ssvv.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ssvv.example.domain.Tema;
import ssvv.example.exceptions.AlreadyExistingEntityException;
import ssvv.example.exceptions.ValidationException;
import ssvv.example.repository.NotaXMLRepository;
import ssvv.example.repository.StudentXMLRepository;
import ssvv.example.repository.TemaXMLRepository;
import ssvv.example.service.Service;
import ssvv.example.validation.NotaValidator;
import ssvv.example.validation.StudentValidator;
import ssvv.example.validation.TemaValidator;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class AssignmentTest {
    private static final String VALID_ID = "1";
    private static final String VALID_DESCRIPTION = "description";
    private static final Integer VALID_DEADLINE = 14;
    private static final Integer VALID_STARTLINE = 1;

    private static final String INVALID_ID_ERROR_MESSAGE = "ID invalid! \n";
    private static final String INVALID_DESCRIPTION_ERROR_MESSAGE = "Descriere invalida! \n";
    private static final String INVALID_DEADLINE_ERROR_MESSAGE = "Deadline invalid! \n";
    private static final String INVALID_STARTLINE_ERROR_MESSAGE = "Data de primire invalida! \n";

    private Service service;

    @Before
    public void setUp() throws IOException {
        File studentFile = XmlTestUtils.createXMLFile(XmlTestUtils.STUDENT_TEST_XML_FILE);
        File temeFile = XmlTestUtils.createXMLFile(XmlTestUtils.HOMEWORK_TEST_XML_FILE);
        File noteFile = XmlTestUtils.createXMLFile(XmlTestUtils.GRADES_TEST_XML_FILE);

        StudentXMLRepository studentRepo = new StudentXMLRepository(new StudentValidator(), studentFile.getPath());
        TemaXMLRepository temaRepo = new TemaXMLRepository(new TemaValidator(), temeFile.getPath());
        NotaXMLRepository notaRepo = new NotaXMLRepository(new NotaValidator(), noteFile.getPath());
        service = new Service(studentRepo, temaRepo, notaRepo);
    }

    @After
    public void tearDown() {
        deleteFileIfExists(XmlTestUtils.STUDENT_TEST_XML_FILE);
        deleteFileIfExists(XmlTestUtils.HOMEWORK_TEST_XML_FILE);
        deleteFileIfExists(XmlTestUtils.GRADES_TEST_XML_FILE);
    }

    private void deleteFileIfExists(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (!file.delete()) {
                System.err.println("Failed to delete file: " + filePath);
            }
        }
    }

    @Test
    public void addAssignment_OK() {
        assertDoesNotThrow(() -> service.saveTema(VALID_ID, VALID_DESCRIPTION, VALID_DEADLINE, VALID_STARTLINE));
        Optional<Tema> addedAssignmentOptional = StreamSupport.stream(service.findAllTeme().spliterator(), false)
                .filter(assignment -> assignment.getID().equals(VALID_ID))
                .findFirst();

        assertTrue(addedAssignmentOptional.isPresent());
        Tema addedAssignment = addedAssignmentOptional.get();

        assertEquals(VALID_ID, addedAssignment.getID());
        assertEquals(VALID_DESCRIPTION, addedAssignment.getDescriere());
        assertEquals(VALID_DEADLINE.intValue(), addedAssignment.getDeadline());
        assertEquals(VALID_STARTLINE.intValue(), addedAssignment.getStartline());
    }

    @Test
    public void addAssignment_Fail_InvalidId() {
        Throwable exception = assertThrows(ValidationException.class,
                () -> service.saveTema(null, VALID_DESCRIPTION, VALID_DEADLINE, VALID_STARTLINE));

        assertEquals(INVALID_ID_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    public void addAssignment_Fail_InvalidDescription() {
        Throwable exception = assertThrows(ValidationException.class,
                () -> service.saveTema(VALID_ID, null, VALID_DEADLINE, VALID_STARTLINE));

        assertEquals(INVALID_DESCRIPTION_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    public void addAssignment_Fail_InvalidDeadline() {
        Throwable exception = assertThrows(ValidationException.class,
                () -> service.saveTema(VALID_ID, VALID_DESCRIPTION, 0, VALID_STARTLINE));

        assertEquals(INVALID_DEADLINE_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    public void addAssignment_Fail_InvalidStartline() {
        Throwable exception = assertThrows(ValidationException.class,
                () -> service.saveTema(VALID_ID, VALID_DESCRIPTION, VALID_DEADLINE, 0));

        assertEquals(INVALID_STARTLINE_ERROR_MESSAGE, exception.getMessage());
    }


    @Test
    public void addAssignment_Fail_EntityWithIdAlreadyPresent() {
        assertThrows(AlreadyExistingEntityException.class, () -> {
            service.saveTema(VALID_ID, VALID_DESCRIPTION, VALID_DEADLINE, VALID_STARTLINE);
            service.saveTema(VALID_ID, "Another Description", VALID_DEADLINE, VALID_STARTLINE);
        });
    }
}


