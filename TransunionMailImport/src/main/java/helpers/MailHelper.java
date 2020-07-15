package helpers;

import com.amazonaws.services.s3.model.S3Object;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

public class MailHelper {

    S3Object mail;
    public MailHelper(S3Object input){
        this.mail = input;
    }

    public void getAttachments(Function<InputStream, Void>  fn) throws Exception  {

        MimeMessage msg = new MimeMessage(null, mail.getObjectContent());
        MimeMessageParser mimeParser = new MimeMessageParser(msg);
        mimeParser.parse();
        mimeParser.getAttachmentList().forEach((item) -> {
            String name = item.getName().toUpperCase();
            if(name.endsWith(".ZIP") && item.getContentType().equals("application/zip")){
                try {
                    fn.apply(item.getInputStream());
                }
                catch(IOException ignored){}
            }
        });
    }


}
