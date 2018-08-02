package com.example.compaq.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;

/**
 * Created by compaq on 5/25/2017.
 */

public class ReportActivity extends Activity{
    int food=0,clothes=0,grocery=0,utilities=0,spent=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null)
        {
            food = (Integer) bd.get("food");
            clothes=(Integer) bd.get("clothes");
            grocery=(Integer) bd.get("grocery");
            utilities=(Integer) bd.get("utility");
            spent=(Integer) bd.get("total");
        }
        PieChart pieChart = (PieChart) findViewById(R.id.piechart);
        Toast.makeText(ReportActivity.this,"mkm"+food+"  "+clothes+"  "+grocery+"  "+utilities+"  "+spent,Toast.LENGTH_LONG).show();
        food=(food*100)/spent;
        clothes=(clothes*100)/spent;
        grocery=(grocery*100)/spent;
        utilities=(utilities*100)/spent;
        Toast.makeText(ReportActivity.this,"jsndnwsdiw "+food+"  "+clothes+"  "+grocery+"  "+utilities+"  "+spent,Toast.LENGTH_LONG).show();

        ArrayList<Entry> yvalues = new ArrayList<Entry>();

        yvalues.add(new Entry(food, 0));
        yvalues.add(new Entry(clothes, 1));
        yvalues.add(new Entry(grocery, 2));
        yvalues.add(new Entry(utilities, 3));

        PieDataSet dataSet = new PieDataSet(yvalues, "Expense Chart");
        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("Food");
        xVals.add("Clothes");
        xVals.add("Grocery");
        xVals.add("Utility");

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        //Toast.makeText(ReportActivity.this,"dds",Toast.LENGTH_LONG).show();*/
    }
}
