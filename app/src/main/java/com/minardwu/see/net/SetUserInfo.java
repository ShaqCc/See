package com.minardwu.see.net;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.minardwu.see.activity.SettingActivity;
import com.minardwu.see.base.Config;
import com.minardwu.see.base.MyApplication;
import com.minardwu.see.event.SetUserInfoEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/6/26.
 */
public class SetUserInfo {

    public static void setAvatar(final String path) {
        final int[] result = new int[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
               AVFile file = null;
                try {
                    file = AVFile.withAbsoluteLocalPath(System.currentTimeMillis() + ".jpg", path);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                AVUser.getCurrentUser().put("avatar", file);
                final AVFile finalFile = file;
                AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if(e==null){
                            Log.d("setAvatar", finalFile.getUrl());
                            Config.me.setAvatar(finalFile.getUrl());
                            Log.d("setAvatar","success");
                            result[0] = 1;
                        }else{
                            result[0] = -1;
                            Log.d("setAvatar","fail");
                            Log.d("setAvatar",e.getMessage());
                        }
                        EventBus.getDefault().post(new SetUserInfoEvent(0,result[0]));
                    }
                });

            }
        }).start();
    }

    public static void setUsername(final String username) {
        final int[] result = new int[1];
        AVUser.getCurrentUser().put("username",username);
        AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e==null){
                    result[0] = 1;
                    Log.d("setUsername","success");
                    Config.me.setUsername(username);
                }else {
                    Log.d("setUsername","fail");
                    Log.d("setUsername",e.getMessage());
                    if(e.getMessage().equals("{\"code\":202,\"error\":\"Username has already been taken\"}")){
                        result[0] = -1;
                    }else{
                        result[0] = -2;
                    }
                }
                EventBus.getDefault().post(new SetUserInfoEvent(1,result[0]));
            }
        });
    }

    public static void setPassword(String oldpsw, String newpsw) {
        final int[] result = new int[1];
        AVUser user = AVUser.getCurrentUser();
        user.updatePasswordInBackground(oldpsw, newpsw, new UpdatePasswordCallback() {
            @Override
            public void done(AVException e) {
                if(e==null){
                    Log.d("setPassword","success");
                    result[0] = 1;
                }else{
                    Log.d("setPassword","fail");
                    Log.d("setPassword",e.getMessage());
                    if(e.getMessage().equals("{\"code\":210,\"error\":\"The username and password mismatch.\"}")){
                        result[0] = -1;
                    }else {
                        result[0] = -2;
                    }
                }
                EventBus.getDefault().post(new SetUserInfoEvent(2,result[0]));
            }
        });
    }

    public static void setSex(final int sex) {
        final int[] result = new int[1];
        AVUser.getCurrentUser().put("sex",sex);
        AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e==null){
                    Log.d("setSex","success");
                    Config.me.setSex(sex);
                    result[0] = 1;
                }else {
                    result[0] = -1;
                    Log.d("setSex","fail");
                    Log.d("setSex",e.getMessage());
                }
                EventBus.getDefault().post(new SetUserInfoEvent(3,result[0]));
            }
        });
    }

}
