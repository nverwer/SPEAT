package org.greenmercury.speat.sax;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.ext.LexicalHandler;

/**
 * Implements all interfaces needed to process SAX events.
 *<p>
 * @author Rakensi
 */
public interface Sax
    extends ContentHandler, DTDHandler, EntityResolver, ErrorHandler, DeclHandler, EntityResolver2, LexicalHandler {
}
