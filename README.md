### Sheet-Cell is a Java desktop & server application enabling multi-user, real-time editing of tabular data with role-based access control. Architected server on Apache Tomcat, exposing REST endpoints consumed via OkHttp; implemented JavaFX GUI for seamless UX.

This repository contains the code of a Java application that allow users to manage and manipulate data in a tabular format. The system refers to it as a sheet (the concept is similar in principle to Excel and Google Sheet).

Written as part of a Java college course, by Meir Zoref and Ramez Mannaa.

The first exercise was a console application.

The second exercise was a GUI application using JavaFX.

### The third and final exercise is a client-server application, using Apache Tomcat server and OkHttp client.
The server manages several users and sheets at the same time, different authorization levels and the possibility of simultaneous editing.

# Sheet-Cell (3rd exercise screenshots and explanation)

![Java Ex3 Screenshot - login screen](https://github.com/user-attachments/assets/09dc7fe6-789b-45f9-9b1f-c645c6004f6e)



![Java Ex3 Screenshot - managing screen](https://github.com/user-attachments/assets/1e90f50f-cb35-4efe-a0d3-ea93bab44078)


![Java Ex3 Screenshot - table screen](https://github.com/user-attachments/assets/811ae3e8-b951-46e5-b186-2838613121fc)


The client is a JavaFX spreadsheet application that allows the user to manage and manipulate data in a tabular format (the system refers to it as a sheet).

The server contains the system engine, which manages several sheets from different users. All communication of a client is only with the server. The clients do not communicate “directly" with each other.


### Users can do the following:

●	Upload their own sheets.

●	See all the sheets that exist in the system and all access permissions/permission requests that each sheet has.

●	Request access to other users' sheets (Access to the sheet can be for reading only or also for editing).

●	Approve/deny access requests to their sheets.

●	Watch and edit a sheet, depending on the permission level.
  
  All capabilities are the same as the 2nd exercise app, with addition of dynamic analysis:
  See the data of a single cell, update cell value, watch previous versions, watch/add/delete a range of cells, filter a range, sort a range. 
  The dynamic analysis is the possibility to mark a cell, set min and max values, and a step size, and using a slider to watch the sheet changes dynamically.

If several users have editing capabilities - they can edit the sheet at the same time and see the changes that each of them makes in the system.
