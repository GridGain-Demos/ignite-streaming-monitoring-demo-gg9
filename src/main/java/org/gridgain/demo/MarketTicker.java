package org.gridgain.demo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.sql.*;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class MarketTicker {

    private static final String STREAM_NAME = "pubnub-market-orders";

    private static final String STREAM_SUBSCRIPION_KEY = "sub-c-99084bc5-1844-4e1c-82ca-a01b18166ca8";

    private static final String TRADE_INSERT_STATEMENT = "INSERT INTO TRADE (id, buyer_id, symbol, order_quantity, bid_price, trade_type, order_date) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private PubNub stream;

    private Connection conn;

    private AtomicLong counter = new AtomicLong();

    public MarketTicker() {
        try {
            this.conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1:10800");
        } catch (SQLException e) {
            System.out.println("Error opening JDBC Connection in MarketTicker");
            e.printStackTrace();
        }
    }

    public void start() {
        PNConfiguration cfg = new PNConfiguration();
        cfg.setSubscribeKey(STREAM_SUBSCRIPION_KEY);

        stream = new PubNub(cfg);

        stream.addListener(new StreamCallback());
        stream.subscribe().channels(Arrays.asList(STREAM_NAME)).execute();
    }

    public void stop() {
        stream.unsubscribe().execute();
    }

    private class StreamCallback extends SubscribeCallback {

        public void status(PubNub nub, PNStatus status) {

            if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
                // Connect event.
                System.out.println("Connected to the market orders stream: " + status.toString());
            }
            else if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                System.err.println("Connection is lost:" + status.getErrorData().toString());
            }
            else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {
                // Happens as part of our regular operation. This event happens when
                // radio / connectivity is lost, then regained.
                System.out.println("Reconnected to the market orders stream");
            }
            else {
                System.out.println("Connection status changes:" + status.toString());
            }
        }

        public void message(PubNub nub, PNMessageResult result) {
            JsonElement mes = result.getMessage();
            JsonObject json = mes.getAsJsonObject();

            TradeKey key = new TradeKey(counter.incrementAndGet(), new Random().nextInt(6) + 1);

            try {
                PreparedStatement pstmt = conn.prepareStatement(TRADE_INSERT_STATEMENT);
                pstmt.setLong(1, key.getId());
                pstmt.setInt(2, key.getBuyerId());
                pstmt.setString(3, json.get("symbol").getAsString());
                pstmt.setInt(4, json.get("order_quantity").getAsInt());
                pstmt.setDouble(5, json.get("bid_price").getAsDouble());
                pstmt.setString(6, json.get("trade_type").getAsString());
                pstmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Error inserting data into Trade table");
                e.printStackTrace();
            }
        }

        public void presence(PubNub nub, PNPresenceEventResult result) {
            System.out.println("Stream presence event: " + result.toString());
        }
    }

}
