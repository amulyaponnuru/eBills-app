package com.example.ebills_trial1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {
    ListView listView;
    Button addItem;
    EditText uN, pN ,eI ,item, price,quantity;
    Button addData,sendEmail;
    ArrayList<String> list;
    ArrayAdapter<String> adp;
    TextView total;
    FirebaseFirestore object_ff;
    DocumentReference documentReference;
    Integer tot;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        listView = (ListView)findViewById(R.id.listView);
        addItem = (Button)findViewById(R.id.addItem);
        uN = (EditText)findViewById(R.id.uN);
        pN = (EditText)findViewById(R.id.pN);
        eI = (EditText)findViewById(R.id.eI);
        item = (EditText)findViewById(R.id.item);
        price = (EditText)findViewById(R.id.price);
        addData = (Button)findViewById(R.id.addData);
        total = (TextView)findViewById(R.id.total);
        sendEmail = (Button)findViewById(R.id.sendEmail);
        quantity = (EditText)findViewById(R.id.quantity);
        tot = 0;
        total.setText("TOTAL: 0");

        object_ff = FirebaseFirestore.getInstance();

        list = new ArrayList<String>();

        adp = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,list);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!item.getText().toString().isEmpty() && !price.getText().toString().isEmpty() && !quantity.getText().toString().isEmpty()) {
                        String item_text = item.getText().toString();
                        String price_text = price.getText().toString();
                        String quan_text = quantity.getText().toString();
                        Integer cost_of_items =  Integer.parseInt(price_text)*Integer.parseInt(quan_text);
                        String full_text = item_text +" Quantity: "+quan_text + " Price: "+ cost_of_items.toString();
                        tot += cost_of_items;
                        String total_cost = "TOTAL: " + tot.toString();
                        total.setText(total_cost);
                        list.add(full_text);
                        item.getText().clear();
                        price.getText().clear();
                        quantity.getText().clear();

                        listView.setAdapter(adp);
                        adp.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AddActivity.this, "Enter item name and price", Toast.LENGTH_SHORT).show();
                    }
                }
                catch(Exception e){
                    Toast.makeText(AddActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendtheMail();
            }
        });

    }

    public void sendtheMail(){
        String toRecipient = eI.getText().toString();
        String subject = "eBill for the purchased items";
        list.add("Total: "+tot);
        StringBuilder txt_email = new StringBuilder("");
        for(String k:list){
            txt_email.append(k+"\n");
        }
        //String[] textEmail = new String[list.size()];
        //textEmail = list.toArray(textEmail);
        //String text_em = String.join("\n",textEmail);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL,new String[] {toRecipient});
        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
        intent.putExtra(Intent.EXTRA_TEXT,txt_email.toString());
        intent.setType("message/rfc822");

        startActivity(Intent.createChooser(intent, "Choose an email client"));

    }


    public void addValuesToFirebase(View view){
        try{
            if(!uN.getText().toString().isEmpty()
                    && !eI.getText().toString().isEmpty() && !pN.getText().toString().isEmpty()){
                Map<String,String> objectMap = new HashMap<>();
                objectMap.put("Name", uN.getText().toString());
                objectMap.put("Mobile No.", pN.getText().toString());
                objectMap.put("Email", eI.getText().toString());

                for(String l:list){
                    int colon = l.indexOf("Quantity");
                    String i_name = l.substring(0,colon);
                    String cost = l.substring(colon);
                    objectMap.put(i_name,cost);
                }

                objectMap.put("Total: ",tot.toString());

                object_ff.collection("HyderabadBranch")
                        .document(uN.getText().toString())
                        .set(objectMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddActivity.this, "Data is stored", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddActivity.this, "Failed to store data", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else{
                Toast.makeText(this,"Please fill all fields",Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }
}
