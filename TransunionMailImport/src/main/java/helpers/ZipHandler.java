package helpers;

import java.io.*;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

interface ZipMethodHandler {
    Void UnzipFile(InputStream stream, Function<byte[], Void> fn);
}

public class ZipHandler implements ZipMethodHandler {

    @Override
    public Void UnzipFile(InputStream streamReader, Function<byte[], Void> csvHandler) {
        try {
            ZipInputStream zipFile = new ZipInputStream(streamReader);
            ZipEntry csvFile = zipFile.getNextEntry();
            byte[] buffer = new byte[1024];
            while (csvFile  != null) {
                if (!csvFile.isDirectory() && csvFile.getName().toUpperCase().endsWith(".CSV")) {
                    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                    int length;
                    while((length = zipFile.read(buffer)) > 0){
                        byteOutputStream.write(buffer, 0, length);
                    }
                    csvHandler.apply(byteOutputStream.toByteArray());
                    csvFile = zipFile.getNextEntry();
                } else if (!csvFile.isDirectory()) {
                    zipFile.skip(csvFile.getSize());
                }
            }
            zipFile.close();
        }
        catch(IOException ignored){

        }
        return null;
    }
}