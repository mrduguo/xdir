import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

public class Google {

    public static void main(String[] args) throws Exception {

    	System.setProperty("debug", "t");

    	OAuthConsumer consumer = new DefaultOAuthConsumer("duguo.com","Aheu7iiguk37aVKybqu16gAd");


        String scope="http://www.google.com/m8/feeds/";
        URI test=new URI("https://www.google.com/accounts/OAuthGetRequestToken?scope=http%3A%2F%2Fwww.google.com%2Fm8%2Ffeeds%2F&oauth_callback=");
        System.out.println(test.getRawQuery());
        System.out.println("GET&https%3A%2F%2Fwww.google.com%2Faccounts%2FOAuthGetRequestToken&oauth_callback%3Dhttps%253A%252F%252Flocalhost%253A8443%252Fxdir%252Faccount%252Flogin%252Foauth%252Ftest%252Fcallback.html%26oauth_consumer_key%3Dduguo.com%26oauth_nonce%3D-8454888014581283821%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1269210590%26oauth_version%3D1.0%26scope%3Dhttp%253A%252F%252Fwww.google.com%252Fm8%252Ffeeds%252F".equals(
        		"GET&https%3A%2F%2Fwww.google.com%2Faccounts%2FOAuthGetRequestToken&oauth_callback%3Dhttps%253A%252F%252Flocalhost%253A8443%252Fxdir%252Faccount%252Flogin%252Foauth%252Ftest%252Fcallback.html%26oauth_consumer_key%3Dduguo.com%26oauth_nonce%3D-8454888014581283821%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1269210590%26oauth_version%3D1.0%26scope%3Dhttp%253A%252F%252Fwww.google.com%252Fm8%252Ffeeds%252F"));
        OAuthProvider provider = new DefaultOAuthProvider(
        		"https://www.google.com/accounts/OAuthGetRequestToken?scope="+scope,
                "https://www.google.com/accounts/OAuthGetAccessToken",
                "https://www.google.com/accounts/OAuthAuthorizeToken?hd=default");
        
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