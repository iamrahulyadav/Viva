package id.co.viva.news.app.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.squareup.picasso.Picasso;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.VivaApp;
import id.co.viva.news.app.interfaces.OnCompleteListener;
import id.co.viva.news.app.services.UserAccount;

/**
 * Created by reza on 03/12/14.
 */
public class ActComment extends FragmentActivity implements View.OnClickListener, OnCompleteListener {

    private String mImageUrl;
    private String mTitle;
    private String mIds;
    private TextView text_title;
    private ImageView image_content;
    private ActionProcessButton btnSubmit;
    private ListView listComment;
    private EditText etComment;
    private String fullname;
    private String email;
    private String mFromKanal;
    private UserAccount userAccount;
    private LinearLayout mLayout;
    private boolean isInternetPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        mIds = bundle.getString("article_id");
        mImageUrl = bundle.getString("imageurl");
        mTitle = bundle.getString("title");
        mFromKanal = bundle.getString("type_kanal");

        setContentView(R.layout.act_comment);

        getStateUser();

        isInternetPresent = VivaApp.getInstance().getConnectionStatus().isConnectingToInternet();

        mLayout = (LinearLayout)findViewById(R.id.background_label_comment);
        text_title = (TextView)findViewById(R.id.text_title_content_coment);
        image_content = (ImageView)findViewById(R.id.img_thumb_content_comment);
        etComment = (EditText)findViewById(R.id.et_comment_user);
        listComment = (ListView)findViewById(R.id.list_comments);
        btnSubmit = (ActionProcessButton)findViewById(R.id.btn_send_comment);
        btnSubmit.setMode(ActionProcessButton.Mode.ENDLESS);

        setThemes(mFromKanal);

        etComment.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        if(mTitle.length() > 0) {
            text_title.setText(mTitle);
        }

        if(mImageUrl.length() > 0) {
            Picasso.with(VivaApp.getInstance()).load(mImageUrl).into(image_content);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setThemes(String kanal) {
        ColorDrawable colorDrawable = new ColorDrawable();
        if(kanal != null) {
            if(kanal.equalsIgnoreCase("bola")) {
                colorDrawable.setColor(getResources().getColor(R.color.color_bola));
                getActionBar().setBackgroundDrawable(colorDrawable);
                mLayout.setBackgroundResource(R.color.color_bola);
                btnSubmit.setBackgroundResource(R.color.color_bola);
            } else if(kanal.equalsIgnoreCase("vivalife")) {
                colorDrawable.setColor(getResources().getColor(R.color.color_life));
                getActionBar().setBackgroundDrawable(colorDrawable);
                mLayout.setBackgroundResource(R.color.color_life);
                btnSubmit.setBackgroundResource(R.color.color_life);
            } else {
                colorDrawable.setColor(getResources().getColor(R.color.color_news));
                getActionBar().setBackgroundDrawable(colorDrawable);
                mLayout.setBackgroundResource(R.color.color_news);
                btnSubmit.setBackgroundResource(R.color.color_news);
            }
        } else {
            colorDrawable.setColor(getResources().getColor(R.color.header_headline_terbaru_new));
            getActionBar().setBackgroundDrawable(colorDrawable);
            mLayout.setBackgroundResource(R.color.header_headline_terbaru_new);
            btnSubmit.setBackgroundResource(R.color.header_headline_terbaru_new);
        }
        getHeaderActionBar();
    }

    private void getStateUser() {
        VivaApp.getInstance().getDefaultEditor();
        fullname = VivaApp.getInstance().getSharedPreferences(VivaApp.getInstance())
                .getString(Constant.LOGIN_STATES_FULLNAME, "");
        email = VivaApp.getInstance().getSharedPreferences(VivaApp.getInstance())
                .getString(Constant.LOGIN_STATES_EMAIL, "");
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_send_comment) {
            String comments = etComment.getText().toString();
            if((fullname.length() == 0 && email.length() == 0) || (fullname.length() == 0) || (email.length() == 0)) {
                Toast.makeText(VivaApp.getInstance(), R.string.label_validation_for_comment, Toast.LENGTH_SHORT).show();
            } else if(comments.length() < 1) {
                Toast.makeText(VivaApp.getInstance(), R.string.label_validation_for_comment_length, Toast.LENGTH_SHORT).show();
            } else {
                if(isInternetPresent) {
                    userAccount = new UserAccount(mIds, email, fullname, comments, "Android", this);
                    disableView();
                    btnSubmit.setProgress(1);
                    userAccount.sendComment();
                }
            }
        } else if(view.getId() == R.id.et_comment_user) {
            if(fullname.length() == 0 && email.length() == 0) {
                Toast.makeText(VivaApp.getInstance(), R.string.label_validation_for_comment, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void disableView() {
        btnSubmit.setEnabled(false);
        etComment.setEnabled(false);
    }

    private void enableView() {
        btnSubmit.setEnabled(true);
        etComment.setEnabled(true);
    }

    private void getHeaderActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setIcon(new ColorDrawable(getResources().
                getColor(android.R.color.transparent)));
        getActionBar().setTitle("Komentar");
    }

    @Override
    public void onComplete() {
        btnSubmit.setProgress(100);
        Toast.makeText(this, R.string.label_success_post_comment, Toast.LENGTH_SHORT).show();
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onFailed() {
        btnSubmit.setProgress(0);
        enableView();
        Toast.makeText(this, R.string.label_failed_post_comment, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        btnSubmit.setProgress(0);
        enableView();
        Toast.makeText(this, R.string.label_error_post_comment, Toast.LENGTH_SHORT).show();
    }

}
