package com.minardwu.see.activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SendCallback;
import com.facebook.drawee.view.SimpleDraweeView;
import com.minardwu.see.R;
import com.minardwu.see.base.Config;
import com.minardwu.see.entity.User;
import com.minardwu.see.event.GetUserInfoEvent;
import com.minardwu.see.event.SendOrReadNewsEvent;
import com.minardwu.see.net.GetUserInfo;
import com.minardwu.see.net.News;
import com.minardwu.see.net.PushNews;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView iv_back;
    private SearchView searchView;
    private TextView tv_info;
    private SimpleDraweeView iv_avatar;
    private TextView tv_username;
    private Button btn_add;
    private LinearLayout ll_result;

    private User searchUser;

//    private String[] history = {"aaa", "bbb", "ccc", "airsaid"};
//    private ListView listView;
//    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);

        tv_info = (TextView) findViewById(R.id.tv_search_info);
        iv_avatar = (SimpleDraweeView) findViewById(R.id.iv_search_image);
        tv_username = (TextView) findViewById(R.id.tv_search_username);
        btn_add = (Button) findViewById(R.id.btn_search_add);
        ll_result = (LinearLayout) findViewById(R.id.ll_search_result);

        tv_info.setText("众里寻他千百度");
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                News.sendNews(searchUser.getUserid());
            }
        });

        iv_back = (ImageView) findViewById(R.id.iv_toolbar_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        listView = (ListView) findViewById(R.id.lv_search);
//        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, history);
//        listView.setAdapter(adapter);
//        listView.setTextFilterEnabled(true);

        searchView = (SearchView) findViewById(R.id.sv);
        searchView.setQueryHint("输入用户名称搜索");
        searchView.setSubmitButtonEnabled(false);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                btn_add.setEnabled(true);
                btn_add.setText("请求");
                ll_result.setVisibility(View.GONE);
                tv_info.setVisibility(View.VISIBLE);
                if(query.equals(Config.me.getUsername())){
                    tv_info.setText("“哗啦啦啦，天在下雨。” ——陶喆•《找自己》");
                }else if(query.equals(Config.you.getUsername())){
                    tv_info.setText("“远在天边，近在眼前。”");
                }else {
                    GetUserInfo.getUserInfoByUserName(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                if (!TextUtils.isEmpty(newText)){
//                    //mListView.setFilterText(newText);使用这种方法的话会弹出一个PuopWindow
//                    adapter.getFilter().filter(newText.toString());
//                }else{
//                    listView.clearTextFilter();
//                }
                return true;
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetUserInfoEvent(GetUserInfoEvent event){
        searchUser = event.getUser();
        if(searchUser.getUserid().equals("0")){
            ll_result.setVisibility(View.GONE);
            tv_info.setVisibility(View.VISIBLE);
            tv_info.setText("找不到该用户");
        }else {
            tv_info.setVisibility(View.GONE);
            ll_result.setVisibility(View.VISIBLE);
            tv_username.setText(searchUser.getUsername());
            iv_avatar.setImageURI(Uri.parse(searchUser.getAvatar()));
        }
    };


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendOrReadNewsEvent(SendOrReadNewsEvent event){
        if(event.getResult()==1){
            Toast.makeText(SearchActivity.this, "请求发送成功", Toast.LENGTH_SHORT).show();
            btn_add.setEnabled(false);
            btn_add.setText("请求已发送");
            //推送消息
            PushNews.push(searchUser.getUserid());
        } else if(event.getResult()==-1){
            Toast.makeText(SearchActivity.this, "你之前已发送过请求了哦", Toast.LENGTH_SHORT).show();
            btn_add.setEnabled(false);
            btn_add.setText("请求已发送");
        } else {
            Toast.makeText(SearchActivity.this, "请求发送失败", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
