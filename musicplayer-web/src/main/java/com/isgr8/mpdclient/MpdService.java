package com.isgr8.mpdclient;

import com.isgr8.mpdclient.io.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class MpdService {

    private static final int CONNECTION_TIMEOUT = 3;
    private MpdClient mpdClient;

    @Autowired
    public MpdService(MpdProperties mpdProperties) throws InterruptedException {
        Connection connection = new Connection(mpdProperties.getHost(), mpdProperties.getPort());
        connection.connect().block(Duration.ofSeconds(CONNECTION_TIMEOUT));
        mpdClient = new MpdClient(connection);
    }

    MpdClient getMpdClient() {
        return mpdClient;
    }
}
