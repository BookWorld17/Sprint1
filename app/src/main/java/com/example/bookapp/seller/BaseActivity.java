package com.example.bookapp.seller;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bookapp.AlertManager;
import com.example.bookapp.R;
import com.example.bookapp.SessionManager;
import com.example.bookapp.URLs;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class BaseActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    public SessionManager session;
    private JSONObject jsonObject;
    private AlertManager alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);

        setContentView(R.layout.activity_base);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        alert = new AlertManager(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                final String appPackageName = getPackageName();

                switch (item.getItemId()) {

                    case R.id.home:
                        Intent i = new Intent(getApplicationContext(), SellerMainActivity.class);
                        startActivity( i);
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.add_item:
                        startActivity(new Intent(getApplicationContext(), addBookActivity.class));
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;


                    case R.id.list_items:
                        startActivity(new Intent(getApplicationContext(), booksList.class));
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;






                    case R.id.logout:
                        session.logoutUser();
                        drawerLayout.closeDrawers();
                        finish();
                        break;

                }
                return false;
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        actionBarDrawerToggle.syncState();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransitionExit();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    protected void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }




/*










    public void loadItems(String request) {

        String user_id = session.getUserID();

        final ProgressDialog loading = ProgressDialog.show(this, "Loading", getString(R.string.waiting), false, false);
        //Toast.makeText(this, request, Toast.LENGTH_LONG).show();

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        doAction(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(register.this,error.toString(),Toast.LENGTH_LONG).show();
                        loading.dismiss();
                        onLoadError();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }

        };

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void onLoadError() {
        alert.showAlertDialog(this,"Error","No data Available",false);
    }

    private JSONArray users = null;

    ArrayList<Item> itemArrayList = new ArrayList<>();
    public ProgressDialog progressDialog = null;

    public void doAction(String response){
        JSONObject jsonObject = null;
        try {
            System.out.println(response);
            jsonObject = new JSONObject(response);
            users = jsonObject.getJSONArray("result");

            JSONObject jo = users.getJSONObject(0);
            String success = jo.getString("success");

            if(success.equals("1")){
                for(int i=1;i<users.length();i++){
                    jo = users.getJSONObject(i);

                    String imgRUL= jo.getString("img");
                    char slash[] = new char[1];
                    slash[0] = (char)92;
                    imgRUL = URLs.SERVER + imgRUL.replace( new String(slash), "" );

                    String id = jo.getString("id");
                    String name = jo.getString("name");
                    String description = jo.getString("description");
                    String item_type = jo.getString("item_type");
                    String category_id = jo.getString("category_id");
                    String status = jo.getString("status");
                    String location = jo.getString("location");
                    String add_date = jo.getString("add_date");
                    String user_id = jo.getString("user_id");

                    itemArrayList.add(
                            new Item(
                                    id,
                                    category_id,
                                    name,
                                    imgRUL,
                                    description,
                                    item_type,
                                    status,
                                    location,
                                    user_id,
                                    add_date
                            ));


                    onLoadSucess();
                }
            }else{
                onLoadError();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    itemAdapter adapter = null;
    public void setItemAdapter(itemAdapter adapter, ArrayList<Item> itemArrayList){
        this.adapter = adapter;
        this.itemArrayList = itemArrayList;
    }

    private void onLoadSucess() {

        adapter.notifyDataSetChanged();

    }


    public void deleteItem(Item item) {
        alert.showMessageOKCancel("Are you sure to delete this item?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(DialogInterface.BUTTON_POSITIVE == i){
                    doDelettion(item);
                }
            }
        });
    }

    private void doDelettion(Item item){
        String user_id = session.getUserID();
        String request = URLS.DELETE_ITEM;

        final ProgressDialog loading = ProgressDialog.show(this, "Loading", getString(R.string.waiting), false, false);
        //Toast.makeText(this, request, Toast.LENGTH_LONG).show();

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        try {
                            System.out.println(response);
                            jsonObject = new JSONObject(response);
                            users = jsonObject.getJSONArray("result");

                            JSONObject jo = users.getJSONObject(0);
                            String success = jo.getString("success");

                            if(success.equals("1")){

                                alert.showAlertDialog( getApplicationContext(),
                                        "",
                                        getString(R.string.delete_success),
                                        true);
                                itemArrayList.remove(item);
                                onLoadSucess();
                            }else{
                                // email / password doesn't match
                                alert.showAlertDialog( getApplicationContext(),
                                        getString(R.string.error),
                                        getString(R.string.couldnt_delete),
                                        false);
                            }
                        } catch (JSONException e) {
                            alert.showAlertDialog( getApplicationContext(),
                                    getString(R.string.error),
                                    getString(R.string.couldnt_delete),
                                    false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(register.this,error.toString(),Toast.LENGTH_LONG).show();
                        loading.dismiss();
                        onLoadError();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                params.put("user_id" , user_id);
                params.put("item_id" , item.getID());

                return params;
            }

        };

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void orderItem(Item item) {
        alert.showMessageOKCancel("Are you sure to get this order?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(DialogInterface.BUTTON_POSITIVE == i){
                    doOrder(item);
                }
            }
        });
    }

    private void doOrder(Item item){
        String user_id = session.getUserID();
        String request = URLS.ORDER_ITEM;

        final ProgressDialog loading = ProgressDialog.show(this, "Loading", getString(R.string.waiting), false, false);
        //Toast.makeText(this, request, Toast.LENGTH_LONG).show();

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        try {
                            System.out.println(response);
                            jsonObject = new JSONObject(response);
                            users = jsonObject.getJSONArray("result");

                            JSONObject jo = users.getJSONObject(0);
                            String success = jo.getString("success");

                            if(success.equals("1")){

                                alert.showAlertDialog( BaseActivity.this,
                                        "",
                                        getString(R.string.order_success),
                                        true);
                            }else{
                                // email / password doesn't match
                                alert.showAlertDialog( BaseActivity.this,
                                        getString(R.string.error),
                                        getString(R.string.couldnt_order),
                                        false);
                            }
                        } catch (JSONException e) {
                            alert.showAlertDialog( BaseActivity.this,
                                    getString(R.string.error),
                                    getString(R.string.couldnt_order),
                                    false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(register.this,error.toString(),Toast.LENGTH_LONG).show();
                        loading.dismiss();
                        onLoadError();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                params.put("user_id" , user_id);
                params.put("item_id" , item.getID());

                return params;
            }

        };

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

 */
}