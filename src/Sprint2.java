import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import java.io.IOException;
//Run the entire program here
public class Sprint2 {
    public static void main(String[] args){
        Configuration configuration = new Configuration();
        try {
            // user chooses to provide a config file as a command arg
            if (args.length == 1) {
                configuration.getSettingsFromFile(configuration, args[0], 1);
            } else {
                // config file is on class path
                configuration.getSettingsFromFile(configuration, "config_file", 0);
            }

            StreamTweets st = new StreamTweets();

            TwitterStream tf = configuration.getTwitterStreamFactory(configuration);

            tf.addListener(st.listener);

            tf.sample();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
