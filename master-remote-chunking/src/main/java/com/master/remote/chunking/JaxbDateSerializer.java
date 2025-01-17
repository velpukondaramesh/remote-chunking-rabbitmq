package com.master.remote.chunking;

import java.text.SimpleDateFormat;
import java.util.Date;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Michael Minella
 */
public class JaxbDateSerializer extends XmlAdapter<String, Date> {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public String marshal(Date date) throws Exception {
		return dateFormat.format(date);
	}

	@Override
	public Date unmarshal(String date) throws Exception {
		return dateFormat.parse(date);
	}
}