package my.bandit.FilesDownloader;

import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

class FtpClient {
    private static FtpClient ftpClient;
    private static List<FTPClient> connectionsPool;

    private FtpClient() {
    }

    static synchronized FtpClient getInstance() {
        if (ftpClient == null) {
            ftpClient = new FtpClient();
            connectionsPool = new LinkedList<>();
        }
        return ftpClient;

    }

    synchronized FTPClient getConnection() throws ExecutionException, InterruptedException {
        Log.d("FTPConnection", "Fetching Connection");
        if (connectionsPool.isEmpty()) {
            FTPClientCreator ftpClientCreator = new FTPClientCreator();
            FutureTask<FTPClient> clientFutureTask = new FutureTask<>(ftpClientCreator);
            Thread thread = new Thread(clientFutureTask);
            thread.start();
            thread.join();
            Log.d("Connected Fetched ", clientFutureTask.get().toString());
            return clientFutureTask.get();
        }
        FTPClient ftpClient = connectionsPool.get(0);
        connectionsPool.remove(0);
        Log.d("Connected Fetched ", ftpClient.toString());
        return ftpClient;
    }

    synchronized void releaseConnection(FTPClient client) {
        connectionsPool.add(client);
    }

    private static class FTPClientCreator implements Callable<FTPClient> {
        @Override
        public FTPClient call() throws Exception {
            FTPClient client = new FTPClient();
            client.setConnectTimeout(3000);
            client.connect(FtpCredentials.FTP_SERVER, FtpCredentials.FTP_PORT);
            int replyCode = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                client.disconnect();
                throw new IOException("Unable to connect to server.");
            }
            client.login(FtpCredentials.FTP_USER, FtpCredentials.FTP_PASS);
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.enterLocalPassiveMode();
            return client;
        }
    }
}
