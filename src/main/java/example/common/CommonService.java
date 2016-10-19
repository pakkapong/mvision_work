package example.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import example.Model.Customer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by admin on 10/18/2016 AD.
 */
public class CommonService {
    public String convertStringToSha1(String input)
    {
        String sha1="";
        MessageDigest mDigest = null;
        try {
            mDigest = MessageDigest.getInstance("SHA1");
            byte[] result = mDigest.digest(input.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < result.length; i++) {
                sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
            }
            sha1 = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return  sha1;
    }

    public String convertCustomerToJson(Customer customer)
    {
        ObjectMapper mapperObj = new ObjectMapper();
        String jsonStr = null;
        try {
            jsonStr = mapperObj.writeValueAsString(customer);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return jsonStr;
    }
}
