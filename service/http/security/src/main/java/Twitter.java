import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

public class Twitter {

    public static void main(String[] args) throws Exception {

    	System.setProperty("debug", "t");


    	OAuthConsumer consumer = new DefaultOAuthConsumer("gA6JjWFSWeQeFfWscSRydQ","uFkNEDHyVxhU3MKJRxE2KWPgJwn4y0JxGE0rSMpF5dY");

        OAuthProvider provider = new DefaultOAuthProvider(
                "http://twitter.com/oauth/request_token",
                "http://twitter.com/oauth/access_token",
                "http://twitter.com/oauth/authorize");
        
        /****************************************************
         * The following steps should only be performed ONCE
         ***************************************************/
        

        // we do not support callbacks, thus pass OOB
        String authUrl = provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND);
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
        URL url = new URL("http://twitter.com/account/verify_credentials.xml");
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