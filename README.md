
**A long time ago in a galaxy far, far away....**

  

<img align="left" width="100" alt="Yoda Star-Wars" src="https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Yoda_Star-Wars.jpg/256px-Yoda_Star-Wars.jpg?20231019100020">

A lone jedi knight had to travel far and wide across the galaxy, even with the help of the force, he found it hard to keep track of different timezones....

  

Help me master, he knelt beside his grand master **Yoda**. Help me to quickly navigate the times... I need to convert dates and time faster...

  

Master Yoda, slowly opened his eyes, "**Time what is?** You ask".Hmm? Hmm.. I will help.. For my ally is the Force, and a powerful ally it is......

  

# About twi

  

A simple CLI tool to be able to convert time and date, including timezone conversions. It uses natural language processing to parse time and date components from english text and convert using the awesome joda-time library.

  

I created this tool because I am more comfortable with CLI and I could not find a open source application that fit my needs.

  

The CLI is built using picocli library.

  

# NLP Parser

There are quite a number of NLP parsers available for java like **Apache OpenNLP**, **Stanford NLP**, etc but I preferred not to use any of them due to a couple of reasons:

- Most of the available libraries are not supporting some of the grammars I frequently use.

- Some of these libraries are under a more restrictive open source license and I wanted this tool to be under MIT License.

  

For these reasons, I created a NLP parser from scratch, for date-time processing. its kept simple intentionally and only supports English as of now. Test folder contains a test class with all supported formats as of now.

## supported Grammars

Following are the supported input grammars:

1. Week day - e.g. Sunday or Sun

2. Month names - e.g. March or Mar

3. 4 digit Year

4. Relative terms - today, yesterday, day after tomorrow, day before yesterday etc.

5. Full date in 10th July 2024 or 10 jul or jul 10 2024 - If month or date or year is not given, then the current date's month, day or year will be used

6. increments or decrements of day,month,year,hour and minutes

7. Timezones (1 or many) - can use short code or id or full name (e.g. SGT or Asia/Singapore)

8. Offset (1 or many) - utc offset in format of +8.5 or +08:30

  

Below table gives a overview of some of the above test outcomes

