package id.co.viva.news.app.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.CommentAdapter;
import id.co.viva.news.app.interfaces.OnCompleteListener;
import id.co.viva.news.app.interfaces.OnDoneListener;
import id.co.viva.news.app.model.Comment;
import id.co.viva.news.app.services.Analytics;
import id.co.viva.news.app.services.UserAccount;

/**
 * Created by reza on 03/12/14.
 */
public class ActComment extends FragmentActivity implements View.OnClickListener,
        OnCompleteListener, OnDoneListener {

    private String mImageUrl;
    private String mTitle;
    private String mIds;
    private TextView text_title;
    private TextView text_label_comment;
    private ImageView image_content;
    private ActionProcessButton btnSubmit;
    private ListView listComment;
    private EditText etComment;
    private String fullname;
    private String email;
    private String userSocialId;
    private String app_id;
    private String mFromKanal;
    private UserAccount userAccount;
    private LinearLayout mLayout;
    private boolean isInternetPresent = false;
    private ArrayList<Comment> commentArrayList;
    private CommentAdapter adapter;
    private Analytics analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        mIds = bundle.getString("article_id");
        mImageUrl = bundle.getString("imageurl");
        mTitle = bundle.getString("title");
        mFromKanal = bundle.getString("type_kanal");

        getAnalytics(mTitle);
        getStateUser();

        setContentView(R.layout.act_comment);

        isInternetPresent = Global.getInstance(this).getConnectionStatus().isConnectingToInternet();

        commentArrayList = new ArrayList<Comment>();
        mLayout = (LinearLayout)findViewById(R.id.background_label_comment);
        text_title = (TextView)findViewById(R.id.text_title_content_coment);
        text_label_comment = (TextView)findViewById(R.id.text_label_comment);
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
            Picasso.with(this).load(mImageUrl).into(image_content);
        }

        if(isInternetPresent) {
            getCommentList();
        }
    }

    private void getAnalytics(String title) {
        analytics = new Analytics(this);
        analytics.getAnalyticByATInternet(Constant.COMMENTED_ARTICLE + "_" + title);
        analytics.getAnalyticByGoogleAnalytic(Constant.COMMENTED_ARTICLE + "_" + title);
    }

    private void getCommentList() {
        userAccount = new UserAccount(this);
        userAccount.getCommentList(fullname, mIds, this);
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
        Global.getInstance(this).getDefaultEditor();
        fullname = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_FULLNAME, "");
        email = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_EMAIL, "");
        userSocialId = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_USER_SOCIAL_ID, "");
        app_id = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_APP_ID, "");
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_send_comment) {
            String comments = etComment.getText().toString();
            if((fullname.length() == 0 && email.length() == 0) || (fullname.length() == 0) || (email.length() == 0)) {
                Toast.makeText(this, R.string.label_validation_for_comment, Toast.LENGTH_SHORT).show();
            } else if(comments.length() < 1) {
                Toast.makeText(this, R.string.label_validation_for_comment_length, Toast.LENGTH_SHORT).show();
            } else {
                if(isInternetPresent) {
                    userAccount = new UserAccount(userSocialId, app_id, mIds, email, fullname, comments, this, ActComment.this);
                    disableView();
                    btnSubmit.setProgress(1);
                    userAccount.sendComment();
                }
            }
        } else if(view.getId() == R.id.et_comment_user) {
            if(fullname.length() == 0 && email.length() == 0) {
                Toast.makeText(this, R.string.label_validation_for_comment, Toast.LENGTH_SHORT).show();
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
    public void onComplete(String message) {
        btnSubmit.setProgress(100);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(getIntent());
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 1000);
    }

    @Override
    public void onDelay(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed(String message) {
        btnSubmit.setProgress(0);
        enableView();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String message) {
        btnSubmit.setProgress(0);
        enableView();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompleteListComment(JSONObject jsonObject) {
        try {
            JSONArray jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
            if (jsonArrayResponses != null) {
                for(int i=0; i<jsonArrayResponses.length(); i++) {
                    JSONObject response = jsonArrayResponses.getJSONObject(i);
                    String id = response.getString(Constant.id);
                    String article_id = response.getString(Constant.article_id);
                    String name = response.getString(Constant.name);
                    String parent_id = response.getString(Constant.parent_id);
                    String comment_text = response.getString(Constant.comment_text);
                    String app_id = response.getString(Constant.app_id);
                    String submitted_date = response.getString(Constant.submitted_date);
                    String status = response.getString(Constant.status);
                    commentArrayList.add(new Comment(id, article_id, name, parent_id, comment_text,
                            app_id, submitted_date, status));
                }
            }
            if (commentArrayList.size() > 0) {
                text_label_comment.setText(commentArrayList.size() + " " + getResources().getString(R.string.label_amount_comments));
                adapter = new CommentAdapter(this, commentArrayList);
                listComment.setAdapter(adapter);
                Constant.setListViewHeightBasedOnChildren(listComment);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void onFailedListComment() {

    }

    @Override
    public void onErrorListComment() {

    }

}
