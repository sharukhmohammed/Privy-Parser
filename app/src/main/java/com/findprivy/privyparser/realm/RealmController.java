package com.findprivy.privyparser.realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.findprivy.privyparser.model.SMSinfo;

import io.realm.Realm;
import io.realm.RealmResults;


public class RealmController
{

    //Basics functions to control Realm instance

    private static RealmController instance;
    private final Realm realm;

    private RealmController(Application application)
    {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment)
    {

        if (instance == null)
        {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity)
    {

        if (instance == null)
        {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application)
    {

        if (instance == null)
        {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance()
    {

        return instance;
    }

    public Realm getRealm()
    {
        return realm;
    }



    //Refresh the realm instance
    public void refresh()
    {
        realm.refresh();
    }

    //clear all objects from SMSinfo.class
    public void clearAll()
    {
        realm.beginTransaction();
        realm.delete(SMSinfo.class);
        realm.commitTransaction();
    }

    //find all objects in the Book.class
    private RealmResults<SMSinfo> getSMSinfos()
    {
        return realm.where(SMSinfo.class).findAll();
    }

    //query a single item with the given id
    public SMSinfo getSMSinfo(String id)
    {
        return realm.where(SMSinfo.class).equalTo("id", id).findFirst();
    }

    //check if SMSinfo.class is empty
    public boolean hasSMSinfos()
    {
        return getSMSinfos().isEmpty();
    }
}