| #  | Current Date Time   | Current Weekday | Input String         | Output                 | Remarks                                     |
| -- | ------------------- | --------------- | -------------------- | ---------------------- | ------------------------------------------- |
| 1  | 17/07/2024 00:00:00 | Wednesday       | Tuesday              | 23-07-2024             |                                             |
| 2  | 17/07/2024 00:00:00 | Wednesday       | Thursday             | 18-07-2024             |                                             |
| 3  | 17/07/2024 00:00:00 | Wednesday       | Today                | 17-07-2024             |                                             |
| 4  | 17/07/2024 00:00:00 | Wednesday       | Tomorrow             | 18-07-2024             |                                             |
| 5  | 17/07/2024 00:00:00 | Wednesday       | Yesterday            | 16-07-2024             |                                             |
| 6  | 17/07/2024 00:00:00 | Wednesday       | Day Before Yesterday | 15-07-2024             |                                             |
| 7  | 17/07/2024 00:00:00 | Wednesday       | Day After Tomorrow   | 19-07-2024             |                                             |
| 8  | 17/07/2024 00:00:00 | Wednesday       | last Monday          | 15-07-2024             |                                             |
| 9  | 17/07/2024 00:00:00 | Wednesday       | past Monday          | 15-07-2024             |                                             |
| 10 | 17/07/2024 00:00:00 | Wednesday       | this past Monday     | 15-07-2024             |                                             |
| 11 | 17/07/2024 00:00:00 | Wednesday       | this Monday          | 22-07-2024             |                                             |
| 12 | 17/07/2024 00:00:00 | Wednesday       | coming Monday        | 22-07-2024             |                                             |
| 13 | 17/07/2024 00:00:00 | Wednesday       | August               | 17-08-2024             |                                             |
| 14 | 17/07/2024 00:00:00 | Wednesday       | January              | 17-01-2025             |                                             |
| 15 | 17/07/2024 00:00:00 | Wednesday       | July                 | 17-07-2024             |                                             |
| 16 | 17/07/2024 00:00:00 | Wednesday       | July 19th            | 19-07-2024             |                                             |
| 17 | 17/07/2024 00:00:00 | Wednesday       | 19th July            | 19-07-2024             |                                             |
| 18 | 17/07/2024 00:00:00 | Wednesday       | August 15th          | 15-08-2024             |                                             |
| 19 | 17/07/2024 00:00:00 | Wednesday       | 15th August          | 15-08-2024             |                                             |
| 20 | 17/07/2024 00:00:00 | Wednesday       | September            | 17-09-2024             |                                             |
| 21 | 17/07/2024 00:00:00 | Wednesday       | 8 PM                 | 17-07-2024 08:00:00 PM |                                             |
| 22 | 17/07/2024 00:00:00 | Wednesday       | 05:30 AM             | 17-07-2024 05:30:00 AM |                                             |
| 23 | 17/07/2024 00:00:00 | Wednesday       | 5 Hours              | 17-07-2024 05:00:00 AM |                                             |
| 24 | 17/07/2024 00:00:00 | Wednesday       | 21 Hours             | 17-07-2024 09:00:00 PM |                                             |
| 25 | 17/07/2024 00:00:00 | Wednesday       | 23rd July 8 PM       | 23-07-2024 08:00:00 PM |                                             |
| 26 | 17/07/2024 00:00:00 | Wednesday       | 23rd July 8 AM       | 23-07-2024 08:00:00 AM |                                             |
| 27 | 17/07/2024 00:00:00 | Wednesday       | 23rd July 7:30 AM    | 23-07-2024 07:30:00 AM |                                             |
| 28 | 17/07/2024 00:00:00 | Wednesday       | 3rd July 3:30 PM     | 03-07-2024 03:30:00 PM |                                             |
| 29 | 17/07/2024 00:00:00 | Wednesday       | Thursday 8 AM        | 18-07-2024 08:00:00 AM |                                             |
| 30 | 17/07/2024 00:00:00 | Wednesday       | 15th August 7:30 PM  | 15-08-2024 07:30:00 PM |                                             |
| 31 | 17/07/2024 00:00:00 | Wednesday       | +2h                  | 17-07-2024 02:00:00 AM | Can use hr,hrs,hour and hours in place of h |
| 33 | 17/07/2024 00:00:00 | Wednesday       | +2.5h                | 17-07-2024 02:30:00 AM | Can use hr,hrs,hour and hours in place of h |
| 33 | 17/07/2024 00:00:00 | Wednesday       | \-10d                | 07-07-2024 12:00:00 AM | negative d-day, can use day,days as well    |


  

# Installation

## Windows

> Note: Requires Java 9 or above

1. Download twi-1.0-windows.zip from latest Release

2. Unzip in a suitable path

3. Open command prompt as administrator

4. cd to expanded folder

5. run twi-win-install.bat

6. After success message close the command prompt

  

# Usage

> Note: run from Command Prompt

  `twi [-hV] [-f=<format>] [-o=<offsets>]... [-z=<zones>]... <input>`

Prints the converted date & time to STDOUT.
where, 

**\<input\>**  - Date Time in Natural Language. (refer *supported Grammars section above for valid input formats*)

-f, --format=**\<format\>** - Date/Time Format(e.g. dd-MM-yyyy/dd-MM-yyyy HH:mm:ss)

-h, --help Show this help message and exit.

-o, --offset=**\<offsets\>** -  Offset from UTC (format: +8,                   +8.5,+08:00,+08:30,-8,-8.5 -08:00,-08:30. repeat for passing multiple values)

-V, --version Print version information and exit.

-z, --timezone=\<zones\> (Timezone short code or id (e.g. IST or Asia/Kolkata. repeat for passing multiple values)

  

# Compatibility

The tool is developed in Java so should work in most platforms. I tested in Windows 11 though.

I developed using JDK 21, but it should work in JDKs 9 or above

# What twi is

Its a lightweight tool that can be used to convert date times. The code is oriented towards simplicity over performance. Feel free to review the code, modify as you wish for your needs.

# What twi is NOT

- This software is provided AS IS without any warranty.

- It is not tested for Production use.

# Acknowledgements

- I would like to thank the following Authors/Websites for making the data set available for use:

- Country Codes and Country Name reference data as JSON from - https://country.io/data/

- Country-Timezone mapping as JSON from - https://stackoverflow.com/a/40160169

- Yoda's Image - <a href="https://commons.wikimedia.org/wiki/File:Yoda_Star-Wars.jpg">photographied by Supremerian1988</a>, Public domain, via Wikimedia Commons