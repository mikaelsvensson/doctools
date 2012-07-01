package info.mikaelsvensson.docutil.xml.documentcreator;

import enumeration.Fruit;
import info.mikaelsvensson.docutil.AnnotatedClass;
import info.mikaelsvensson.docutil.ClassA;
import info.mikaelsvensson.docutil.Contact;
import info.mikaelsvensson.docutil.Vehicle;
import info.mikaelsvensson.docutil.xml.extensivedocumentcreator.ExtensiveDocumentCreator;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;

public class ExtensiveDocumentCreatorTest extends AbstractDocumentCreatorTest {
    @Test
    public void testClassA() throws Exception {
        performTest(ClassA.class,
                "-format.property." + ExtensiveDocumentCreator.EXCLUDE_PACKAGE,
                "java");
    }

    @Test
    public void testFruit() throws Exception {
        performTest(Fruit.class);
    }

    @Test
    public void testAnnotated() throws Exception {
        performTest(AnnotatedClass.class,
                "-format.property." + ExtensiveDocumentCreator.SHOW_ANNOTATIONS,
                "true");
    }

    @Test
    public void testContact() throws Exception {
        performTest(
                Contact.class,
                "-format.property." + ExtensiveDocumentCreator.SHOW_ANNOTATIONS,
                "true");
    }

    @Test
    @Ignore
    public void testVehicle() throws Exception {
        performTest(
                Vehicle.class,
                "-format.property." + ExtensiveDocumentCreator.CLASS_MEMBER_TYPE_FILTER,
                "si",
                "-format.property." + ExtensiveDocumentCreator.INTERFACE_MEMBER_TYPE_FILTER,
                "si");
    }

    private void performTest(final Class<?> testClass, String... documentCreatorArgs) throws IOException, URISyntaxException, SAXException, ParserConfigurationException {
        performTest(ExtensiveDocumentCreator.NAME, testClass, documentCreatorArgs);
    }

    protected Node findClassElement(final Class cls, final Document doc) {
        return AbstractDocumentCreatorTest.findClassElementByQName(cls, doc, "class", "qualified-name");
    }
}
