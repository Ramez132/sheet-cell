This repository contains the code of an application that allow users to manage and manipulate data in a tabular format (the system refers to it as a sheet - the concept is similar in principle to Excel and Google Sheet).

Written as part of a college course, by Meir Zoref and Ramez Mannaa.

The first exercise is a console application.

The second exercise is a GUI application using JavaFX.

### The third exercise, which we are currently working on, will be a client-server application, using Apache Tomcat server and OkHttp client.
The server will manage several users and sheets at the same time, different authorization levels and the possibility of simultaneous editing.

# Sheet-Cell (2nd exercise example and explanation)

![java ex2 screenshot exmaple](https://github.com/user-attachments/assets/2422a611-b5e0-4d72-86d9-2aa50786805a)


The system is a JavaFX spreadsheet application that allows the user to manage and manipulate data in a tabular format (the system refers to it as a sheet).

The GUI app expands the options and capabilities for the user, compared to the console application from the 1st exercise. 

In addition, the app makes the system more friendly and usable. 

### The main differences from the previous application are:

●	The transition from a console application to a JavaFX application.

●	Displaying and using the sheet in a user-friendly graphical user interface.

●	Easy way to access and change the cells in the sheet.

●	Working with ranges of cells, spreading across different rows and columns: getting data of ranges from the file and then allowing to display cells in range, add new range and trying to delete unused range.

●	Filtering rows in a range, according to selected column and unique values.

●	Sorting rows in a selected range, from lowest number to highest, according to one or more columns provided by the user.
