package com.stratio.specs;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stratio.tests.utils.CassandraUtil;
import com.stratio.tests.utils.CassandraUtils;
import com.stratio.tests.utils.ExceptionList;
import com.stratio.tests.utils.ThreadProperty;

public class CommonG {

	private final Logger logger = LoggerFactory.getLogger(ThreadProperty
			.get("class"));
	private final ExceptionList exceptions = ExceptionList.getInstance();
	private final CassandraUtil cassandraClient = CassandraUtil.getInstance();

	public Logger getLogger() {
		return this.logger;
	}

	public List<Exception> getExceptions() {
		return exceptions.getExceptions();
	}

	public CassandraUtils getCassandraClient() {
		return cassandraClient.getCassandraUtils();
	}		
}
