package info.mikaelsvensson.docutil.xml.extensivedocumentcreator;

import com.sun.javadoc.PackageDoc;
import info.mikaelsvensson.docutil.shared.ElementWrapper;

class PackageDocHandler extends DocHandler<PackageDoc> {
// --------------------------- CONSTRUCTORS ---------------------------

    PackageDocHandler(final Dispatcher dispatcher) {
        super(PackageDoc.class, dispatcher);
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    void handleImpl(final ElementWrapper el, final PackageDoc doc) throws JavadocItemHandlerException {
        super.handleImpl(el, doc);

        handleDocImpl(el, doc.allClasses(), "classes", "class", true);
    }
}
