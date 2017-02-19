package com.ulfric.turtle.log;

import java.util.Collections;

import com.google.cloud.MonitoredResource;
import com.google.cloud.errorreporting.spi.v1beta1.ReportErrorsServiceClient;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.logging.Payload;
import com.google.cloud.logging.Severity;
import com.google.devtools.clouderrorreporting.v1beta1.ErrorContext;
import com.google.devtools.clouderrorreporting.v1beta1.ProjectName;
import com.google.devtools.clouderrorreporting.v1beta1.ReportedErrorEvent;
import com.google.devtools.clouderrorreporting.v1beta1.SourceLocation;
import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.cdi.scope.Shared;
import com.ulfric.commons.exception.ThrowableUtils;

@Shared
public class LoggingProvider {

	private static final String LOG_NAME = "turtle";

	@Inject private ObjectFactory factory;

	private final Logging logging = LoggingOptions.getDefaultInstance().getService();

	public Logging getLogging()
	{
		return this.logging;
	}

	public void log(String string)
	{
		this.log(string, Severity.INFO);
	}

	public void log(String string, Severity severity)
	{
		LogEntry entry = this.entryOf(Payload.StringPayload.of(string), severity);

		this.logging.write(Collections.singleton(entry));
	}

	public void log(Throwable throwable)
	{
		StackTraceElement trace = this.getFinalElement(throwable);

		try (ReportErrorsServiceClient reportErrorsServiceClient = ReportErrorsServiceClient.create())
		{
			ProjectName projectName = ProjectName.create(LoggingProvider.LOG_NAME);

			SourceLocation source = SourceLocation.newBuilder()
					.setFilePath(trace.getFileName())
					.setFunctionName(trace.getMethodName())
					.setLineNumber(trace.getLineNumber())
					.build();

			ErrorContext context = ErrorContext.newBuilder()
					.setReportLocation(source)
					.build();

			ReportedErrorEvent event = ReportedErrorEvent.newBuilder()
					.setContext(context)
					.build();

			reportErrorsServiceClient.reportErrorEvent(projectName, event);
		}
		catch (Exception exception)
		{
			throw ThrowableUtils.getPropogated(throwable);
		}
	}

	private StackTraceElement getFinalElement(Throwable throwable)
	{
		return throwable.getStackTrace()[throwable.getStackTrace().length - 1];
	}

	private LogEntry entryOf(Payload<?> payload, Severity severity)
	{
		return LogEntry.newBuilder(payload)
				.setLogName(LoggingProvider.LOG_NAME)
				.setSeverity(severity)
				.setResource(MonitoredResource.newBuilder("global").build())
				.build();
	}

}
