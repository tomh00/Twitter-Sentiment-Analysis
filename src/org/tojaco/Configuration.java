package org.tojaco;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

// This Configuration class is for loading a configuration file with the necessary named parameters for the task
//(directory names, file names, Twitter keys, numeric settings, etc.).

public class Configuration {

    private String API_KEY;
    private String API_SECRET_KEY;
    private String BEARER_TOKEN;
    private String ACCESS_TOKEN;
    private String ACCESS_TOKEN_SECRET;
    private String DATA_DIRECTORY;
    private String DATA_FILE;
    private String USER_FILE;
    private String GRAPH_DIRECTORY;
    private String RTGRAPH_OUTPUT_FILE;
    private String RTWEETEDGRAPH_OUTPUT_FILE;
    private String USERS_TO_HASHTAGS;
    private String HASHTAGS_TO_USERS;

    private String LEXICON_FOLDER;

    private String LEXICON_DATA_FILE;
    private String LEXICON_FILE;
    private String HASHTAG_TO_WORDS;
    private String USER_QUALITIES;
    private String STANCE_FILE;
    private String HASHTAG_SUMMARY_FILE;
    private String LANGUAGE;
    private String BATCH_SIZE;
    private String SLEEP_TIME;
    private String[] HASHTAGS;
    private String GEPHI_FILE_1;
    private String GEPHI_HASHTAG_TO_USER_FILE;

    public String getGEPHI_FILE_1() { return GEPHI_FILE_1; }

    public String getGEPHI_HASHTAG_TO_USER_FILE() { return GEPHI_HASHTAG_TO_USER_FILE; }

    public String getAPIKey() {
        return API_KEY;
    }

    public String getAPISecretKey() {
        return API_SECRET_KEY;
    }

    public String getBearerToken() {
        return BEARER_TOKEN;
    }

    public String getDataDirectory() {
        return DATA_DIRECTORY;
    }

    public String getDataFile() {
        return DATA_FILE;
    }

    public String getUserFile() {
        return USER_FILE;
    }

    public String getGRAPH_DIRECTORY() {
        return GRAPH_DIRECTORY;
    }

    public String getRTGRAPH_OUTPUT_FILE() {
        return RTGRAPH_OUTPUT_FILE;
    }

    public String getRTWEETEDGRAPH_OUTPUT_FILE() {
        return RTWEETEDGRAPH_OUTPUT_FILE;
    }

    public String getUSERS_TO_HASHTAGS() {
        return USERS_TO_HASHTAGS;
    }

    public String getHASHTAGS_TO_USERS() { return HASHTAGS_TO_USERS; }

    public String getLEXICON_FOLDER() {
        return LEXICON_FOLDER;
    }

    public String getLEXICON_DATA_FILE() {
        return LEXICON_DATA_FILE;
    }

    public String getLEXICON_FILE() { return LEXICON_FILE; }

    public String getHASHTAG_TO_WORDS() { return HASHTAG_TO_WORDS; }

    public String getUSER_QUALITIES() { return USER_QUALITIES; }

    public String getSTANCE_FILE(){ return STANCE_FILE; }

    public String getHASHTAG_SUMMARY_FILE() { return HASHTAG_SUMMARY_FILE; }

    public String getACCESS_TOKEN() {
        return ACCESS_TOKEN;
    }

    public String getACCESS_TOKEN_SECRET() {
        return ACCESS_TOKEN_SECRET;
    }

    public String getLanguage() {
        return LANGUAGE;
    }

    public int getSleepTime() {
        return Integer.parseInt(SLEEP_TIME);
    }

    public int getBatchSize() {
        return Integer.parseInt(BATCH_SIZE);
    }

    public String[] getHashTags() {
        return HASHTAGS;
    }

    public String[] splitHashTags(String hashTags) {

        return hashTags.split(" ");
    }

