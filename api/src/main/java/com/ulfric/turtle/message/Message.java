package com.ulfric.turtle.message;

import com.ulfric.commons.bean.Bean;

public class Message extends Bean<Message> {

	private Common common;

	public Common getCommon()
	{
		return this.common;
	}

	public void setCommon(Common common)
	{
		this.common = common;
	}

}
