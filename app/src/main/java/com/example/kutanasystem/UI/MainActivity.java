package com.example.kutanasystem.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.kutanasystem.Adapters.GridproductAdapter;
import com.example.kutanasystem.Adapters.My_Adapter;
import com.example.kutanasystem.Model.HorizontalProductModel;
import com.example.kutanasystem.Model.Offers;
import com.example.kutanasystem.Model.favouritesClass;
import com.example.kutanasystem.Model.model;
import com.example.kutanasystem.Model.user;
import com.example.kutanasystem.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar mToolBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mtoggle;
    private CircleImageView image;
    private TextView mperson_name;
    private FirebaseAuth mAuth;
    private String Uid, name, photo;
    private FirebaseUser CurrentUser;
    private NavigationView navigationView;
    private ViewPager pager;
    private My_Adapter adapter;
    private List<model> models;
    private DatabaseReference m;
    private View mnavigationview;
    private static List<favouritesClass> favourites;
    //Custom Xml Views (cart Icon)
    private RelativeLayout CustomCartContainer;
    private TextView PageTitle;
    private TextView CustomCartNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();
        Uid = CurrentUser.getUid();

        navigationView = findViewById(R.id.navegation_view);
        navigationView.setNavigationItemSelectedListener(this);
        mnavigationview = navigationView.getHeaderView(0);
        mperson_name = mnavigationview.findViewById(R.id.persname);
        image = mnavigationview.findViewById(R.id.circimage);
        drawerLayout = findViewById(R.id.drawer);

        mToolBar = findViewById(R.id.main_TooBar);
        setSupportActionBar(mToolBar);
        mtoggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(mtoggle);
        mtoggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        //Retrieve Header View User Data
        Navigation_view_header_data();

        //Retrieve Favourites
        Retrieve_fav();

        // FirstView
        Retrieve_Electroncis();

        // SecondView
        Retrieve_Fruits();

        //Third View
        Retrieve_Meats();

        // Fourth View
        Retrieve_Vegatables();

        // OFFERS
        Retrieve_offers();

        //Refresh CartIcon
        showCartIcon();

        //to check if the total price is zero or not
        HandleTotalPriceToZeroIfNotExist();
    }

    public void Retrieve_Electroncis() {
        LinearLayout mylayout = (LinearLayout) findViewById(R.id.my_cardView);
        LayoutInflater inflater = getLayoutInflater();
        inflater.inflate(R.layout.grid_product_layout, mylayout, false);
        TextView gridlayouttitle = mylayout.findViewById(R.id.grid_product_layout_textview);
        gridlayouttitle.setText("Photography and Tech");
        Button GridLayoutViewBtn = mylayout.findViewById(R.id.grid_button_layout_viewall_button);
        final GridView gv = mylayout.findViewById(R.id.product_layout_gridview);
        final List<HorizontalProductModel> lastmodels = new ArrayList<>();
        lastmodels.add(new HorizontalProductModel("https://407261-1281864-raikfcquaxqncofqfm.stackpathdns.com/wp-content/uploads/2019/10/Nikon-D3500-DSLR-Camera-with-18-55mm-Lens.jpg",
                "Camera",
                "50,000",
                false,
                ""));
        final GridproductAdapter my_adapter;
        my_adapter = new GridproductAdapter(lastmodels, favourites,MainActivity.this);
        m = FirebaseDatabase.getInstance().getReference().child("product").child("Electronics");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    user my_user = new user();
                    my_user = ds.getValue(user.class);
                    my_user.setCategory(ds.getKey().toString());
                    lastmodels.add(new HorizontalProductModel(my_user.getImage(), my_user.getCategory(), my_user.getPrice(), false,my_user.getExpired()));
                }
                gv.setAdapter(my_adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        m.addListenerForSingleValueEvent(eventListener);

        GridLayoutViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CategoryActivity.class);
                intent.putExtra("Category Name","Photography and Tech");
                startActivity(intent);
            }
        });

    }

    public void Retrieve_fav() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("favourites")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        favourites = new ArrayList<>();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    favouritesClass fav = new favouritesClass();
                    fav = ds.getValue(favouritesClass.class);
                    favourites.add(fav);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        ref.addListenerForSingleValueEvent(eventListener);
    }

    public void Retrieve_Fruits() {
        LinearLayout mylayout = (LinearLayout) findViewById(R.id.my_cardView2);
        LayoutInflater inflater = getLayoutInflater();
        inflater.inflate(R.layout.grid_product_layout, mylayout, false);
        TextView gridlayouttitle = mylayout.findViewById(R.id.grid_product_layout_textview);
        gridlayouttitle.setText("Food and Drinks");
        Button GridLayoutViewBtn = mylayout.findViewById(R.id.grid_button_layout_viewall_button);
        final GridView gv = mylayout.findViewById(R.id.product_layout_gridview);
        final List<HorizontalProductModel> lastmodels = new ArrayList<>();
        lastmodels.add(new HorizontalProductModel("https://m.media-amazon.com/images/I/51uJdR27f7L.jpg",
                "Coke",
                "200",
                false,
                ""));
        m = FirebaseDatabase.getInstance().getReference().child("product").child("Fruits");
        ValueEventListener eventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    user my_user = new user();
                    my_user = ds.getValue(user.class);
                    my_user.setCategory(ds.getKey().toString());
                    lastmodels.add(new HorizontalProductModel(my_user.getImage(), my_user.getCategory(), my_user.getPrice(), false,my_user.getExpired()));
                }
                GridproductAdapter my_adapter = new GridproductAdapter(lastmodels, favourites,MainActivity.this);
                gv.setAdapter(my_adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        m.addListenerForSingleValueEvent(eventListener);

        GridLayoutViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CategoryActivity.class);
                intent.putExtra("Category Name","Food and Drinks");
                startActivity(intent);
            }
        });
    }

    public void Retrieve_Meats() {
        LinearLayout mylayout = (LinearLayout) findViewById(R.id.my_cardView3);
        LayoutInflater inflater = getLayoutInflater();
        inflater.inflate(R.layout.grid_product_layout, mylayout, false);
        TextView gridlayouttitle = mylayout.findViewById(R.id.grid_product_layout_textview);
        gridlayouttitle.setText("Party Extras");
        Button GridLayoutViewBtn = mylayout.findViewById(R.id.grid_button_layout_viewall_button);
        final GridView gv = mylayout.findViewById(R.id.product_layout_gridview);
        final List<HorizontalProductModel> lastmodels = new ArrayList<>();
        lastmodels.add(new HorizontalProductModel("https://m.media-amazon.com/images/I/91HnLmz7bsL._AC_SL1500_.jpg",
                "JBL",
                "20,000",
                false,
                ""));
        final GridproductAdapter my_adapter;
        my_adapter = new GridproductAdapter(lastmodels, favourites,MainActivity.this);
        m = FirebaseDatabase.getInstance().getReference().child("product").child("Meats");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    user my_user = new user();
                    my_user = ds.getValue(user.class);
                    my_user.setCategory(ds.getKey().toString());
                    lastmodels.add(new HorizontalProductModel(my_user.getImage(), my_user.getCategory(), my_user.getPrice(), false,my_user.getExpired()));
                }
                gv.setAdapter(my_adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        m.addListenerForSingleValueEvent(eventListener);

        GridLayoutViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CategoryActivity.class);
                intent.putExtra("Category Name","Party Extras");
                startActivity(intent);
            }
        });

    }

    public void Retrieve_Vegatables() {
        LinearLayout mylayout = (LinearLayout) findViewById(R.id.my_cardView4);
        LayoutInflater inflater = getLayoutInflater();
        inflater.inflate(R.layout.grid_product_layout, mylayout, false);
        TextView gridlayouttitle = mylayout.findViewById(R.id.grid_product_layout_textview);
        gridlayouttitle.setText("Lighting and Sound");
        Button GridLayoutViewBtn = mylayout.findViewById(R.id.grid_button_layout_viewall_button);
        final GridView gv = mylayout.findViewById(R.id.product_layout_gridview);
        final List<HorizontalProductModel> lastmodels = new ArrayList<>();
        lastmodels.add(new HorizontalProductModel("https://ak.picdn.net/shutterstock/videos/1024370081/thumb/1.jpg",
                "Neon",
                "50,000",
                false,
                ""));
        final GridproductAdapter my_adapter;
        my_adapter = new GridproductAdapter(lastmodels, favourites,MainActivity.this);
        m = FirebaseDatabase.getInstance().getReference().child("product").child("Vegetables");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    user my_user = new user();
                    my_user = ds.getValue(user.class);
                    my_user.setCategory(ds.getKey().toString());
                    lastmodels.add(new HorizontalProductModel(my_user.getImage(), my_user.getCategory(),my_user.getPrice(), false,my_user.getExpired()));
                }
                gv.setAdapter(my_adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        m.addListenerForSingleValueEvent(eventListener);

        GridLayoutViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CategoryActivity.class);
                intent.putExtra("Category Name","Lighting and Sound");
                startActivity(intent);
            }
        });
    }

    public void Retrieve_offers() {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference m = root.child("offers");
        models = new ArrayList<>();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Offers offer = new Offers();
                    offer = ds.getValue(Offers.class);
                    offer.setTitle(ds.getKey().toString());
                    models.add(new model(offer.getImg(), offer.getTitle(), offer.getDescribtion()));
                    adapter = new My_Adapter(models, MainActivity.this);
                    pager = findViewById(R.id.cardview);
                    pager.setAdapter((PagerAdapter) adapter);
                    pager.setPadding(130, 0, 130, 0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        m.addListenerForSingleValueEvent(eventListener);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mtoggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Profile) {
            startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
        }
        else if(id == R.id.favourites){
            startActivity(new Intent(MainActivity.this, favourites_activity.class));
        }
        else if(id == R.id.Cart){
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        }
        else if(id == R.id.MyOrders){
            startActivity(new Intent(MainActivity.this, OrderActivity.class));
        }
        else if(id==R.id.fruits){
            Intent intent =new Intent(MainActivity.this,CategoryActivity.class);
            intent.putExtra("Category Name","Food and Drinks");
            startActivity(intent);
        }
        else if(id==R.id.vegetables){
            Intent intent =new Intent(MainActivity.this,CategoryActivity.class);
            intent.putExtra("Category Name","Lighting and Sound");
            startActivity(intent);
        }
        else if(id==R.id.meats){
            Intent intent =new Intent(MainActivity.this,CategoryActivity.class);
            intent.putExtra("Category Name","Party Extras");
            startActivity(intent);
        }
        else if(id==R.id.electronics){
            Intent intent =new Intent(MainActivity.this,CategoryActivity.class);
            intent.putExtra("Category Name","Photography and Tech");
            startActivity(intent);
        }
        else if (id == R.id.Logout) {
            CheckLogout();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void CheckLogout(){
        AlertDialog.Builder checkAlert = new AlertDialog.Builder(MainActivity.this);
        checkAlert.setMessage("Do you want to Logout?")
                .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(MainActivity.this,loginActivity.class);
                startActivity(intent);
                finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = checkAlert.create();
        alert.setTitle("LogOut");
        alert.show();

    }


    @Override
    public void onStop() {
        super.onStop();
    }

    public void Navigation_view_header_data() {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference m = root.child("users").child(Uid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    name = dataSnapshot.child("Name").getValue().toString();
                    photo = dataSnapshot.child("Image").getValue().toString();
                    if (photo.equals("default")) {
                        Picasso.get().load(R.drawable.profile).into(image);
                    } else
                        Picasso.get().load(photo).placeholder(R.drawable.profile).into(image);
                    mperson_name.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        m.addListenerForSingleValueEvent(eventListener);
    }


    private void showCartIcon(){
        //toolbar & cartIcon
        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view= inflater.inflate(R.layout.main2_toolbar,null);
        actionBar.setCustomView(view);

        //************custom action items xml**********************
        CustomCartContainer = (RelativeLayout)findViewById(R.id.CustomCartIconContainer);
        PageTitle =(TextView)findViewById(R.id.PageTitle);
        CustomCartNumber = (TextView)findViewById(R.id.CustomCartNumber);

        PageTitle.setText("Kutana Planner");
        setNumberOfItemsInCartIcon();

        CustomCartContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
        });

    }


    private void setNumberOfItemsInCartIcon(){
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference m = root.child("cart").child(Uid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if(dataSnapshot.getChildrenCount()==1){
                        CustomCartNumber.setVisibility(View.GONE);
                    }
                    else {
                        CustomCartNumber.setVisibility(View.VISIBLE);
                        CustomCartNumber.setText(String.valueOf(dataSnapshot.getChildrenCount()-1));
                    }
                }
                else{
                    CustomCartNumber.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);
    }


    private void HandleTotalPriceToZeroIfNotExist(){
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference m = root.child("cart").child(Uid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    FirebaseDatabase.getInstance().getReference().child("cart").child(Uid).child("totalPrice").setValue("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);

    }

}
