package info.mikaelsvensson.docutil.xml.extensivedocumentcreator;

import com.sun.javadoc.ThrowsTag;
import info.mikaelsvensson.docutil.shared.ElementWrapper;

class ThrowsTagHandler extends TagHandler<ThrowsTag> {
// --------------------------- CONSTRUCTORS ---------------------------

    ThrowsTagHandler(final Dispatcher dispatcher) {
        super(ThrowsTag.class, dispatcher);
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    void handleImpl(final ElementWrapper el, final ThrowsTag doc) throws JavadocItemHandlerException {
        super.handleImpl(el, doc);

        el.removeAttributes("name", "text");

        el.setAttributes("exception-comment", doc.exceptionComment());

        handleDocImpl(el, "exception-type", doc.exceptionType());
    }
}
