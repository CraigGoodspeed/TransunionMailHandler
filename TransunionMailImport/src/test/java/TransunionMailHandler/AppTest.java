package TransunionMailHandler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class AppTest {

  @Test
  public void successfulResponse() throws Exception {
    App app = new App();
    //app.handleRequest(null,null);
    //app.performUpload("craig@goodspeed.co.za","dvbq879uvep192enthmteluqk91gpd6gpprubmo1");
  }
}
