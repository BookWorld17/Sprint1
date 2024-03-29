package com.example.bookapp.buyer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.bookapp.adapters.bookAdapter;
import com.example.bookapp.models.Book;
import com.example.bookapp.models.BookCopy;
import com.example.bookapp.seller.booksList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BuyerMainActivity extends BaseActivity {

    ArrayList<Book> bookArrayList = new ArrayList<>();
    public ProgressDialog progressDialog = null;
    RecyclerView listview;
    private SessionManager session;
    private AlertManager alert ;
    private bookAdapter adapter = null;
    private TextView interface_title;
    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.seller_books_list_activity, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);

        session = new SessionManager(BuyerMainActivity.this);
        alert = new AlertManager(BuyerMainActivity.this);


        interface_title = (TextView) findViewById(R.id.interface_title);
        interface_title.setText("List of Books");
        listview = findViewById(R.id.listview);

        listview.setHasFixedSize(true);
        listview.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1  ));


        adapter = new bookAdapter( bookArrayList , BuyerMainActivity.this);
        listview.setAdapter(adapter);

        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.bookAddBtn);
        add.setVisibility(View.GONE);
        loadProperties();
    }



    private void loadProperties() {

        String seller_id = session.getUserID();
        String request = URLs.GET_BOOKS;

        final ProgressDialog loading = ProgressDialog.show(this, "Loading", getString(R.string.waiting), false, false);
        Toast.makeText(this, request, Toast.LENGTH_LONG).show();

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
                params.put("user_id" , seller_id);
                return params;
            }

        };

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void onLoadError() {
        alert.showAlertDialog(BuyerMainActivity.this,"Error","No data Available",false);
    }

    private JSONArray booksJSONlist = null;

    public void doAction(String response){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            booksJSONlist = jsonObject.getJSONArray("result");

            JSONObject jo = booksJSONlist.getJSONObject(0);
            String success = jo.getString("success");

            if(success.equals("1")){
                for(int i=1;i<booksJSONlist.length();i++){
                    jo = booksJSONlist.getJSONObject(i);

                    String imgRUL= jo.getString("image");
                    char slash[] = new char[1];
                    slash[0] = (char)92;
                    imgRUL = URLs.SERVER + imgRUL.replace( new String(slash), "" );


                    String id = jo.getString("id");
                    String bookName = jo.getString("bookName");
                    String authorName = jo.getString("authorName");
                    String publisherName = jo.getString("publisherName");
                    String summary = jo.getString("summary");
                    String seller_id = jo.getString("seller_id");
                    String addDate = jo.getString("addDate");
                    String bookType = jo.getString("bookType");
                    Book b = new Book(id, bookName,  authorName, publisherName, summary, seller_id, addDate, bookType,imgRUL);

                    JSONArray copiesJSONlist = jo.getJSONArray("copies");
                    ArrayList<BookCopy> copiesList = new ArrayList<>();
                    for(int j=0; j<copiesJSONlist.length();j++){
                        JSONObject jsob = copiesJSONlist.getJSONObject(j);
                        String copy_id = jsob.getString("id");
                        String book_id = jsob.getString("book_id");
                        String isbn = jsob.getString("isbn");
                        String price = jsob.getString("price");
                        String numberOfCopies = jsob.getString("numberOfCopies");
                        String numberOfPages = jsob.getString("numberOfPages");
                        String copyAddDate = jsob.getString("addDate");
                        BookCopy copy = new BookCopy(copy_id, book_id,  isbn, price, numberOfCopies, numberOfPages, copyAddDate );
                        copiesList.add(copy);
                    }
                    b.setCopyArrayList(copiesList);
                    bookArrayList.add(b);

                    onLoadSucess();
                }
            }else{
                onLoadError();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onLoadSucess() {

        adapter.notifyDataSetChanged();

    }
}
