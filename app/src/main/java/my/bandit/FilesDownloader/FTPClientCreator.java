package my.bandit.FilesDownloader;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class FTPClientCreator implements Callable<FTPClient> {
    private FTPClientCreator() {
    }

    public static FTPClient getConnection() throws ExecutionException, InterruptedException {
        Callable<FTPClient> callable = new FTPClientCreator();
        FutureTask<FTPClient> connectionFutureTask = new FutureTask<>(callable);
        Thread thread = new Thread(connectionFutureTask);
        thread.start();
        return connectionFutureTask.get();
    }

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
