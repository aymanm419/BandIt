package my.bandit.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import lombok.Getter;
import lombok.Setter;
import my.bandit.Service.MusicService;

public class MainViewModel extends AndroidViewModel {

    @Setter
    @Getter
    private MusicService musicService;


    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }



}
