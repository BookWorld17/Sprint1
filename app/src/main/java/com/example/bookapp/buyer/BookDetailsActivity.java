package com.example.bookapp.buyer;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bookapp.AlertManager;
import com.example.bookapp.LoadImgFromURL;
import com.example.bookapp.R;
import com.example.bookapp.SessionManager;
import com.example.bookapp.URLs;
import com.example.bookapp.adapters.spinnerCopiesAdapter;
import com.example.bookapp.models.Book;
import com.example.bookapp.models.BookCopy;
import com.example.bookapp.seller.BaseActivity;
import com.example.bookapp.seller.booksList;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookDetailsActivity extends BaseActivity {

    private static final String TAG = "viewBookActivity";
    private ImageView bookImage;

    private TextView bookName,authorName,publisherName,summary ,bookTypeTxt;
    private TextView price,numberOfPages,numberOfCopies ;


    AlertManager alert ;
    private Book book;
    private TextView interface_title;
    private ArrayList<BookCopy> copiesList = new ArrayList<>();
    private Spinner copiesSpinner;
    private BookCopy selectedCopy;
    private spinnerCopiesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.book_details_activity, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);


        bookName = (TextView) findViewById(R.id.txtBookName);
        authorName = (TextView) findViewById(R.id.authorName);
        publisherName = (TextView) findViewById(R.id.publisherName);
        summary = (TextView) findViewById(R.id.summary);
        bookTypeTxt = (TextView) findViewById(R.id.bookTypeTxt);
        interface_title = (TextView) findViewById(R.id.interface_title);

        price = (TextView) findViewById(R.id.price);
        numberOfPages = (TextView) findViewById(R.id.numberOfPages);
        numberOfCopies = (TextView) findViewById(R.id.numberOfCopies);



        //link the adapter to the spinner
        copiesSpinner = (Spinner) findViewById(R.id.copiesSpinner);


        adapter = new spinnerCopiesAdapter(BookDetailsActivity.this,
                R.layout.spinner_dropdown_item, android.R.id.text1, copiesList);

        copiesSpinner.setAdapter(adapter);

        alert = new AlertManager(BookDetailsActivity.this);

        Button saveBtn = (Button) findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showConfirmDialog( );
            }
        });

        bookImage = (ImageView) findViewById(R.id.imageView2);

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if(extras != null)
        {
            if(extras.containsKey("Book")){
                book = (Book)extras.getSerializable("Book");
                interface_title.setText(  book.getBookName() + " Details");
                bookName.setText(book.getBookName());
                authorName.setText(book.getAuthorName());
                publisherName.setText(book.getPublisherName());
                summary.setText(book.getSummary());
                bookTypeTxt.setText(book.getBookType());
                new LoadImgFromURL(bookImage,0,0).execute(book.getImage());

                copiesList = book.getCopyArrayList();


                adapter = new spinnerCopiesAdapter(BookDetailsActivity.this,
                        R.layout.spinner_dropdown_item, android.R.id.text1, book.getCopyArrayList());

                copiesSpinner.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                selectedCopy = copiesList.get(0);
                price.setText(selectedCopy.getPrice());
                numberOfCopies.setText(selectedCopy.getNumberOfCopies());
                numberOfPages.setText(selectedCopy.getNumberOfPages());

                if(extras.containsKey("orderBtn")){
                    if(extras.getString("orderBtn").equals("hide")){
                        saveBtn.setVisibility(View.GONE);
                    }
                }
            }
        }

        copiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCopy = copiesList.get(i);
                price.setText(selectedCopy.getPrice());
                numberOfCopies.setText(selectedCopy.getNumberOfCopies());
                numberOfPages.setText(selectedCopy.getNumberOfPages());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(BookDetailsActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }



    private void showConfirmDialog( ){
        final Dialog dialog = new Dialog(BookDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.order_dialog_form);
        Button confirmBtn = dialog.findViewById(R.id.confirmBtn);
        TextView orderMsg = dialog.findViewById(R.id.orderMsg);
        EditText qty = dialog.findViewById(R.id.qty);
        EditText address = dialog.findViewById(R.id.deliveryAddress);
        TextView total = dialog.findViewById(R.id.total);


        Spinner deliveryOption = (Spinner)dialog. findViewById(R.id.deliveryOption);

        orderMsg.setText("You ordered ["+book.getBookName()+"], " +
                "Version/ISBN ["+selectedCopy.getIsbn()+"] " +
                "whith price ("+selectedCopy.getPrice()+") and available qty is "+selectedCopy.getNumberOfCopies());

        int qtyNum = Integer.parseInt(qty.getText().toString());
        int avaQty = Integer.parseInt(selectedCopy.getNumberOfCopies());
        total.setText(Double.toString(qtyNum * Double.parseDouble(selectedCopy.getPrice())));
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(qty.getText().toString().equals(""))
                {
                    Toast.makeText( getApplicationContext(), "You must set the quantity", Toast.LENGTH_LONG).show();
                }
                    if(address.getText().toString().equals("")){
                    Toast.makeText( getApplicationContext(), "You must set the Address", Toast.LENGTH_LONG).show();
                }
                else {
                    int qtyNum = Integer.parseInt(qty.getText().toString());
                    if(qtyNum<1 || qtyNum > avaQty){
                        Toast.makeText( getApplicationContext(), "Quantity must be  between 1 and "+avaQty, Toast.LENGTH_LONG).show();
                    }
                    else {
                        String dOption = deliveryOption.getSelectedItem().toString();
                        String addressO = address.getText().toString();
                        doOrder( dialog, qty.getText().toString(), dOption,  addressO);
                        Toast.makeText(getApplicationContext(), "Submitting Order...", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        qty.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                calculate(total, s);
            }
        });
        dialog.show();
    }

    private void calculate(TextView total, CharSequence s){
        try {
            int qtyNum = Integer.parseInt(s.toString());
            total.setText(Double.toString(qtyNum * Double.parseDouble(selectedCopy.getPrice())));
        }catch (Exception e){}
    }

    private void doOrder(Dialog dialog, String qtyNum, String option, String address) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.SET_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(dialog != null)
                            dialog.dismiss();
                        JSONObject jsonObject = null;
                        try {
                            System.out.println(response);
                            jsonObject = new JSONObject(response);
                            JSONArray result = jsonObject.getJSONArray("result");

                            JSONObject jo = result.getJSONObject(0);
                            String success = jo.getString("success");

                            if(success.equals("1")){
                                String msg = "Your order has been submitted successfully";
                                alert.showAlertDialog(BookDetailsActivity.this,"Success",msg, true);
                            }else{
                                String msg = "You have already ordered this book";
                                alert.showAlertDialog(BookDetailsActivity.this,"Error",msg, false);
                            }
                        } catch (JSONException e) {
                            alert.showAlertDialog(BookDetailsActivity.this,"Error","Couldn't submit your order", false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(BookDetailsActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("book_copy_id" , selectedCopy.getID());
                params.put("user_id" , session.getUserID());
                params.put("qty" , qtyNum);
                params.put("deliveryOption" , option);
                params.put("deliveryAddress" , address);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
