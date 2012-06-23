package info.mikaelsvensson.docutil.xml.extensivedocumentcreator;

import com.sun.javadoc.SeeTag;
import info.mikaelsvensson.docutil.shared.ElementWrapper;

class SeeTagHandler extends TagHandler<SeeTag> {
// --------------------------- CONSTRUCTORS ---------------------------

    SeeTagHandler(final Dispatcher dispatcher) {
        super(SeeTag.class, dispatcher);
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    void handleImpl(final ElementWrapper el, final SeeTag doc) throws JavadocItemHandlerException{
        super.handleImpl(el, doc);

        el.setAttributes(
                "label", doc.label(),
                "referenced-class", doc.referencedClassName(),
                "referenced-member", doc.referencedMemberName(),
                "referenced-package", doc.referencedPackage().name());
    }
}
