package com.ben.drivenbluetooth.threads;

import com.ben.drivenbluetooth.Global;
import com.ben.drivenbluetooth.MainActivity;

import java.util.Random;

public class RandomGenerator extends Thread {
	Random rnd = new Random();
	private volatile boolean stopWorker = false;

	public void run() {
		this.stopWorker = false;
		byte i0 = 0;
		byte i1 = 0;

		while(!this.stopWorker){
			byte[] Message = new byte[5];

			byte[] IDS = new byte[7];
			IDS[0] = Global.THR_INPUT_ID;
			IDS[1] = Global.AMPS_ID;
			IDS[2] = Global.VOLTS_ID;
			IDS[3] = Global.TEMP1ID;
			IDS[4] = Global.MOTOR_RPM_ID;
			IDS[5] = Global.SPEED_MPH_ID;
			IDS[6] = Global.THR_ACTUAL_ID;

			for (byte ID : IDS) {
				// fill with random shit
				rnd.nextBytes(Message);

				// organise key bytes
				Message[0] = Global.STARTBYTE;
				Message[1] = ID;
				Message[2] = (byte) rnd.nextInt(127);
				Message[3] = (byte) rnd.nextInt(99);
				Message[4] = Global.STOPBYTE;

				// push to queue
				Global.BTStreamQueue.add(Message);

				// send message to BTDataParser
				BTDataParser.mHandler.sendEmptyMessage(0);
				try {
					Thread.sleep(10); // this needs to be here otherwise the queue gets overloaded
				} catch (Exception ignored) {
				}
			}

			// wait 250 milliseconds
			try {
				Thread.sleep(250);
			} catch (Exception e) {
				// ??
			}

			i0 += 10;
			i1 += 50;

			if (i0 > 100) i0 = 0;
			if (i1 > 50) i1 = 0;
		}
	}

	public void cancel() {
		this.stopWorker = true;
	}
}
