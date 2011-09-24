import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

import org.duguo.xdir.util.http.HttpUtil;

public class LinkedIn {

    public static void main(String[] args) throws Exception {
    	System.out.println(HttpUtil.decodeParam("GET%26https%253A%252F%252Fapi.linkedin.com%252Fuas%252Foauth%252FrequestToken%26oauth_callback%253Dhttps%25253A%25252F%25252Flocalhost%25253A8443%25252Fxdir%25252Faccount%25252Flogin%25252Foauth%25252Flinkedin%25252Fcallback.html%2526oauth_consumer_key%253D6CuRZomIfI08ArO_6xg67sK2aVyhvlTitGrCeFeiML0J8coOeiKXmfzLJqc88Lfd%2526oauth_nonce%253D7937709621658824700%2526oauth_signature_method%253DHMAC-SHA1%2526oauth_timestamp%253D1269873035%2526oauth_version%253D1.0"));

    	System.setProperty("debug", "t");


    	OAuthConsumer consumer = new DefaultOAuthConsumer("6CuRZomIfI08ArO_6xg67sK2aVyhvlTitGrCeFeiML0J8coOeiKXmfzLJqc88Lfd","-0CRzm1w20QjZfwNm5kWW72K_BFhp5L0CGEH1fLlseGIiY_27lJXqFU0R9xlJggK");

    	

    	
        OAuthProvider provider = new DefaultOAuthProvider(
        		"https://api.linkedin.com/uas/oauth/requestToken",
                "https://api.linkedin.com/uas/oauth/accessToken",
                "https://api.linkedin.com/uas/oauth/authorize");
        
        /****************************************************
         * The following steps should only be performed ONCE
         ***************************************************/
        

        // we do not support callbacks, thus pass OOB
        String authUrl = provider.retrieveRequestToken(consumer, "https://localhost:8443/xdir/account/login/oauth/linkedin/callback.html");
        //String authUrl = provider.retrieveRequestToken(consumer, "http://duguo.com");
        System.out.println(authUrl);

        // bring the user to authUrl, e.g. open a web browser and note the PIN code
        // ...  

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String pinCode = br.readLine();
        //String pinCode = "7717655";// ... you have to ask this from the user, or obtain it
        // from the callback if you didn't do an out of band request

        // user must have granted authorization at this point
        
        provider.retrieveAccessToken(consumer, pinCode);
        
        
        System.out.println("consumer.getToken():"+consumer.getToken());
        System.out.println("consumer.getTokenSecret():"+consumer.getTokenSecret());
        
        
        

        String ACCESS_TOKEN=consumer.getToken();
        String TOKEN_SECRET=consumer.getTokenSecret();

        // store consumer.getToken() and consumer.getTokenSecret(),
        // for the current user, e.g. in a relational database
        // or a flat file
        // ...

        /****************************************************
         * The following steps are performed everytime you
         * send a request accessing a resource on Twitter
         ***************************************************/

        // if not yet done, load the token and token secret for
        // the current user and set them
        //consumer.setTokenWithSecret(ACCESS_TOKEN, TOKEN_SECRET);

        // create a request that requires authentication
        URL url = new URL("http://api.linkedin.com/v1/people/~:public");
        HttpURLConnection request = (HttpURLConnection) url.openConnection();

        // sign the request
        consumer.sign(request);

        // send the request
        request.connect();

        // response status should be 200 OK
        int statusCode = request.getResponseCode();
        System.out.println("status:"+statusCode);
    }

	private static String generateTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000L);
	}
}