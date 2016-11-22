package com.cs442.iitc_fall2016_g13.mad_proj.Seller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs442.iitc_fall2016_g13.mad_proj.R;

import java.util.ArrayList;

import static android.view.View.INVISIBLE;

/**
 * Created by lyd on 2016/11/10.
 */

public class SellerOrderDetailActivity extends Activity{

    int selected_order_position;
    String order_id_for_process;
    public static String cust_phone;

    public static ArrayList<String> OneOrderDetail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail);

        TextView detail_textview = (TextView) findViewById(R.id.detail_textview);
        ListView detail_listview = (ListView) findViewById(R.id.detail_listview);
        final Button proceed_button = (Button)findViewById(R.id.proceed_button);
        final Button get_phone_button =(Button)findViewById(R.id.get_phone_button);
        selected_order_position = SellerMainActivity.currentOrder;

        order_id_for_process = SellerMainActivity.pendingOrders.get(selected_order_position).getOrder_id();

        final String order_id = "Order ID: " + SellerMainActivity.pendingOrders.get(selected_order_position).getOrder_id();
        final String customer_id ="Customer ID: " + SellerMainActivity.pendingOrders.get(selected_order_position).getCust_id();
        String menu_list ="Menu list: " + SellerMainActivity.pendingOrders.get(selected_order_position).getMenu_list();
        String Status;
        if(SellerMainActivity.pendingOrders.get(selected_order_position).getStatus().equals("0")){
            Status = "Status: Not Started";
        }
        else if(SellerMainActivity.pendingOrders.get(selected_order_position).getStatus().equals("1")){
            Status = "Status: Cooking";
        }
        else {
            Status = "Status: Finished Cooking";
        }

        OneOrderDetail = new ArrayList<String>();

        OneOrderDetail.add(order_id);
        OneOrderDetail.add(customer_id);
        OneOrderDetail.add(menu_list);
        OneOrderDetail.add(Status);

        final ArrayAdapter<String> aa;

        aa = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                OneOrderDetail);

        detail_listview.setAdapter(aa);
        aa.notifyDataSetChanged();


        new SellerGetPhoneProcess(SellerOrderDetailActivity.this).execute(SellerMainActivity.pendingOrders.get(selected_order_position).getCust_id());

        get_phone_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent call = new Intent(Intent.ACTION_DIAL);
                call.setData(Uri.parse("tel:" + cust_phone));
                startActivity(call);
            }
        });


        proceed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SellerMainActivity.pendingOrders.get(selected_order_position).getStatus().equals("3")){
                    Toast.makeText(getApplicationContext(),"Already finished.",Toast.LENGTH_LONG).show();
                }
                else if(SellerMainActivity.pendingOrders.get(selected_order_position).getStatus().equals("0")){
                    Toast.makeText(getApplicationContext(),"Starting cooking now",Toast.LENGTH_LONG).show();
                    SellerMainActivity.pendingOrders.get(selected_order_position).setStatus("1");
                    SellerMainActivity.orderString.set(selected_order_position,
                            "Order ID: " + SellerMainActivity.pendingOrders.get(selected_order_position).getOrder_id()+"\n"+
                                    "Customer ID: " + SellerMainActivity.pendingOrders.get(selected_order_position).getCust_id()+"\n"+
                                    "Menu List: " + SellerMainActivity.pendingOrders.get(selected_order_position).getMenu_list()+"\n"+
                                    "Status: Cooking");
                    OneOrderDetail.set(3,"Status: Cooking");
                    aa.notifyDataSetChanged();
                    new SellerUpdateOrderStatusProcess(SellerOrderDetailActivity.this).execute(order_id_for_process);
                }
                else if(SellerMainActivity.pendingOrders.get(selected_order_position).getStatus().equals("1")){
                    Toast.makeText(getApplicationContext(),"Finished Cooking",Toast.LENGTH_LONG).show();
                    SellerMainActivity.pendingOrders.get(selected_order_position).setStatus("2");
                    SellerMainActivity.orderString.set(selected_order_position,
                            "Order ID: " + SellerMainActivity.pendingOrders.get(selected_order_position).getOrder_id()+"\n"+
                                    "Customer ID: " + SellerMainActivity.pendingOrders.get(selected_order_position).getCust_id()+"\n"+
                                    "Menu List: " + SellerMainActivity.pendingOrders.get(selected_order_position).getMenu_list()+"\n"+
                                    "Status: Finished Cooking");
                    OneOrderDetail.set(3,"Status: Finished Cooking");
                    aa.notifyDataSetChanged();
                    new SellerUpdateOrderStatusProcess(SellerOrderDetailActivity.this).execute(order_id_for_process);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Order is Taken away!",Toast.LENGTH_LONG).show();
                    SellerMainActivity.pendingOrders.get(selected_order_position).setStatus("3");
                    SellerMainActivity.orderString.set(selected_order_position,
                            "Order ID: " + SellerMainActivity.pendingOrders.get(selected_order_position).getOrder_id()+"\n"+
                                    "Customer ID: " + SellerMainActivity.pendingOrders.get(selected_order_position).getCust_id()+"\n"+
                                    "Menu List: " + SellerMainActivity.pendingOrders.get(selected_order_position).getMenu_list()+"\n"+
                                    "Status: Finished");
                    OneOrderDetail.set(3,"Status: Taken Away");
                    OneOrderDetail.removeAll(OneOrderDetail);
                    aa.notifyDataSetChanged();
                    proceed_button.setVisibility(INVISIBLE);
                    get_phone_button.setVisibility(INVISIBLE);
                    SellerMainActivity.orderString.remove(selected_order_position);
                    SellerMainActivity.pendingOrders.remove(selected_order_position);
                    new SellerUpdateOrderStatusProcess(SellerOrderDetailActivity.this).execute(order_id_for_process);
                }
            }
        });
    }
}
