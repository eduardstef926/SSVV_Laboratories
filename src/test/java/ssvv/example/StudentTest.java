package ssvv.example;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StudentTest {
    private static final String VALID_ID = "9";
    private static final String VALID_NAME = "Joel";
    private static final int VALID_GROUP = 937;

    private static final String INVALID_ID_ERROR_MESSAGE = "ID invalid!\n";
    private static final String INVALID_NAME_ERROR_MESSAGE = "Nume invalid!\n";
    private static final String GROUP_NAME_ERROR_MESSAGE = "Grupa invalid!\n";

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
    public void addStudent_Fail_nullIdTest() {
        assertThrows(ValidationException.class, () -> service.saveStudent(null, VALID_NAME, VALID_GROUP), INVALID_ID_ERROR_MESSAGE);
    }

    @Test
    public void addStudent_Fail_emptyIdTest() {
        assertThrows(ValidationException.class, () -> service.saveStudent("", VALID_NAME, VALID_GROUP), INVALID_ID_ERROR_MESSAGE);
    }

    @Test
    public void addStudent_Fail_nullNameTest() {
        assertThrows(ValidationException.class, () -> service.saveStudent(VALID_ID, null, VALID_GROUP), INVALID_NAME_ERROR_MESSAGE);
    }

    @Test
    public void addStudent_Fail_emptyNameTest() {
        assertThrows(ValidationException.class, () -> service.saveStudent(VALID_ID, "", VALID_GROUP), INVALID_NAME_ERROR_MESSAGE);
    }

    @Test
    public void addStudent_Fail_tooLowGroupTest() {
        assertThrows(ValidationException.class, () -> service.saveStudent(VALID_ID, VALID_NAME, 110), GROUP_NAME_ERROR_MESSAGE);
    }

    @Test
    public void addStudent_Fail_tooHighGroupTest() {
        assertThrows(ValidationException.class, () -> service.saveStudent(VALID_ID, null, 938), GROUP_NAME_ERROR_MESSAGE);
    }

    private void addStudentAndAssert(String id, String name, int group) {
        assertDoesNotThrow(() -> service.saveStudent(id, name, group));
        var addedStudentOptional = StreamSupport.stream(service.findAllStudents().spliterator(), false).filter(student -> student.getID().equals(id)).findFirst();
        assertDoesNotThrow(addedStudentOptional::get);

        var addedStudent = addedStudentOptional.orElse(null);
        Assert.assertNotNull(addedStudent);

        Assert.assertEquals(addedStudent.getID(), id);
        Assert.assertEquals(addedStudent.getNume(), name);
        Assert.assertEquals(addedStudent.getGrupa(), group);
    }

    @Test
    public void addStudent_OK_lowLimitGroupTest() {
        addStudentAndAssert(VALID_ID, VALID_NAME, 111);
    }

    @Test
    public void addStudent_OK_lowLimitPlusOneGroupTest() {
        addStudentAndAssert(VALID_ID, VALID_NAME, 112);
    }

    @Test
    public void addStudent_OK_highLimitMinusOneGroupTest() {
        addStudentAndAssert(VALID_ID, VALID_NAME, 936);
    }

    @Test
    public void addStudent_OK_highLimitGroupTest() {
        addStudentAndAssert(VALID_ID, VALID_NAME, VALID_GROUP);
    }

    @Test
    public void addStudent_OK_highIdTest() {
        String testId = "99";
        addStudentAndAssert(testId, VALID_NAME, VALID_GROUP);
    }

    @Test
    public void addStudent_OK_oneLetterNameTest() {
        String testName = "A";
        addStudentAndAssert(VALID_ID, testName, VALID_GROUP);
    }

    @Test
    public void addStudent_OK_twoLettersNameTest() {
        String testName = "Ab";
        addStudentAndAssert(VALID_ID, testName, VALID_GROUP);
    }
}

