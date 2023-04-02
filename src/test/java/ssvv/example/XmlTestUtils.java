package ssvv.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class XmlTestUtils {

    public static final String STUDENT_TEST_XML_FILE = "student_test.tmp";
    public static final String HOMEWORK_TEST_XML_FILE = "homework_test.tmp";
    public static final String GRADES_TEST_XML_FILE = "grade_test.tmp";

    // Prevent instantiation of the utility class
    private XmlTestUtils() {
    }

    public static File createXMLFile(String fileName) throws IOException {
        File createdFile = new File(fileName);
        if (createdFile.createNewFile()) {
            try (FileWriter fileWriter = new FileWriter(createdFile)) {
                fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<Entitati>\n</Entitati>");
            }
        } else {
            throw new RuntimeException("File " + fileName + " could not be created!");
        }
        return createdFile;
    }
}
