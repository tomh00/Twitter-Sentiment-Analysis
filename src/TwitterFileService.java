import twitter4j.Status;
import twitter4j.User;

import java.io.*;

public class TwitterFileService {

    public void writeTweet(Status tweet, boolean retweet) throws IOException {
        File file = new File("s.txt");  // this is a file handle, s.txt may or may not exist
        boolean foundInFile = false;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {  // classic way of reading a file line-by-line
                String lineContents[] = line.split("\t");
                String id = String.valueOf(tweet.getId());
                if (lineContents[0].equals(id)){
                    foundInFile = true;
                }
                break;// if the tweet ids are the same, we do not have to read the rest after all
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        if (!foundInFile) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
                String tweetText;
                if(retweet)
                     tweetText = tweet.getRetweetedStatus().getText();
                else
                    tweetText = tweet.getText();

                pw.println(tweet.getId() + "\t"
                            + "@" + tweet.getUser().getScreenName() + "\t"
                            + tweetText.replaceAll("\n", " ") + "\t"
                            + tweet.getRetweetCount() + "\t"
                            + tweet.getCreatedAt());
            }
        }
    }

    public void writeUser(User user) throws IOException {
        File file = new File("users.txt");

        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
                pw.println("@" + user.getScreenName() + "\t"
                        + user.getLocation() + "\t"
                        + user.getDescription().replaceAll("\n", " ") + "\t"
                        + user.getFollowersCount());
            }

        }
    }
