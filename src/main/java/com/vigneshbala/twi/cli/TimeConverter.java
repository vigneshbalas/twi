package com.vigneshbala.twi.cli;

import java.time.ZonedDateTime;
import java.util.concurrent.Callable;

import com.vigneshbala.twi.util.TimeConversionUtil;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Unmatched;

/**
 * (c) 2024 Vignesh Balasubramanian
 * 
 * This code is licensed under MIT license (see LICENSE for details)
 */
@Command(name = "twi", version = "twi 1.0", description = "Prints the converted date & time to STDOUT.")
public class TimeConverter implements Callable<Integer> {

	@Parameters(index = "0", description = "Date Time in Natural Language")
	private String input;

	@Option(names = { "-f", "--format" }, description = "Date/Time Format(e.g. dd-MM-yyyy/dd-MM-yyyy HH:mm:ss)")
	private String format = "dd/MM/yyyy hh:mm:ss a Z";

	@Option(names = { "-z",
			"--timezone" }, description = "Timezone short code or id (e.g. IST or Asia/Kolkata. repeat for passing multiple values)")
	private String[] zones;

	@Unmatched
	private String[] unmatched;

	@Option(names = { "-o",
			"--offset" }, description = "Offset from UTC format: +8, +8.5,+08:00,+08:30,-8, -8.5 -08:00,-08:30. repeat for passing multiple values")
	private String[] offsets;

	@Override
	public Integer call() throws Exception {
		Integer exitCode = 0;
		try {
			System.out.println(
					TimeConversionUtil.convertDateTime(input, format, ZonedDateTime.now(), zones, null, offsets));
		} catch (Exception e) {
			exitCode = 500;
			throw e;
		}
		return exitCode;
	}

	public static void main(String... args) {
		CommandLine commandLine = new CommandLine(new TimeConverter());
		commandLine.setUnmatchedOptionsArePositionalParams(true);
		int exitCode = commandLine.execute(args);
		System.exit(exitCode);
	}
}