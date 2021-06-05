# Xchange-me-sir
Sample project of a REST service to retrieve daily and historized exchange rate data from EUR to other currency


Project description:

Implement a simple foreign exchange rate service that uses data provided by the European Central Bank. Provide a REST API to retrieve the exchange rate to EUR from any other currency provided by the ECB and allow querying current (https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml) and past (https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml) data. Use an asynchronous job to retrieve and update the xrates storing them locally in memory and have the REST API use the data in memory to return this information.

Limitations:

- Use Java 8
- Use Spring 4
- Return the response in JSON format

Implementation description:

We need to provide two methods to retrieve the current and historical data which will be stored in two HashMaps in memory; we can do this by providing two URLs with different parameters. Current data would be retrieved with:

http://localhost:8080/api/getXrate/{currency}

and historical data would be retrieved with:

http://localhost:8080/api/getXrate/{currency}/{date}

Where currency is the 3 characters uppercase currency name (eg CHF) and date is in the format YYYY-MM-DD

When a method is called, we check if we have the data in memory, if yes, we try to retrieve the xrate and either return it or return null; if not, we return a "Retry later" message and start the async data loading process.

The loading process works in three steps:
locally download the XML files from the ECB
parse the XML files - which by the way have a bad structure in my opinion since they reuse the same node name multiple times with different meanings and store the values in attributes rather than the node-value format
populate the HashMaps with the parsed data

For the full description check http://groglogs.blogspot.ch/2017/05/java-xchange-me-sir-sample-rest-project.html

Project is licensed under a [CreativeCommons Attribution-ShareAlike 4.0 International license](https://creativecommons.org/licenses/by-sa/4.0/legalcode)
