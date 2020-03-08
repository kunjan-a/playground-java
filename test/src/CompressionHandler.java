
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;
import java.util.zip.ZipException;

public class CompressionHandler
{
    private final MyDeflaterOutputStream _deflaterOutputStream;
    private final MyInflaterOutputStream _inflaterOutputStream;
    private final ByteArrayOutputStream _compressionResultOutputStream;
    private final ByteArrayOutputStream _decompressionResultOutputStream;

    public CompressionHandler()
    {
        _compressionResultOutputStream = new ByteArrayOutputStream();
        _decompressionResultOutputStream = new ByteArrayOutputStream();
        _deflaterOutputStream = new MyDeflaterOutputStream(_compressionResultOutputStream);
        _inflaterOutputStream = new MyInflaterOutputStream(_decompressionResultOutputStream);
    }

    public byte[] compress(byte[] source) throws IOException
    {
        if (source == null || source.length == 0) {
            return source;
        }

        _deflaterOutputStream.compressData(source);
        _deflaterOutputStream.flush();
        byte[] compressedBytes = _compressionResultOutputStream.toByteArray();
        System.out.println("compressed to:"+compressedBytes.length+" from source of:"+source.length);
        _compressionResultOutputStream.reset();
        return compressedBytes;
    }

    public byte[] decompress(byte[] source) throws IOException
    {
        System.out.println("decompressing from:"+source.length);
        _inflaterOutputStream.write(source,0,source.length);
        _inflaterOutputStream.flush();
        byte[] bytes = _decompressionResultOutputStream.toByteArray();
        _decompressionResultOutputStream.reset();
        return bytes;
    }

    private class MyDeflaterOutputStream extends DeflaterOutputStream{

        public MyDeflaterOutputStream(OutputStream os)
        {
            super(os);
        }
        public void compressData(byte[] source) throws IOException
        {
            write(source);
            def.finish();
            while (!def.finished()) {
                int byteCount = def.deflate(buf);
                out.write(buf, 0, byteCount);
            }
            def.reset();
        }
    }

    private class MyInflaterOutputStream extends InflaterOutputStream{

        public MyInflaterOutputStream(OutputStream out)
        {
            super(out);
        }


        public void writeTested(byte[] bytes, int offset, int byteCount) throws IOException, ZipException
        {
            inf.reset();
            inf.setInput(bytes, offset, byteCount);
            try {
                int inflated;
                while ((inflated = inf.inflate(buf)) > 0) {
                    out.write(buf, 0, inflated);
                }
            } catch (DataFormatException e) {
                throw new ZipException();
            }
        }

        @Override
        public void write(byte[] bytes, int offset, int byteCount) throws IOException, ZipException
        {
            byte[] buff=new byte[102400];
            inf.setInput(bytes, offset, byteCount);
            int inflatedLength;
            try {
                inflatedLength = inf.inflate(buff);
                out.write(buff, 0, inflatedLength);
            } catch (DataFormatException e) {
                throw new ZipException();
            }
            System.out.println("Inflated " + byteCount + " to " + inflatedLength);
        }
    }
}
