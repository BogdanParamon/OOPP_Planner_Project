# Code Contributions and Code Reviews


#### Focused Commits

Grade: Insufficient

Feedback: The source code has a good amount of commits, but it seems like there are big pieces of commented code in the main, which is not a good practice in 'prod'. You should try to remove the commented code and leave only the Javadoc. Those features could be stored in another branch, not as commented code in the main. 


#### Isolation

Grade: Very Good

Feedback: I like that you split the features in 'frontend' and 'backend' and the fact that you use a 'dev' branch. You use branches and the scope of the MR seems appropriate.


#### Reviewability

Grade: Good

Feedback: The merge requests seem granular and target a small number of commits and a single feature. They seem easy to review and they contain suggestive title and description (some outliners such as "Dev" and the description is quite short for the weekly merge to 'main' or "
Database" which is also quite big and the title and description are too short for the size of the MR).


#### Code Reviews

Grade: Insufficient

Feedback: There were no reviews done. I recommend you start reviewing each other's code to start improving the codebase and have discussions about features in case of disagreement.

#### Build Server

Grade: Insufficient

Feedback: You have the 10+ checkstyle rules, but you did not pay attention to the pipeline. You have merged multiple failed branches into the main without an immediate fix. Please pay more attention to the pipelines and try to make them pass, especially in 'main' or 'dev'.

