package com.master.remote.chunking;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;


@XmlRootElement(name="transaction")
public class Transaction implements Serializable {

	private String account;

	private String timestamp;

	private String amount;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "Transaction{" +
				"account='" + account + '\'' +
				", timestamp=" + timestamp +
				", amount=" + amount +
				'}';
	}
}