package com.ben.drivenbluetooth.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ben.drivenbluetooth.Global;
import com.ben.drivenbluetooth.MainActivity;
import com.ben.drivenbluetooth.R;
import com.ben.drivenbluetooth.util.UnitHelper;
import com.ben.drivenbluetooth.util.UpdateFragment;

import java.util.Timer;
import java.util.TimerTask;

public class LapHistoryFragment extends UpdateFragment {

	private TableLayout LapTable;

	private Timer FragmentUpdateTimer;

	private TextView Current;
	private TextView Voltage;
	private TextView RPM;
	private TextView Speed;
	private TextView AmpHours;

	public LapHistoryFragment() {
		// Required empty public constructor
	}

	private void InitializeDataFields() {
		View v = getView();
		Current 		= (TextView) v.findViewById(R.id.current);
		Voltage 		= (TextView) v.findViewById(R.id.voltage);
		RPM 			= (TextView) v.findViewById(R.id.rpm);
		Speed 			= (TextView) v.findViewById(R.id.speed);
		AmpHours		= (TextView) v.findViewById(R.id.ampHours);
	}

	/*===================*/
	/* LIFECYCLE
	/*===================*/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_lap_history, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LapTable = (TableLayout) getView().findViewById(R.id.laptable);
		InitializeDataFields();
		StartFragmentUpdater();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		StopFragmentUpdater();
		LapTable = null;

		Current	= null;
		Voltage	= null;
		RPM		= null;
		Speed	= null;
	}

	private void UpdateLapTable() {
		if (LapTable != null) {
			LapTable.removeAllViews();
			_createHeaders();

			Context ctx = getActivity();
			TableRow.LayoutParams textViewParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
			textViewParams.setMargins(20, 10, 20, 10);

			for (int i = 0; i < Global.LapDataList.size(); i++) {
				String[] values = new String[]{
						String.format("%d", i + 1),
						String.format("%.1f", Global.LapDataList.get(i).getAverageAmps()),
						String.format("%.1f", Global.LapDataList.get(i).getAverageVolts()),
                        String.format("%.1f", Global.Unit == Global.UNIT.KPH ? Global.LapDataList.get(i).getAverageSpeedKPH() : Global.LapDataList.get(i).getAverageSpeedMPH()),
                        String.format("%.0f", Global.LapDataList.get(i).getAverageRPM()),
                        String.format("%.2f", Global.LapDataList.get(i).getAmpHours()),
                        String.format("%.2f", Global.LapDataList.get(i).getWattHoursPerKM()),
                        Global.LapDataList.get(i).getLapTimeString()
                };

				TableRow tr = new TableRow(ctx);
				tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                for (String value : values) {
                    TextView hv = new TextView(ctx);
                    hv.setText(value);
                    hv.setGravity(Gravity.CENTER);
                    hv.setLayoutParams(textViewParams);
                    tr.addView(hv);
                }
				TextView laptime = new TextView(ctx);
				laptime.setText(Global.LapDataList.get(i).getLapTimeString());
				laptime.setGravity(Gravity.CENTER);
				laptime.setLayoutParams(textViewParams);
				tr.addView(laptime);
				LapTable.addView(tr);
			}
		}
	}

	public void UpdateFragmentUI() {
		UpdateVolts();
		UpdateAmps();
		UpdateAmpHours();
		UpdateSpeed();
		UpdateMotorRPM();
        UpdateWattHours();
	}

    @Override
    public synchronized void UpdateVolts() {
        try {
			this.Voltage.setText(String.format("%.2f", Global.Volts) + " V");
		} catch (Exception e) {
			e.getMessage();
		}
	}

    @Override
    public synchronized void UpdateAmps() {
        try {
			this.Current.setText(String.format("%.1f", Global.Amps) + " A");
		} catch (Exception e) {
			e.getMessage();
		}
	}

    @Override
    public synchronized void UpdateAmpHours() {
        try {
			AmpHours.setText(String.format("%.2f", Global.AmpHours) + " Ah");
		} catch (Exception e) {
			e.toString();
		}
	}

    @Override
    public synchronized void UpdateSpeed() {
        try {
            Speed.setText(UnitHelper.getSpeedText(Global.SpeedMPS, Global.Unit));

		} catch (Exception e) {
			e.getMessage();
		}
	}

    @Override
    public synchronized void UpdateMotorRPM() {
        try {
			this.RPM.setText(String.format("%.0f", Global.MotorRPM) + " RPM");
		} catch (Exception e) {
			e.getMessage();
		}
	}

    @Override
    public synchronized void UpdateWattHours() {
        // TODO: implement method
    }

    @Override
    public void UpdatePerformanceMetric() {

    }

    @Override
    public void UpdateThrottleMode() {

    }

    private void _createHeaders() {
        Context ctx = getActivity();
		TableRow.LayoutParams textViewParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
							// left, top, right, bottom
		textViewParams.setMargins(20, 10, 20, 10);

		String[] headers = new String[] {
				"Lap",
				"Avg Amps (A)",
				"Avg Volts (V)",
                Global.Unit == Global.UNIT.KPH ? "Avg Spd (kph)" : "Avg Spd (mph)",
                "Avg RPM",
				"Amp hours (Ah)",
                "Avg Wh/km",
                "Lap time"
		};

		TableRow tr = new TableRow(ctx);
		tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

		for (String header : headers) {
			TextView hv = new TextView(ctx);
			hv.setText(header);
			hv.setGravity(Gravity.CENTER);
			hv.setLayoutParams(textViewParams);
			hv.setTypeface(null, Typeface.BOLD);
			tr.addView(hv);
		}
		LapTable.addView(tr);
	}

	private void StartFragmentUpdater() {
		TimerTask tableUpdateTask = new TimerTask() {
			public void run() {
				MainActivity.MainActivityHandler.post(new Runnable() {
					public void run() {
						UpdateLapTable();
					}
				});
			}
		};

		FragmentUpdateTimer = new Timer();
		FragmentUpdateTimer.schedule(tableUpdateTask, 0, 2000);
	}

	private void StopFragmentUpdater() {
		try {
			FragmentUpdateTimer.cancel();
			FragmentUpdateTimer.purge();
		} catch (Exception ignored) {}
	}

	@Deprecated
	public void UpdateTemp() {}	// required as per UpdateFragment contract

	@Deprecated
	public void UpdateThrottle() {}	// required as per UpdateFragment contract
}