    public void getSettingsFromFile(Configuration configuration, String file, int mode) throws IOException {

        Properties properties = new Properties();
        FileInputStream customInputFile = null;
        InputStream classPathInputFile = null;

        if (mode == 1) {
            customInputFile = new FileInputStream(file);
        } else {
            classPathInputFile = configuration.getClass().getClassLoader().getResourceAsStream(file);
        }
        try {
            if (customInputFile != null) {
                properties.load(customInputFile);
            } else if (classPathInputFile != null) {
                properties.load(classPathInputFile);
            } else {
                throw new RuntimeException("NO CONFIG FILE IN THE CLASS PATH OR GIVEN AS A COMMAND LINE ARGUMENT");
            }

            configuration.API_KEY = properties.getProperty("API_KEY");
            configuration.API_SECRET_KEY = properties.getProperty("APIKEY_SECRET");
            configuration.BEARER_TOKEN = properties.getProperty("BEARER_TOKEN");
            configuration.ACCESS_TOKEN = properties.getProperty("ACCESS_TOKEN");
            configuration.ACCESS_TOKEN_SECRET = properties.getProperty("ACCESS_TOKEN_SECRET");
            configuration.DATA_DIRECTORY = properties.getProperty("DATA_DIRECTORY");
            configuration.DATA_FILE = properties.getProperty("DATA_FILE");
            configuration.USER_FILE = properties.getProperty("USER_FILE");
            configuration.GRAPH_DIRECTORY = properties.getProperty("GRAPH_OUTPUT_DIRECTORY");
            configuration.RTGRAPH_OUTPUT_FILE = properties.getProperty("RTGRAPH_OUTPUT_FILE");
            configuration.RTWEETEDGRAPH_OUTPUT_FILE = properties.getProperty("RETWEETGPHOUTPUTFILE");
            configuration.USERS_TO_HASHTAGS = properties.getProperty("USERS_TO_HASHTAGS");
            configuration.HASHTAGS_TO_USERS = properties.getProperty("HASHTAGS_TO_USERS");
            configuration.LEXICON_FOLDER = properties.getProperty("LEXICON_FOLDER");
            configuration.LEXICON_FILE = properties.getProperty("LEXICON_FILE");
            configuration.LEXICON_DATA_FILE = properties.getProperty("LEXICON_DATA_FILE");
            configuration.HASHTAG_SUMMARY_FILE = properties.getProperty("HASHTAG_SUMMARY_FILE");
            configuration.HASHTAG_TO_WORDS = properties.getProperty("HASHTAG_TO_WORDS");
            configuration.USER_QUALITIES = properties.getProperty("USER_QUALITIES");
            configuration.STANCE_FILE = properties.getProperty("STANCE_FILE");
            configuration.SLEEP_TIME = properties.getProperty("SLEEPTIMEMS");
            configuration.LANGUAGE = properties.getProperty("LANGUAGE");
            configuration.BATCH_SIZE = properties.getProperty("BATCH_SIZE");
            configuration.HASHTAGS = splitHashTags(properties.getProperty("HASHTAGS"));
            configuration.GEPHI_FILE_1 = properties.getProperty("GEPHI_FILE_1");
            configuration.GEPHI_HASHTAG_TO_USER_FILE=properties.getProperty("GEPHI_HASHTAG_TO_USER_FILE");
        } catch (IOException e) {
            throw new RuntimeException("Could not read properties from file:", e);
        }
    }

    public TwitterFactory getTwitterFactory(Configuration configuration){

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(configuration.getAPIKey())
                .setOAuthConsumerSecret(configuration.getAPISecretKey())
                .setOAuthAccessToken(configuration.getACCESS_TOKEN())
                .setOAuthAccessTokenSecret(configuration.getACCESS_TOKEN_SECRET())
                .setTweetModeExtended(true);

        return new TwitterFactory(cb.build());
    }

    public TwitterStream getTwitterStream(Configuration configuration){

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(configuration.getAPIKey())
                .setOAuthConsumerSecret(configuration.getAPISecretKey())
                .setOAuthAccessToken(configuration.getACCESS_TOKEN())
                .setOAuthAccessTokenSecret(configuration.getACCESS_TOKEN_SECRET())
                .setTweetModeExtended(true)
                .setJSONStoreEnabled(true);
                return new TwitterStreamFactory(cb.build()).getInstance();
    }
}