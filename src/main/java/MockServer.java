import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class MockServer implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

        String name = "Guest";
        String method = "GET";
        String body = "";
        String output = "Hello there ";
        String message = "default";
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        if (event.getQueryStringParameters() != null && event.getQueryStringParameters().containsKey("name")) {
            name = event.getQueryStringParameters().get("name");
        }
        if (event.getQueryStringParameters() != null && event.getQueryStringParameters().containsKey("message")) {
            message = event.getQueryStringParameters().get("message");
        }
        output += name + " " + message;
        if (event.getHttpMethod().equalsIgnoreCase("POST")) {
            method = event.getHttpMethod();
            body = event.getBody();
            output += " " + method + " " + body;
            response.setBody(output);
            System.out.println(output);
            writeToS3(body);
        } else {
            output += " " + method;
            response.setBody(output);
        }
        return response;
    }

    void writeToS3(String data){

        JSONArray jsonArrayData = new JSONArray(data);
        for (int i = 0; i < jsonArrayData.length(); i++) {
            JSONObject jsonData = jsonArrayData.getJSONObject(0);
            System.out.println("Writing Logs to S3 to folder : OTSData");
            try {
                AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();
                String bucketName = "mockserver-test";

                //create a file to be uploaded in s3
                String fileName = String.valueOf(System.currentTimeMillis());
                File file = File.createTempFile( fileName, ".json");
//                File file = new File(fileName+".json");
                file.deleteOnExit();
                System.out.println("file Name : " + fileName);
                Writer writer = new OutputStreamWriter(new FileOutputStream(file));
                writer.write(data);
                writer.close();

                System.out.println("Uploading a new object to S3 from a file " + file.getName());
                s3.putObject(new PutObjectRequest(bucketName, "Data/"+file.getName(), file));

            }catch (Exception exp){
                System.out.println("Exception in writing logs to s3 " + exp.getMessage());
                exp.printStackTrace();
            }
        }


    }
}