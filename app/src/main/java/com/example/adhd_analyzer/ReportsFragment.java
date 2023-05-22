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
import com.example.adhd_analyzer.entities.QAUobjects;

import com.example.adhd_analyzer.logger_sensors.ProcessedDataDB;
import com.example.adhd_analyzer.logger_sensors.processedDataDao;
import com.example.adhd_analyzer.user.UserDetails;
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_report_unit, container, false);


        AnyChartView anyChartView = view.findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));

        Cartesian cartesian = AnyChart.line();

        cartesian.animation(true);

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        cartesian.title("Trend of Sales of the Most Popular Products of ACME Corp.");

        cartesian.yAxis(0).title("Number of Bottles Sold (thousands)");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        List<DataEntry> seriesData = new ArrayList<>();
        seriesData.add(new CustomDataEntry("1986", 3.6, 2.3, 2.8));
        seriesData.add(new CustomDataEntry("1987", 7.1, 4.0, 4.1));
        seriesData.add(new CustomDataEntry("1988", 8.5, 6.2, 5.1));
        seriesData.add(new CustomDataEntry("1989", 9.2, 11.8, 6.5));
        seriesData.add(new CustomDataEntry("1990", 10.1, 13.0, 12.5));
        seriesData.add(new CustomDataEntry("1991", 11.6, 13.9, 18.0));
        seriesData.add(new CustomDataEntry("1992", 16.4, 18.0, 21.0));
        seriesData.add(new CustomDataEntry("1993", 18.0, 23.3, 20.3));
        seriesData.add(new CustomDataEntry("1994", 13.2, 24.7, 19.2));
        seriesData.add(new CustomDataEntry("1995", 12.0, 18.0, 14.4));
        seriesData.add(new CustomDataEntry("1996", 3.2, 15.1, 9.2));
        seriesData.add(new CustomDataEntry("1997", 4.1, 11.3, 5.9));
        seriesData.add(new CustomDataEntry("1998", 6.3, 14.2, 5.2));
        seriesData.add(new CustomDataEntry("1999", 9.4, 13.7, 4.7));
        seriesData.add(new CustomDataEntry("2000", 11.5, 9.9, 4.2));
        seriesData.add(new CustomDataEntry("2001", 13.5, 12.1, 1.2));
        seriesData.add(new CustomDataEntry("2002", 14.8, 13.5, 5.4));
        seriesData.add(new CustomDataEntry("2003", 16.6, 15.1, 6.3));
        seriesData.add(new CustomDataEntry("2004", 18.1, 17.9, 8.9));
        seriesData.add(new CustomDataEntry("2005", 17.0, 18.9, 10.1));
        seriesData.add(new CustomDataEntry("2006", 16.6, 20.3, 11.5));
        seriesData.add(new CustomDataEntry("2007", 14.1, 20.7, 12.2));
        seriesData.add(new CustomDataEntry("2008", 15.7, 21.6, 10));
        seriesData.add(new CustomDataEntry("2009", 12.0, 22.5, 8.9));

        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
        Mapping series3Mapping = set.mapAs("{ x: 'x', value: 'value3' }");

        Line series1 = cartesian.line(series1Mapping);
        series1.name("Brandy");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        Line series2 = cartesian.line(series2Mapping);
        series2.name("Whiskey");
        series2.hovered().markers().enabled(true);
        series2.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series2.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        Line series3 = cartesian.line(series3Mapping);
        series3.name("Tequila");
        series3.hovered().markers().enabled(true);
        series3.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series3.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        anyChartView.setChart(cartesian);
        anyChartView.setBackgroundColor(getResources().getColor(R.color.black));


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

        return view;
    }



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

        ProcessedDataDB dataDB = ModuleDB.getProcessedDB(getContext());
        processedDataDao dataDao = dataDB.processedDataDao();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<ProcessedData> data = ModuleDB.getProcessedDB(getContext()).processedDataDao().index();
                int precent, counter;
                for (int i = 0; i < data.size(); i = i + 15) {
                    counter = 0;
                    for (int j = 0; j < 15; j++) {
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
                    precent = (counter / 15) * 100;
                    entries.add(new Entry(i, precent));
                }


//        entries.add(new Entry(0, 4));
//        entries.add(new Entry(1, 8));
//        entries.add(new Entry(2, 6));
//        entries.add(new Entry(3, 2));
//        entries.add(new Entry(4, 7));
//        entries.add(new Entry(5, 4));
//        entries.add(new Entry(6, 8));
//        entries.add(new Entry(7, 6));
//        entries.add(new Entry(8, 2));
//        entries.add(new Entry(9, 7));
//        entries.add(new Entry(10, 4));
//        entries.add(new Entry(11, 8));
//        entries.add(new Entry(12, 6));
//        entries.add(new Entry(13, 2));
//        entries.add(new Entry(14, 7));
//        entries.add(new Entry(15, 4));
//        entries.add(new Entry(16, 8));
//        entries.add(new Entry(17, 6));
//        entries.add(new Entry(18, 2));
//        entries.add(new Entry(19, 7));

                // Create a LineDataSet from the entries
                LineDataSet dataSet = new LineDataSet(entries, "Function Data");

                // Customize the LineDataSet with desired styling options
                dataSet.setColor(Color.BLUE);
                dataSet.setValueTextColor(Color.BLACK);

                // Create a LineData object from the LineDataSet
                LineData lineData = new LineData(dataSet);

                // Set the LineData to the chart
                chart.setData(lineData);

                // Customize the chart appearance and behavior
                chart.getDescription().setText("My Line Chart");
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

                WebServiceApi api = UserApi.getRetrofitInstance().create(WebServiceApi.class);
                String username = ModuleDB.getUserDetailsDB(getContext()).userDao().index().get(0).getUserName().toString();
                Call<List<QAUobjects>> call = api.getQuizAnswersByUser(username);
                call.enqueue(new Callback<List<QAUobjects>>() {
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
