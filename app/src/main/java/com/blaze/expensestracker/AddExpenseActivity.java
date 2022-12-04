package com.blaze.expensestracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.blaze.expensestracker.databinding.ActivityAddExpenseBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.UUID;

public class AddExpenseActivity extends AppCompatActivity {
    ActivityAddExpenseBinding binding;
    private String type;
    private ExpenseModel expenseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        type=getIntent().getStringExtra("type");
        expenseModel=(ExpenseModel) getIntent().getSerializableExtra("model");

        if(type==null){
            type=expenseModel.getType();
            binding.Amount.setText(String.valueOf(expenseModel.getAmount()));
            binding.category.setText(expenseModel.getCategory());
            binding.Note.setText(expenseModel.getNote());

        }

        if (type.equals("Income")){
            binding.incomeRadio.setChecked(true);
        }else{
            binding.expenseRadio.setChecked(true);
        }

        binding.incomeRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type="Income";
            }
        });

        binding.expenseRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type="Expense";
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        if(expenseModel==null){
            menuInflater.inflate(R.menu.add_menu, menu);
        }else{
            menuInflater.inflate(R.menu.update_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.saveExpense) {
            if(type!=null){
                createExpense();
            }else {
                updateExpense();
            }

            return true;
        }
        if(id==R.id.deleteExpense){
            deleteExpense();

        }
        return false;
    }

    private void deleteExpense() {
        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .document(expenseModel.getExpenseId())
                .delete();
        finish();
    }


    public void createExpense(){

        String expenseid= UUID.randomUUID().toString();
        String amount=binding.Amount.getText().toString();
        String note=binding.Note.getText().toString();
        String category=binding.category.getText().toString();


        boolean incomeCheked=binding.incomeRadio.isChecked();
        if(incomeCheked){
            type="Income";
        }else{
            type="Expenses";
        }
        if(amount.trim().length()==0){
            binding.Amount.setError("Empty");
            return;
        }

        ExpenseModel expenseModel=new ExpenseModel(expenseid,note,category,type,Long.parseLong(amount), Calendar.getInstance().getTimeInMillis(),
                FirebaseAuth.getInstance().getUid());
        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .document(expenseid)
                .set(expenseModel);
        finish();


    }
    public void updateExpense(){

        String expenseid=expenseModel.getExpenseId();
        String amount=binding.Amount.getText().toString();
        String note=binding.Note.getText().toString();
        String category=binding.category.getText().toString();


        boolean incomeCheked=binding.incomeRadio.isChecked();
        if(incomeCheked){
            type="Income";
        }else{
            type="Expenses";
        }
        if(amount.trim().length()==0){
            binding.Amount.setError("Empty");
            return;
        }

        ExpenseModel model=new ExpenseModel(expenseid,note,category,type,Long.parseLong(amount),expenseModel.getTime(),
                FirebaseAuth.getInstance().getUid());
        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .document(expenseid)
                .set(model);
        finish();


    }
}