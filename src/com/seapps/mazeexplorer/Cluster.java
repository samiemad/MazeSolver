package com.seapps.mazeexplorer;

public class Cluster implements Comparable<Cluster> {
	private long sum;
	private int num;

	public Cluster(int p) {
		sum = p;
		num = 1;
	}

	public Cluster(int p, int n) {
		sum = p * n;
		num = n;
	}

	public int getPos() {
		return (int) (sum / num);
	}

	public int getNum() {
		return num;
	}

	public boolean same(int p) {
		return Math.abs(getPos() - p) < ImageAnalyzer.THRESH;
	}

	public void add(int p) {
		sum += p;
		num++;
	}

	@Override
	public int compareTo(Cluster another) {
		return getPos() < another.getPos() ? -1 : 1;
	}
}
