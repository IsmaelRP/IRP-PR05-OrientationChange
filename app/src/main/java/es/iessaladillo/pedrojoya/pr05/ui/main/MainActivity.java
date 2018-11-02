package es.iessaladillo.pedrojoya.pr05.ui.main;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import es.iessaladillo.pedrojoya.pr05.R;
import es.iessaladillo.pedrojoya.pr05.data.local.Database;
import es.iessaladillo.pedrojoya.pr05.data.local.model.Avatar;
import es.iessaladillo.pedrojoya.pr05.ui.avatar.AvatarActivity;
import es.iessaladillo.pedrojoya.pr05.utils.KeyboardUtils;
import es.iessaladillo.pedrojoya.pr05.utils.SnackbarUtils;
import es.iessaladillo.pedrojoya.pr05.utils.ValidationUtils;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel viewModel;

    private static final String EXTRA_AVATAR = "EXTRA_AVATAR";
    private static final int RC_AVATAR = 1;
    private Avatar avatar;

    private static final String EXTRA_DISABLED = "EXTRA_DISABLED";

    private final Database database = Database.getInstance();

    private ImageView imgAvatar;
    private TextView lblAvatar;

    private TextView lblName;
    private TextView lblEmail;
    private TextView lblPhoneNumber;
    private TextView lblAddress;
    private TextView lblWeb;

    private EditText txtName;
    private EditText txtEmail;
    private EditText txtPhoneNumber;
    private EditText txtAddress;
    private EditText txtWeb;

    private ImageView imgEmail;
    private ImageView imgPhonenumber;
    private ImageView imgAddress;
    private ImageView imgWeb;

    private GenericTextWatcher nameWatcher;
    private GenericTextWatcher emailWatcher;
    private GenericTextWatcher phoneWatcher;
    private GenericTextWatcher addressWatcher;
    private GenericTextWatcher webWatcher;

    private boolean[] fieldsDisableds;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBooleanArray(EXTRA_DISABLED, fieldsDisableds);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        final Observer<Avatar> observerAvatar = this::restoreStateAvatar;
        viewModel.getAvatar().observe(this, observerAvatar);

        if (savedInstanceState != null) {
            restoreDisableds(Objects.requireNonNull(savedInstanceState.getBooleanArray(EXTRA_DISABLED)));
        }

        checkDisableds();
    }

    private void checkDisableds() {
        fieldsDisableds[0] = !lblName.isEnabled();
        fieldsDisableds[1] = !lblEmail.isEnabled();
        fieldsDisableds[2] = !lblPhoneNumber.isEnabled();
        fieldsDisableds[3] = !lblAddress.isEnabled();
        fieldsDisableds[4] = !lblWeb.isEnabled();
    }

    private void restoreDisableds(boolean[] disableds) {

        lblName.setEnabled(!disableds[0]);

        lblEmail.setEnabled(!disableds[1]);
        imgEmail.setEnabled(!disableds[1]);

        lblPhoneNumber.setEnabled(!disableds[2]);
        imgPhonenumber.setEnabled(!disableds[2]);

        lblAddress.setEnabled(!disableds[3]);
        imgAddress.setEnabled(!disableds[3]);

        lblWeb.setEnabled(!disableds[4]);
        imgWeb.setEnabled(!disableds[4]);
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtName.addTextChangedListener(nameWatcher);
        txtEmail.addTextChangedListener(emailWatcher);
        txtPhoneNumber.addTextChangedListener(phoneWatcher);
        txtAddress.addTextChangedListener(addressWatcher);
        txtWeb.addTextChangedListener(webWatcher);
    }

    @Override
    protected void onPause() {
        super.onPause();
        txtName.removeTextChangedListener(nameWatcher);
        txtEmail.removeTextChangedListener(emailWatcher);
        txtPhoneNumber.removeTextChangedListener(phoneWatcher);
        txtAddress.removeTextChangedListener(addressWatcher);
        txtWeb.removeTextChangedListener(webWatcher);
    }

    private void initViews() {
        avatar = database.getDefaultAvatar();

        imgAvatar = ActivityCompat.requireViewById(this, R.id.imgAvatar);
        showAvatar(database.getDefaultAvatar());
        imgAvatar.setOnClickListener(v -> startAvatarActivity());

        lblAvatar = ActivityCompat.requireViewById(this, R.id.lblAvatar);
        showLabel(database.getDefaultAvatar().getName());
        lblAvatar.setOnClickListener(v -> startAvatarActivity());

        lblName = ActivityCompat.requireViewById(this, R.id.lblName);
        txtName = ActivityCompat.requireViewById(this, R.id.txtName);
        txtName.setOnFocusChangeListener((v, hasFocus) -> txtSwapBold(lblName));


        lblEmail = ActivityCompat.requireViewById(this, R.id.lblEmail);
        txtEmail = ActivityCompat.requireViewById(this, R.id.txtEmail);
        txtEmail.setOnFocusChangeListener((v, hasFocus) -> txtSwapBold(lblEmail));

        lblPhoneNumber = ActivityCompat.requireViewById(this, R.id.lblPhonenumber);
        txtPhoneNumber = ActivityCompat.requireViewById(this, R.id.txtPhonenumber);
        txtPhoneNumber.setOnFocusChangeListener((v, hasFocus) -> txtSwapBold(lblPhoneNumber));

        lblAddress = ActivityCompat.requireViewById(this, R.id.lblAddress);
        txtAddress = ActivityCompat.requireViewById(this, R.id.txtAddress);
        txtAddress.setOnFocusChangeListener((v, hasFocus) -> txtSwapBold(lblAddress));

        lblWeb = ActivityCompat.requireViewById(this, R.id.lblWeb);
        txtWeb = ActivityCompat.requireViewById(this, R.id.txtWeb);
        txtWeb.setOnFocusChangeListener((v, hasFocus) -> txtSwapBold(lblWeb));

        txtWeb.setOnEditorActionListener((v, actionId, event) -> {
            save();
            return true;
        });

        imgEmail = ActivityCompat.requireViewById(this, R.id.imgEmail);
        imgEmail.setOnClickListener(v -> sendEmail());

        imgPhonenumber = ActivityCompat.requireViewById(this, R.id.imgPhonenumber);
        imgPhonenumber.setOnClickListener(v -> dial());

        imgAddress = ActivityCompat.requireViewById(this, R.id.imgAddress);
        imgAddress.setOnClickListener(v -> maps());

        imgWeb = ActivityCompat.requireViewById(this, R.id.imgWeb);
        imgWeb.setOnClickListener(v -> searchWeb());

        nameWatcher = new GenericTextWatcher(txtName);
        emailWatcher = new GenericTextWatcher(txtEmail);
        phoneWatcher = new GenericTextWatcher(txtPhoneNumber);
        addressWatcher = new GenericTextWatcher(txtAddress);
        webWatcher = new GenericTextWatcher(txtWeb);

        fieldsDisableds = new boolean[5];
    }

    private void showLabel(String text) {
        lblAvatar.setText(text);
    }

    private void restoreStateAvatar(Avatar newAvatar) {
        showAvatar(newAvatar);
        showLabel(newAvatar.getName());
    }

    private void startAvatarActivity() {
        AvatarActivity.startForResult(MainActivity.this, RC_AVATAR, avatar);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == RC_AVATAR) {
            if (data != null && data.hasExtra(AvatarActivity.EXTRA_AVATAR)) {
                avatar = data.getParcelableExtra(EXTRA_AVATAR);

                showAvatar(avatar);
                showLabel(avatar.getName());
                viewModel.setAvatar(avatar);
            }
        }
    }

    private void showAvatar(Avatar newAvatar) {
        imgAvatar.setImageResource(newAvatar.getImageResId());
        imgAvatar.setTag(newAvatar.getImageResId());
        this.avatar = newAvatar;
    }

    private void sendEmail() {
        Intent intent;
        String address = txtEmail.getText().toString();

        if (!isWrongEmail()) {
            intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + address));
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                KeyboardUtils.hideSoftKeyboard(this);
                SnackbarUtils.snackbar(imgEmail, getString(R.string.error_email), Snackbar.LENGTH_SHORT);
            }
        } else {
            setErrorEmail(isWrongEmail());
        }
    }

    private void dial() {
        Intent intent;
        String phoneNumber = txtPhoneNumber.getText().toString();

        if (!isWrongPhonenumber()) {
            intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                KeyboardUtils.hideSoftKeyboard(this);
                SnackbarUtils.snackbar(imgPhonenumber, getString(R.string.error_phonenumber), Snackbar.LENGTH_SHORT);
            }
        } else {
            setErrorPhonenumber(isWrongPhonenumber());
        }
    }

    private void maps() {
        Intent intent;
        String address = txtAddress.getText().toString();

        if (!isWrongAddress()) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("geo:0,0?q=" + address));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                KeyboardUtils.hideSoftKeyboard(this);
                SnackbarUtils.snackbar(imgAddress, getString(R.string.error_address), Snackbar.LENGTH_SHORT);
            }
        } else {
            setErrorAddress(isWrongAddress());
        }
    }

    private void searchWeb() {
        Intent intent;
        String web = txtWeb.getText().toString();

        if (!isWrongWeb()) {
            if (web.substring(0, 8).matches("https://") || web.substring(0, 7).matches("http://")) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(web));
            } else {
                intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, web);
            }

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                KeyboardUtils.hideSoftKeyboard(this);
                SnackbarUtils.snackbar(imgWeb, getString(R.string.error_web), Snackbar.LENGTH_SHORT);
            }
        } else {
            setErrorWeb(isWrongWeb());
        }
    }


    private void txtSwapBold(TextView txt) {
        if (txt.getTypeface().isBold()) {
            txt.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        } else {
            txt.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
    }

    private class GenericTextWatcher implements TextWatcher {

        private final View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.txtName:
                    setErrorName(isWrongName());
                    fieldsDisableds[0] = isWrongName();
                    break;
                case R.id.txtEmail:
                    setErrorEmail(isWrongEmail());
                    fieldsDisableds[1] = isWrongEmail();
                    break;
                case R.id.txtPhonenumber:
                    setErrorPhonenumber(isWrongPhonenumber());
                    fieldsDisableds[2] = isWrongPhonenumber();
                    break;
                case R.id.txtAddress:
                    setErrorAddress(isWrongAddress());
                    fieldsDisableds[3] = isWrongAddress();
                    break;
                case R.id.txtWeb:
                    setErrorWeb(isWrongWeb());
                    fieldsDisableds[4] = isWrongWeb();
                    break;
            }

        }
    }

    private boolean isWrongName() {
        boolean isWrong = false;
        if (txtName.getText().toString().length() <= 0) {
            isWrong = true;
        }
        return isWrong;
    }

    private void setErrorName(boolean wrong) {
        if (wrong) {
            txtName.setError((getString(R.string.main_invalid_data)));
            lblName.setEnabled(false);
        } else {
            txtName.setError(null);
            lblName.setEnabled(true);
        }
    }

    private boolean isWrongEmail() {
        boolean isWrong = false;
        if (!ValidationUtils.isValidEmail(txtEmail.getText().toString())) {
            isWrong = true;
        }
        return isWrong;
    }

    private void setErrorEmail(boolean wrong) {
        if (wrong) {
            txtEmail.setError((getString(R.string.main_invalid_data)));
            imgEmail.setEnabled(false);
            lblEmail.setEnabled(false);
        } else {
            txtAddress.setError(null);
            imgEmail.setEnabled(true);
            lblEmail.setEnabled(true);
        }
    }

    private boolean isWrongPhonenumber() {
        boolean isWrong = false;
        if (!ValidationUtils.isValidPhone(txtPhoneNumber.getText().toString())) {
            isWrong = true;
        }
        return isWrong;
    }

    private void setErrorPhonenumber(boolean wrong) {
        if (wrong) {
            txtPhoneNumber.setError((getString(R.string.main_invalid_data)));
            imgPhonenumber.setEnabled(false);
            lblPhoneNumber.setEnabled(false);
        } else {
            txtPhoneNumber.setError(null);
            imgPhonenumber.setEnabled(true);
            lblPhoneNumber.setEnabled(true);
        }
    }

    private boolean isWrongAddress() {
        boolean isWrong = false;
        if (txtAddress.getText().toString().length() <= 0) {
            isWrong = true;
        }
        return isWrong;
    }

    private void setErrorAddress(boolean wrong) {
        if (wrong) {
            txtAddress.setError((getString(R.string.main_invalid_data)));
            imgAddress.setEnabled(false);
            lblAddress.setEnabled(false);
        } else {
            txtAddress.setError(null);
            imgAddress.setEnabled(true);
            lblAddress.setEnabled(true);
        }
    }

    private boolean isWrongWeb() {
        boolean isWrong = false;
        if (!ValidationUtils.isValidUrl(txtWeb.getText().toString())) {
            isWrong = true;
        }
        return isWrong;
    }

    private void setErrorWeb(boolean wrong) {
        if (wrong) {
            txtWeb.setError((getString(R.string.main_invalid_data)));
            imgWeb.setEnabled(false);
            lblWeb.setEnabled(false);
        } else {
            txtWeb.setError(null);
            imgWeb.setEnabled(true);
            lblWeb.setEnabled(true);
        }
    }

    private void save() {
        boolean valid;
        KeyboardUtils.hideSoftKeyboard(this);

        valid = isFormValid();

        if (valid) {
            SnackbarUtils.snackbar(imgAvatar, getString(R.string.main_saved_succesfully), Snackbar.LENGTH_SHORT);
        } else {
            SnackbarUtils.snackbar(imgAvatar, getString(R.string.main_error_saving), Snackbar.LENGTH_SHORT);
        }
    }

    private boolean isFormValid() {
        boolean valid = true;

        if (isWrongName()) {
            valid = false;
            setErrorName(isWrongName());
        }
        if (isWrongEmail()) {
            valid = false;
            setErrorEmail(isWrongEmail());
        }
        if (isWrongPhonenumber()) {
            valid = false;
            setErrorPhonenumber(isWrongPhonenumber());
        }
        if (isWrongAddress()) {
            valid = false;
            setErrorAddress(isWrongAddress());
        }

        if (isWrongWeb()) {
            valid = false;
            setErrorWeb(isWrongWeb());
        }

        return valid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnuSave) {
            save();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
