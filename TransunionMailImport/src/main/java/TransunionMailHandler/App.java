package TransunionMailHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvValidationException;
import helpers.CSVHandler;
import helpers.DB.TransunionDataSave;
import helpers.MailHelper;
import helpers.S3FileGetter;
import helpers.ZipHandler;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<SNSEvent, Object> {
    final String[] authorisedFrom = new String[]{"craig@goodspeed.co.za","craig.goodspeed@partner.bmw.co.za"};
    public Object handleRequest(final SNSEvent input, final Context context) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode messageData = mapper.readTree(input.getRecords().get(0).getSNS().getMessage());
            System.out.println(messageData.get("mail").get("source").asText());
            performUpload(messageData.get("mail").get("source").asText(), messageData.get("mail").get("messageId").asText());
        }
        catch(Exception err){
            System.out.println(err
            .getMessage());
            return err.getMessage();
        }

        return "completed";
    }

    public void performUpload(String fromMail, String filename) throws Exception{
        if (verifySource(fromMail)) {
            S3FileGetter getter = new S3FileGetter
                    (
                            getEnvironment("S3MailContainer", "transunion-mail"),
                            filename
                            //"3ql2qvt9kp8b9statjcuvq0hilq3n1p1auije281"
                    );
            System.out.println("got the file");
            MailHelper helper = new MailHelper(getter.getObject());
            ZipHandler zip = new ZipHandler();
            CSVHandler csv = new CSVHandler(Integer.parseInt(System.getenv("BatchSize")));
            System.out.println("starting the process");
            try {
                helper.getAttachments(
                        (is) -> zip.UnzipFile
                                (
                                        is,
                                        (bt) ->
                                        {
                                            System.out.println("unzipped now parsing csv");
                                            try {
                                                TransunionDataSave.truncateTransunionWrite();
                                                csv.handleCSVInput(bt, (item) -> {
                                                    try {
                                                        TransunionDataSave.saveData(item);
                                                    }
                                                    catch (Exception e) {
                                                        System.out.println(e.getMessage());
                                                    }
                                                    return null;
                                                });
                                                TransunionDataSave.updateReadTable();

                                            } catch (Exception e) {
                                                System.out.println(e.getMessage());
                                            }
                                            return null;
                                        }
                                ));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw e;
            }
        } else {
            System.out.println("not from authorized source");
            throw new Exception("This email address is not authorized");
        }
    }

    private Boolean verifySource(String source){
        return Arrays.stream(this.authorisedFrom).anyMatch(item -> item.toLowerCase().equals(source.toLowerCase()));
    }

    private String getEnvironment(String key, String defaultVal){
        String toReturn = System.getenv(key);
        return toReturn == null ? defaultVal : toReturn;
    }

    private String getPageContents(String address) throws IOException{
        URL url = new URL(address);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
