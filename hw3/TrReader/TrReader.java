import java.io.Reader;
import java.io.IOException;

/** Translating Reader: a stream that is a translation of an
 *  existing reader.
 *  @author Scott Shao
 */
public class TrReader extends Reader {
    /** A new TrReader that produces the stream of characters produced
     *  by STR, converting all characters that occur in FROM to the
     *  corresponding characters in TO.  That is, change occurrences of
     *  FROM.charAt(0) to TO.charAt(0), etc., leaving other characters
     *  unchanged.  FROM and TO must have the same length. */
    public TrReader(Reader str, String from, String to) {
        _reader = str;
        _from = from;
        _to = to;
    }

    /** Close the source file.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        _reader.close();
    }

    /** This method translate the INSTREAM to desired char
     *  if index is not -1 and return it.
     * @param instream instream from the source.
     * @return return translated char or instream if index is -1.
     */
    private char translate(char instream) {
        int index = _from.indexOf(instream);
        if (index == -1) {
            return instream;
        } else {
            return _to.charAt(index);
        }
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int output = _reader.read(cbuf, off, len);
        for (int i = off; i < off + output; i++) {
            cbuf[i] = translate(cbuf[i]);
        }
        return output;
    }

    /**
     * The source.
     */
    private Reader _reader;

    /**
     * The string that should be translated.
     */
    private String _from;

    /**
     * The string that source should be translated to.
     */
    private String _to;
}


