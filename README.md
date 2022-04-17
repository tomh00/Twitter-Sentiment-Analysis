# 3rdYearProject
COMP30880 Software Engineering Project Sprint 2 by Conor Barry, Jane O'Brien and Tom Higgins

Team NAME: ToJaCo

RUNNING FROM JAR

- java -jar tojaco.jar to use the bundled in config file.
- To use your own config file use java -jar tojaco.jar {pathtoconfigfile}

- If using a custom config file, note that a sample configuration file is given in config/default_config.txt. Use that file as a template for your config file
  and put in your API KEYS

If running from source ensure to add the Twitter4J library to the classpath.

# Answers to Sprint 4 questions 

- The tasks were divided evenly between the team members. See commit history for a clearer idea of who carried out which tasks, but pair work was often carried out with just one person committing the finished result.

3c)
Answers obtained by running the jar file and selecting option 4 in the menu

- percentage of users who have stances: 95.3451693% 
- percentage of users who don't have stances: 4.6548307%

- percentage of users who have anti vax stances: 58.892816%
- percentage of users who have pro vax stances: 41.104653%

4) 
- Our stance assignment algorithm correctly assigns stances to 95% of users in the data set 100% of the time, in terms of labelling someone as "pro vax" with a positive number less than 1000, or "anti vax" with a negative number greater than -1000 (which was checked and confirmed in part 4a and 4b, where each of the users who were assigned a stance produced accurate results). The weight of the stance that each user received may not be completely accurate, as certain users who seemed to only retweet pro vax evangelists might only have a stance of 600 or 700, when they deserve to have a stance of 1000, and the same results appeared for anti vaxxers deserving of a stance of -1000 but were actually assigned a stance of around -800.

4a & 4b) 
- see 100Users.txt

4c) 
- Positive Stances: 40/40 = 1 ( There are 41 pro vax users in total, @Lukedutchh was not assigned a stance, this user is retweeted by a few users but does not retweet any other users in the data set)
- Negative Stances: 59/59 = 1


4d) Based on the precision scores given above it doesn't appear that there is an apparent bias in our algorithm. 

# Answers to Sprint 5 questions

- The tasks were divided evenly between the team members. See commit history for a clearer idea of who carried out which tasks, but pair work was often carried out with just one person committing the finished result.

3c)

- percentage of users who have stances: 98.90558%
- percentage of users who don't have stances: 1.0944214%

- percentage of users who have anti vax stances: 59.75823%
- percentage of users who have pro vax stances: 40.19258%

For Sprint 4 the percentage of users who have stances was  95.3451693% so adding stances to hashtags and bootstrapping the results to update the user stances results in a 3.5604107% increase in the coverage of users in the graph.
Interestingly, the ratio of Pro/Anti vax stance assignments changes very little compared to Sprint 4, with the percentages only varying by +- 2%

4a) After calculating the stances for the hashtags, and setting all user stances back to 0/hasStance==false, and by percolating stances through the hashtag graph, the percentage of pro vax and anti vax is almost the same:

Calculating stances using retweeted users only
Percentage positive stances: 41.267586%
Percentage negative stance: 58.732414%

Calculating stances using hashtags only
ercentage positive stances using ONLY hashtags: 40.33209%
Percentage negative stance using ONLY hashtags: 59.667915%

The percentages are only varying by +- 2%

4b) The two perspectives agree with each other for roughly 98% of the stances, and disagree with each other for roughly 2% of the stances based on the results from 4a.

4c)