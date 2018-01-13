package com.findprivy.privyparser.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.findprivy.privyparser.R;
import com.findprivy.privyparser.adapter.RecyclerItemClickListener;
import com.findprivy.privyparser.adapter.SMSinfoAdapter;
import com.findprivy.privyparser.model.SMSinfo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity
{

    FloatingActionButton fab;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    SMSinfoAdapter smsInfoAdapter;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        realm = Realm.getDefaultInstance();

        setUpViews();
        addListeners();

        //Requesting Runtime Permission for Reading SMS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 255);


    }

    void setUpViews()
    {
        //Basic binding views to particular objects
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        //Setting up the layout manager for the Recycler View to inflate
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Setting up the List, adapter and the binding it to the recycler view
        smsInfoAdapter = new SMSinfoAdapter(realm.where(SMSinfo.class).findAll());
        recyclerView.setAdapter(smsInfoAdapter);

    }

    void addListeners()
    {



        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                readSmsData();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_clear:
            {
                realm.beginTransaction();
                realm.delete(SMSinfo.class);
                realm.commitTransaction();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void readSmsData()
    {

        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"}, null, null, null);
        cursor.moveToFirst();
        while (cursor.moveToNext())
        {
            String address = cursor.getString(1);
            String body = cursor.getString(3);

            //For cross checking in the logcat
            System.out.println("Mobile number: " + address);
            System.out.println("SMS Text: " + body);

            //Checking if it is a bank transaction
            if (checkIfBankTransaction(body))
            {
                final SMSinfo newSmsInfo = new SMSinfo(address, body);
                try
                {
                    realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction()
                    {
                        @Override
                        public void execute(Realm realm)
                        {
                            realm.insertOrUpdate(newSmsInfo);
                        }
                    });
                } finally
                {
                    if (realm != null)
                    {
                        realm.close();
                    }
                }
            }


        }

        cursor.close();
    }

    private boolean checkIfBankTransaction(String messageBody)
    {
        Pattern amountPattern = Pattern.compile("INR\\s?([\\d,]+).(\\d\\d)|Rs[.\\s]+([\\d,]+).(\\d\\d)");
        Pattern merchantPattern = Pattern.compile("at ([\\w\\s,]+)|Info:\\s?([\\w*]+)|towards ([\\w]+\\s?[\\w]+)");
        Pattern datePattern = Pattern.compile("on (\\d{1,2}-\\w{3,4}-\\d{0,4}[\\d:]+)|on (\\d{0,4}-[\\w\\d]{2}-\\d{1,2}[\\d:]+)");
        Pattern successPattern = Pattern.compile("^((?!declined|failed|could not).)*$");

        Matcher amountMatcher = amountPattern.matcher(messageBody);
        Matcher merchantMatcher = merchantPattern.matcher(messageBody);
        Matcher dateMatcher = datePattern.matcher(messageBody);
        Matcher successMatcher = successPattern.matcher(messageBody);

        if (amountMatcher.find() || merchantMatcher.find() || !getBankFromRawSender(messageBody).equals("?"))
            return true;
        else
            return false;
    }

    private String getBankFromRawSender(String rawSender)
    {
        String bank;
        //Every Bank SMS sender has it's short specified name.
        if (rawSender.toUpperCase().contains("CITI")) bank = "CITI";
        else if (rawSender.toUpperCase().contains("HDFC")) bank = "HDFC";
        else if (rawSender.toUpperCase().contains("ICICI")) bank = "ICICI";
        else bank = "?";
        return bank;
    }
}
