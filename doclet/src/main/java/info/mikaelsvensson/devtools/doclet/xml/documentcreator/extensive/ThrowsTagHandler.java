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

package info.mikaelsvensson.devtools.doclet.xml.documentcreator.extensive;

import com.sun.javadoc.ThrowsTag;
import info.mikaelsvensson.devtools.doclet.shared.ElementWrapper;

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