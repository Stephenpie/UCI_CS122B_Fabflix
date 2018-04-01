## CS 122B Project 2 API example

This example shows how frontend and backend are separated by implementing a star list page.

### To import this example to Eclipse: 
0. Make sure you finish all the setup steps in project 1 description.
1. clone this repository using `git clone https://github.com/UCI-Chenli-teaching/project1-star-example.git`
2. open Eclipse -> File -> import -> under "Maven" -> "Existing Maven Projects" -> Click "Finish".
3. For "Root Directory", click "Browse" and select this repository's folder. Click "Finish".
4. In "Java Resources" folder, open `src->(default package)->StarServlet.java`. Change the mysql username and password and make sure you have the `moviedb` database.

### To Run this example
- If you want to run it on your local machine, make sure tomcat and mysql are started, then just click "run" in Eclipse. 
- If you want to generate a WAR file and deploy it through tomcat webpage, you can right click the project -> Export -> WAR file.

### Brief Explanation
`index.html` is a simple welcome page. Open the url `localhost:8080/project1-star-example`, if the welcome text shows up, then means that you have successfully deployed the project.

`StarServlet.java` is a Java servlet that talks to the database and get the stars data. It generates the HTML strings and write it to response.

