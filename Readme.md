 This app is meant to support an exhibit at the Basque Museum and Cultural Center in which visitors can see how families migrated from the Basque Country to the Western United States. Visitors select a Family name or a town name of a town in the Basque Country. The app draws arcs from an originating town in the Basque Country to the places in the US people with that family name or from that town migrated to.
 
 The app is developed in Java, using the JavaFX UI framework for stand alone apps. 
 In order to run on Ubuntu Linux at the time of developement, it was developed to run on open jdk 21.
 
 "Database"
 No actual database is used. Data is read from spreadsheet files in .csv format. 
 3 files are used
 FamilyLocationData.csv contains entries of family name, origin town, destination town in the US
 USLocationLatLong.csv contains entries of US town names and GPS longitude and latitude of the town.
 EusLocationLatLongProvinceUTF8 contains entries of Basque Country town names an their longitude and latitude.
 
 Dependencies
 Java JDK jdk-21.0.2
 JavaFX SDK - javafx-sdk-21.0.7
 opencsv-5.8.jar
 commons-lang3-3.17.0.jar
 
 Basic Installation/Deployment
 Download and install the java jdk for your operating system. https://jdk.java.net/archive/
 Download and install the javafx sdk for your operating system. https://gluonhq.com/products/javafx/
 Download jar files of libraries in the dependencies list.
  https://mvnrepository.com/artifact/com.opencsv/opencsv/5.8
  https://commons.apache.org/proper/commons-lang/download_lang.cgi
 Compile source .java files in .class files
 Edit example command line script file (.bat or .sh) to correct locations for the dependencies.
 Run the script file.
 
 