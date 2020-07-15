package helpers;


import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;

public class S3FileGetter {

    private String bucketName;
    private String key;

    public S3FileGetter(String bucket, String fileName){
        this.bucketName = bucket;
        this.key = fileName;
    }

    public S3Object getObject() {
        return AmazonS3ClientBuilder.defaultClient().getObject(
                this.bucketName,
                this.key
        );
    }

}
