package es.iessaladillo.pedrojoya.pr05.ui.avatar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import es.iessaladillo.pedrojoya.pr05.R;
import es.iessaladillo.pedrojoya.pr05.data.local.Database;
import es.iessaladillo.pedrojoya.pr05.data.local.model.Avatar;
import es.iessaladillo.pedrojoya.pr05.utils.ResourcesUtils;

public class AvatarActivity extends AppCompatActivity {

    @VisibleForTesting
    public static final String EXTRA_AVATAR = "EXTRA_AVATAR";

    private AvatarActivityViewModel viewModel;

    private Avatar avatar;

    private Database database;

    private ImageView imgAvatar1;
    private ImageView imgAvatar2;
    private ImageView imgAvatar3;
    private ImageView imgAvatar4;
    private ImageView imgAvatar5;
    private ImageView imgAvatar6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
        initViews();
        getIntentData();
        viewModel = ViewModelProviders.of(this).get(AvatarActivityViewModel.class);
        final Observer<Avatar> observerAvatar = this::restoreAvatar;
        viewModel.getAvatar().observe(this, observerAvatar);
    }

    private void restoreAvatar(Avatar newAvatar) {
        this.avatar = newAvatar;
        selectCatSelected(avatar.getImageResId());
    }

    private void initViews() {
        database = Database.getInstance();

        imgAvatar1 = ActivityCompat.requireViewById(this, R.id.imgAvatar1);
        imgAvatar2 = ActivityCompat.requireViewById(this, R.id.imgAvatar2);
        imgAvatar3 = ActivityCompat.requireViewById(this, R.id.imgAvatar3);
        imgAvatar4 = ActivityCompat.requireViewById(this, R.id.imgAvatar4);
        imgAvatar5 = ActivityCompat.requireViewById(this, R.id.imgAvatar5);
        imgAvatar6 = ActivityCompat.requireViewById(this, R.id.imgAvatar6);

        TextView lblAvatar1 = ActivityCompat.requireViewById(this, R.id.lblAvatar1);
        TextView lblAvatar2 = ActivityCompat.requireViewById(this, R.id.lblAvatar2);
        TextView lblAvatar3 = ActivityCompat.requireViewById(this, R.id.lblAvatar3);
        TextView lblAvatar4 = ActivityCompat.requireViewById(this, R.id.lblAvatar4);
        TextView lblAvatar5 = ActivityCompat.requireViewById(this, R.id.lblAvatar5);
        TextView lblAvatar6 = ActivityCompat.requireViewById(this, R.id.lblAvatar6);

        imgAvatar1.setOnClickListener(v -> configureIntent(imgAvatar1));
        imgAvatar2.setOnClickListener(v -> configureIntent(imgAvatar2));
        imgAvatar3.setOnClickListener(v -> configureIntent(imgAvatar3));
        imgAvatar4.setOnClickListener(v -> configureIntent(imgAvatar4));
        imgAvatar5.setOnClickListener(v -> configureIntent(imgAvatar5));
        imgAvatar6.setOnClickListener(v -> configureIntent(imgAvatar6));

        lblAvatar1.setOnClickListener(v -> configureIntent(imgAvatar1));
        lblAvatar2.setOnClickListener(v -> configureIntent(imgAvatar2));
        lblAvatar3.setOnClickListener(v -> configureIntent(imgAvatar3));
        lblAvatar4.setOnClickListener(v -> configureIntent(imgAvatar4));
        lblAvatar5.setOnClickListener(v -> configureIntent(imgAvatar5));
        lblAvatar6.setOnClickListener(v -> configureIntent(imgAvatar6));

    }

    public static void startForResult(Activity activity, int requestCode, Avatar avatar) {
        Intent intent = new Intent(activity, AvatarActivity.class);
        intent.putExtra(EXTRA_AVATAR, avatar);
        activity.startActivityForResult(intent, requestCode);
    }

    private void configureIntent(ImageView img) {
        setAvatar(img.getId());
        viewModel.setAvatar(avatar);
    }

    public void onClick(View v) {
        setAvatar(v.getId());
        selectCatSelected(avatar.getImageResId());
    }

    private void sendIntent(@NonNull Avatar avatar) {
        Intent result = new Intent();
        result.putExtra(EXTRA_AVATAR, avatar);
        setResult(RESULT_OK, result);
        finish();
    }

    private void setAvatar(int id) {
        switch (id) {
            case R.id.imgAvatar1:
                avatar = database.queryAvatar(1);
                break;
            case R.id.imgAvatar2:
                avatar = database.queryAvatar(2);
                break;
            case R.id.imgAvatar3:
                avatar = database.queryAvatar(3);
                break;
            case R.id.imgAvatar4:
                avatar = database.queryAvatar(4);
                break;
            case R.id.imgAvatar5:
                avatar = database.queryAvatar(5);
                break;
            case R.id.imgAvatar6:
                avatar = database.queryAvatar(6);
                break;
        }
        selectCatSelected(avatar.getImageResId());
    }

    private void selectCatSelected(int resId) {
        Database database = Database.getInstance();
        ImageView imgAvatars[] = new ImageView[6];
        int i = 1;

        imgAvatars[0] = imgAvatar1;
        imgAvatars[1] = imgAvatar2;
        imgAvatars[2] = imgAvatar3;
        imgAvatars[3] = imgAvatar4;
        imgAvatars[4] = imgAvatar5;
        imgAvatars[5] = imgAvatar6;

        for (ImageView image : imgAvatars) {
            if (resId == database.queryAvatar(i).getImageResId()) {
                selectImageView(image);
            } else {
                deselectImageView(image);
            }
            i++;
        }
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_AVATAR)) {
            avatar = intent.getParcelableExtra(EXTRA_AVATAR);
            selectCatSelected(avatar.getImageResId());
        } else {
            throw new IllegalArgumentException("Activity cannot find extras " + EXTRA_AVATAR);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_avatar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnuSelect) {
            sendIntent(avatar);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectImageView(ImageView imageView) {
        imageView.setAlpha(ResourcesUtils.getFloat(this, R.dimen.avatar_selected_image_alpha));
    }

    private void deselectImageView(ImageView imageView) {
        imageView.setAlpha(ResourcesUtils.getFloat(this, R.dimen.avatar_deselect_image_alpha));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
