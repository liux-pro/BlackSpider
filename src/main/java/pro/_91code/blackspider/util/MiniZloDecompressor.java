package pro._91code.blackspider.util;

import org.anarres.lzo.LzoAlgorithm;
import org.anarres.lzo.LzoDecompressor;
import org.anarres.lzo.LzoLibrary;
import org.anarres.lzo.lzo_uintp;


/**
 * @author LEGEND
 */
public class MiniZloDecompressor {
    private final LzoAlgorithm algorithm = LzoAlgorithm.LZO1X;
    private final LzoDecompressor decompressor = LzoLibrary.getInstance().newDecompressor(algorithm, null);
    private final byte[] buffer;
    lzo_uintp outSize = new lzo_uintp();


    public MiniZloDecompressor(int maxByteSize) {
        buffer=new byte[maxByteSize*2];
    }
    public MiniZloDecompressor(int screenWidth, int screenHeight) {
        buffer=new byte[screenWidth*screenHeight*3*2];
    }

    public byte[] decompress(byte[] source,int inSize){

        decompressor.decompress(source,0,inSize,buffer,0, outSize);
        byte[] bytes = new byte[outSize.value];
        System.arraycopy(buffer,0,bytes,0,bytes.length);
        return bytes;

    }

}
