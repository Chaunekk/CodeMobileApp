package com.example.adminbookingapp.Admin;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.adminbookingapp.Model.Khachsan;
import com.example.adminbookingapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import kr.co.prnd.readmore.ReadMoreTextView;

public class DetailHotelActivity extends AppCompatActivity {

    Khachsan khachsan;
    TextView txttenks, txtdiachi, txtgia, toolbartenks;
    ReadMoreTextView txtmota;
    ImageView img, img2, img3, img4;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FusedLocationProviderClient fusedLocationProviderClient;
    Toolbar toolbar;
    MenuItem menuItem;
    DatabaseReference reference;
    List<Khachsan> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailhotel);

        //toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_detail);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        toolbartenks = findViewById(R.id.toolbar_tenks);
        //end toolbar

        auth = FirebaseAuth.getInstance();
        txttenks = findViewById(R.id.txtTenks);
        txtdiachi = findViewById(R.id.txtDiachi);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        txtgia = findViewById(R.id.txtGia);
        txtmota = findViewById(R.id.txtMoto);
        img = findViewById(R.id.img);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);

        Intent intent = getIntent();
        khachsan = (Khachsan) intent.getSerializableExtra("clickdetail");

        txttenks.setText(khachsan.getTenks());
        txtdiachi.setText(khachsan.getDiachiCT());

        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        String get_gia = currencyVN.format(Integer.parseInt(khachsan.getGia()));

        txtgia.setText(get_gia);
        txtmota.setText(khachsan.getMota());

        Picasso.get().load(khachsan.getHinh()).fit().centerCrop().into(img);
        Picasso.get().load(khachsan.getHinh2()).fit().centerCrop().into(img2);
        Picasso.get().load(khachsan.getHinh3()).fit().centerCrop().into(img3);
        Picasso.get().load(khachsan.getHinh4()).fit().centerCrop().into(img4);
        showname();
    }

    private void onStatus() {
        String tenDiaDiem = txttenks.getText().toString();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Hotel");
        reference.child(tenDiaDiem).child("trangthai").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showToast("Đã mở hoạt động của khách sạn!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Mở hoạt động của khách sạn thất bại!");
            }
        });
    }

    private void offStatus() {
        String tenDiaDiem = txttenks.getText().toString();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Hotel");
        reference.child(tenDiaDiem).child("trangthai").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showToast("Đã tắt hoạt động của khách sạn!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Tắt hoạt động của khách sạn thất bại!");
            }
        });
    }
    public void showname() {
        String tenks = khachsan.getTenks();
        reference = FirebaseDatabase.getInstance().getReference("Hotel");
        reference.child(tenks).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                toolbartenks.setText(tenks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toolbartenks.setText("");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Boolean tt = khachsan.getTrangthai();
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_status:
                if (tt.equals(true)) {
                    offStatus();
                    finish();
                } else {
                    onStatus();
                    finish();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_status, menu);
        menuItem = menu.findItem(R.id.menu_status);

        Boolean tt = khachsan.getTrangthai();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Hotel");
        String tenDiaDiem = txttenks.getText().toString();
        reference.child(tenDiaDiem).child("trangthai").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (tt) {
                            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_baseline_circle_24_green);
                            menuItem.setIcon(myDrawable);
                        } else {
                            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_baseline_circle_24);
                            menuItem.setIcon(myDrawable);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return super.onCreateOptionsMenu(menu);
    }

    private void showToast(String mess) {
        Toast.makeText(this, mess, Toast.LENGTH_LONG).show();
    }
}