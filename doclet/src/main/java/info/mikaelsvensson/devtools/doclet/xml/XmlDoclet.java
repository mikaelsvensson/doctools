/*
 * Copyright 2014 Mikael Svensson
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package info.mikaelsvensson.devtools.doclet.xml;

import com.sun.javadoc.RootDoc;
import info.mikaelsvensson.devtools.doclet.AbstractDoclet;
import info.mikaelsvensson.devtools.doclet.shared.DocumentCreator;
import info.mikaelsvensson.devtools.doclet.shared.DocumentCreatorException;
import info.mikaelsvensson.devtools.doclet.shared.DocumentCreatorFactory;
import info.mikaelsvensson.devtools.doclet.shared.FileUtil;
import info.mikaelsvensson.devtools.doclet.shared.propertyset.PropertySet;
import info.mikaelsvensson.devtools.doclet.shared.propertyset.PropertySetException;
import org.w3c.dom.Document;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The XmlDoclet outputs XML documents instead of HTML documents. The XML documents can then be further processed, using
 * other tools, in order to produce reports or as input to other applications.
 * <p/>
 * Creates XML documents based on the Java source code and, if specified by the user,
 * processes the XML document using an XSLT style sheet.
 * <p/>
 * The source code can be processed using a couple of different document creators, each one
 * designed to produce XML documents for different purposes (be they generic or specific
 * purposes).
 * <p/>
 * The primary objective of this doclet is to aid developers in creating more useful web
 * sites for their source code when using the {@code mvn site} command.
 * <p/>
 * The doclet has a couple of configuration attributes which makes it highly versatile.
 *
 * @doclet
 * @doclet-tagline XML Documents From Source Code
 */
public class XmlDoclet extends AbstractDoclet {

    /**
     * Specifies which formatter to use to produce the XML document. A formatter is sometimes also referred to
     * as a document creator. Each formatter produces XML content is a very specific way and choosing the most
     * appropriate depends highly on your documentation needs.
     * <p/>
     * In order to further customize the output, some formatters have their own configuration options.
     */
    @FormatProperty
    public static final String FORMAT = XmlDocletAction.FORMAT;

    /**
     * Specifies the name of the resulting file. Usually this is an XML document.
     */
    @FormatProperty
    public static final String OUTPUT = XmlDocletAction.OUTPUT;

    /**
     * Forces the Doclet to save the untransformed XML document as a separate file.
     */
    @FormatProperty
    public static final String UNTRANSFORMED_OUTPUT = XmlDocletAction.UNTRANSFORMED_OUTPUT;

    /**
     * Specifies an (optional) post processor to invoke after the document creator (i.e. the formatter) has
     * been invoked. This is, for example, useful when the formatter produces multiple files and these files
     * need to be "handled" in some way.
     */
    @FormatProperty
    public static final String POSTPROCESSOR = XmlDocletAction.POSTPROCESSOR;

    /**
     * Specifies an XSL transformer document to apply to the XML document generated by the formatter.
     */
    @FormatProperty
    public static final String TRANSFORMER = XmlDocletAction.TRANSFORMER;

    protected XmlDoclet(RootDoc root) {
        super(root);
    }

    public static boolean start(RootDoc root) throws PropertySetException {
        return new XmlDoclet(root).generate();
    }

    private boolean generate() {
        try {
            XmlDocletAction action = new XmlDocletAction(new PropertySet(root.options()));

            root.printNotice("Building XML document.");

            DocumentCreator documentCreator = DocumentCreatorFactory.getDocumentCreator(action.format);
            Document document = documentCreator.generateDocument(root, action.getParameters());
            root.printNotice("Finished building XML document.");

            generate(document,
                    action.getOutput(),
                    action.getUntransformedOutput(),
                    action.getTransformer(),
                    action.getParameters().getProperties());

            postProcess(action);
        } catch (IOException e) {
            printError(new DocumentCreatorException("Could not post-process.", e));
        } catch (DocumentCreatorException e) {
            printError(e);
        } catch (PropertySetException e) {
            printError(e);
        } catch (Throwable e) {
            root.printError(e.getMessage());
            e.printStackTrace();
        }
        return true;
    }

    //TODO: MISV 20120618 Refactor postProcess into the same kind of "factory mechanism" used for DocumentCreator. Perhaps call it PostProcessor?
    private void postProcess(XmlDocletAction action) throws IOException {
        if (action.getPostProcessor() != null && action.getPostProcessor().length() > 0) {
            String folderExpr = action.getPostProcessingParameters().get("folder");
            String templateFileExpr = action.getPostProcessingParameters().get("templateFile");
            String filePatternExpr = action.getPostProcessingParameters().get("filePattern");
            String replaceStringExpr = action.getPostProcessingParameters().get("replaceString");

            File folder = new File(folderExpr);
            Pattern filePattern = Pattern.compile(filePatternExpr);
            File templateFile = new File(templateFileExpr);

            String template = FileUtil.getFileContent(templateFile);

            List<File> files = FileUtil.findFiles(folder, filePattern);
            for (File file : files) {
                System.out.println("Look for " + replaceStringExpr + " in " + templateFile.getName() + " and apply it to " + file.getName());

                String source = FileUtil.getFileContent(file);
                String result = template.replace(replaceStringExpr, source);
                FileWriter writer = new FileWriter(file);
                writer.write(result);
                writer.close();

                System.out.println(file.getAbsolutePath());
            }
        }
    }

    private void generate(Document doc, File outputFile, File untransformedOutput, File xsltFile, Map<String, String> parameters) {
        try {
            if (xsltFile != null) {
                if (untransformedOutput != null) {
                    root.printNotice("Saving untransformed XML document");
                    writeFile(doc, untransformedOutput);
                    root.printNotice("XML document saved as " + untransformedOutput.getAbsolutePath());
                }
                root.printNotice("Transforming XML document using XSLT");
                writeFile(doc, outputFile, xsltFile, parameters);
                root.printNotice("Transformed XML document saved as " + outputFile.getAbsolutePath());
            } else {
                root.printNotice("Saving XML document");
                writeFile(doc, outputFile);
                root.printNotice("XML document saved as " + outputFile.getAbsolutePath());
            }
        } catch (TransformerException e) {
            root.printError(e.getMessage());
        } catch (FileNotFoundException e) {
            root.printError(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            root.printError(e.getMessage());
        }
    }

    private static void writeFile(Document doc, File file, File xsltFile, Map<String, String> parameters) throws TransformerException {
        Source xsltSource = new StreamSource(xsltFile);
        writeFile(doc, file, xsltSource, parameters);
    }

    private static void writeFile(Document doc, File file, Source xsltSource, Map<String, String> parameters) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(xsltSource);
        StreamResult outputTarget = new StreamResult(file);

        Source xmlSource = new DOMSource(doc);
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            transformer.setParameter(entry.getKey(), entry.getValue());
        }

        transformer.setParameter("outputFile", file.getName());
        transformer.transform(xmlSource, outputTarget);
    }

    private static void writeFile(Document doc, File file) throws TransformerException, FileNotFoundException, UnsupportedEncodingException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
//            transformerFactory.setAttribute("indent-number", 4);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        try {
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.transform(new DOMSource(doc.getDocumentElement()), new StreamResult(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")));
        System.out.println(file.getAbsolutePath());
    }

    public static int optionLength(String option) {
        return XmlDocletAction.optionLength(option);
    }

}
