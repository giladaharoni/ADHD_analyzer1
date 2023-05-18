package com.example.adhd_analyzer;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import static androidx.core.content.ContextCompat.getSystemService;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.adhd_analyzer.logger_sensors.ModuleDB;
import com.example.adhd_analyzer.logger_sensors.ProcessedData;
import com.itextpdf.awt.geom.Line2D;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportsFragment extends Fragment {


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
//        List<ProcessedData> data = ModuleDB.getProcessedDB(this.getContext()).processedDataDao().index();
//        View view;
//        if (data.isEmpty()){
//            view = inflater.inflate(R.layout.fragment_reports_fragment, container, false);
//        } else {
//            view = inflater.inflate(R.layout.fragment_report_unit,container,false);
//            Pie pie = AnyChart.pie();
//            float generalRatio = (float)data.stream().filter(ProcessedData::isHighAdhd).count() /
//                    (float)data.stream().filter(ProcessedData::isStayInPlace).count();
//
//        }
        View view = inflater.inflate(R.layout.fragment_reports_fragment,container,false);

        // Find the download button by its ID and set an OnClickListener
        Button downloadButton = view.findViewById(R.id.download_button);



        return view;
    }

}
