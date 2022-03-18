import twitter4j.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GrabTweets {

    // This file is just for testing so far
    // we can delete it / modify if needs be ;)

    HashSet<Long> foundTweets = new HashSet<>();
    ArrayList<User> users = new ArrayList<>();

    // experimental
    // inspired by: https://stackoverflow.com/questions/44611659/rate-limit-exceeded
    // TO TRY AND AVOID EXCEEDING RATE LIMITS
    private void handleRateLimit(RateLimitStatus rateLimitStatus, Configuration configuration) {
        if (rateLimitStatus != null) {
            int remaining = rateLimitStatus.getRemaining();
            int resetTime = rateLimitStatus.getSecondsUntilReset();
            int sleep;
            if (remaining == 0) {
                sleep = resetTime + 1;
            } else {
                sleep = (resetTime / remaining) + 1;
            }

            try {
                Thread.sleep(Math.max(sleep * configuration.getSleepTime(), 0));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // CAUTION: will keep running for a very long time.
    // don't run for too long or the API KEY's might get suspended ;)
    public void grabSomeTweets(TwitterFactory tf, Configuration configuration) throws IOException {

        String[] hashTags = configuration.getHashTags();


        for (int i = 0; i < hashTags.length; i++) {
            try {
                Query query = new Query(hashTags[i]);
                query.setCount(configuration.getBatchSize());
                query.setResultType(Query.ResultType.recent);
                query.setLang(configuration.getLanguage());

                QueryResult result;
                do {
                    result = tf.getInstance().search(query);
                    handleRateLimit(result.getRateLimitStatus(), configuration);
                    List<Status> tweets = result.getTweets();
                    for (Status tweet : tweets) {
                        // create User out of each tweet
                        User user = tweet.getUser();
                        if (!(foundTweets.contains(tweet.getId()))) {
                            TwitterFileService tfs = new TwitterFileService();
                            tfs.writeTweet(tweet);
                            foundTweets.add(tweet.getId());
                        }
                        // write new users to file and add to arraylist
                        if(!(users.contains(user))){
                            TwitterFileService tfs = new TwitterFileService();
                            tfs.writeUser(user);
                            users.add(user);
                        }
                    }

                } while ((query = result.nextQuery()) != null);

            } catch (TwitterException te) {
                te.printStackTrace();
                System.out.println("Failed to search tweets: " + te.getMessage());
                System.exit(-1);
            }
        }
        System.exit(0);
    }

    public static void main(String[] args) {

        Configuration configuration = new Configuration();
        try {
            // user chooses to provide a config file as a command arg
            if (args.length == 1) {
                configuration.getSettingsFromFile(configuration, args[0], 1);
            } else {
                // config file is on class path
                configuration.getSettingsFromFile(configuration, "config_file", 0);
            }

            TwitterFactory tf = configuration.getTwitterFactory(configuration);

            GrabTweets grabTweets = new GrabTweets();

            grabTweets.grabSomeTweets(tf, configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}