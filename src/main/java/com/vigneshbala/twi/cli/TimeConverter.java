package com.vigneshbala.twi.cli;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.github.cliftonlabs.json_simple.JsonException;
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
	private static Logger log = LoggerFactory.getLogger(TimeConverter.class);
	static {
		try {
			SLF4JBridgeHandler.removeHandlersForRootLogger();
			SLF4JBridgeHandler.install();

			ReferenceDataUtil.loadCountryData();
			log.debug("Reference Data load Complete...");
		} catch (IOException | JsonException e) {
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

	@Option(names = { "-c",
			"--country" }, description = "3 digit Country Code/2 digit Country Code/country name (e.g. IND or IN or India. repeat for passing multiple values)")
	private String[] countries;

	@Option(names = { "-o",
			"--offset" }, description = "Offset from UTC format: +8, +8.5,+08:00,+08:30,-8, -8.5 -08:00,-08:30. repeat for passing multiple values")
	private String[] offsets;

	@Override
	public Integer call() throws Exception {
		Integer exitCode = 0;
		try {
			System.out.println(
					TimeConversionUtil.convertDateTime(input, format, new DateTime(), zones, countries, offsets));
		} catch (Exception e) {
			e.printStackTrace();
			exitCode = 500;
			throw e;
		}
		return exitCode;
	}

	// this example implements Callable, so parsing, error handling and handling
	// user
	// requests for usage help or version help can be done with one line of code.
	public static void main(String... args) {
		int exitCode = new CommandLine(new TimeConverter()).execute(args);
	}
}