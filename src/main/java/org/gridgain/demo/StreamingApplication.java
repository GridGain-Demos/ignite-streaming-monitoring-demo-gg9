/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gridgain.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class StreamingApplication {
    /* Application default execution time. */
    private static int DEFAULT_EXEC_COUNT = 0;
    private static int DEFAULT_EXEC_TIME_MINS = 15;
    private static final String EXEC_COUNT = "execCount";
    private static final String EXEC_TIME = "execTime";

    /* Market ticker. */
    private static MarketTicker ticker;

    public static void main(String args[]) {
        int execTime = DEFAULT_EXEC_TIME_MINS;
        int execCount = DEFAULT_EXEC_COUNT;

        if (args != null) {
            for (String arg : args) {
                if (arg.startsWith(EXEC_TIME)) {
                    execTime = Integer.parseInt(arg.split("=")[1]);
                } else if (arg.startsWith(EXEC_COUNT)) {
                    execCount = Integer.parseInt(arg.split("=")[1]);
                } else {
                    System.err.println("Unsupported parameter: " + arg);
                    return;
                }
            }
        }

        if (DEFAULT_EXEC_COUNT < execCount) {
            System.out.println("Application execution count: " + execCount);
        } else {
            System.out.println("Application execution time: " + execTime + " minutes");
        }

        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1:10800")) {
            createSchema(conn);

            // Starting Market Ticker.
            ticker = new MarketTicker();
            ticker.start();

            if(DEFAULT_EXEC_COUNT < execCount) {
                while(ticker.getTradeCount() < execCount) {
                    System.out.println("Current trade count = " + ticker.getTradeCount());
                    try {
                        TimeUnit.SECONDS.sleep(30);
                    } catch (InterruptedException ie) {
                        System.out.println("Caught InterruptedException!");
                    }
                }
                ticker.stop();
                System.exit(0);
            } else {
                // Shutting down the application in 'execTime' minutes.
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("The execution time is over. Shutting down the application...");
                        ticker.stop();
                        System.exit(0);
                    }
                }, execTime * 60 * 1000);
            }
        } catch (SQLException e) {
            System.out.println("Error using JDBC Connection");
            e.printStackTrace();
        }
    }

    private static void createSchema(Connection conn) {
        Schema schema = new Schema(conn);
        schema.dropTables();
        schema.createTables();
        schema.populateBuyerTable();
    }

}
