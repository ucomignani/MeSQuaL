# MeSQuaL

One Paragraph of project description goes here

## Installation

### Prerequisites

* [Grafana](https://grafana.com/) - Analytics and monitoring platform
* [Maven](https://maven.apache.org/) - Dependency Management
* Java SDK 11


### Installing MeSQuaL

#### Installing the engine
In the MeSQuaL-engine directory, to generate the parser, run:
```
mvn javacc:javacc
```

then, to produce a jar file, run:
```
mvn install
```
To avoid executing the unitary tests, instead of previous command run:
```
mvn install -DskipTests
```

#### Installing the visualization platform

At first, copy the querying interface plugin in the dedicated directory(/var/lib/grafana/plugins/ by default). For example, on linux systems run:
```
cp -R ./MeSQuaL-visualization/MeSQuaL-query-grafana-panel/ /var/lib/grafana/plugins/
```

Then, in Grafana use the "Import" function and paste the content of the json file [grafana-dashboard-setting.json](MeSQuaL-visualization/grafana-dashboard-setting.json) to import the MeSQuaL dashboard.

## Deployment
Launch MeSQuaL engine by running:
```
java -jar common-1.0-SNAPSHOT-jar-with-dependencies.jar
```

then MeSQuaL interface can be accessed using Grafana. For Example, for a local installation of Grafana, connect to:
 ```
 http://localhost:3000
 ```

## Example datasets

We provides few datasets in the 'demo' directory.
These datasets corresponds to the one used in the demo paper presented at the EDBT2020 conference.

For MySQL, the csv files in ./data/datasets/ should be copied in the default directory /var/lib/mysql-files/.
Then the [createTablesAndLoadData.sql](demo/data/createTablesAndLoadData.sql) script 
can be used to generates the tables and imports the data.

## Built With
 * [Maven](https://maven.apache.org/) - Dependency Management used for MeSQuaL engine
 * [Yarn](https://yarnpkg.com/) - Dependency Management used for MeSQuaL plugins (if you want to modify the querying 
 plugin, please see the dedicated [README.md](MeSQuaL-visualization/MeSQuaL-query-grafana-panel/README.md) in 
 the plugin directory)

## Authors

* **Ugo Comignani**
* **Noël Novelli**
* **Laure Berti-Equille**

## License

This project is licensed under the GNU General Public License Version 3 - see the [LICENSE.md](LICENSE.md) file for details


