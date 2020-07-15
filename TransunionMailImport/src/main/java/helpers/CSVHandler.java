package helpers;

import Entity.TransunionEntity;
import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CSVHandler {
    int batchSize;
    public CSVHandler(int batchSize){
        this.batchSize= batchSize;
    }

    public void handleCSVInput(byte[] theFile, Function<List<TransunionEntity>, Void> handleItem) throws IOException, CsvValidationException {
        try(InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(theFile));
        CSVReaderHeaderAware csvFile = new CSVReaderHeaderAware(reader);) {
            Map<String, String> dat;
            List<TransunionEntity> toSend = new ArrayList();
            int count = 0;
            while ((dat = csvFile.readMap()) != null) {
                TransunionEntity entity = new TransunionEntity(dat);
                toSend.add(entity);

                if (toSend.size() == batchSize) {
                    handleItem.apply(toSend);
                    toSend.clear();
                    count += batchSize;
                    System.out.println(count);
                }
            }
            if (toSend.size() > 0) {
                handleItem.apply(toSend);
            }
        }
    }
}
