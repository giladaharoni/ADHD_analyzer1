package com.example.adhd_analyzer;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.example.adhd_analyzer.api.UserApi;
import com.example.adhd_analyzer.api.WebServiceApi;
import com.example.adhd_analyzer.entities.DataGet;
import com.example.adhd_analyzer.entities.QAUobjects;

import com.example.adhd_analyzer.logger_sensors.ProcessedDataDB;
import com.example.adhd_analyzer.logger_sensors.processedDataDao;
import com.example.adhd_analyzer.user.UserDetails;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.graphics.pdf.PdfDocument;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


import com.example.adhd_analyzer.logger_sensors.ModuleDB;
import com.example.adhd_analyzer.logger_sensors.ProcessedData;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportsFragment extends Fragment {


    private static final int STORAGE_PERMISSION_REQUEST_CODE = 23;

    private Button downloadButton;


    public ReportsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment reports_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportsFragment newInstance(String param1, String param2) {
        ReportsFragment fragment = new ReportsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_report_unit, container, false);
        downloadButton = view.findViewById(R.id.download_button);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Check if the app has write permission to external storage
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Request the permission if it is not granted
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                STORAGE_PERMISSION_REQUEST_CODE);
                        createAndDownloadPDF();
                        return;
                    }
                }
                // If permission is already granted or on older Android versions,
                // directly create and download the PDF
                createAndDownloadPDF();
               }
        });

        //Chart chart = magic_function()


        return view;
    }


    // line chart


    // pie chart

    // histogram



    private void createAndDownloadPDF() {
        // Create a new PDF document
        PdfDocument document = new PdfDocument();

        // Create a page with the desired attributes
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 800, 1).create();
        PdfDocument.Page firstPage = document.startPage(pageInfo);

        // Create a canvas to draw the content onto the page
        Canvas canvas = firstPage.getCanvas();

        // Create your graph using MPAndroidChart
        LineChart chart = new LineChart(getContext());

        // Create an ArrayList of Entry objects to hold your data points
        ArrayList<Entry> entries = new ArrayList<>();


        WebServiceApi api = UserApi.getRetrofitInstance().create(WebServiceApi.class);
        String username = ModuleDB.getUserDetailsDB(getContext()).userDao().index().get(0).getUserName().toString();
        Call<List<DataGet>> callii = api.getDatas(username, 123);
        callii.enqueue(new Callback<List<DataGet>>() {
            @Override
            public void onResponse(Call<List<DataGet>> call, Response<List<DataGet>> response) {
                List<DataGet> data = response.body();
                float counter;
                float precent;
                for (int i = 0; i < data.size(); i = i + 30) {
                    counter = 0;
                    for (int j = 0; j < 30; j++) {
                        if ((i+j)< data.size()) {
                            if (data.get(i + j).isHighAdhd()) {
                                counter++;
                            }
                        }
                    }
                    Date date = new Date(data.get(i).getTimestamp());

                    // Create a SimpleDateFormat object to specify the desired date format
                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");

                    // Format the date as a string using the desired format
                    String formattedDate = sdf.format(date);
                    precent = (counter / 30) * 100;
                    float q = (float) (i/60.0);
                    entries.add(new Entry(q, precent));
                }

                // Create a LineDataSet from the entries
                LineDataSet dataSet = new LineDataSet(entries, "level of ADHD");

                // Customize the LineDataSet with desired styling options
                dataSet.setColor(Color.BLUE);
                dataSet.setValueTextColor(Color.BLACK);
                dataSet.setDrawValues(false);

                // Create a LineData object from the LineDataSet
                LineData lineData = new LineData(dataSet);

                // Set the LineData to the chart
                chart.setData(lineData);

                // Customize the chart appearance and behavior
                chart.getDescription().setText("Precent of ADHD diagnosis");
                chart.setExtraOffsets(10f, 10f, 10f, 10f);

                // Calculate the available width and height for the chart
                int chartWidth = canvas.getWidth() - (chart.getPaddingLeft() + chart.getPaddingRight());
                int chartHeight = canvas.getHeight() - (chart.getPaddingTop() + chart.getPaddingBottom());

                // Create LayoutParams object and set the calculated width and height
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(chartWidth, chartHeight);
                chart.setLayoutParams(layoutParams);

                // Measure and layout the chart view
                chart.measure(View.MeasureSpec.makeMeasureSpec(chartWidth, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(chartHeight, View.MeasureSpec.EXACTLY));
                chart.layout(0, 0, chart.getMeasuredWidth(), chart.getMeasuredHeight());

                // Render the chart onto a Bitmap
                Bitmap chartBitmap = Bitmap.createBitmap(chartWidth, chartHeight, Bitmap.Config.ARGB_8888);
                Canvas chartCanvas = new Canvas(chartBitmap);
                chart.draw(chartCanvas);

                // Position the chart on the page
                int chartLeft = (canvas.getWidth() - chartBitmap.getWidth()) / 2;
                int chartTop = (canvas.getHeight() - chartBitmap.getHeight()) / 2;

                // Draw the chart bitmap onto the PDF page's canvas
                canvas.drawBitmap(chartBitmap, chartLeft, chartTop, null);
                // Finish the page
                document.finishPage(firstPage);

                // Create the second page with the desired attributes
                PdfDocument.PageInfo secondPageInfo = new PdfDocument.PageInfo.Builder(600, 800, 2).create();
                PdfDocument.Page secondPage = document.startPage(secondPageInfo);
                // Create a canvas to draw the content onto the page2
                Canvas canvas2 = secondPage.getCanvas();

                // Define text attributes
                Paint textPaint = new Paint();
                textPaint.setColor(Color.BLACK);
                textPaint.setTextSize(9f);

                // Calculate the position to write the text
                float textX = 20;
                float textY = 20;

                String text1;
                // Define the text to be written
                StringBuilder text = new StringBuilder("The quiz answers:\n");
                // Write the text on the PDF page's canvas
                canvas2.drawText(text.toString(), textX, textY, textPaint);


                Call<List<QAUobjects>> call2 = api.getQuizAnswersByUser(username);
                call2.enqueue(new Callback<List<QAUobjects>>() {
                    @Override
                    public void onResponse(Call<List<QAUobjects>> call, Response<List<QAUobjects>> response) {
                        if (response.body() != null) {
                            for (int i = 0; i < response.body().size(); i++) {
                                float textX = 20;
                                float textY = 20 + (i * 11) + 11;
                                switch (response.body().get(i).getAnswer()) {
                                    case 1:
                                        // Write the text on the PDF page's canvas
                                        canvas2.drawText(QuestionAnswer.question[i] + ": Never", textX, textY, textPaint);
                                        //text.append(QuestionAnswer.question[i]+": Never\n");
                                        continue;
                                    case 2:
                                        // Write the text on the PDF page's canvas
                                        canvas2.drawText(QuestionAnswer.question[i] + ": Rarely", textX, textY, textPaint);
                                        //text.append(QuestionAnswer.question[i]+": Rarely\n");
                                        continue;
                                    case 3:
                                        // Write the text on the PDF page's canvas
                                        canvas2.drawText(QuestionAnswer.question[i] + ": Often", textX, textY, textPaint);
                                        //text.append(QuestionAnswer.question[i]+": Often\n");
                                        continue;
                                    case 4:
                                        // Write the text on the PDF page's canvas
                                        canvas2.drawText(QuestionAnswer.question[i] + ": Very often", textX, textY, textPaint);
                                        //text.append(QuestionAnswer.question[i]+": Very often\n");
                                        continue;
                                    default:
                                        // Write the text on the PDF page's canvas
                                        canvas2.drawText(QuestionAnswer.question[i] + ": ---", textX, textY, textPaint);
                                        //text.append(QuestionAnswer.question[i]+": ---\n");
                                }

                            }
                        }
                        // Write the text on the PDF page's canvas
                        //canvas.drawText(text.toString(), textX, textY, textPaint);

                        // Finish the page
                        document.finishPage(secondPage);

                        // Save the document to a file
                        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        File file = new File(directory, "example.pdf");

                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            document.writeTo(fos);
                            document.close();
                            fos.close();

                            // Show a toast message to indicate that the PDF file was created and downloaded successfully
                            Toast.makeText(getContext(), "PDF file created and downloaded", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            // Show a toast message if an error occurs while creating or downloading the PDF file
                            Toast.makeText(getContext(), "Error creating or downloading PDF file", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<QAUobjects>> call, Throwable t) {
                        Toast.makeText(getContext(), "Error creating or downloading PDF file BY QUIZ", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<DataGet>> call, Throwable t) {

            }
        });
    }

    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Number value, Number value2, Number value3) {
            super(x, value);
            setValue("value2", value2);
            setValue("value3", value3);
        }

    }

}
