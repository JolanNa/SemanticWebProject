INSTALLATION AND RUNNING INSTRUCTIONS

As the populated ontology exceeded 100MB, the Webscraper must be run locally to bypass Inforestudante's upload limit

-Copy the Webscraper folder to your computer
-Copy the SemWebApp to a Tomcat v7.0's web application folder
-Edit the Webscraper.java file to locate the "WrestlingOntologyRaw.rdf" file location in your computer
-Run the Webscraper and allow it to populate the ontology
-Start your Fuseki server with "--file=WrestlingOntology.ttl /DatasetPathName" as parameters on port 3030
-Start your Tomcat server on port 8080
-Enter localhost:8080/SemWebApp to use the Web Application