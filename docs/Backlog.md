# Talio - Your personal Task List Organizer

## Stakeholders:

-   **User:** Any person that is able to use the app, and can create a board and lists to organise various tasks.
-   **Admin:** A person that manages the server and the database

## Terminology:

-   **Board**: A board that shows all the lists and tasks created by the user. 
-  **Your personal Task List Organizer** A list inside the board that should have a title showing the goal of that list, and it also contains the tasks/cards. 
-   **Task/Card:** A single item in a list on a board, that can be dragged to different lists on that board. It has a title and optionally a description and subtasks
-   Tag: An item you attach to tasks so that you can categorise them.

## Epics:

### Minimal application:

#### As a user, I want:

-   to be able to create/remove lists, so that I can manage my tasks into the categories that I need
-   to be able to add/remove cards from any list, so that I can keep the board updated with new tasks as they come and easily remove old ones
-   to be able to drag and drop cards/tasks from one list to another, so that I can quickly and easily change their category/state
-   to be able to edit a card’s title, so that I can quickly fix my mistakes or update them if they change
-   to be able to collaborate with other users on the same board, so that we can keep track of our tasks together
-   the boards to be synchronised with the server for everyone working on the board, so that we all see the same tasks and aren’t confused by seeing different ones
-   no registration process, so that I can quickly view and organise my tasks

#### As an admin, I want:

-   To be able to start the server and database, so that the app can start running
-   To be able to restart the server without losing any data (persistence), so that I can make changes to the code and update the app without interrupting the users
-   The server to be running without any runtime errors, so that the users can access it whenever they need and use it without any problems


### Multi-board functionality:

#### As a user, I want:

-   To be able to connect to multiple different boards, where each board can be found with its name/key, so I can work on multiple boards for different things.
-   To be able to create new boards, with a specified name, so that I can use different boards to organise different tasks
-   To be able to delete boards that I have access to, so that the application is cleaner and not filled with old boards

#### As an admin, I want:

-   To be able to see all the boards, so that I can have a good overview of the application
-   To be able to edit, delete or add any board, so that I have full control over the application

  
### Safety:

#### As a user, I want:

-   To have password protection on different boards so that one can’t view or edit the board unless they have the password

#### As an admin, I want:

-   every board’s passwords to be hashed, so that they are secure.
-   To be able to restore a board’s password in case some users forget it
-   to see all users on a board, so that I can have a better overview of the current state of the app

  
### Customisation: 

#### As a user, I want:

-   To be able to customise the board, like changing the background colour of the board, lists and cards, so that it is easier to navigate
-   cards to have details such as description and nested task list, so that I can create more complicated tasks
-   To be able to add tags to different cards so that I can categorise different cards.
-   To be able to add colours to different tags, so that they are easily distinguishable.

  

### Search and filter:

#### As a user, I want:

-   To be able to search within each lists by keywords in the titles so that I can find the task I’m looking for
-   To be able to filter out tasks that have certain tags, so that I can organise my tasks better


### Keyboard shortcuts:

#### As a user, I want:

-   To have keyboard shortcuts for adding/removing different lists and cards, so that the application is easier to use

### Sharing and editing:

#### As a user, I want:

-   To be able to export the board as a PDF and share it so that I can show it to others 
-   To be able to share a link so that others can view and edit my board
-   To see the edit history of the board so that I can undo things if I want to 
  

## Mocks:


**Minimal application:**![](https://lh3.googleusercontent.com/NScC_WxjAJKN1tyzsahjyyQE7gDgWdfgnXR88Z7gIiuLYhJ2NB7hlYmAS-uly089N8lr7Av292XRveHZF3prSgx7d6c1u89375jY_t7qdoDmWv-Y8boi2NbzzovHTeP97LgbDdhrHdPo6sjN50P3oGI)

  

**Multi boards with search and customisation:![](https://lh6.googleusercontent.com/_dXZhd9889m6QEX4KxJqfvosNhMAD63Zyo0KSQDt5JDzKSfiyLIEVDHTC_NrgYZ2ugau1dRJnO3sw1LkRwgmJqfETMwyAaMg7P6miT6qeKrQFreMYekv8xYVhOlcSx6PaTib-LoBjXZ8olgddQBT4wE)**

  

**Detailed cards with titles, descriptions and tags:**![](https://lh4.googleusercontent.com/m__317DJpbU7xwUHLrOfMsBBW0N2V-ZkFR7iQl_xGauBqLGetw2qYYhUO4GJ6htS1-zPwMHGrRDURkhgI0eewdOXmnvNeU5OkUOxeXjleeBcT2KfgS_i0FEJPdStroEljyTA6dIJ6m3Es7O1ciLvipU)

  

**Password protected boards:**![](https://lh5.googleusercontent.com/Y__K_0CzodfkLhNU7j5wULmBkebJEgncgECWi6hjTst9LtQt66fL6u3TGq2nQhD-YLSh7EfKyO-1EjEKgnYQPFS6yh_5Oorjxv2pTopBeCSOIpEJ6Q03yCeqy0GCRLPHe4mlFiY0L9k9IP_lUBTAZCY)

