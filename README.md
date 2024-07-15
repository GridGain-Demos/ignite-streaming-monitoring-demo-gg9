# Apache Ignite Market Orders Streaming Demo  

The demo starts a simple application that simulates trading activities of a stock exchange.   
The application receives simulated market orders in real-time via the following data providor:  

[PubNub Market Order Data Stream]  
(https://www.pubnub.com/developers/realtime-data-streams/financial-securities-market-orders/)  

The received data is subsequently forwarded to a locally running GridGain cluster.  

## Dependencies, Compile & Build  

This demo is configured to execute against GridGain version 9.0.0    
This program is compiled using Java version 11.0.20  
This program is built using Maven version 3.8.7  
mvn clean package  

## Other Requirements  

You will need to obtain a demo license and change the license file    
location to a valid path for your environment in your server  
configuration file before starting your server nodes.  

## Program Process  
Launch one or more GridGain 9 server nodes each in its own command window.    
The Cluster node should have native persistence enabled. Upon execution the   
program will delete the Buyer and Trade tables if they exist. The program  
will then create the Buyer table and populate it with 6 default buyers.  
The program will then attach to the PubNub Server and stream Trade orders  
from PubNub and populate these into the Trade table for 15 minutes by default.  
  
There is an optional execTime argument that will override the default 15 minute    
time if specified.  

Example executions are listed below:  
java -cp .\target\ignite-streaming-app.jar org.gridgain.demo.StreamingApplication  
java -cp .\target\ignite-streaming-app.jar org.gridgain.demo.StreamingApplication execTime=5  


