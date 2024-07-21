package com.vigneshbala.twi.cli;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.concurrent.Callable;

import org.slf4j.bridge.SLF4JBridgeHandler;

import com.vigneshbala.twi.util.ReferenceDataUtil;
import com.vigneshbala.twi.util.TimeConversionUtil;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * (c) 2024 Vignesh Balasubramanian
 * 
 * This code is licensed under MIT license (see LICENSE for details)
 */
@Command(name = "twi", mixinStandardHelpOptions = true, version = "twi 1.0.0", description = "Natural language date and time processor and converter")
public class TimeConverter implements Callable<Integer> {
	static {
		try {
			SLF4JBridgeHandler.removeHandlersForRootLogger();
			SLF4JBridgeHandler.install();

			ReferenceDataUtil.loadCountryData();
			System.out.println("Reference Data load Complete...");
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			// exit if reference data could not be loaded
			System.exit(0);
		}

	}
	private static final String DATE_TIME_FORMAT = "dd-MMM-yyyy hh:mm:ss a Z";
	@Parameters(index = "0", description = "Natural language input of the time conversion")
	private String input;

	@Option(names = { "-f", "--format" }, description = "Date/Time Format(e.g. dd-MM-yyyy/dd-MM-yyyy HH:mm:ss)")
	private String format = DATE_TIME_FORMAT;

	@Option(names = { "-z",
			"--timezone" }, description = "Timezone short code or id (e.g. IST or Asia/Kolkata. repeat for passing multiple values)")
	private String[] zones;

	@Option(names = { "-o",
			"--offset" }, description = "Offset from UTC format: +8, +8.5,+08:00,+08:30,-8, -8.5 -08:00,-08:30. repeat for passing multiple values")
	private String[] offsets;

	@Override
	public Integer call() throws Exception {
		Integer exitCode = 0;
		System.out.println(input);
		try {
			System.out.println(
					TimeConversionUtil.convertDateTime(input, format, ZonedDateTime.now(), zones, null, offsets));
		} catch (Exception e) {
			e.printStackTrace();
			exitCode = 500;
			throw e;
		}
		return exitCode;
	}

	public static void main(String... args) {
		new CommandLine(new TimeConverter()).execute(args);
	}
}