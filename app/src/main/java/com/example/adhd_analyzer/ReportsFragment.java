package com.example.adhd_analyzer;

import com.example.adhd_analyzer.api.UserApi;
import com.example.adhd_analyzer.api.WebServiceApi;
import com.example.adhd_analyzer.entities.DataGet;
import com.example.adhd_analyzer.entities.QAUobjects;

import com.example.adhd_analyzer.user.UserDetails;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;

import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.graphics.pdf.PdfDocument;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.example.adhd_analyzer.logger_sensors.ModuleDB;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportsFragment extends Fragment {


    private static final int STORAGE_PERMISSION_REQUEST_CODE = 23;

    private Button downloadButton;
    private FrameLayout graphContainer;
    private List<QAUobjects> answers;
    private List<DataGet> dataGetList;
    private UserDetails details;
    List<Chart> charts = new ArrayList<>();


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

    /**
     * when the fragment is shown, it downloads the ansers and process data for running the view.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        details = ModuleDB.getUserDetailsDB(getContext()).userDao().index().get(0);

        View view = inflater.inflate(R.layout.fragment_report_unit, container, false);
        getAnswers();
        getLastData();
        downloadButton = view.findViewById(R.id.download_button);
        graphContainer = view.findViewById(R.id.graph_container);


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
                        DownloadPDF(charts,answers);
                        return;
                    }
                }
                // If permission is already granted or on older Android versions,
                // directly create and download the PDF
                DownloadPDF(charts,answers);
               }
        });



        return view;
    }


    /**
     * get the session ID of the tracking, parsing it to title and set the title view.
     */
    private void setTitle(){
        long timestamp = dataGetList.get(0).getTimestamp();
        Date date = new Date(timestamp);
        TextView reportTitle = getView().findViewById(R.id.report_title);
        android.icu.util.Calendar calendar = android.icu.util.Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(android.icu.util.Calendar.MONTH)+1;
        int day = calendar.get(android.icu.util.Calendar.DAY_OF_MONTH);
        String title = "Report from "+day+"/"+month;
        reportTitle.setText(title);
    }

    /**
     * set the chart to view in a selected container.
     * @param chart the chart object.
     * @param chartContainer linear layout container.
     */
    private void addChartToLayout(Chart chart, FrameLayout chartContainer) {

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        chart.setLayoutParams(layoutParams);

        chartContainer.addView(chart);
    }

    /**
     * make pie graph.
     * @return pie graph.
     */
    private Chart pieChartGraph(){
        PieChart chart = new PieChart(getContext());
        ArrayList<PieEntry> entries = new ArrayList<>();
        long size = dataGetList.size();
        long stayCount = dataGetList.stream().filter(DataGet::isStayInPlace).count();
        long adhdCount = dataGetList.stream().filter(DataGet::isHighAdhd).count();
        long NoStayInplace = (dataGetList.size() - stayCount);
        long stayWithoutADHD = stayCount - adhdCount;
        entries.add(new PieEntry((float)stayWithoutADHD/size,"stay in place without adhd"));
        entries.add(new PieEntry((float)NoStayInplace/size,"no stay inplace"));
        entries.add(new PieEntry((float)adhdCount/size,"ADHD while stay inplace"));
        PieDataSet dataSet = new PieDataSet(entries, "Election Results");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        chart.setData(data);
        chart.highlightValues(null);
        return chart;
    }

    /**
     * resample the data from minutes to half of hours.
     * @return list of data entries.
     */
    private List<Entry> AdhdPerHalfHours(){
        ArrayList<Entry> entries = new ArrayList<>();
        float counter;
        float precent;
        for (int i = 0; i < dataGetList.size(); i = i + 30) {
            counter = 0;
            for (int j = 0; j < 30; j++) {
                if ((i+j)< dataGetList.size()) {
                    if (dataGetList.get(i + j).isHighAdhd()) {
                        counter++;
                    }
                }
            }
            precent = (counter / 30) * 100;
            float q = (float) (i/60.0);
            entries.add(new Entry(q, precent));
        }
        return entries;
    }

    /**
     * create the line chart graph based on the data.
     * @return chart object.
     */
    private Chart lineChartGraph(){
        LineChart chart = new LineChart(getContext());
        List<Entry> entries = AdhdPerHalfHours();
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
        chart.getDescription().setText("Percent of ADHD diagnosis of "+details.getFullName());
        chart.setExtraOffsets(10f, 10f, 10f, 10f);
        return chart;
    }

    /**
     * download from the webAPI the quiz answers.
     */
    private void getAnswers(){
        WebServiceApi api = UserApi.getRetrofitInstance().create(WebServiceApi.class);
        String username = ModuleDB.getUserDetailsDB(getContext()).userDao().index().get(0).getUserName().toString();
        Call<List<QAUobjects>> call2 = api.getQuizAnswersByUser(username);
        call2.enqueue(new Callback<List<QAUobjects>>() {
            @Override
            public void onResponse(Call<List<QAUobjects>> call, Response<List<QAUobjects>> response) {
                answers = response.body();
            }

            @Override
            public void onFailure(Call<List<QAUobjects>> call, Throwable t) {
                answers = new ArrayList<QAUobjects>();
            }
        });
    }

    /**
     * download from the webAPI the last tracking data.
     */
    private void getLastData(){
        WebServiceApi api = UserApi.getRetrofitInstance().create(WebServiceApi.class);
        String username = ModuleDB.getUserDetailsDB(getContext()).userDao().index().get(0).getUserName().toString();
        Call<List<DataGet>> call = api.getLastDatas(username);
        call.enqueue(new Callback<List<DataGet>>() {
            @Override
            public void onResponse(Call<List<DataGet>> call, Response<List<DataGet>> response) {
                dataGetList = response.body();
                if(dataGetList == null){
                    TextView reportTitle = getView().findViewById(R.id.report_title);
                    reportTitle.setText("No Tracking done yet");
                    return;
                }
                charts.clear();
                charts.add(lineChartGraph());
                addChartToLayout(lineChartGraph(),graphContainer);
                setTitle();
                //charts.add(pieChartGraph());
            }

            @Override
            public void onFailure(Call<List<DataGet>> call, Throwable t) {
                Call<List<DataGet>> call1 = api.getLastDatas(username);
                call1.enqueue(new Callback<List<DataGet>>() {
                    @Override
                    public void onResponse(Call<List<DataGet>> call, Response<List<DataGet>> response) {
                        dataGetList = response.body();
                        if(dataGetList == null){
                            TextView reportTitle = getView().findViewById(R.id.report_title);
                            reportTitle.setText("No Tracking done yet");
                            return;
                        }
                        charts.clear();
                        charts.add(lineChartGraph());
                        addChartToLayout(lineChartGraph(),graphContainer);
                        setTitle();
                        //charts.add(pieChartGraph());
                    }

                    @Override
                    public void onFailure(Call<List<DataGet>> call, Throwable t) {
                        dataGetList = new ArrayList<>();
                    }
                });
            }
        });
    }

    /**
     * design sketch of answered quiz and save it to the pdf file of the report.
     * @param answers the answers, encoded.
     * @param page the page pdf file.
     */
    private void AddQuestions(List<QAUobjects> answers, PdfDocument.Page page){
        Canvas canvas2 = page.getCanvas();

        // Define text attributes
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(9f);

        // Calculate the position to write the text
        float textX = 20;
        float textY = 20;

        String text1;
        // Define the text to be written
        StringBuilder text = new StringBuilder("The quiz answers:");
        // Write the text on the PDF page's canvas
        canvas2.drawText(text.toString(), textX, textY, textPaint);
        for (int i = 0; i < answers.size(); i++) {
            textX = 20;
            textY = 20 + (i * 11) + 11;
            switch (answers.get(i).getAnswer()) {
                case 1:
                    canvas2.drawText(QuestionAnswer.question[i] + ": Never", textX, textY, textPaint);
                    continue;
                case 2:
                    canvas2.drawText(QuestionAnswer.question[i] + ": Rarely", textX, textY, textPaint);
                    continue;
                case 3:
                    canvas2.drawText(QuestionAnswer.question[i] + ": Often", textX, textY, textPaint);
                    continue;
                case 4:
                    canvas2.drawText(QuestionAnswer.question[i] + ": Very often", textX, textY, textPaint);
                    continue;
                default:
                    canvas2.drawText(QuestionAnswer.question[i] + ": ---", textX, textY, textPaint);
            }

        }

    }

    private ActivityResultLauncher<String> createDocumentLauncher;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        createDocumentLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    try {
                        OutputStream outputStream = getActivity().getContentResolver().openOutputStream(result);
                        if (outputStream != null) {
                            document.writeTo(outputStream);
                            outputStream.close();
                        }
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

    }
    private PdfDocument document;

    /**
     * attach the chart object to the pdf file, print the answers in the format, and the maje an intent that create the pdf file.
     * @param charts chart object.
     * @param answers the answers to the quiz in the foramt.
     */
    private void DownloadPDF(List<Chart> charts, List<QAUobjects> answers){
        int pages = charts.size()+1;
        document = new PdfDocument();
        int pageNumber = 1;

        for (Chart chart: charts) {
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 800, pageNumber).create();
            pageNumber++;
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            int chartWidth = canvas.getWidth() - (chart.getPaddingLeft() + chart.getPaddingRight());
            int chartHeight = canvas.getHeight() - (chart.getPaddingTop() + chart.getPaddingBottom());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(chartWidth, chartHeight);
            chart.setLayoutParams(layoutParams);
            chart.measure(View.MeasureSpec.makeMeasureSpec(chartWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(chartHeight, View.MeasureSpec.EXACTLY));
            chart.layout(0, 0, chart.getMeasuredWidth(), chart.getMeasuredHeight());
            Bitmap chartBitmap = Bitmap.createBitmap(chartWidth, chartHeight, Bitmap.Config.ARGB_8888);
            Canvas chartCanvas = new Canvas(chartBitmap);
            chart.draw(chartCanvas);

            // Position the chart on the page
            int chartLeft = (canvas.getWidth() - chartBitmap.getWidth()) / 2;
            int chartTop = (canvas.getHeight() - chartBitmap.getHeight()) / 2;
            canvas.drawBitmap(chartBitmap, chartLeft, chartTop, null);
            // Finish the page
            document.finishPage(page);

        }
        PdfDocument.PageInfo secondPageInfo = new PdfDocument.PageInfo.Builder(600, 800, pageNumber).create();
        PdfDocument.Page secondPage = document.startPage(secondPageInfo);
        AddQuestions(answers,secondPage);
        document.finishPage(secondPage);
//        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        File file = new File(directory, "report.pdf");
        createDocumentLauncher.launch("report.pdf");

//
//        try {
////            FileOutputStream fos = new FileOutputStream(file);
////            document.writeTo(fos);
////            document.close();
////            fos.close();
////            Uri pdfUri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file);
////            Intent intent = new Intent(Intent.ACTION_SEND);
////            intent.setDataAndType(pdfUri, "application/pdf");
////            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
////
////            try {
////                startActivity(Intent.createChooser(intent,"open pdf"));
////            } catch (ActivityNotFoundException  e) {
////                Toast.makeText(getContext(), "No PDF viewer app installed", Toast.LENGTH_SHORT).show();
////            }
//
//
//
//            // Show a toast message to indicate that the PDF file was created and downloaded successfully
//            Toast.makeText(getContext(), "PDF file created and downloaded", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            // Show a toast message if an error occurs while creating or downloading the PDF file
//            Toast.makeText(getContext(), "Error in saving the file", Toast.LENGTH_SHORT).show();
//        }
    }


}
