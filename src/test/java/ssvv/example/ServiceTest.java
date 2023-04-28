package ssvv.example;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ssvv.example.domain.Nota;
import ssvv.example.domain.Pair;
import ssvv.example.domain.Student;
import ssvv.example.domain.Tema;
import ssvv.example.repository.NotaXMLRepository;
import ssvv.example.repository.StudentXMLRepository;
import ssvv.example.repository.TemaXMLRepository;
import ssvv.example.service.Service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ServiceTest {

    @InjectMocks
    private Service service;

    @Mock
    private StudentXMLRepository studentRepo;

    @Mock
    private TemaXMLRepository temaRepo;

    @Mock
    private NotaXMLRepository notaRepo;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddStudent() {
        Student student = new Student("1", "John Doe", 934);
        when(studentRepo.save(student)).thenReturn(student);

        service.saveStudent("1", "John Doe", 934);

        verify(studentRepo, times(1)).save(student);
    }


    @Test
    public void testAddAssignment() {
        Tema tema = new Tema("1", "Assignment 1", 2, 1);

        when(temaRepo.findOne("1")).thenReturn(null);
        when(temaRepo.save(tema)).thenReturn(tema);

        assertEquals(0, service.saveTema("1", "Assignment 1", 2, 1));
    }

    @Test
    public void testAddGrade() {
        Student student = new Student("1", "John Doe", 934);
        Tema tema = new Tema("1", "Assignment 1", 2, 1);
        Pair<String, String> idNota = new Pair<>("1", "1");
        Nota nota = new Nota(idNota, 9.5, 2, "feedback");

        when(studentRepo.findOne("1")).thenReturn(student);
        when(temaRepo.findOne("1")).thenReturn(tema);
        when(notaRepo.save(nota)).thenReturn(nota);

        assertEquals(1, service.saveNota("1", "1", 9.5, 2, "feedback"));
    }

    @Test
    public void testIntegration() {
        testAddStudent();

        testAddAssignment();

        testAddGrade();
    }
}
