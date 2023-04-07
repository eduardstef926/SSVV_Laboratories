package ssvv.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ssvv.example.exceptions.*;
import ssvv.example.repository.*;
import ssvv.example.service.Service;
import ssvv.example.validation.*;
import ssvv.example.domain.*;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class AssignmentTest {

    private static final String VALID_ID = "1";
    private static final String VALID_DESCRIPTION = "ultimii dintre noi";
    private static final Integer VALID_DEADLINE = 14;
    private static final Integer VALID_STARTLINE = 1;

    private Service service;

    @Before
    public void setUp() throws IOException {
        File studentFile = XmlTestUtils.createXMLFile(XmlTestUtils.STUDENT_TEST_XML_FILE);
        File temeFile = XmlTestUtils.createXMLFile(XmlTestUtils.HOMEWORK_TEST_XML_FILE);
        File noteFile = XmlTestUtils.createXMLFile(XmlTestUtils.GRADES_TEST_XML_FILE);

        StudentXMLRepository fileRepository1 = new StudentXMLRepository(new StudentValidator(), studentFile.getPath());
        TemaXMLRepository fileRepository2 = new TemaXMLRepository(new TemaValidator(), temeFile.getPath());
        NotaXMLRepository fileRepository3 = new NotaXMLRepository(new NotaValidator(), noteFile.getPath());
        service = new Service(fileRepository1, fileRepository2, fileRepository3);
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
    public void addAssignment_Fail_EntityWithIdAlreadyPresent() {
        assertThrows(AlreadyExistingEntityException.class, () -> {
            service.saveTema(VALID_ID, VALID_DESCRIPTION, VALID_DEADLINE, VALID_STARTLINE);
            service.saveTema(VALID_ID, "penultimii dintre noi", VALID_DEADLINE, VALID_STARTLINE);
        });
    }

    @Test
    public void addAssignment_OK() {
        // Execute and assert no exceptions are thrown
        assertDoesNotThrow(() -> service.saveTema(VALID_ID, VALID_DESCRIPTION, VALID_DEADLINE, VALID_STARTLINE));

        // Find the added assignment
        Optional<Tema> addedAssignmentOptional = StreamSupport.stream(service.findAllTeme().spliterator(), false)
                .filter(assignment -> assignment.getID().equals(VALID_ID))
                .findFirst();

        // Assert that the assignment was found and is not null
        assertTrue(addedAssignmentOptional.isPresent());
        Tema addedAssignment = addedAssignmentOptional.get();
        // Assert that the added assignment has the expected properties
        assertEquals(VALID_ID, addedAssignment.getID());
        assertEquals(VALID_DESCRIPTION, addedAssignment.getDescriere());
        assertEquals(VALID_DEADLINE.intValue(), addedAssignment.getDeadline());
        assertEquals(VALID_STARTLINE.intValue(), addedAssignment.getStartline());
    }

}
