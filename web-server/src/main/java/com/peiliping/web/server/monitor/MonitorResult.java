package com.peiliping.web.server.monitor;

public class MonitorResult {

	public static final int TYPE_SUCCESS = 1;
	public static final int TYPE_FAILURE = 2;
	public static final int TYPE_EXCEPTION = 3;

	private long in;
	private long out;

	private long success;
	private long failure;
	private long excep;

	private long duration;

	/**
	 * 基础
	 * 
	 * @param in
	 * @param out
	 * @param success
	 * @param failure
	 * @param excep
	 * @param duration
	 */
	public MonitorResult(long in, long out, long success, long failure,
			long excep, long duration) {
		this.in = in;
		this.out = out;
		this.success = success;
		this.failure = failure;
		this.excep = excep;
		this.duration = duration;
	}

	/**
	 * 进入
	 * 
	 * @param mr
	 * @param in
	 * @param out
	 * @param success
	 * @param failure
	 * @param excep
	 * @param duration
	 */
	public MonitorResult(MonitorResult mr, long in, long out, long success,
			long failure, long excep, long duration) {
		this.in = in + mr.getIn();
		this.out = out + mr.getOut();
		this.success = success + mr.getSuccess();
		this.failure = failure + mr.getFailure();
		this.excep = excep + mr.getExcep();
		this.duration = duration + mr.getDuration();
	}

	/**
	 * 退出
	 * 
	 * @param mr
	 * @param out
	 * @param duration
	 * @param type
	 */
	public MonitorResult(MonitorResult mr, long out, long duration, int type) {
		this.in = mr.getIn();
		this.out = out + mr.getOut();
		this.duration = duration + mr.getDuration();

		if (type == TYPE_SUCCESS) {
			this.success = out + mr.getSuccess();
			this.failure = mr.getFailure();
			this.excep = mr.getExcep();
		} else if (type == TYPE_FAILURE) {
			this.success = mr.getSuccess();
			this.failure = out + mr.getFailure();
			this.excep = mr.getExcep();
		} else if (type == TYPE_EXCEPTION) {
			this.success = mr.getSuccess();
			this.failure = mr.getFailure();
			this.excep = out + mr.getExcep();
		}
	}

	/**
	 * 清0
	 * 
	 * @param mr
	 */
	public MonitorResult(MonitorResult mr) {
		new MonitorResult(mr.getIn() - mr.getOut(), 0, 0, 0, 0, 0);
	}

	public long getActiveThread() {
		return in - out;
	}

	public String getSuccessPerCent() {
		return ((out != 0) ? ((double) success * 100 / out) : 0) + "%";
	}

	public String getFailurePerCent() {
		return ((out != 0) ? ((double) failure * 100 / out) : 0) + "%";
	}

	public String getExceptionPerCent() {
		return ((out != 0) ? ((double) excep * 100 / out) : 0) + "%";
	}

	public long getAverageDuration() {
		return (out != 0) ? (duration / out) : 0;
	}

	public long getIn() {
		return in;
	}

	public long getOut() {
		return out;
	}

	public long getSuccess() {
		return success;
	}

	public long getFailure() {
		return failure;
	}

	public long getExcep() {
		return excep;
	}

	public long getDuration() {
		return duration;
	}

	public boolean isEmpty(){
		return in == 0 ; 
	}
	
	public String tolog() {
		StringBuilder r = new StringBuilder();
		r.append("In:" + in + ",");
		r.append("Out:" + out + ",");
		r.append("Success:" + success + ",");
		r.append("Failure:" + failure + ",");
		r.append("Excep:" + excep + ",");
		r.append("Duration:" + duration + ",");
		r.append("AliveThread:" + getActiveThread() + ",");
		r.append("SuccessPer:" + getSuccessPerCent() + ",");
		r.append("FailurePer:" + getFailurePerCent() + ",");
		r.append("ExcepPer:" + getExceptionPerCent() + ",");
		r.append("AVGDuration:" + getAverageDuration() + ",");
		return r.toString();
	}
}