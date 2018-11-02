package es.iessaladillo.pedrojoya.pr05.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import es.iessaladillo.pedrojoya.pr05.data.local.model.Avatar;

public class MainActivityViewModel extends ViewModel {
//  En el InspectCode dice que esta clase puede ser Package-private, pero es mentira
// si no es Public no tiene accesibilidad y da un nullPointerException

    private MutableLiveData<Avatar> avatar;

    public LiveData<Avatar> getAvatar() {
        if (avatar == null) {
            avatar = new MutableLiveData<>();
        }
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar.setValue(avatar);
    }

}
