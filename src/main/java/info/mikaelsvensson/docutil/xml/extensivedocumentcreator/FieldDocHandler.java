package info.mikaelsvensson.docutil.xml.extensivedocumentcreator;

import com.sun.javadoc.FieldDoc;
import info.mikaelsvensson.docutil.shared.ElementWrapper;

class FieldDocHandler extends MemberDocHandler<FieldDoc> {

    FieldDocHandler() {
        super(FieldDoc.class);
    }

    @Override
    void handleImpl(final ElementWrapper el, final FieldDoc doc) {
        super.handleImpl(el, doc);

        el.setAttributes(
                "transient", Boolean.toString(doc.isTransient()),
                "volatile", Boolean.toString(doc.isVolatile())
        );

        if (doc.constantValue() != null) {
            el.setAttribute("constant-value", doc.constantValue().toString());
        }

        Handler.process(el, "type", doc.type());

        handleDocImpl(el, doc.serialFieldTags(), "serial-field-tags", "serial-field-tag");
    }
}
