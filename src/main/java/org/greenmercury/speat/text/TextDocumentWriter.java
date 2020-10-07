package org.greenmercury.speat.text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.greenmercury.speat.Pipeline;
import org.greenmercury.speat.io.output.OutputSource;
import org.greenmercury.speat.io.output.OutputSourceWriter;

public class TextDocumentWriter extends OutputSourceWriter<TextDocumentApi>
    implements TextDocumentApi, Pipeline<TextDocumentApi, OutputSource>
{

  @Override
  public TextDocumentApi getEventApi() {
    return this;
  }

  @Override
  public void process(CharSequence text) throws UnsupportedEncodingException, IOException {
    Writer writer = outputSource.getWriter();
    writer.write(text.toString());
    writer.flush();
  }


}
