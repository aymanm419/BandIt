package my.bandit.FilesDownloader;

import java.io.File;

public interface OnFileDownload {
    void onSuccess(File file);

    void onFailure();
}
